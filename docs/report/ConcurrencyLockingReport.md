# 동시성 이슈를 해결하기 위한 DB Lock 적용 보고서

## 🎟️ 선착순 쿠폰 발급

### ⚠️ 문제 상황

- 쿠폰 재고가 2개 남은 상황에서 3명의 사용자가 동시에 발급 요청
    - **기대 결과**: 2명 발급 성공, 1명 발급 실패
    - **실제 결과**: 3명 모두 발급 실패


### 🔍 원인 분석

> 문제 요약: 선착순 처리 실패 → 전체 실패 처리 발생
>
- `Coupon` 테이블에 대한 **동시 접근** 중 **데드락(Deadlock)** 발생
- **예외 로그 예시**:

    ```sql
    org.springframework.dao.CannotAcquireLockException:
      could not execute statement
      [Deadlock found when trying to get lock; try restarting transaction]
    ```

- 트랜잭션 A는 `coupon_item`에 insert 시, 참조 무결성 검사로 인해 `coupon` 테이블에 **공유 락(S-Lock)** 발생
- 트랜잭션 B는 `coupon` 수량을 update하면서 **배타 락(X-Lock)**을 시도하며 충돌

  → 이로 인해 **Deadlock 발생**


### 💡 해결 방안: **비관적 락 (Pessimistic Locking)**

- 트랜잭션 시작 시 쿠폰 조회 시점에 **쓰기 락(X-Lock)**을 걸어 **동시 수정 방지**

    ```java
    @Lock(LockModeType.PESSIMISTIC_WRITE)
    @Query("select c from Coupon c where c.id = :couponId")
    Optional<Coupon> findByIdForUpdate(Long couponId);
    ```


### ✅ 적용 효과

- 동시 발급 요청 시 하나의 트랜잭션만 수량 차감 가능
- 나머지는 락 해제 후 순차 처리 → **Deadlock 방지**
- 재고가 2개일 때 3명이 동시에 요청하더라도 **2명 성공, 1명 실패**로 처리 가능

### 🏷️ 대안: **낙관적 락 (Optimistic Lock)**

- `@Version` 기반 충돌 감지 + 재시도 로직 필요

장점 : DB 락 미사용 → 성능 우수

단점

- 선착순 특성상 충돌 가능성 높음 → **재시도 비용 큼**
- 충돌 시 수량이 남아도 실패 가능

> 결론: 선착순 로직에는 비관적 락이 더 적합
>

---

## 💰 회원 포인트 충전

### ⚠️ 문제 상황

- 하나의 회원이 동시에 3번 포인트 충전 요청
    - **기대 결과**: 모든 충전 요청이 누적 반영
    - **실제 결과**: 마지막 요청만 반영됨 (이전 요청은 덮어쓰기됨)


### 🔍 원인 분석

> 문제 요약: 동시성으로 인한 Race Condition
>
- 각각의 트랜잭션이 **동일한 시점의 잔액(0.00)**을 기준으로 업데이트 수행
- 마지막 커밋만 반영되고, 이전 충전 내역은 무시됨

    ```
    pool-2-thread-2 - start
    pool-2-thread-3 - start
    pool-2-thread-1 - start
    현재 balance: 0.00
    현재 balance: 0.00
    현재 balance: 0.00
    충전할 금액: 10
    충전할 금액: 12
    충전된 balance: 10.00
    충전할 금액: 11
    충전된 balance: 11.00
    충전된 balance: 12.00
    최종 balance 저장됨 (12.00)
    ```


### 💡 해결 방안: **낙관적 락 + 재시도 처리**

- 사용자 포인트 충전은 동시 발생 확률이 낮다고 판단
- `@Version` 필드로 낙관적 락 적용 + `@Retryable`로 재시도 처리

    ```java
    @Version
    private Long version;
    ```

    ```java
    @Retryable(
        retryFor = {ObjectOptimisticLockingFailureException.class},
        maxAttempts = 3,
        backoff = @Backoff(delay = 100),
        recover = "recoverCharge"
    )
    @Transactional
    public MemberPointResult.ChargeBalance charge(MemberPointCriteria.Charge criteria) {
        // 충전 도메인 로직
    }
    
    @Recover
    public MemberPointResult.ChargeBalance recoverCharge(Exception e, MemberPointCriteria.Charge criteria) {
        throw new ECommerceException(MemberPointErrorCode.CONCURRENCY_CHARGE);
    }
    ```


### ✅ 적용 효과

- 동시 충전 요청 시 누락되던 이전 충전 내역이 재시도를 통해 누적 반영됨
- 최대 3회 재시도로 충전 성공률 개선

---

## 📦 상품 재고 차감

### ⚠️ 문제 상황

- 재고가 1개 남은 상품에 대해 3명의 회원이 동시에 결제 요청 (구매 수량 1개)
    - **기대 결과**: 한 건만 성공, 나머지 2건은 재고 부족으로 실패
    - **실제 결과**: 3건 모두 결제 성공 → 재고 수량은 1개만 감소


### 🔍 원인 분석

> 문제 요약: **Race Condition** 발생
>
- 모든 차감 요청이 성공하면서 `Payment` 객체 3개 생성
- 재고 수량 부족 예외 발생하지 않음

### 💡 해결 방안: **비관적 락(Pessimistic Lock) 적용**

- `ProductStock` 조회 시점에 **쓰기 락(X-Lock)** 을 걸어 동시 차감 방지

    ```java
    @Lock(LockModeType.PESSIMISTIC_WRITE)
    @Query("select ps from ProductStock ps where ps.product.id = :productId")
    Optional<ProductStock> findByProductIdForUpdate(Long productId);
    ```

- 동시 재고 차감 요청 중 하나의 트랜잭션만 수량 차감 가능
- 수량 부족 시 예외 발생

    ```java
    kr.hhplus.be.server.shared.exception.ECommerceException: 상품의 재고가 부족합니다.
    ```


### ✅ 적용 효과

- 재고 수량이 정확하게 유지됨
- 재고가 1개일 때 3명이 동시에 요청하더라도 **1명만 성공**, **2명은 예외 처리**

---

## 🎟 결제 요청 중 쿠폰 사용

### ⚠️ 문제 상황

- 하나의 회원이 동일한 주문에 동일한 쿠폰을 사용하여 결제 요청을 **동시에 3번** 시도
    - **기대 결과**: 첫 요청만 쿠폰 적용 성공, 이후 2건은 **이미 사용된 쿠폰**으로 실패
    - **실제 결과**: **모든 요청에서 쿠폰 사용 처리 성공** → 쿠폰이 중복 사용됨


### 🔍 원인 분석

> 문제 요약: 쿠폰 사용 상태 업데이트 중 동시성 Race Condition 발생
>
- 여러 트랜잭션이 동시에 같은 `CouponItem` 객체를 조회 → 쿠폰이 사용되지 않은 상태로 판단
- 각각의 트랜잭션이 독립적으로 쿠폰을 "사용 처리" → **중복 사용 허용됨**

### 💡 해결 방안: **비관적 락(Pessimistic Lock) 적용**

- `CouponItem` 조회 시점에 **쓰기 락(X-Lock)** 을 걸어 **동시 업데이트 방지**

```java
@Lock(LockModeType.PESSIMISTIC_WRITE)
@Query("select ci from CouponItem ci where ci.id = :id")
Optional<CouponItem> findByIdForUpdate(Long id);
```

- 락을 통해 하나의 트랜잭션만 쿠폰 상태(예: `USED`) 변경 가능
- 이후 요청은 **이미 사용된 쿠폰**으로 판단 → 아래와 같은 예외 발생 처리

```java
kr.hhplus.be.server.shared.exception.ECommerceException: 사용할 수 없는 쿠폰입니다.
```

### ✅ 적용 효과

- 쿠폰 사용 상태가 **정확하게 반영**됨
- 동일 쿠폰에 대한 **동시 결제 요청** 시, **1건만 사용 처리 성공**
- 중복 적용 방지 → **정합성 확보**

---

## 💰 결제 요청 중 포인트 사용

### ⚠️ 문제 상황

- 하나의 회원이 서로 다른 주문에 대해 결제 요청을 **동시에 3번 시도**
    - **기대 결과**: 첫 요청만 잔액 내에서 결제 성공, 나머지 2건은 **잔액 부족**으로 실패
    - **실제 결과**: 낙관적 락 충돌 발생 → 2건 이상 결제 실패


### 🔍 원인 분석

> 문제 요약: 포인트 차감 중 낙관적 락 충돌 발생
>
- `@Version` 기반 낙관적 락 사용 중, **동시 트랜잭션 간 충돌 발생**
- `@Retryable` 미적용 상태이므로 충돌 발생 시 재시도 없이 **예외 발생**

```java
org.springframework.orm.ObjectOptimisticLockingFailureException:
Row was updated or deleted by another transaction
```

### 💡 해결 방안: **비관적 락(Pessimistic Lock) 적용**

- `MemberPoint` 조회 시점에 **쓰기 락(X-Lock)**을 걸어 **동시 수정 방지**

```java
@Lock(LockModeType.PESSIMISTIC_WRITE)
@Query("select mp from MemberPoint mp where mp.member.id = :memberId")
Optional<MemberPoint> findByMemberIdForUpdate(Long memberId);
```

- 하나의 트랜잭션만 포인트 차감 가능
- 이후 트랜잭션은 **차감된 잔액**으로 판단 → **잔액 부족 예외 처리**

```java
kr.hhplus.be.server.shared.exception.ECommerceException: 잔액이 부족합니다.

```

### ✅ 적용 효과

- 포인트 차감 과정의 **정합성 보장**
- **하나의 요청만 결제 성공**, 나머지는 **잔액 부족으로 실패**
- 낙관적 락 충돌 및 재시도(`@Retryable`) 필요성 제거

---

## 🛠 향후 개선 방향 정리

### 1) 락 전략의 유연한 전환 및 조합

- 충돌 가능성/요청 빈도/성능 민감도/비즈니스 정책에 따라

  **낙관적 락 ↔ 비관적 락을 기능별로 적절히 조합**하여 적용하기


### 2) Redis 기반 분산 락 또는 메시지 큐 도입

- **DB 락 경합이 심한 시점**에 대해 분산 락 적용 시, DB 부하 감소 및 스케일 확장성 확보 가능
- 주문/결제 등은 **이벤트 기반 비동기 처리 구조**로 전환 가능

### 3) 주요 도메인에 대한 정합성 모니터링 도입

- 쿠폰, 포인트, 재고 등 핵심 리소스의 사용 현황, 충돌 로그, 실패 비율을 **정기적으로 수집/분석할 수 있는 지표 체계 구성**

### 4) 재시도 정책 및 타임아웃 정책 세분화

- 낙관적 락 기반 트랜잭션에는 **재시도 횟수와 딜레이를 상황에 따라 동적으로 조절**
- 비관적 락의 **데드락 회피를 위한 타임아웃 설정 및 경고 로그 구성**
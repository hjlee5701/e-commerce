# DB 성능 최적화 보고서

###  개요
- 조회 쿼리 중, 성능 저하가 발생할 수 있는 기능을 선별했습니다.


- 성능 개선을 위해 쿼리 튜닝과 인덱스 생성을 기반해 분석한 보고서 입니다.


- `EXPLAIN ANALYZE` : 인덱스 적용 전후의 성능을 분석했습니다.


- `Dummy Data` : 최소 10만건~100만 건 이상의 더미 데이터를 입력했습니다.

---

# 1. 상품 전체 조회
- 등록된 모든 상품을 조회합니다.
- 상품이 누적될 수록 조회 성능 저하가 예상 됩니다.
- 페이징과 인덱스와 커서 기반 페이징을 비교 분석했습니다.

## 1) 페이징
```sql
explain analyze
SELECT * FROM product LIMIT 100, 150;
```

```sql
-> Limit/Offset: 150/100 row(s)  (cost=31509 rows=150) (actual time=0.0474..0.0751 rows=150 loops=1)
    -> Table scan on product  (cost=31509 rows=306273) (actual time=0.0363..0.0656 rows=250 loops=1)
```

## 2) 커서 기반 페이징
```sql
explain analyze
SELECT * FROM product where product_id > 100 LIMIT 0, 50;
```
```sql
-> Limit: 50 row(s)  (cost=31509 rows=50) (actual time=0.0628..0.0761 rows=50 loops=1)
    -> Table scan on product  (cost=31509 rows=306273) (actual time=0.0615..0.0704 rows=50 loops=1)

```

### 성능 비교 분석
| 항목                    | product_id > 100 LIMIT 100, 150   | LIMIT 100, 150         |
|-----------------------|-----------------------------------|------------------------|
| 쿼리 제한        | 50 rows                           | 50 rows                |
|예상 비용| 30775                             | 31509                  |
| 예상 행 수     | 50                                | 50                     |
| 실제 실행 시간 | 약 0.044초                          | 약 0.07초                |
| 스캔 방식                 | 인덱스 범위 스캔 (Index range scan)      | 전체 테이블 스캔 (Table scan) |
| 스캔 대상                 | product 테이블 (조건: product_id > 100) | product 테이블 (전체 스캔)    |

### 결과
- 일반 페이징은 전체 테이블을 스캔해야 한다는 단점이 있지만, 커서 기반한 페이징은 인덱스를 활용한 빠른 조회가 가능합니다.
- 조회 대상이 많아 질 수록 커서 기반의 페이징이 성능상 이점이 더 많이 가져 갈 것으로 예상됩니다.

---

# 2. 보유 쿠폰 조회

## 사용 가능한 쿠폰 조회
- 사용자가 보유한 `USABLE` (사용 가능) 상태의 쿠폰을 조회합니다.

- 쿠폰을 보유한 회원 수가 많아질 수록 성능 저하가 예상됩니다.
  - `user_id`: 카디널리티 높다.
  - `coupon_item_status`: 카디널리티 가 낮다.
  > 2개의 조건 중 높은 카디널리티의 값을 앞에 둠으로서 더 좁은 범위의 데이터를 빠르게 찾을 수 있습니다.

#### 인덱스 생성
```sql
CREATE INDEX idx_member_id_status ON coupon_item(member_id, status);
```

#### 인덱스 생성 전
```sql
-> Index lookup on ci using FK84if0pa10y0l9th8yl92n4f97 (member_id=5044)
    (cost=21 rows=60) (actual time=0.223..0.227 rows=60 loops=1)
```

#### 인덱스 생성 후
```sql
-> Index lookup on coupon_item using idx_member_id_status (member_id=100) 
    (cost=4.2 rows=12) (actual time=0.0445..0.0495 rows=12 loops=1)
```

### 성능 비교 분석
|       항목        | 인덱스 생성 전  | 인덱스 생성 후 |   개선 비율|    
|:---------------:|:---------:|:--------:|:-----------:|
|  예상 비용   |    21     |   4.2    |  약 80% 감소   |
| 조회된 행 수  |    60     |    12    |  약 80% 감소   |
| 실제 실행 시간 | 약 0.225 초 | 약 0.047초 |  약 80% 감소 |  

### 결과
- 인덱스 생성 후 성능이 크게 개선되었습니다.
- 기존 인덱스는 FK(member_id) 에 해당하는 인덱스를 사용했지만, 새로 생성한 인덱스 `idx_member_id_status는 member_id`와 `status`를 기반으로 한 복합 인덱스로 성능을 최적화하였습니다.

- 평균 80% 의 감소 결과와 더불어 약 0.225초에서 0.047초로 매우 빠른 실행 시간이 달성되었습니다.

---

# 3. 인기 상품 조회

- 결제 완료된 상품 통계 테이블에서 집계한 순위를 바탕으로 인기 상품을 조회합니다.
- 현재 시간을 기준으로 가장 높은 판매 상품을 5개를 반환합니다.<br><br>

- 더미 데이터는 하루에 동일한 상품이 입력되지 않는다는 전제로 입력했습니다.

```sql
explain analyze
select product.product_id as 'join',
       product.title,
       product.price,
       sum(os.total_sold_quantity) as total_quantity
  from order_statistics os
  left join product product on os.product_id = product.product_id
 WHERE os.statistics_at >= '2025-04-12' AND os.statistics_at <= '2025-04-15'
 group by os.product_id
order by total_quantity desc;
```

## 1) 단일 인덱스
#### 인덱스 생성

- `statistics_at`: 비교 조건에 인덱스를 적용해 성능 향상을 비교해보았습니다.

```sql
CREATE INDEX idx_order_statistics_statistics_at ON order_statistics(statistics_at);
```

#### 인덱스 적용 전

```sql
-> Sort: total DESC (actual time=1202..1206 rows=47899 loops=1)
    -> Table scan on <temporary> (actual time=1172..1181 rows=47899 loops=1)
        -> Aggregate using temporary table (actual time=1172..1172 rows=47899 loops=1)
            -> Nested loop left join (cost=233917 rows=128071) (actual time=9.22..593 rows=346801 loops=1)
                -> Filter: (os.statistics_at >= TIMESTAMP '2025-04-12 00:00:00' AND os.statistics_at <= TIMESTAMP '2025-04-15 00:00:00') 
                    (cost=116709 rows=128071) (actual time=9.2..439 rows=346801 loops=1)
                    -> Table scan on os 
                        (cost=116709 rows=1.15e+6) (actual time=9.19..361 rows=1.16e+6 loops=1)
                -> Single-row index lookup on product using PRIMARY (product_id=os.product_id) 
                    (cost=0.815 rows=1) (actual time=327e-6..343e-6 rows=1 loops=346801)

```

#### 인덱스 적용 후
```sql
-> Sort: total DESC (actual time=1941..1944 rows=47899 loops=1)
    -> Table scan on <temporary> (actual time=1914..1922 rows=47899 loops=1)
        -> Aggregate using temporary table (actual time=1914..1914 rows=47899 loops=1)
            -> Nested loop left join (cost=233645 rows=128071) (actual time=5.35..1309 rows=346801 loops=1)
                -> Filter: (os.statistics_at >= TIMESTAMP '2025-04-12 00:00:00' AND os.statistics_at <= TIMESTAMP '2025-04-15 00:00:00') 
                    (cost=116437 rows=128071) (actual time=5.33..1028 rows=346801 loops=1)
                    -> Table scan on os 
                        (cost=116437 rows=1.15e+6) (actual time=5.32..945 rows=1.16e+6 loops=1)
                -> Single-row index lookup on product using PRIMARY (product_id=os.product_id) 
                    (cost=0.815 rows=1) (actual time=676e-6..694e-6 rows=1 loops=346801)

```
### 성능 비교 분석
| 항목 | 인덱스 적용 전 | 인덱스 적용 후 | 개선 비율|
|:--:|:---------:|:--------:|:-----------:|
정렬 (Sort) | 약 1105.5ms | 약 999.5ms | 약 9.6% 개선
임시 테이블 스캔 | 약 1078ms | 약 973.5ms | 약 9.7% 개선
집계 (Aggregate) | 약 1074ms | 약 969ms | 약 9.8% 개선
중첩 루프 왼쪽 조인 | 약 243.625ms | 약 202.224ms | 약 17% 개선
필터 및 테이블 스캔 | 약 125.65ms | 약 92.216ms | 약 26.6% 개선

### 결과
- 평균 10% 이상은 성능이 향상되었습니다.
- 특히 스캔 후 필터링 (날짜 비교) 에서 가장 높은 개선을 볼 수 있었습니다.

 
## 2) 커버링 인덱스

#### 커버링 인덱스 생성
- 주문 통계와 조회 상품을 포함할 수 있는 인덱스를 적용해보았습니다.

```sql
CREATE INDEX idx_order_statistics_covering
ON order_statistics (product_id, statistics_at, total_sold_quantity);

CREATE INDEX idx_product_covering 
ON product (product_id, title, price);
```

```sql
-> Sort: total DESC (actual time=381..384 rows=47899 loops=1)
    -> Stream results (cost=173898 rows=47381) (actual time=0.556..360 rows=47899 loops=1)
        -> Group aggregate: sum(os.total_sold_quantity) (cost=173898 rows=47381) (actual time=0.549..351 rows=47899 loops=1)
            -> Nested loop left join (cost=161090 rows=128071) (actual time=0.525..328 rows=346801 loops=1)
                -> Filter: (os.statistics_at >= TIMESTAMP '2025-04-12 00:00:00' AND os.statistics_at <= TIMESTAMP '2025-04-15 00:00:00') 
                    (cost=116266 rows=128071) (actual time=0.458..241 rows=346801 loops=1)
                    -> Covering index scan on os using idx_order_statistics_covering 
                        (cost=116266 rows=1.15e+6) (actual time=0.45..176 rows=1.16e+6 loops=1)
                -> Single-row index lookup on product using PRIMARY (product_id=os.product_id)
                    (cost=0.25 rows=1) (actual time=136e-6..152e-6 rows=1 loops=346801)
```
### 성능 비교 분석

|       항목       | 인덱스 적용 후 | 커버링 인덱스 적용 후 | 개선 비율|
|:--------------:|:---------:|:--------:|:-----------:|
   정렬 (Sort)    | 약 999.5ms | 약 381ms | 약 61.8% 개선
   임시 테이블 스캔    | 약 973.5ms | N/A | 성능 개선
 집계 (Aggregate) | 약 969ms | N/A | 성능 개선
  중첩 루프 왼쪽 조인   | 약 202.224ms | N/A | 성능 개선
  필터 및 테이블 스캔   | 약 92.216ms | N/A | 성능 개선
    전체 쿼리 성능    | 약 5.5초 | 약 0.56초 | 약 80% 개선

### 결과
- 커버링 인덱스 적용 후, 성능이 약 80% 개선되었습니다.
- 커버링 인덱스를 사용함으로서 불필요한 테이블 스캔을 피하고 데이터 검색을 더욱 효율적으로 처리할 수 있었습니다.
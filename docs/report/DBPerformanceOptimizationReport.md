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
- OFFSET 기반 페이징과 커서 기반 페이징을 비교 분석했습니다.

## 1) OFFSET 기반 페이징
```sql
explain analyze
SELECT * FROM product LIMIT 100, 150;
```

```sql
-> Limit/Offset: 150/100 row(s)  
   (cost=29749 rows=150)  
   (actual time=0.0899..0.155 rows=150 loops=1)

   -> Table scan on product  
      (cost=29749 rows=283135)  
      (actual time=0.0662..0.134 rows=250 loops=1)

```

## 2) 커서 기반 페이징
```sql
explain analyze
SELECT * FROM product where product_id > 100 LIMIT 0, 50;
```
```sql
-> Limit: 50 row(s)  
   (cost=28537 rows=50)  
   (actual time=0.328..0.34 rows=50 loops=1)
   
   -> Filter: (product.product_id > 100)  
      (cost=28537 rows=141567)  
      (actual time=0.327..0.337 rows=50 loops=1)
      
      -> Index range scan on product using PRIMARY over (100 < product_id)  
         (cost=28537 rows=141567)  
         (actual time=0.325..0.332 rows=50 loops=1)
```

### 성능 비교 분석
| 항목             | OFFSET 기반 페이징 (LIMIT 100, 150)        | 커서 기반 페이징(product_id > 100 LIMIT 100, 150) |
|------------------|---------------------------------------|--------------------------------------------|
| 쿼리 제한         | 150 rows (앞 100건 skip, 이후 150건 fetch) | 150 rows (앞 100건 skip, 이후 150건 fetch)      |
| 예상 비용         | 29,749                                | 28,537                                     |
|                  | -- 전체 테이블 스캔으로 비용이 더 큼                | -- 인덱스 탐색으로 상대적으로 낮은 비용                    |
| 예상 행 수        | 283,135                               | 141,567                                    |
|                  | -- 전체 테이블 대상 추정 행 수                   | -- 인덱스 범위 조건으로 절반 수준                       |
| 실제 실행 시간     | 약 0.0899~0.155초                       | 약 0.328~0.340초                             |
|                  | -- 지금은 빠르지만, 페이지 커지면 성능 하락 가능         | -- 시간이 일정하지만, 조건에 따라 더 느릴 수도 있음            |
| 스캔 방식         | 전체 테이블 스캔 (Table scan)                | 인덱스 범위 스캔 (Index range scan)               |
|                  | -- 조건 없이 테이블 전체를 탐색                   | -- PRIMARY 키 인덱스 범위 조건 사용                  |

### 결과
- OFFSET 기반 페이징은 전체 테이블을 스캔해야 한다는 단점이 있지만, 커서 기반한 페이징은 인덱스를 활용한 조회가 가능합니다.
- 위 성능 분석에 사용한 더미 데이터는 약 30만 개로 데이터 양은 충분 했습니다.
- 다만 페이징 깊이가 얕아 기대했던 인덱스의 빠른 조회를 볼 수 없었습니다.

- 따라서 페이징 깊이가 깊은 경우, 기대했던 인덱스를 기반한 성능 개선을 확인할 수 있었습니다.
```sql
- OFFSET 기반
-> Limit/Offset: 10050/10000 row(s)
   (cost=36870 rows=10050)
   (actual time=3.26..6.77 rows=10050 loops=1)

   -> Table scan on product
      (cost=36870 rows=354919)
      (actual time=0.0497..5.66 rows=20050 loops=1)


- 커서 기반
-> Limit: 50 row(s)
   (cost=35717 rows=50)
   (actual time=0.0294..0.0379 rows=50 loops=1)

   -> Filter: (product.product_id > 10000)
      (cost=35717 rows=177459)
      (actual time=0.0284..0.0351 rows=50 loops=1)

      -> Index range scan on product using PRIMARY over (10000 < product_id)
         (cost=35717 rows=177459)
         (actual time=0.0274..0.0318 rows=50 loops=1)


```
> 실행 시간은 **약 6.8초** -> **약 0.038초** 로 속도 개선
- 즉, 페이징이 깊을 수록, 데이터가 많을 수록 커서 기반 페이징 방식이 적절하다는 것을 확인할 수 있습니다.

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

## 1) 복합 인덱스
#### 인덱스 생성

- 기존에 있던 product_id 의 제약 조건과 인덱스를 삭제 후 새로운 인덱스를 생성했습니다.
- 조건절에 있는 statistics_at 앞에, Join 에 사용되는 product_id 를 뒤로 넣어 복합 인덱스를 구성했습니다.

```sql
CREATE INDEX idx_stat_pid ON order_statistics(statistics_at, product_id);
```

#### 인덱스 적용 전

```sql
-> Sort: total  (actual time=162..162 rows=2323 loops=1)
    -> Table scan on <temporary>  (actual time=161..161 rows=2323 loops=1)
        -> Aggregate using temporary table  (actual time=161..161 rows=2323 loops=1)
            -> Nested loop left join  (cost=144311 rows=75092) (actual time=0.64..157 rows=5851 loops=1)
                -> Filter: (
                       os.statistics_at >= TIMESTAMP '2025-02-12 00:00:00' AND 
                       os.statistics_at <= TIMESTAMP '2025-02-15 00:00:00'
                   )
                   (cost=68133 rows=75092) (actual time=0.617..153 rows=5851 loops=1)
                    -> Table scan on os  
                       (cost=68133 rows=675961) (actual time=0.574..119 rows=724760 loops=1)
                -> Single-row index lookup on product using PRIMARY (product_id=os.product_id)  
                   (cost=0.914 rows=1) (actual time=496e-6..513e-6 rows=1 loops=5851)

```

#### 인덱스 적용 후
```sql
-> Sort: total  (actual time=86.5..86.6 rows=2323 loops=1)
    -> Table scan on <temporary>  (actual time=85.8..86 rows=2323 loops=1)
        -> Aggregate using temporary table  (actual time=85.8..85.8 rows=2323 loops=1)
            -> Nested loop left join  (cost=16315 rows=10636) (actual time=14.1..82.3 rows=5851 loops=1)
                -> Index range scan on os using idx_stat_pid 
                   over ('2025-02-12 00:00:00.000000' <= statistics_at <= '2025-02-15 00:00:00.000000'), 
                   with index condition: (
                       os.statistics_at >= TIMESTAMP '2025-02-12 00:00:00' AND 
                       os.statistics_at <= TIMESTAMP '2025-02-15 00:00:00'
                   )
                   (cost=4786 rows=10636) (actual time=10.8..23.8 rows=5851 loops=1)
                -> Single-row index lookup on product using PRIMARY (product_id=os.product_id)  
                   (cost=0.984 rows=1) (actual time=0.00985..0.00987 rows=1 loops=5851)

```

### 성능 비교 분석
|       항목       |   인덱스 적용 전   | 인덱스 적용 후 | 개선 비율|
|:--------------:|:------------:|:--------:|:-----------:|
|   정렬 (Sort)    |   약 163ms    | 약 25.8ms | 약 84.2% 개선|
| 임시 테이블 스캔  | 약 163ms | 약 25.2ms | 약 84.5% 개선 |
| 집계 (Aggregate) |   약 163ms    | 약 25ms | 약 84.7% 개선|
|  중첩 루프 왼쪽 조인   |  약 158.4ms   | 약 18.4ms | 약 88.4% 개선|
|  필터 및 테이블 스캔   | 약 155ms (추정) | 약 17.5ms | 약 88.7% 개선|


### 결과
- 평균 80% 이상은 성능이 향상되었습니다.
- 특히 스캔 후 필터링 (날짜 비교) 에서 가장 높은 개선을 볼 수 있었습니다.

 
## 2) 커버링 인덱스

#### 커버링 인덱스 생성
- 주문 통계와 조회 상품을 포함할 수 있는 인덱스를 적용해보았습니다.

```sql
CREATE INDEX idx_covering
  ON order_statistics (statistics_at, product_id, total_sold_quantity);
```

```sql
-> Sort: total  (actual time=29.3..29.4 rows=2323 loops=1)
    -> Table scan on <temporary>  (actual time=28.6..28.9 rows=2323 loops=1)
        -> Aggregate using temporary table  (actual time=28.6..28.6 rows=2323 loops=1)
            -> Nested loop left join  (cost=13507 rows=10648) (actual time=0.214..25.6 rows=5851 loops=1)
                -> Filter: (
                       os.statistics_at >= TIMESTAMP '2025-02-12 00:00:00' AND
                       os.statistics_at <= TIMESTAMP '2025-02-15 00:00:00'
                   )
                   (cost=2169 rows=10648) (actual time=0.202..3.96 rows=5851 loops=1)
                    -> Covering index range scan on os using idx_covering
                       over ('2025-02-12 00:00:00.000000' <= statistics_at <= '2025-02-15 00:00:00.000000')
                       (cost=2169 rows=10648) (actual time=0.197..3.41 rows=5851 loops=1)
                -> Single-row index lookup on product using PRIMARY (product_id=os.product_id)
                   (cost=0.965 rows=1) (actual time=0.00356..0.00358 rows=1 loops=5851)

```
### 성능 비교 분석

|항목 | 인덱스 적용 전 (Table Scan) | 인덱스 적용 후 (Covering Index) | 개선 비율|
|:--------------:|:---------:|:--------:|:-----------:|
|정렬 (Sort) | 약 163ms | 약 29.4ms | 약 81.9% 개선|
|임시 테이블 스캔 | 약 163ms | 약 28.9ms | 약 82.3% 개선|
|집계 (Aggregate) | 약 163ms | 약 28.6ms | 약 82.4% 개선|
|중첩 루프 왼쪽 조인 | 약 158.4ms | 약 25.6ms | 약 83.8% 개선|
|필터 및 테이블 스캔 | 약 120~155ms (추정) | 약 3.41ms | 약 97% 개선 이상|
|전체 쿼리 성능 | 약 163ms | 약 29.4ms | 약 82% 개선|

### 결과
- 커버링 인덱스 적용 후, 성능이 약 80% 개선되었습니다.
- 커버링 인덱스를 사용함으로서 불필요한 테이블 스캔을 피하고 데이터 검색을 더욱 효율적으로 처리할 수 있었습니다.

- 하지만, 커버링 인덱스라도, `SUM`은 전체 범위 읽기 필수이므로 위 쿼리의 경우 복합 인덱스의 성능이 더 뛰어남을 확인할 수 있었습니다.
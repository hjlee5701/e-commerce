# 보유 쿠폰 목록 조회 시퀀스

```mermaid
sequenceDiagram
    actor Client
    participant Coupon

    Client->>Coupon: 보유 쿠폰 조회 요청 (사용자 ID)
    activate Coupon

    Coupon->>Coupon: 사용자 ID 로 보유 쿠폰 조회
    alt 존재하지 않는 쿠폰
        Coupon -->> Client : 404 존재하지 않는 쿠폰 응답 
    end
    Coupon-->>Client: 200 보유 쿠폰 목록 응답
    

    deactivate Coupon

```
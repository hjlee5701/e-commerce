# 클래스 다이어그램

```mermaid
classDiagram

class Member {
    +Long id
    +String userId
    +LocalDateTime regAt
    +hasCoupons()
    
}
class MemberPoint {
    +Long id
    +BigDecimal amount
    +charge(amount)
    +use(amount)
    +getAmount()
}
class MemberPointHistory {
    +Long id
    +Long memberId
    +BigDecimal amount
    +TransactionType type
    +addChargeHistory()
    +addUseHistory()
}
class Product {
    +Long id
    +String title
    +BigDecimal price
    +int quantity
    +isSufficient(quantity)
    +increaseStock(quantity)
    +decreaseStock(quantity)
}
class ProductStock {
    +Long id
    +Long productId
    +int quantity
    +StockType type
    +LocalDateTime regAt
    +add(quantity)
    +release(quantity)
}

class Order {
    +Long id
    +Long memberId
    +Long couponItemId
    +BigDecimal totalAmount
    +OrderStatus status
    +LocalDateTime orderAt
    +addItem()
    +setTotalAmount(amount)
    +getTotalAmount()
    +completed()
    
}

class OrderItem {
    +Long id
    +Long orderId
    +Long productId
    +String title
    +int quantity
    +BigDecimal price
}

class OrderStatistic {
    +Long id
    +Long productId
    +int totalSoldQuantity
    +LocalDateTime statisticAt
}

class Payment {
    +Long id
    +Long orderId
    +Long couponItemId
    +BigDecimal originalAmount
    +BigDecimal discountAmount
    +BigDecimal finalAmount
    +PaymentStatus status
    +applyCoupon(amount)
    +completed()
    +cancel()
}

class Coupon {
    +Long id
    +String title
    +int initialQuantity
    +int remainingQuantity
    +int discountAmount
    +CouponStatus couponStatus
    +LocalDateTime issuedAt
    +LocalDateTime expiredAt
    +hasRemaining()
    +isExpired()
    +discount()
}

class CouponStatus {
    <<enumeration>>
    ACTIVE
    SOLD_OUT
    EXPIRED
}

class CouponItem {
    +Long id
    +Long memberId
    +Long couponId
    +CouponItemStatus status
    +use()
    +isUsed()
}

class CouponItemStatus {
    <<enumeration>>
    USABLE
    USED
    EXPIRED
}

Coupon --> CouponStatus
CouponItem --> CouponItemStatus
Product "1" -- "0..*" OrderStatistic
Member "1" -- "1" MemberPoint : has
MemberPoint "1" -- "0..*" MemberPointHistory : has
Member "1" -- "0..*" Order : places
Order "1" -- "0..*" OrderItem : contains
Product "1" -- "0..*" OrderItem : contains
Order "1" -- "1" Payment : contains
Order "1" -- "0..1" CouponItem : has
Product "1" -- "0..*" ProductStock : has
Coupon "1" -- "0..*" CouponItem : has
Member "1" -- "0..*" CouponItem : has
Payment "1" -- "0..1" CouponItem : has
```
---
## 정책
- 쿠폰 상태
  - 선착순 쿠폰 발급 시작 : `ACTIVE`
  - 선착순 쿠폰 소진 : `SOLD_OUT`
  - 선착순 쿠폰 기한 만료 : `EXPIRED`
- 쿠폰 아이템 상태
  - 쿠폰 사용 가능 : `USABLE`
  - 쿠폰 사용 완료 : `USED`
  - 쿠폰 기한 만료 : `EXPIRED`
package kr.hhplus.be.server.support.instancio;

import kr.hhplus.be.server.domain.coupon.Coupon;
import kr.hhplus.be.server.domain.coupon.CouponItem;
import kr.hhplus.be.server.domain.coupon.CouponItemStatus;
import kr.hhplus.be.server.domain.coupon.CouponStatus;
import kr.hhplus.be.server.domain.member.Member;
import kr.hhplus.be.server.domain.product.Product;
import kr.hhplus.be.server.domain.statistics.OrderStatistics;
import org.instancio.Instancio;
import org.instancio.Select;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.*;

@Component
public class DummyDataGenerator {

    private static final Random random = new Random();

    private int randomNumber(int start, int end) {
        return random.nextInt(end) + start;
    }


    // 10일 간의 랜덤 날짜 생성
    private LocalDateTime generateRandomDateTime() {
        return LocalDateTime.now().minusDays(randomNumber(0, 3));
    }

    private LocalDate generateRandomDate() {
        return LocalDate.now().minusDays(randomNumber(0, 3));
    }
    private LocalDateTime generateBeforeDate(int minusDays) {
        int daysAgo = random.nextInt(minusDays);
        return LocalDateTime.now().minusDays(daysAgo);
    }

    private LocalDateTime generateAfterDate(int afterDays) {
        int daysAgo = random.nextInt(afterDays);
        return LocalDateTime.now().minusDays(daysAgo);
    }

    public List<Product> generateDummyProducts(int count) {
        return Instancio.of(Product.class)
                .set(Select.field("title"), "Product-#{random.number()}")
                .set(Select.field("price"), new BigDecimal("50.00"))
                .set(Select.field("quantity"), 10)
                .stream()
                .limit(count)
                .toList();
    }

    public List<OrderStatistics> generateOrderStatisticsForProducts(List<Long> productsIds, int count) {
        Set<String> usedCombinations = new HashSet<>();
        List<OrderStatistics> orderStatisticsList = new ArrayList<>();

        for (Long productId : productsIds) {
            LocalDate randomDate = generateRandomDate();

            // 날짜만 비교하기 위해 randomDate의 시간 부분을 제외한 날짜만 사용
            String combinationKey = randomDate + "_" + productId;

            // 같은 날짜, 동일한 productId의 조합이 없다면 생성
            if (!usedCombinations.contains(combinationKey)) {
                OrderStatistics orderStatistics = Instancio.of(OrderStatistics.class)
                        .set(Select.field("totalSoldQuantity"), randomNumber(1, 100))
                        .set(Select.field("product"), Product.referenceById(productId))
                        .set(Select.field("statisticsDate"), randomDate)
                        .create();

                orderStatisticsList.add(orderStatistics);
                usedCombinations.add(combinationKey);
            }
        }
        return orderStatisticsList;

    }



    public List<Coupon> generateCoupon(int count) {

        return Instancio.of(Coupon.class)
                .set(Select.field("title"), "Coupon-#{random.number()}")
                .set(Select.field("initialQuantity"), randomNumber(100, 200))
                .set(Select.field("remainingQuantity"), randomNumber(10, 100))
                .set(Select.field("discountAmount"), BigDecimal.valueOf(12).multiply(BigDecimal.valueOf(randomNumber(10, 100))))
                .set(Select.field("status"), CouponStatus.ACTIVE)
                .set(Select.field("issuedAt"), generateBeforeDate(20))
                .set(Select.field("expiredAt"), generateAfterDate(20))
                .stream()
                .limit(count)
                .toList();

    }

    public List<CouponItem> generateCouponItem(List<Long>memberIds, List<Long> couponIds) {
        List<CouponItem> result = new ArrayList<>();
        CouponItemStatus status = CouponItemStatus.USABLE;
        for (Long memberId : memberIds) {
                int count = randomNumber(1, 30);
                result.addAll(Instancio.of(CouponItem.class)
                        .set(Select.field("member"), Member.referenceById(memberId))
                        .set(Select.field("coupon"), Coupon.referenceById(memberId+randomNumber(1, 4)))
                        .set(Select.field("status"), CouponItemStatus.EXPIRED)
                        .stream()
                        .limit(count)
                        .toList());
        }
        return result;
    }

    public List<Member> generateMember(int count) {
        return Instancio.of(Member.class)
                .set(Select.field("userId"), "Member-#{random.number()}")
                .set(Select.field("regAt"), generateRandomDateTime())
                .stream()
                .limit(count)
                .toList();
    }

    public List<Product> generateProducts(int endNum) {
        return Instancio.of(Product.class)
                .set(Select.field("title"), "Title-#{random.number()}")
                .set(Select.field("price"), BigDecimal.valueOf(randomNumber(10000, 500000)))
                .set(Select.field("quantity"), randomNumber(100, 10000))
                .stream()
                .limit(endNum)
                .toList();
    }


}

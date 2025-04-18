package kr.hhplus.be.server.support.instancio;

import kr.hhplus.be.server.domain.coupon.Coupon;
import kr.hhplus.be.server.domain.coupon.CouponItem;
import kr.hhplus.be.server.domain.member.Member;
import kr.hhplus.be.server.domain.product.Product;
import kr.hhplus.be.server.domain.statistics.OrderStatistics;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.ArrayList;
import java.util.List;

@RequiredArgsConstructor
@RestController
@RequestMapping("/dummy")
public class DummyController {

    private final DummyDataGenerator dummyDataGenerator;
    private final BatchDataInserter batchDataInserter;


    @GetMapping("/order-statistics")
    public String insertOrderStatistics(@RequestParam("startNum") int startNum, @RequestParam("endNum") int endNum) {
        List<Long> productIds = new ArrayList<>();
        for (long i = startNum; i < endNum; i++) {
            productIds.add(i);
        }
        List<OrderStatistics> orderStatistics = dummyDataGenerator.generateOrderStatisticsForProducts(productIds, endNum);
        batchDataInserter.insertEntities(orderStatistics);
        return "Success Insert!";

    }
    @GetMapping("/coupons")
    public String insertCoupon(@RequestParam("count") int count) {
        List<Coupon> coupons = dummyDataGenerator.generateCoupon(count);
        batchDataInserter.insertEntities(coupons);
        return "Success Insert!";

    }

    @GetMapping("/coupon-items")
    public String insertCouponItems(@RequestParam("startNum") int startNum, @RequestParam("endNum") int endNum) {
        List<Long> ids = new ArrayList<>();

        for (long i = startNum; i < endNum; i++) {
            ids.add(i);
        }
        List<CouponItem> items = dummyDataGenerator.generateCouponItem(ids, ids);
        batchDataInserter.insertEntities(items);
        return "Success Insert!";

    }

    @GetMapping("/members")
    public String insertMembers(@RequestParam("count") int count) {
        List<Member> members = dummyDataGenerator.generateMember(count);
        batchDataInserter.insertEntities(members);
        return "Success Insert!";
    }

    @GetMapping("/products")
    public String insertProducts(@RequestParam("count") int count) {
        List<Product> products = dummyDataGenerator.generateProducts(count);
        batchDataInserter.insertEntities(products);
        return "Success Insert!";

    }
}

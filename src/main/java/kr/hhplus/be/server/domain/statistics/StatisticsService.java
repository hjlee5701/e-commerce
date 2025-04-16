package kr.hhplus.be.server.domain.statistics;

import kr.hhplus.be.server.domain.product.ProductInfo;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@RequiredArgsConstructor
@Service
public class StatisticsService {

    private final OrderStatisticsRepository statisticsRepository;

    public List<ProductInfo.Popular> getPopular() {

    }
}

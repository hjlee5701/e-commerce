import http from 'k6/http';
import { check } from 'k6';

export let options = {
    scenarios: {
        sustained_load: {
            executor: 'constant-arrival-rate',
            rate: 1000,              // 초당 요청 수 (RPS)
            timeUnit: '1s',
            duration: '3m',          // 총 3분간 테스트
            preAllocatedVUs: 1000,   // 최소 VU 수
            maxVUs: 1200             // 여유 확보
        },
    },
};


export default function () {
    // 캐시 정상 작동 흐름: 캐시된 ID 리스트 기반 응답
    const host = __ENV.TARGET_HOST || 'localhost';
    const url = `http://${host}:8080/products/popular`;

    const res = http.get(url);

    check(res, {
        'status is 200': (r) => r.status === 200,
        'response time < 100ms': (r) => r.timings.duration < 100,
    });
}

# Kafka 기본 개념 보고서

## 1. 서론
- 기존 시스템은 소스와 타겟 간 직접 통신 구조로, 확장성과 유지보수에 한계를 드러냈습니다. 
- 이를 해결하고자 LinkedIn은 Apache Kafka를 개발하였으며, Kafka는 대용량 이벤트 스트리밍에 최적화된 분산형 메시징 플랫폼입니다.

---

## 2. Kafka 개요 및 아키텍처
- Kafka는 실시간 데이터 처리, 재처리 가능성, 확장성을 강점으로 하는 이벤트 브로커입니다.
- 메시지는 토픽(Topic) 단위로 구성되며, 토픽은 다수의 파티션(Partition) 으로 분할되어 병렬성과 순서를 보장합니다.


## 3. Kafka 핵심 구성요소
### 3.1 Kafka의 주요 구성
- Producer: 메시지를 Kafka로 발행
- Consumer: Kafka에서 메시지를 수신
- Broker: 메시지를 저장·전달하는 Kafka 서버 인스턴스
- Cluster: 여러 Broker로 구성된 분산 환경

### 3.2 Topic, Partition, Key
- Topic: 메시지의 논리적 그룹
- Partition: 병렬 처리 단위이며, Key를 기준으로 메시지 순서를 보장
- Key: 동일 Key의 메시지는 동일 파티션에 저장되어 순서 보장

### 3.3 Broker, Replication, ISR
- Broker: Kafka 메시지를 저장 및 서비스하는 서버
- Replication: Partition을 여러 Broker에 복제
- ISR: 리더와 동기화된 복제본 목록, 장애 발생 시 새 리더로 승격됨

### 3.4 Consumer Group 및 Rebalancing
- Consumer Group: 동일 토픽을 병렬 처리하는 컨슈머 집합
- Rebalancing: 컨슈머 수 변동 또는 장애 발생 시 파티션 재분배 발생

### 3.5 Offset
- Offset: 컨슈머가 메시지를 읽은 위치 / 수동 커밋 또는 자동 커밋 방식으로 관리 가능

---
## 4. 메시지 처리 전략 및 안정성
### 4.1 중복/유실 방지 전략
- Exactly-Once Semantics(EOS): 메시지 처리 중복 방지
- `acks=all`, `min.insync.replicas` 설정: 유실 방지
- Idempotent Producer, 재시도 로직 구현: 메시지 안정성 확보

### 4.2 장애 메시지 처리 전략
| 전략                          | 설명                 |
| --------------------------- | ------------------ |
| **Dead Letter Queue (DLQ)** | 실패 메시지를 별도 토픽으로 이관 |
| **Retry 토픽**                | 일정 시간 후 재처리        |
| **Poison Pill Skip**        | 역직렬화 실패 등 건너뛰기     |
| **커스텀 에러 핸들러**              | 예외 유형별 대응          |
| **Kafka 트랜잭션**              | 데이터 처리 원자성 확보      |

---
## 5. 클러스터 확장 전략
### 5.1 파티션 수 설정
- 파티션 수 ≥ 컨슈머 수
- 과도한 파티션은 오버헤드 초래
- TPS와 확장성 고려하여 산정

### 5.2 컨슈머 수 조절
- 파티션 수 < 컨슈머 수일 경우 일부 유휴 상태
- 컨슈머 수 증가 시 Rebalancing 발생 → Cooperative Sticky 전략 고려

### 5.3 복제본 설정
- 일반적으로 복제본 수는 2~3개
- 브로커 수보다 많을 수 없음
- 내구성과 고가용성 확보 수단

---


## 6. Kafka와 Spring Boot 연동 실습
### 6.1 Gradle 의존성
```groovy
implementation 'org.springframework.kafka:spring-kafka'
```
### 6.2 application.yml 기본 설정 예시
```yaml
kafka:
   bootstrap-servers: localhost:9094 # Kafka 브로커의 접속 주소 (클러스터 외부 노출 포트 사용)

   properties:
      request.timeout.ms: 20000       # 클라이언트 요청 타임아웃 (ms 단위, 브로커 응답 대기 시간)
      retry.backoff.ms: 500           # 재시도 간 대기 시간 (ms 단위)

      auto:
         create.topics.enable: false   # 프로듀서/컨슈머가 존재하지 않는 토픽 자동 생성 여부 (false 권장)
         register.schemas: false       # 스키마 레지스트리에 스키마 자동 등록 비활성화 (false로 명시적 관리 권장)
         offset.reset: latest          # 오프셋이 없을 경우 최신 메시지부터 읽기 (earliest: 과거부터 읽기)

      use.latest.version: true        # 스키마 레지스트리에서 최신 스키마 버전 사용 (true 시 항상 최신 버전 사용)
      basic.auth.credentials.source: USER_INFO # 인증 방식 설정 (스키마 레지스트리 접속 시 사용)

   producer:
      key-serializer: org.apache.kafka.common.serialization.StringSerializer
      # 메시지 키 직렬화 방식 (문자열로 변환)

      value-serializer: org.springframework.kafka.support.serializer.JsonSerializer
      # 메시지 값 직렬화 방식 (JSON 변환)

      retries: 5
      # 메시지 전송 실패 시 최대 재시도 횟수

   consumer:
      key-deserializer: org.apache.kafka.common.serialization.StringDeserializer
      # 메시지 키 역직렬화 방식 (문자열 해석)

      value-deserializer: org.apache.kafka.common.serialization.ByteArrayDeserializer
      # 메시지 값 역직렬화 방식 (바이트 배열로 수신, 이후 수동 처리 가능)

      properties:
         enable-auto-commit: false
         # 오프셋 자동 커밋 비활성화 → 수동 커밋 방식으로 전환하여 데이터 정확성 보장

   listener:
      ack-mode: manual
      # 메시지 수신 후 수동으로 오프셋 커밋 (비즈니스 로직 성공 후 커밋 가능)

```
### 6.3 메시지 발행 예시
```java
kafkaTemplate.send("order-topic", "order-key", orderEvent);
```
1. `order-topic` : 메시지를 전송할 Kafka 토픽 이름
2. `order-key` : 메시지 Key 값
   - 메시지의 파티션 배정 기준으로 사용됨

3. orderEvent: 실제 전송할 메시지 데이터 (Value)

- Kafka는 기본적으로 Key의 해시 값을 기준으로 파티션을 결정함
- 동일한 Key를 가진 메시지는 항상 같은 파티션에 저장되므로, 순서 보장이 가능함
  - 예: `user-123` 을 Key로 쓰면 해당 사용자의 이벤트는 항상 같은 파티션에 저장되어 처리 순서 유지 가능


> 💡 Tip <br>
>  Key를 지정하지 않으면 Kafka는 Round-Robin 방식으로 파티션을 배정하므로, 메시지 순서가 보장되지 않습니다. <br>
>  사용자, 주문, 세션 등 동일 객체 단위의 순서를 보장해야 하는 경우 반드시 Key 설정 필요합니다.

---
## 7. 결론
- Apache Kafka는 높은 확장성과 내결함성, 실시간 처리 능력을 갖춘 이벤트 스트리밍 플랫폼입니다. 
- 핵심 구성요소와 운영 전략, Spring Boot 연동 방법을 충분히 이해하면 안정적이고 유연한 MSA 기반 아키텍처를 구현할 수 있습니다.
package com.odcloud.adapter.out.persistence.jpa;

import static org.assertj.core.api.Assertions.assertThat;

import com.odcloud.IntegrationTestSupport;
import com.odcloud.domain.model.Subscription;
import jakarta.persistence.EntityManager;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicReference;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.support.TransactionTemplate;

/**
 * {@code FakeSubscriptionStoragePort}는 인메모리 리스트에 저장된 동일 Java 객체 참조를 그대로
 * 반환하므로 트랜잭션/영속성 컨텍스트 개념을 모델링할 수 없다. 따라서 {@code findByIdForUpdate}의
 * 비관적 락이 실제로 동시 트랜잭션을 직렬화하는지는 H2 기반 통합 테스트로만 검증할 수 있다.
 *
 * <p>클래스 레벨에 {@code @Transactional}을 붙이지 않는다 — 두 스레드가 각자 실제로 커밋해야
 * 서로 다른 트랜잭션 간의 락 대기 상황을 재현할 수 있기 때문이다.
 */
class SubscriptionRepositoryConcurrencyTest extends IntegrationTestSupport {

    @Autowired
    SubscriptionStorageAdapter adapter;

    @Autowired
    PlatformTransactionManager transactionManager;

    @Autowired
    EntityManager entityManager;

    @AfterEach
    void tearDown() {
        new TransactionTemplate(transactionManager).executeWithoutResult(status ->
            entityManager.createQuery("DELETE FROM SubscriptionEntity").executeUpdate());
    }

    @Nested
    @DisplayName("[findByIdForUpdate] 비관적 쓰기 락으로 구독을 조회하는 메소드")
    class Describe_findByIdForUpdate {

        @Test
        @DisplayName("[success] 락을 보유한 트랜잭션이 커밋하기 전까지 다른 트랜잭션의 동일 행 조회를 대기시킨다")
        void success_blocksConcurrentTransactionUntilCommit() throws Exception {
            // given: 별도의 트랜잭션으로 초기 구독을 커밋해 둔다.
            TransactionTemplate transactionTemplate = new TransactionTemplate(transactionManager);
            Long subscriptionId = transactionTemplate.execute(status -> adapter.save(Subscription.builder()
                .productId(100L)
                .groupId(1L)
                .buyerId(10L)
                .status("ACTIVE")
                .billingKey("billing-key-123")
                .nextBillingDate(LocalDate.now().plusDays(15))
                .expiredDate(LocalDate.now().plusDays(15))
                .regDt(LocalDateTime.now())
                .build()).getId());

            CountDownLatch lockAcquiredByA = new CountDownLatch(1);
            CountDownLatch releaseLockFromA = new CountDownLatch(1);
            AtomicReference<String> statusSeenByB = new AtomicReference<>();

            ExecutorService executor = Executors.newFixedThreadPool(2);
            try {
                // A: 락을 먼저 획득하고, 신호를 받을 때까지 커밋하지 않고 대기한다.
                Future<?> futureA = executor.submit(() ->
                    transactionTemplate.executeWithoutResult(status -> {
                        Subscription locked = adapter.findByIdForUpdate(subscriptionId);
                        lockAcquiredByA.countDown();
                        awaitQuietly(releaseLockFromA);
                        locked.cancel();
                        adapter.save(locked);
                    }));

                lockAcquiredByA.await(5, TimeUnit.SECONDS);
                long beforeB = System.currentTimeMillis();

                // B: A가 락을 보유한 상태에서 동일 행을 findByIdForUpdate로 조회 시도 → 블로킹되어야 한다.
                Future<?> futureB = executor.submit(() ->
                    transactionTemplate.executeWithoutResult(status -> {
                        entityManager.createNativeQuery("SET LOCK_TIMEOUT 10000").executeUpdate();
                        Subscription seenByB = adapter.findByIdForUpdate(subscriptionId);
                        statusSeenByB.set(seenByB.getStatus());
                    }));

                // A가 일정 시간 락을 들고 있다가 해제(커밋)하도록 유도한다.
                Thread.sleep(400);
                releaseLockFromA.countDown();

                futureA.get(5, TimeUnit.SECONDS);
                futureB.get(5, TimeUnit.SECONDS);
                long elapsedForB = System.currentTimeMillis() - beforeB;

                // then: B는 A가 커밋(status=EXP_PENDING)한 뒤에야 락을 획득해 최신 상태를 관측해야 하고,
                // 그만큼 대기 시간이 소요되어야 한다(= 동시 접근이 직렬화되었다는 증거).
                assertThat(statusSeenByB.get()).isEqualTo("EXP_PENDING");
                assertThat(elapsedForB).isGreaterThanOrEqualTo(300L);
            } finally {
                executor.shutdownNow();
            }
        }
    }

    private void awaitQuietly(CountDownLatch latch) {
        try {
            latch.await(5, TimeUnit.SECONDS);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
    }
}

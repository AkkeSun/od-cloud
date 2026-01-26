package com.odcloud.application.voucher.service.create_voucher;

import static org.assertj.core.api.Assertions.assertThat;

import com.odcloud.application.voucher.port.in.command.CreateVoucherCommand;
import com.odcloud.domain.model.Group;
import com.odcloud.domain.model.Payment;
import com.odcloud.domain.model.PaymentStatus;
import com.odcloud.domain.model.StoreType;
import com.odcloud.domain.model.Voucher;
import com.odcloud.domain.model.VoucherStatus;
import com.odcloud.domain.model.VoucherType;
import com.odcloud.fakeClass.FakeGroupStoragePort;
import com.odcloud.fakeClass.FakePaymentStoragePort;
import com.odcloud.fakeClass.FakeVoucherStoragePort;
import java.time.LocalDateTime;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

class CreateVoucherServiceTest {

    private CreateVoucherService service;
    private FakePaymentStoragePort paymentStoragePort;
    private FakeVoucherStoragePort voucherStoragePort;
    private FakeGroupStoragePort groupStoragePort;

    @BeforeEach
    void setUp() {
        paymentStoragePort = new FakePaymentStoragePort();
        voucherStoragePort = new FakeVoucherStoragePort();
        groupStoragePort = new FakeGroupStoragePort();
        service = new CreateVoucherService(
            paymentStoragePort,
            voucherStoragePort,
            groupStoragePort
        );
    }

    @Nested
    @DisplayName("[create] 바우처 생성 메서드")
    class Describe_create {

        @Test
        @DisplayName("[success] STORAGE_PLUS 바우처를 생성하고 그룹 스토리지를 증가시킨다")
        void success_createStoragePlusVoucher() {
            // given
            LocalDateTime now = LocalDateTime.now();
            Group group = Group.builder()
                .id(10L)
                .name("Test Group")
                .ownerEmail("test@example.com")
                .storageUsed(0L)
                .storageTotal(3221225472L) // 3GB
                .regDt(now)
                .build();
            groupStoragePort.save(group);

            CreateVoucherCommand command = CreateVoucherCommand.builder()
                .accountId(1L)
                .storeType(StoreType.APPLE)
                .subscriptionKey("sub_apple_123")
                .orderTxId("APPLE_TX_12345")
                .storeProcessDt(now)
                .voucherType(VoucherType.STORAGE_PLUS)
                .groupId(10L)
                .memo("프리미엄 플랜 구매")
                .build();

            // when
            CreateVoucherServiceResponse response = service.create(command);

            // then
            assertThat(response).isNotNull();
            assertThat(response.result()).isTrue();

            // Verify payment created
            assertThat(paymentStoragePort.database).hasSize(1);
            Payment payment = paymentStoragePort.database.get(0);
            assertThat(payment.getAccountId()).isEqualTo(1L);
            assertThat(payment.getStoreType()).isEqualTo(StoreType.APPLE);
            assertThat(payment.getStatus()).isEqualTo(PaymentStatus.PAID);

            // Verify voucher created
            assertThat(voucherStoragePort.database).hasSize(1);
            Voucher voucher = voucherStoragePort.database.get(0);
            assertThat(voucher.getVoucherType()).isEqualTo(VoucherType.STORAGE_PLUS);
            assertThat(voucher.getStatus()).isEqualTo(VoucherStatus.ACTIVE);
            assertThat(voucher.getGroupId()).isEqualTo(10L);
            assertThat(voucher.getEndDt()).isNotNull();
            assertThat(voucher.getEndDt()).isAfter(voucher.getStartAt());

            // Verify group storage increased by 300GB
            Group updatedGroup = groupStoragePort.findById(10L);
            long expectedTotal = 3221225472L + (300L * 1024 * 1024 * 1024);
            assertThat(updatedGroup.getStorageTotal()).isEqualTo(expectedTotal);
        }

        @Test
        @DisplayName("[success] STORAGE_BASIC 바우처를 생성하고 endDt가 설정된다")
        void success_createStorageBasicVoucher() {
            // given
            LocalDateTime now = LocalDateTime.now();
            Group group = Group.builder()
                .id(20L)
                .name("Test Group 2")
                .ownerEmail("test@example.com")
                .storageTotal(3221225472L)
                .regDt(now)
                .build();
            groupStoragePort.save(group);

            CreateVoucherCommand command = CreateVoucherCommand.builder()
                .accountId(2L)
                .storeType(StoreType.GOOGLE)
                .subscriptionKey("sub_google_456")
                .orderTxId("GOOGLE_TX_67890")
                .storeProcessDt(now)
                .voucherType(VoucherType.STORAGE_BASIC)
                .groupId(20L)
                .memo("100GB 플랜")
                .build();

            // when
            CreateVoucherServiceResponse response = service.create(command);

            // then
            assertThat(response).isNotNull();
            assertThat(response.result()).isTrue();

            // Verify voucher created
            assertThat(voucherStoragePort.database).hasSize(1);
            Voucher voucher = voucherStoragePort.database.get(0);
            assertThat(voucher.getVoucherType()).isEqualTo(VoucherType.STORAGE_BASIC);
            assertThat(voucher.getEndDt()).isNotNull(); // STORAGE_BASIC has 30 days duration

            // Verify group storage increased by 100GB
            Group updatedGroup = groupStoragePort.findById(20L);
            long expectedTotal = 3221225472L + (100L * 1024 * 1024 * 1024);
            assertThat(updatedGroup.getStorageTotal()).isEqualTo(expectedTotal);
        }

        @Test
        @DisplayName("[success] ADVERTISE 바우처를 생성하고 그룹 스토리지는 변경되지 않는다")
        void success_createAdvertiseVoucher() {
            // given
            LocalDateTime now = LocalDateTime.now();
            CreateVoucherCommand command = CreateVoucherCommand.builder()
                .accountId(3L)
                .storeType(StoreType.APPLE)
                .subscriptionKey("sub_advertise")
                .orderTxId("APPLE_TX_ADVERTISE")
                .storeProcessDt(now)
                .voucherType(VoucherType.ADVERTISE_30)
                .groupId(null)
                .memo("광고 제거")
                .build();

            // when
            CreateVoucherServiceResponse response = service.create(command);

            // then
            assertThat(response).isNotNull();
            assertThat(response.result()).isTrue();

            // Verify voucher created
            assertThat(voucherStoragePort.database).hasSize(1);
            Voucher voucher = voucherStoragePort.database.get(0);
            assertThat(voucher.getVoucherType()).isEqualTo(VoucherType.ADVERTISE_30);
            assertThat(voucher.getGroupId()).isNull();
            assertThat(voucher.getEndDt()).isNotNull();
            assertThat(voucher.getEndDt()).isAfter(voucher.getStartAt());
        }

        @Test
        @DisplayName("[success] STORAGE_BASIC 바우처를 생성하고 그룹 스토리지를 100GB 증가시킨다 v2")
        void success_createStorageBasicVoucher_v2() {
            // given
            LocalDateTime now = LocalDateTime.now();
            Group group = Group.builder()
                .id(30L)
                .name("Test Group 3")
                .ownerEmail("test@example.com")
                .storageTotal(3221225472L)
                .regDt(now)
                .build();
            groupStoragePort.save(group);

            CreateVoucherCommand command = CreateVoucherCommand.builder()
                .accountId(4L)
                .storeType(StoreType.GOOGLE)
                .subscriptionKey("sub_basic")
                .orderTxId("GOOGLE_TX_BASIC")
                .storeProcessDt(now)
                .voucherType(VoucherType.STORAGE_BASIC)
                .groupId(30L)
                .memo("기본 플랜")
                .build();

            // when
            CreateVoucherServiceResponse response = service.create(command);

            // then
            assertThat(response).isNotNull();
            assertThat(response.result()).isTrue();

            // Verify voucher created
            assertThat(voucherStoragePort.database).hasSize(1);
            Voucher voucher = voucherStoragePort.database.get(0);
            assertThat(voucher.getVoucherType()).isEqualTo(VoucherType.STORAGE_BASIC);

            // Verify group storage increased by 100GB
            Group updatedGroup = groupStoragePort.findById(30L);
            long expectedTotal = 3221225472L + (100L * 1024 * 1024 * 1024);
            assertThat(updatedGroup.getStorageTotal()).isEqualTo(expectedTotal);
        }

        @Test
        @DisplayName("[success] 여러 바우처를 생성한다")
        void success_createMultipleVouchers() {
            // given
            LocalDateTime now = LocalDateTime.now();

            Group group1 = Group.builder()
                .id(40L)
                .name("Group 1")
                .ownerEmail("test@example.com")
                .storageUsed(0L)
                .storageTotal(3221225472L)
                .regDt(now)
                .build();
            groupStoragePort.save(group1);

            Group group2 = Group.builder()
                .id(50L)
                .name("Group 2")
                .ownerEmail("test@example.com")
                .storageUsed(0L)
                .storageTotal(3221225472L)
                .regDt(now)
                .build();
            groupStoragePort.save(group2);

            CreateVoucherCommand command1 = CreateVoucherCommand.builder()
                .accountId(1L)
                .storeType(StoreType.APPLE)
                .subscriptionKey("sub_1")
                .orderTxId("TX_1")
                .storeProcessDt(now)
                .voucherType(VoucherType.STORAGE_BASIC)
                .groupId(40L)
                .memo("첫 번째 바우처")
                .build();

            CreateVoucherCommand command2 = CreateVoucherCommand.builder()
                .accountId(2L)
                .storeType(StoreType.GOOGLE)
                .subscriptionKey("sub_2")
                .orderTxId("TX_2")
                .storeProcessDt(now)
                .voucherType(VoucherType.STORAGE_PLUS)
                .groupId(50L)
                .memo("두 번째 바우처")
                .build();

            // when
            CreateVoucherServiceResponse response1 = service.create(command1);
            CreateVoucherServiceResponse response2 = service.create(command2);

            // then
            assertThat(response1).isNotNull();
            assertThat(response1.result()).isTrue();
            assertThat(response2).isNotNull();
            assertThat(response2.result()).isTrue();
            assertThat(paymentStoragePort.database).hasSize(2);
            assertThat(voucherStoragePort.database).hasSize(2);
        }
    }
}

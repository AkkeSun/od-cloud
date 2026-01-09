package com.odcloud.adapter.in.controller.voucher.create_voucher;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.mock;

import com.odcloud.application.voucher.port.in.command.CreateVoucherCommand;
import com.odcloud.domain.model.Account;
import com.odcloud.domain.model.StoreType;
import com.odcloud.domain.model.VoucherType;
import jakarta.validation.ConstraintViolation;
import jakarta.validation.Validation;
import jakarta.validation.Validator;
import java.util.Set;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

class CreateVoucherRequestTest {

    private Validator validator;

    @BeforeEach
    void setUp() {
        validator = Validation.buildDefaultValidatorFactory().getValidator();
    }

    @Nested
    @DisplayName("[toCommand] Request를 Command로 변환하는 메서드")
    class Describe_toCommand {

        @Test
        @DisplayName("[success] STORAGE_PLUS Request를 Command로 변환한다")
        void success_storagePlus() {
            // given
            CreateVoucherRequest request = CreateVoucherRequest.builder()
                .storeType(StoreType.APPLE)
                .subscriptionKey("sub_apple_12345")
                .orderTxId("APPLE_TX_12345")
                .storeProcessDt("2026-01-09 10:30:00")
                .voucherType(VoucherType.STORAGE_PLUS)
                .groupId(10L)
                .memo("프리미엄 플랜")
                .build();

            Account account = mock(Account.class);
            given(account.getId()).willReturn(1L);

            // when
            CreateVoucherCommand command = request.toCommand(account);

            // then
            assertThat(command).isNotNull();
            assertThat(command.accountId()).isEqualTo(1L);
            assertThat(command.storeType()).isEqualTo(StoreType.APPLE);
            assertThat(command.subscriptionKey()).isEqualTo("sub_apple_12345");
            assertThat(command.orderTxId()).isEqualTo("APPLE_TX_12345");
            assertThat(command.voucherType()).isEqualTo(VoucherType.STORAGE_PLUS);
            assertThat(command.groupId()).isEqualTo(10L);
            assertThat(command.memo()).isEqualTo("프리미엄 플랜");
            assertThat(command.storeProcessDt()).isNotNull();
        }

        @Test
        @DisplayName("[success] ADVERTISE Request를 Command로 변환한다")
        void success_advertise() {
            // given
            CreateVoucherRequest request = CreateVoucherRequest.builder()
                .storeType(StoreType.GOOGLE)
                .subscriptionKey("sub_google_456")
                .orderTxId("GOOGLE_TX_67890")
                .storeProcessDt("2026-01-09 11:00:00")
                .voucherType(VoucherType.ADVERTISE)
                .groupId(null)
                .memo("광고 제거")
                .build();

            Account account = mock(Account.class);
            given(account.getId()).willReturn(2L);

            // when
            CreateVoucherCommand command = request.toCommand(account);

            // then
            assertThat(command).isNotNull();
            assertThat(command.accountId()).isEqualTo(2L);
            assertThat(command.voucherType()).isEqualTo(VoucherType.ADVERTISE);
            assertThat(command.groupId()).isNull();
        }
    }

    @Nested
    @DisplayName("[validation] 바우처 검증 테스트")
    class Describe_validation {

        @Test
        @DisplayName("[success] 모든 필수값이 있는 STORAGE_BASIC 바우처는 검증을 통과한다")
        void success_storageBasicWithGroupId() {
            // given
            CreateVoucherRequest request = CreateVoucherRequest.builder()
                .storeType(StoreType.APPLE)
                .subscriptionKey("sub_basic")
                .orderTxId("APPLE_TX_BASIC")
                .storeProcessDt("2026-01-09 10:00:00")
                .voucherType(VoucherType.STORAGE_BASIC)
                .groupId(10L)
                .memo("베이직 플랜")
                .build();

            // when
            Set<ConstraintViolation<CreateVoucherRequest>> violations = validator.validate(request);

            // then
            assertThat(violations).isEmpty();
        }

        @Test
        @DisplayName("[success] ADVERTISE 바우처는 groupId 없이도 검증을 통과한다")
        void success_advertiseWithoutGroupId() {
            // given
            CreateVoucherRequest request = CreateVoucherRequest.builder()
                .storeType(StoreType.GOOGLE)
                .subscriptionKey("sub_ad")
                .orderTxId("GOOGLE_TX_AD")
                .storeProcessDt("2026-01-09 10:00:00")
                .voucherType(VoucherType.ADVERTISE)
                .groupId(null)
                .memo("광고 제거")
                .build();

            // when
            Set<ConstraintViolation<CreateVoucherRequest>> violations = validator.validate(request);

            // then
            assertThat(violations).isEmpty();
        }

        @Test
        @DisplayName("[error] STORAGE_BASIC 바우처에 groupId가 없으면 검증에 실패한다")
        void error_storageBasicWithoutGroupId() {
            // given
            CreateVoucherRequest request = CreateVoucherRequest.builder()
                .storeType(StoreType.APPLE)
                .subscriptionKey("sub_basic")
                .orderTxId("APPLE_TX_BASIC")
                .storeProcessDt("2026-01-09 10:00:00")
                .voucherType(VoucherType.STORAGE_BASIC)
                .groupId(null)
                .memo("베이직 플랜")
                .build();

            // when
            Set<ConstraintViolation<CreateVoucherRequest>> violations = validator.validate(request);

            // then
            assertThat(violations).hasSize(1);
            assertThat(violations.iterator().next().getMessage())
                .isEqualTo("스토리지 바우처인 경우 groupId는 필수값 입니다");
        }

        @Test
        @DisplayName("[error] STORAGE_PLUS 바우처에 groupId가 없으면 검증에 실패한다")
        void error_storagePlusWithoutGroupId() {
            // given
            CreateVoucherRequest request = CreateVoucherRequest.builder()
                .storeType(StoreType.APPLE)
                .subscriptionKey("sub_plus")
                .orderTxId("APPLE_TX_PLUS")
                .storeProcessDt("2026-01-09 10:00:00")
                .voucherType(VoucherType.STORAGE_PLUS)
                .groupId(null)
                .memo("플러스 플랜")
                .build();

            // when
            Set<ConstraintViolation<CreateVoucherRequest>> violations = validator.validate(request);

            // then
            assertThat(violations).hasSize(1);
            assertThat(violations.iterator().next().getMessage())
                .isEqualTo("스토리지 바우처인 경우 groupId는 필수값 입니다");
        }

        @Test
        @DisplayName("[error] STORAGE_50 바우처에 groupId가 없으면 검증에 실패한다")
        void error_storage50WithoutGroupId() {
            // given
            CreateVoucherRequest request = CreateVoucherRequest.builder()
                .storeType(StoreType.GOOGLE)
                .subscriptionKey("sub_50")
                .orderTxId("GOOGLE_TX_50")
                .storeProcessDt("2026-01-09 10:00:00")
                .voucherType(VoucherType.STORAGE_50)
                .groupId(null)
                .build();

            // when
            Set<ConstraintViolation<CreateVoucherRequest>> violations = validator.validate(request);

            // then
            assertThat(violations).hasSize(1);
            assertThat(violations.iterator().next().getMessage())
                .isEqualTo("스토리지 바우처인 경우 groupId는 필수값 입니다");
        }

        @Test
        @DisplayName("[error] STORAGE_100 바우처에 groupId가 없으면 검증에 실패한다")
        void error_storage100WithoutGroupId() {
            // given
            CreateVoucherRequest request = CreateVoucherRequest.builder()
                .storeType(StoreType.GOOGLE)
                .subscriptionKey("sub_100")
                .orderTxId("GOOGLE_TX_100")
                .storeProcessDt("2026-01-09 10:00:00")
                .voucherType(VoucherType.STORAGE_100)
                .groupId(null)
                .build();

            // when
            Set<ConstraintViolation<CreateVoucherRequest>> violations = validator.validate(request);

            // then
            assertThat(violations).hasSize(1);
            assertThat(violations.iterator().next().getMessage())
                .isEqualTo("스토리지 바우처인 경우 groupId는 필수값 입니다");
        }

        @Test
        @DisplayName("[error] storeType이 null이면 검증에 실패한다")
        void error_nullStoreType() {
            // given
            CreateVoucherRequest request = CreateVoucherRequest.builder()
                .storeType(null)
                .subscriptionKey("sub_test")
                .orderTxId("APPLE_TX_12345")
                .storeProcessDt("2026-01-09 10:00:00")
                .voucherType(VoucherType.STORAGE_BASIC)
                .groupId(10L)
                .build();

            // when
            Set<ConstraintViolation<CreateVoucherRequest>> violations = validator.validate(request);

            // then
            assertThat(violations).hasSize(1);
            assertThat(violations.iterator().next().getMessage())
                .isEqualTo("스토어 타입은 필수값 입니다");
        }

        @Test
        @DisplayName("[error] orderTxId가 blank이면 검증에 실패한다")
        void error_blankOrderTxId() {
            // given
            CreateVoucherRequest request = CreateVoucherRequest.builder()
                .storeType(StoreType.APPLE)
                .subscriptionKey("sub_test")
                .orderTxId("")
                .storeProcessDt("2026-01-09 10:00:00")
                .voucherType(VoucherType.STORAGE_BASIC)
                .groupId(10L)
                .build();

            // when
            Set<ConstraintViolation<CreateVoucherRequest>> violations = validator.validate(request);

            // then
            assertThat(violations).hasSize(1);
            assertThat(violations.iterator().next().getMessage())
                .isEqualTo("결제 단위 트랜젝션 아이디는 필수값 입니다");
        }

        @Test
        @DisplayName("[error] storeProcessDt이 blank이면 검증에 실패한다")
        void error_blankStoreProcessDt() {
            // given
            CreateVoucherRequest request = CreateVoucherRequest.builder()
                .storeType(StoreType.APPLE)
                .subscriptionKey("sub_test")
                .orderTxId("APPLE_TX_12345")
                .storeProcessDt("")
                .voucherType(VoucherType.STORAGE_BASIC)
                .groupId(10L)
                .build();

            // when
            Set<ConstraintViolation<CreateVoucherRequest>> violations = validator.validate(request);

            // then
            assertThat(violations).hasSize(1);
            assertThat(violations.iterator().next().getMessage())
                .isEqualTo("스토어 처리 일시는 필수값 입니다");
        }

        @Test
        @DisplayName("[error] voucherType이 null이면 검증에 실패한다")
        void error_nullVoucherType() {
            // given
            CreateVoucherRequest request = CreateVoucherRequest.builder()
                .storeType(StoreType.APPLE)
                .subscriptionKey("sub_test")
                .orderTxId("APPLE_TX_12345")
                .storeProcessDt("2026-01-09 10:00:00")
                .voucherType(null)
                .groupId(10L)
                .build();

            // when
            Set<ConstraintViolation<CreateVoucherRequest>> violations = validator.validate(request);

            // then
            assertThat(violations).hasSize(1);
            assertThat(violations.iterator().next().getMessage())
                .isEqualTo("바우처 타입은 필수값 입니다");
        }
    }

    @Nested
    @DisplayName("[builder] Builder 패턴 테스트")
    class Describe_builder {

        @Test
        @DisplayName("[success] Builder로 Request를 생성한다")
        void success() {
            // when
            CreateVoucherRequest request = CreateVoucherRequest.builder()
                .storeType(StoreType.APPLE)
                .subscriptionKey("sub_test")
                .orderTxId("APPLE_TX_12345")
                .storeProcessDt("2026-01-09 10:00:00")
                .voucherType(VoucherType.STORAGE_BASIC)
                .groupId(10L)
                .memo("테스트 메모")
                .build();

            // then
            assertThat(request).isNotNull();
            assertThat(request.storeType()).isEqualTo(StoreType.APPLE);
            assertThat(request.subscriptionKey()).isEqualTo("sub_test");
            assertThat(request.orderTxId()).isEqualTo("APPLE_TX_12345");
            assertThat(request.voucherType()).isEqualTo(VoucherType.STORAGE_BASIC);
            assertThat(request.groupId()).isEqualTo(10L);
            assertThat(request.memo()).isEqualTo("테스트 메모");
        }

        @Test
        @DisplayName("[success] null 값으로 Request를 생성한다")
        void success_nullValues() {
            // when
            CreateVoucherRequest request = CreateVoucherRequest.builder()
                .storeType(null)
                .subscriptionKey(null)
                .orderTxId(null)
                .storeProcessDt(null)
                .voucherType(null)
                .groupId(null)
                .memo(null)
                .build();

            // then
            assertThat(request.storeType()).isNull();
            assertThat(request.subscriptionKey()).isNull();
            assertThat(request.orderTxId()).isNull();
            assertThat(request.voucherType()).isNull();
            assertThat(request.groupId()).isNull();
            assertThat(request.memo()).isNull();
        }
    }

    @Nested
    @DisplayName("[record] Record 불변성 테스트")
    class Describe_immutability {

        @Test
        @DisplayName("[success] Request는 불변 객체이다")
        void success() {
            // given
            CreateVoucherRequest request1 = CreateVoucherRequest.builder()
                .storeType(StoreType.APPLE)
                .subscriptionKey("sub_test")
                .orderTxId("APPLE_TX_12345")
                .storeProcessDt("2026-01-09 10:00:00")
                .voucherType(VoucherType.STORAGE_BASIC)
                .groupId(10L)
                .memo("테스트")
                .build();

            CreateVoucherRequest request2 = CreateVoucherRequest.builder()
                .storeType(StoreType.APPLE)
                .subscriptionKey("sub_test")
                .orderTxId("APPLE_TX_12345")
                .storeProcessDt("2026-01-09 10:00:00")
                .voucherType(VoucherType.STORAGE_BASIC)
                .groupId(10L)
                .memo("테스트")
                .build();

            // when & then
            assertThat(request1).isEqualTo(request2);
            assertThat(request1.hashCode()).isEqualTo(request2.hashCode());
        }

        @Test
        @DisplayName("[success] 다른 값으로 생성된 Request는 동등하지 않다")
        void success_notEqual() {
            // given
            CreateVoucherRequest request1 = CreateVoucherRequest.builder()
                .storeType(StoreType.APPLE)
                .subscriptionKey("sub_1")
                .orderTxId("APPLE_TX_12345")
                .storeProcessDt("2026-01-09 10:00:00")
                .voucherType(VoucherType.STORAGE_BASIC)
                .groupId(10L)
                .build();

            CreateVoucherRequest request2 = CreateVoucherRequest.builder()
                .storeType(StoreType.GOOGLE)
                .subscriptionKey("sub_2")
                .orderTxId("GOOGLE_TX_67890")
                .storeProcessDt("2026-01-09 11:00:00")
                .voucherType(VoucherType.STORAGE_PLUS)
                .groupId(20L)
                .build();

            // when & then
            assertThat(request1).isNotEqualTo(request2);
        }
    }
}

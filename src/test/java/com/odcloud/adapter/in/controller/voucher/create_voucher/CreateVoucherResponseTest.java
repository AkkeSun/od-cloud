package com.odcloud.adapter.in.controller.voucher.create_voucher;

import static org.assertj.core.api.Assertions.assertThat;

import com.odcloud.application.voucher.service.create_voucher.CreateVoucherServiceResponse;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

class CreateVoucherResponseTest {

    @Nested
    @DisplayName("[of] ServiceResponse를 Response로 변환하는 메서드")
    class Describe_of {

        @Test
        @DisplayName("[success] ServiceResponse를 Response로 변환한다")
        void success() {
            // given
            CreateVoucherServiceResponse serviceResponse = CreateVoucherServiceResponse.ofSuccess();

            // when
            CreateVoucherResponse response = CreateVoucherResponse.of(serviceResponse);

            // then
            assertThat(response).isNotNull();
            assertThat(response.result()).isTrue();
        }

        @Test
        @DisplayName("[success] false 값을 가진 ServiceResponse를 Response로 변환한다")
        void success_falseResult() {
            // given
            CreateVoucherServiceResponse serviceResponse = new CreateVoucherServiceResponse(false);

            // when
            CreateVoucherResponse response = CreateVoucherResponse.of(serviceResponse);

            // then
            assertThat(response).isNotNull();
            assertThat(response.result()).isFalse();
        }
    }

    @Nested
    @DisplayName("[record] Record 불변성 테스트")
    class Describe_immutability {

        @Test
        @DisplayName("[success] Response는 불변 객체이다")
        void success() {
            // given
            CreateVoucherResponse response1 = new CreateVoucherResponse(true);
            CreateVoucherResponse response2 = new CreateVoucherResponse(true);

            // when & then
            assertThat(response1).isEqualTo(response2);
            assertThat(response1.hashCode()).isEqualTo(response2.hashCode());
        }

        @Test
        @DisplayName("[success] 다른 값으로 생성된 Response는 동등하지 않다")
        void success_notEqual() {
            // given
            CreateVoucherResponse response1 = new CreateVoucherResponse(true);
            CreateVoucherResponse response2 = new CreateVoucherResponse(false);

            // when & then
            assertThat(response1).isNotEqualTo(response2);
        }
    }
}

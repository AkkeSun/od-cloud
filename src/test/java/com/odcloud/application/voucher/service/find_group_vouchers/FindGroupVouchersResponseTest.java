package com.odcloud.application.voucher.service.find_group_vouchers;

import static org.assertj.core.api.Assertions.assertThat;

import com.odcloud.application.voucher.port.out.VoucherDetail;
import com.odcloud.domain.model.Group;
import java.time.LocalDateTime;
import java.util.List;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

class FindGroupVouchersResponseTest {

    @Nested
    @DisplayName("[of] 그룹 목록과 바우처 상세 목록으로 Response 생성")
    class Describe_of {

        @Test
        @DisplayName("[success] 각 그룹의 바우처가 올바르게 매핑된다")
        void success_withVouchers() {
            // given
            LocalDateTime expiredAt = LocalDateTime.of(2025, 12, 31, 0, 0);

            List<Group> groups = List.of(
                Group.builder().id(1L).name("개발팀").build(),
                Group.builder().id(2L).name("마케팅팀").build()
            );

            List<VoucherDetail> details = List.of(
                new VoucherDetail("개발팀", "CLOUD_100GB", "홍길동", expiredAt),
                new VoucherDetail("개발팀", "CLOUD_50GB", "김철수", null),
                new VoucherDetail("마케팅팀", "CLOUD_100GB", "이영희", expiredAt)
            );

            // when
            FindGroupVouchersResponse response = FindGroupVouchersResponse.of(groups, details);

            // then
            assertThat(response.groups()).hasSize(2);

            FindGroupVouchersResponse.GroupVouchers devGroup = response.groups().get(0);
            assertThat(devGroup.groupName()).isEqualTo("개발팀");
            assertThat(devGroup.vouchers()).hasSize(2);
            assertThat(devGroup.vouchers().get(0).voucherName()).isEqualTo("CLOUD_100GB");
            assertThat(devGroup.vouchers().get(0).payer()).isEqualTo("홍길동");
            assertThat(devGroup.vouchers().get(0).expiredAt()).isNotNull();
            assertThat(devGroup.vouchers().get(1).voucherName()).isEqualTo("CLOUD_50GB");
            assertThat(devGroup.vouchers().get(1).expiredAt()).isNull();

            FindGroupVouchersResponse.GroupVouchers marketingGroup = response.groups().get(1);
            assertThat(marketingGroup.groupName()).isEqualTo("마케팅팀");
            assertThat(marketingGroup.vouchers()).hasSize(1);
            assertThat(marketingGroup.vouchers().get(0).payer()).isEqualTo("이영희");
        }

        @Test
        @DisplayName("[success] 바우처가 없는 그룹은 빈 vouchers 리스트를 가진다")
        void success_groupWithNoVouchers() {
            // given
            List<Group> groups = List.of(
                Group.builder().id(1L).name("개발팀").build(),
                Group.builder().id(2L).name("바우처없는팀").build()
            );

            List<VoucherDetail> details = List.of(
                new VoucherDetail("개발팀", "CLOUD_100GB", "홍길동", null)
            );

            // when
            FindGroupVouchersResponse response = FindGroupVouchersResponse.of(groups, details);

            // then
            assertThat(response.groups()).hasSize(2);

            FindGroupVouchersResponse.GroupVouchers groupWithVoucher = response.groups().get(0);
            assertThat(groupWithVoucher.groupName()).isEqualTo("개발팀");
            assertThat(groupWithVoucher.vouchers()).hasSize(1);

            FindGroupVouchersResponse.GroupVouchers groupWithoutVoucher = response.groups().get(1);
            assertThat(groupWithoutVoucher.groupName()).isEqualTo("바우처없는팀");
            assertThat(groupWithoutVoucher.vouchers()).isEmpty();
        }

        @Test
        @DisplayName("[success] 그룹 목록이 비어있으면 빈 groups 리스트를 반환한다")
        void success_emptyGroups() {
            // given
            List<Group> groups = List.of();
            List<VoucherDetail> details = List.of();

            // when
            FindGroupVouchersResponse response = FindGroupVouchersResponse.of(groups, details);

            // then
            assertThat(response.groups()).isEmpty();
        }

        @Test
        @DisplayName("[success] 바우처 목록이 비어있으면 모든 그룹이 빈 vouchers 리스트를 가진다")
        void success_emptyVouchers() {
            // given
            List<Group> groups = List.of(
                Group.builder().id(1L).name("개발팀").build(),
                Group.builder().id(2L).name("마케팅팀").build()
            );
            List<VoucherDetail> details = List.of();

            // when
            FindGroupVouchersResponse response = FindGroupVouchersResponse.of(groups, details);

            // then
            assertThat(response.groups()).hasSize(2);
            assertThat(response.groups().get(0).vouchers()).isEmpty();
            assertThat(response.groups().get(1).vouchers()).isEmpty();
        }
    }

    @Nested
    @DisplayName("[VoucherItem.of] VoucherDetail로 VoucherItem 생성")
    class Describe_VoucherItem_of {

        @Test
        @DisplayName("[success] endDt가 있는 경우 expiredAt이 문자열로 변환된다")
        void success_withEndDt() {
            // given
            LocalDateTime endDt = LocalDateTime.of(2025, 12, 31, 0, 0);
            VoucherDetail detail = new VoucherDetail("개발팀", "CLOUD_100GB", "홍길동", endDt);

            // when
            FindGroupVouchersResponse.VoucherItem item = FindGroupVouchersResponse.VoucherItem.of(detail);

            // then
            assertThat(item.voucherName()).isEqualTo("CLOUD_100GB");
            assertThat(item.payer()).isEqualTo("홍길동");
            assertThat(item.expiredAt()).isEqualTo(endDt.toString());
        }

        @Test
        @DisplayName("[success] endDt가 null인 경우 expiredAt은 null이다")
        void success_nullEndDt() {
            // given
            VoucherDetail detail = new VoucherDetail("개발팀", "CLOUD_100GB", "홍길동", null);

            // when
            FindGroupVouchersResponse.VoucherItem item = FindGroupVouchersResponse.VoucherItem.of(detail);

            // then
            assertThat(item.voucherName()).isEqualTo("CLOUD_100GB");
            assertThat(item.payer()).isEqualTo("홍길동");
            assertThat(item.expiredAt()).isNull();
        }
    }
}

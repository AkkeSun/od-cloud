package com.odcloud.application.voucher.service.find_group_vouchers;

import static org.assertj.core.api.Assertions.assertThat;

import com.odcloud.application.voucher.port.out.VoucherDetail;
import com.odcloud.domain.model.Account;
import com.odcloud.domain.model.Group;
import com.odcloud.fakeClass.FakeVoucherStoragePort;
import java.time.LocalDateTime;
import java.util.List;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

class FindGroupVouchersServiceTest {

    private FakeVoucherStoragePort fakeVoucherStoragePort;
    private FindGroupVouchersService findGroupVouchersService;

    @BeforeEach
    void setUp() {
        fakeVoucherStoragePort = new FakeVoucherStoragePort();
        findGroupVouchersService = new FindGroupVouchersService(fakeVoucherStoragePort);
    }

    @Nested
    @DisplayName("[find] 계정의 그룹별 활성 바우처 조회")
    class Describe_find {

        @Test
        @DisplayName("[success] 각 그룹의 활성 바우처가 그룹별로 조회된다")
        void success_withVouchers() {
            // given
            LocalDateTime expiredAt = LocalDateTime.of(2025, 12, 31, 0, 0);

            Group groupA = Group.builder().id(1L).name("개발팀").build();
            Group groupB = Group.builder().id(2L).name("마케팅팀").build();

            Account account = Account.builder()
                .id(1L)
                .email("user@example.com")
                .groups(List.of(groupA, groupB))
                .build();

            fakeVoucherStoragePort.database.add(
                new VoucherDetail("개발팀", "CLOUD_100GB", "홍길동", expiredAt));
            fakeVoucherStoragePort.database.add(
                new VoucherDetail("마케팅팀", "CLOUD_50GB", "김철수", null));

            // when
            FindGroupVouchersResponse response = findGroupVouchersService.find(account);

            // then
            assertThat(response.groups()).hasSize(2);

            FindGroupVouchersResponse.GroupVouchers devGroup = response.groups().get(0);
            assertThat(devGroup.groupName()).isEqualTo("개발팀");
            assertThat(devGroup.vouchers()).hasSize(1);
            assertThat(devGroup.vouchers().get(0).voucherName()).isEqualTo("CLOUD_100GB");

            FindGroupVouchersResponse.GroupVouchers marketingGroup = response.groups().get(1);
            assertThat(marketingGroup.groupName()).isEqualTo("마케팅팀");
            assertThat(marketingGroup.vouchers()).hasSize(1);
            assertThat(marketingGroup.vouchers().get(0).voucherName()).isEqualTo("CLOUD_50GB");
        }

        @Test
        @DisplayName("[success] 바우처가 없는 그룹은 빈 vouchers 리스트로 응답된다")
        void success_groupWithNoVouchers() {
            // given
            Group groupA = Group.builder().id(1L).name("개발팀").build();
            Group groupB = Group.builder().id(2L).name("바우처없는팀").build();

            Account account = Account.builder()
                .id(1L)
                .email("user@example.com")
                .groups(List.of(groupA, groupB))
                .build();

            fakeVoucherStoragePort.database.add(
                new VoucherDetail("개발팀", "CLOUD_100GB", "홍길동", null));

            // when
            FindGroupVouchersResponse response = findGroupVouchersService.find(account);

            // then
            assertThat(response.groups()).hasSize(2);

            assertThat(response.groups().get(0).groupName()).isEqualTo("개발팀");
            assertThat(response.groups().get(0).vouchers()).hasSize(1);

            assertThat(response.groups().get(1).groupName()).isEqualTo("바우처없는팀");
            assertThat(response.groups().get(1).vouchers()).isEmpty();
        }

        @Test
        @DisplayName("[success] 가입된 그룹이 없으면 빈 groups 리스트를 반환한다")
        void success_noGroups() {
            // given
            Account account = Account.builder()
                .id(1L)
                .email("user@example.com")
                .groups(List.of())
                .build();

            // when
            FindGroupVouchersResponse response = findGroupVouchersService.find(account);

            // then
            assertThat(response.groups()).isEmpty();
        }

        @Test
        @DisplayName("[success] 그룹은 있지만 활성 바우처가 전혀 없으면 모든 그룹이 빈 vouchers 리스트를 가진다")
        void success_allGroupsWithNoVouchers() {
            // given
            Account account = Account.builder()
                .id(1L)
                .email("user@example.com")
                .groups(List.of(
                    Group.builder().id(1L).name("개발팀").build(),
                    Group.builder().id(2L).name("마케팅팀").build()
                ))
                .build();

            // when
            FindGroupVouchersResponse response = findGroupVouchersService.find(account);

            // then
            assertThat(response.groups()).hasSize(2);
            assertThat(response.groups().get(0).vouchers()).isEmpty();
            assertThat(response.groups().get(1).vouchers()).isEmpty();
        }
    }
}

package com.odcloud.application.voucher.service.find_group_vouchers;

import com.odcloud.application.voucher.port.out.VoucherDetail;
import com.odcloud.domain.model.Group;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import lombok.Builder;

@Builder
public record FindGroupVouchersResponse(
    List<GroupVouchers> groups
) {

    public static FindGroupVouchersResponse of(List<Group> groups, List<VoucherDetail> details) {
        Map<String, List<VoucherItem>> vouchersByGroup = details.stream()
            .collect(Collectors.groupingBy(
                VoucherDetail::groupName,
                Collectors.mapping(VoucherItem::of, Collectors.toList())
            ));

        List<GroupVouchers> result = groups.stream()
            .map(group -> GroupVouchers.builder()
                .groupName(group.getName())
                .vouchers(vouchersByGroup.getOrDefault(group.getName(), List.of()))
                .build())
            .toList();

        return FindGroupVouchersResponse.builder()
            .groups(result)
            .build();
    }

    @Builder
    public record GroupVouchers(
        String groupName,
        List<VoucherItem> vouchers
    ) {

    }

    @Builder
    public record VoucherItem(
        String voucherName,
        String payer,
        String expiredAt
    ) {

        static VoucherItem of(VoucherDetail detail) {
            return VoucherItem.builder()
                .voucherName(detail.voucherType())
                .payer(detail.payerNickname())
                .expiredAt(detail.endDt() != null ? detail.endDt().toString() : null)
                .build();
        }
    }
}

package com.odcloud.application.voucher.service.find_group_vouchers;

import com.odcloud.application.voucher.port.in.FindGroupVouchersUseCase;
import com.odcloud.application.voucher.port.out.VoucherDetail;
import com.odcloud.application.voucher.port.out.VoucherStoragePort;
import com.odcloud.domain.model.Account;
import com.odcloud.domain.model.Group;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
class FindGroupVouchersService implements FindGroupVouchersUseCase {

    private final VoucherStoragePort voucherStoragePort;

    @Override
    public FindGroupVouchersResponse find(Account account) {
        List<Group> groups = account.getGroups();
        if (groups.isEmpty()) {
            return FindGroupVouchersResponse.of(List.of(), List.of());
        }
        List<VoucherDetail> details = voucherStoragePort.findActiveByGroupIds(account.getGroupIds());
        return FindGroupVouchersResponse.of(groups, details);
    }
}

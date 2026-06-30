package com.odcloud.application.voucher.port.in;

import com.odcloud.application.voucher.service.find_group_vouchers.FindGroupVouchersResponse;
import com.odcloud.domain.model.Account;

public interface FindGroupVouchersUseCase {

    FindGroupVouchersResponse find(Account account);
}

package com.odcloud.application.voucher.service.create_voucher;

import static com.odcloud.infrastructure.constant.CommonConstant.GROUP_LOCK;

import com.odcloud.application.auth.port.out.RedisStoragePort;
import com.odcloud.application.group.port.out.GroupStoragePort;
import com.odcloud.application.voucher.port.in.CreateVoucherUseCase;
import com.odcloud.application.voucher.port.in.command.CreateVoucherCommand;
import com.odcloud.application.voucher.port.out.PaymentStoragePort;
import com.odcloud.application.voucher.port.out.VoucherStoragePort;
import com.odcloud.domain.model.Group;
import com.odcloud.domain.model.Payment;
import com.odcloud.domain.model.Voucher;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
class CreateVoucherService implements CreateVoucherUseCase {

    private final PaymentStoragePort paymentStoragePort;
    private final VoucherStoragePort voucherStoragePort;
    private final GroupStoragePort groupStoragePort;
    private final RedisStoragePort redisStoragePort;

    @Override
    @Transactional
    public CreateVoucherServiceResponse create(CreateVoucherCommand command) {
        Payment savedPayment = paymentStoragePort.save(Payment.create(command));
        voucherStoragePort.save(Voucher.create(savedPayment.getId(), command));

        if (command.voucherType().isStorageVoucher()) {
            for (Group group : groupStoragePort.findByOwnerId(command.accountId())) {
                redisStoragePort.executeWithLock(GROUP_LOCK + group.getId(), () -> {
                    Group lockedGroup = groupStoragePort.findById(group.getId());
                    lockedGroup.updateStorageTotal(
                        command.voucherType().getStorageIncreaseSize());
                    groupStoragePort.updateStorageTotal(lockedGroup);
                    return null;
                });
            }
        }
        return CreateVoucherServiceResponse.ofSuccess();
    }
}

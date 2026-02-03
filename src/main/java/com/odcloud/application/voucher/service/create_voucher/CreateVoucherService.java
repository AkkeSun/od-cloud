package com.odcloud.application.voucher.service.create_voucher;

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

    @Override
    @Transactional
    public CreateVoucherServiceResponse create(CreateVoucherCommand command) {
        Payment savedPayment = paymentStoragePort.save(Payment.create(command));
        voucherStoragePort.save(Voucher.create(savedPayment.getId(), command));

        if (command.voucherType().isStorageVoucher()) {
            Group group = groupStoragePort.findById(command.groupId());
            group.increaseStorageTotal(command.voucherType().getStorageIncreaseSize());
            groupStoragePort.save(group);
        }
        return CreateVoucherServiceResponse.ofSuccess();
    }
}

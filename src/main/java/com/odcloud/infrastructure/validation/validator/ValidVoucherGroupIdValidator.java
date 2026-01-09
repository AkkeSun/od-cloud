package com.odcloud.infrastructure.validation.validator;

import com.odcloud.adapter.in.controller.voucher.create_voucher.CreateVoucherRequest;
import com.odcloud.infrastructure.validation.ValidVoucherGroupId;
import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;

public class ValidVoucherGroupIdValidator implements
    ConstraintValidator<ValidVoucherGroupId, CreateVoucherRequest> {

    @Override
    public boolean isValid(CreateVoucherRequest request, ConstraintValidatorContext context) {
        if (request == null) {
            return true;
        }
        if (request.voucherType() == null) {
            return true;
        }
        return !request.voucherType().isStorageVoucher() || request.groupId() != null;
    }
}

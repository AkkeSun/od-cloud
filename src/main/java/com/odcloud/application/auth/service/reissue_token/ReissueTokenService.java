package com.odcloud.application.auth.service.reissue_token;

import static com.odcloud.infrastructure.exception.ErrorCode.INVALID_REFRESH_TOKEN;

import com.odcloud.application.account.port.out.AccountStoragePort;
import com.odcloud.application.auth.port.in.ReissueTokenUseCase;
import com.odcloud.application.auth.port.out.RedisStoragePort;
import com.odcloud.application.voucher.port.out.VoucherStoragePort;
import com.odcloud.domain.model.Account;
import com.odcloud.domain.model.Voucher;
import com.odcloud.infrastructure.constant.ProfileConstant;
import com.odcloud.infrastructure.exception.CustomAuthenticationException;
import com.odcloud.infrastructure.util.JwtUtil;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.util.ObjectUtils;

@Service
@RequiredArgsConstructor
class ReissueTokenService implements ReissueTokenUseCase {

    private final JwtUtil jwtUtil;
    private final ProfileConstant constant;
    private final RedisStoragePort redisStoragePort;
    private final AccountStoragePort accountStoragePort;
    private final VoucherStoragePort voucherStoragePort;

    @Override
    public ReissueTokenServiceResponse reissueToken(String refreshToken) {
        if (!jwtUtil.validateTokenExceptExpiration(refreshToken)) {
            throw new CustomAuthenticationException(INVALID_REFRESH_TOKEN);
        }

        String email = jwtUtil.getEmail(refreshToken);
        String deviceId = jwtUtil.getDeviceId(refreshToken);
        String redisKey = String.format(constant.redisKey().token(), email, deviceId);

        String savedRefreshToken = redisStoragePort.findData(redisKey, String.class);
        if (ObjectUtils.isEmpty(savedRefreshToken) || !savedRefreshToken.equals(refreshToken)) {
            throw new CustomAuthenticationException(INVALID_REFRESH_TOKEN);
        }

        Account account = accountStoragePort.findByEmail(email);

        List<Voucher> vouchers = voucherStoragePort.findActiveByAccountId(account.getId());
        account.updateVouchers(vouchers);

        String newRefreshToken = jwtUtil.createRefreshToken(account, deviceId);

        redisStoragePort.register(redisKey, newRefreshToken, constant.getRefreshTokenTtl());
        return ReissueTokenServiceResponse.builder()
            .accessToken(jwtUtil.createAccessToken(account))
            .refreshToken(newRefreshToken)
            .build();
    }
}

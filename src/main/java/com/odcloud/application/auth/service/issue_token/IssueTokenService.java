package com.odcloud.application.auth.service.issue_token;

import com.odcloud.adapter.out.client.google.GoogleUserInfoResponse;
import com.odcloud.application.account.port.out.AccountStoragePort;
import com.odcloud.application.auth.port.in.IssueTokenUseCase;
import com.odcloud.application.auth.port.out.GoogleOAuth2Port;
import com.odcloud.application.auth.port.out.RedisStoragePort;
import com.odcloud.application.voucher.port.out.VoucherStoragePort;
import com.odcloud.domain.model.Account;
import com.odcloud.domain.model.Voucher;
import com.odcloud.infrastructure.constant.ProfileConstant;
import com.odcloud.infrastructure.util.JwtUtil;
import com.odcloud.infrastructure.util.UserAgentUtil;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
class IssueTokenService implements IssueTokenUseCase {

    private final JwtUtil jwtUtil;
    private final ProfileConstant constant;
    private final UserAgentUtil userAgentUtil;
    private final GoogleOAuth2Port googleOAuth2Port;
    private final RedisStoragePort redisStoragePort;
    private final AccountStoragePort accountStoragePort;
    private final VoucherStoragePort voucherStoragePort;

    @Override
    public IssueTokenServiceResponse issue(String googleAuthorization) {
        GoogleUserInfoResponse userInfo = googleOAuth2Port.getUserInfo(googleAuthorization);
        Account account = accountStoragePort.findByEmail(userInfo.email());

        List<Voucher> vouchers = voucherStoragePort.findActiveByAccountId(account.getId());
        account.updateVouchers(vouchers);

        String accessToken = jwtUtil.createAccessToken(account);
        String refreshToken = jwtUtil.createRefreshToken(account);

        redisStoragePort.register(String.format(constant.redisKey().token(), account.getEmail(),
            userAgentUtil.getUserAgent()), refreshToken, constant.getRefreshTokenTtl());

        return IssueTokenServiceResponse.builder()
            .accessToken(accessToken)
            .refreshToken(refreshToken)
            .build();
    }
}

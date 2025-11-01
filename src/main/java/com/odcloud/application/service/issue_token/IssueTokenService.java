package com.odcloud.application.service.issue_token;

import static com.odcloud.infrastructure.exception.ErrorCode.Business_ADMIN_NOT_APPROVED;
import static com.odcloud.infrastructure.exception.ErrorCode.Business_INVALID_OTP;

import com.odcloud.application.port.in.IssueTokenUseCase;
import com.odcloud.application.port.in.command.IssueTokenCommand;
import com.odcloud.application.port.out.AccountStoragePort;
import com.odcloud.application.port.out.RedisStoragePort;
import com.odcloud.domain.model.Account;
import com.odcloud.infrastructure.constant.ProfileConstant;
import com.odcloud.infrastructure.exception.CustomBusinessException;
import com.odcloud.infrastructure.util.GoogleOTPUtil;
import com.odcloud.infrastructure.util.JwtUtil;
import com.odcloud.infrastructure.util.UserAgentUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
class IssueTokenService implements IssueTokenUseCase {

    private final JwtUtil jwtUtil;
    private final ProfileConstant constant;
    private final UserAgentUtil userAgentUtil;
    private final RedisStoragePort redisStoragePort;
    private final AccountStoragePort accountStoragePort;

    @Override
    public IssueTokenServiceResponse issue(IssueTokenCommand command) {
        Account account = accountStoragePort.findByUsername(command.username());
        if (!account.isAdminApproved()) {
            throw new CustomBusinessException(Business_ADMIN_NOT_APPROVED);
        }
        if (!GoogleOTPUtil.valid(account.getTwoFactorSecret(), command.opt())) {
            throw new CustomBusinessException(Business_INVALID_OTP);
        }

        String accessToken = jwtUtil.createAccessToken(account);
        String refreshToken = jwtUtil.createRefreshToken(account);

        redisStoragePort.register(String.format(constant.tokenRedisKey(), account.getUsername(),
            userAgentUtil.getUserAgent()), refreshToken, constant.getRefreshTokenTtl());

        return IssueTokenServiceResponse.builder()
            .accessToken(accessToken)
            .refreshToken(refreshToken)
            .build();
    }
}

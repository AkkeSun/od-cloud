package com.odcloud.application.service.issue_token;

import static com.odcloud.infrastructure.exception.ErrorCode.Business_EMPTY_GROUP_ACCOUNT;

import com.odcloud.adapter.out.client.google.GoogleUserInfoResponse;
import com.odcloud.application.port.in.IssueTokenUseCase;
import com.odcloud.application.port.out.AccountStoragePort;
import com.odcloud.application.port.out.GoogleOAuth2Port;
import com.odcloud.application.port.out.RedisStoragePort;
import com.odcloud.domain.model.Account;
import com.odcloud.infrastructure.constant.ProfileConstant;
import com.odcloud.infrastructure.exception.CustomBusinessException;
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
    private final GoogleOAuth2Port googleOAuth2Port;
    private final RedisStoragePort redisStoragePort;
    private final AccountStoragePort accountStoragePort;

    @Override
    public IssueTokenServiceResponse issue(String googleAuthorization) {
        GoogleUserInfoResponse userInfo = googleOAuth2Port.getUserInfo(googleAuthorization);
        Account account = accountStoragePort.findByEmail(userInfo.email());
        if (account.getGroups().isEmpty()) {
            throw new CustomBusinessException(Business_EMPTY_GROUP_ACCOUNT);
        }

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

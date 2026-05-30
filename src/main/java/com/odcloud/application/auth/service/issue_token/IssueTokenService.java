package com.odcloud.application.auth.service.issue_token;

import com.odcloud.application.account.port.out.AccountStoragePort;
import com.odcloud.application.auth.port.in.IssueTokenUseCase;
import com.odcloud.application.auth.port.out.GoogleOAuth2Port;
import com.odcloud.application.auth.port.out.GoogleUserInfo;
import com.odcloud.application.auth.port.out.RedisStoragePort;
import com.odcloud.domain.model.Account;
import com.odcloud.infrastructure.constant.ProfileConstant;
import com.odcloud.infrastructure.util.JwtUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
class IssueTokenService implements IssueTokenUseCase {

    private final JwtUtil jwtUtil;
    private final ProfileConstant constant;
    private final GoogleOAuth2Port googleOAuth2Port;
    private final RedisStoragePort redisStoragePort;
    private final AccountStoragePort accountStoragePort;

    @Override
    public IssueTokenResponse issue(String googleAuthorization, String deviceId) {
        GoogleUserInfo userInfo = googleOAuth2Port.getUserInfo(googleAuthorization);
        Account account = accountStoragePort.findByEmail(userInfo.email());

        String accessToken = jwtUtil.createAccessToken(account);
        String refreshToken = jwtUtil.createRefreshToken(account, deviceId);

        redisStoragePort.register(String.format(constant.redisKey().token(), account.getEmail(),
            deviceId), refreshToken, constant.getRefreshTokenTtl());

        return IssueTokenResponse.builder()
            .accessToken(accessToken)
            .refreshToken(refreshToken)
            .build();
    }
}

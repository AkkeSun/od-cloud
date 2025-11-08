package com.odcloud.application.service.reissue_token;

import static com.odcloud.infrastructure.exception.ErrorCode.INVALID_REFRESH_TOKEN;

import com.odcloud.application.port.in.ReissueTokenUseCase;
import com.odcloud.application.port.out.AccountStoragePort;
import com.odcloud.application.port.out.RedisStoragePort;
import com.odcloud.domain.model.Account;
import com.odcloud.infrastructure.constant.ProfileConstant;
import com.odcloud.infrastructure.exception.CustomAuthenticationException;
import com.odcloud.infrastructure.util.JwtUtil;
import com.odcloud.infrastructure.util.UserAgentUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.util.ObjectUtils;

@Service
@RequiredArgsConstructor
class ReissueTokenService implements ReissueTokenUseCase {

    private final JwtUtil jwtUtil;
    private final UserAgentUtil userAgent;
    private final ProfileConstant constant;
    private final RedisStoragePort redisStoragePort;
    private final AccountStoragePort accountStoragePort;

    @Override
    public ReissueTokenServiceResponse reissueToken(String refreshToken) {
        if (!jwtUtil.validateTokenExceptExpiration(refreshToken)) {
            throw new CustomAuthenticationException(INVALID_REFRESH_TOKEN);
        }

        String email = jwtUtil.getEmail(refreshToken);
        String redisKey = String.format(constant.redisKey().token(), email,
            userAgent.getUserAgent());

        String savedRefreshToken = redisStoragePort.findData(redisKey, String.class);
        if (ObjectUtils.isEmpty(savedRefreshToken) || !savedRefreshToken.equals(refreshToken)) {
            throw new CustomAuthenticationException(INVALID_REFRESH_TOKEN);
        }

        Account account = accountStoragePort.findByEmail(email);
        String newRefreshToken = jwtUtil.createRefreshToken(account);

        redisStoragePort.register(redisKey, newRefreshToken, constant.getRefreshTokenTtl());
        return ReissueTokenServiceResponse.builder()
            .accessToken(jwtUtil.createAccessToken(account))
            .refreshToken(newRefreshToken)
            .build();
    }
}

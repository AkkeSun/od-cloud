package com.odcloud.application.service.issue_temp_token;

import com.odcloud.application.port.in.IssueTempTokenUseCase;
import com.odcloud.application.port.in.command.IssueTempTokenCommand;
import com.odcloud.application.port.out.AccountStoragePort;
import com.odcloud.domain.model.Account;
import com.odcloud.infrastructure.util.JwtUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
class IssueTempTokenService implements IssueTempTokenUseCase {

    private final JwtUtil jwtUtil;
    private final AccountStoragePort accountStoragePort;

    @Override
    public IssueTempTokenServiceResponse issue(IssueTempTokenCommand command) {
        Account account = accountStoragePort.findByUsernameAndPassword(
            command.username(), command.password());
        String tempToken = jwtUtil.createTempToken(account.getUsername());
        return IssueTempTokenServiceResponse.of(tempToken);
    }
}

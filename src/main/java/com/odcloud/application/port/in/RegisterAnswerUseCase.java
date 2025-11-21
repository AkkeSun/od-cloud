package com.odcloud.application.port.in;

import com.odcloud.application.port.in.command.RegisterAnswerCommand;
import com.odcloud.application.service.register_answer.RegisterAnswerServiceResponse;

public interface RegisterAnswerUseCase {

    RegisterAnswerServiceResponse registerAnswer(RegisterAnswerCommand command);
}

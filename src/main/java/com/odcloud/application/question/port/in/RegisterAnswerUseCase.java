package com.odcloud.application.question.port.in;

import com.odcloud.application.question.port.in.command.RegisterAnswerCommand;
import com.odcloud.application.question.service.register_answer.RegisterAnswerServiceResponse;

public interface RegisterAnswerUseCase {

    RegisterAnswerServiceResponse registerAnswer(RegisterAnswerCommand command);
}

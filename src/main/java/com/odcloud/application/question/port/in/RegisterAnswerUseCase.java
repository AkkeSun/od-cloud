package com.odcloud.application.question.port.in;

import com.odcloud.application.question.service.register_answer.RegisterAnswerCommand;
import com.odcloud.application.question.service.register_answer.RegisterAnswerResponse;

public interface RegisterAnswerUseCase {

    RegisterAnswerResponse registerAnswer(RegisterAnswerCommand command);
}

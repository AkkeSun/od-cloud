package com.odcloud.application.port.in;

import com.odcloud.application.port.in.command.RegisterQuestionCommand;
import com.odcloud.application.service.register_question.RegisterQuestionServiceResponse;

public interface RegisterQuestionUseCase {

    RegisterQuestionServiceResponse registerQuestion(RegisterQuestionCommand command);
}

package com.odcloud.application.question.port.in;

import com.odcloud.application.question.port.in.command.RegisterQuestionCommand;
import com.odcloud.application.question.service.register_question.RegisterQuestionServiceResponse;

public interface RegisterQuestionUseCase {

    RegisterQuestionServiceResponse registerQuestion(RegisterQuestionCommand command);
}

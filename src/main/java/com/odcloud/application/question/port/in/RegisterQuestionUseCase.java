package com.odcloud.application.question.port.in;

import com.odcloud.application.question.service.register_question.RegisterQuestionCommand;
import com.odcloud.application.question.service.register_question.RegisterQuestionResponse;

public interface RegisterQuestionUseCase {

    RegisterQuestionResponse registerQuestion(RegisterQuestionCommand command);
}

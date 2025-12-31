package com.odcloud.application.question.service.register_question;

import com.odcloud.application.question.port.out.QuestionStoragePort;
import com.odcloud.application.question.port.in.RegisterQuestionUseCase;
import com.odcloud.application.question.port.in.command.RegisterQuestionCommand;
import com.odcloud.domain.model.Question;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
class RegisterQuestionService implements RegisterQuestionUseCase {

    private final QuestionStoragePort questionStoragePort;

    @Override
    @Transactional
    public RegisterQuestionServiceResponse registerQuestion(RegisterQuestionCommand command) {
        questionStoragePort.save(Question.create(command));
        return RegisterQuestionServiceResponse.ofSuccess();
    }
}

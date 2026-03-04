package com.odcloud.application.question.service.register_question;

import com.odcloud.application.question.port.in.RegisterQuestionUseCase;
import com.odcloud.application.question.port.out.QuestionStoragePort;
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
    public RegisterQuestionResponse registerQuestion(RegisterQuestionCommand command) {
        questionStoragePort.save(Question.create(command));
        return RegisterQuestionResponse.ofSuccess();
    }
}

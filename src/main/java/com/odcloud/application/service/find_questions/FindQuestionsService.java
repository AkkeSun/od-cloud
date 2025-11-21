package com.odcloud.application.service.find_questions;

import com.odcloud.application.port.in.FindQuestionsUseCase;
import com.odcloud.application.port.in.command.FindQuestionsCommand;
import com.odcloud.application.port.out.QuestionStoragePort;
import com.odcloud.domain.model.Question;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
class FindQuestionsService implements FindQuestionsUseCase {

    private final QuestionStoragePort questionStoragePort;

    @Override
    public FindQuestionsServiceResponse findQuestions(FindQuestionsCommand command) {
        Page<Question> questions = questionStoragePort.findAll(command.page(), command.size());
        return FindQuestionsServiceResponse.of(questions);
    }
}

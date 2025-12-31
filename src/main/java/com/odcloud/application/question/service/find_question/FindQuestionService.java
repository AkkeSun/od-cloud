package com.odcloud.application.question.service.find_question;

import com.odcloud.application.question.port.out.AnswerStoragePort;
import com.odcloud.application.question.port.out.QuestionStoragePort;
import com.odcloud.application.question.port.in.FindQuestionUseCase;
import com.odcloud.domain.model.Answer;
import com.odcloud.domain.model.Question;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
class FindQuestionService implements FindQuestionUseCase {

    private final QuestionStoragePort questionStoragePort;
    private final AnswerStoragePort answerStoragePort;

    @Override
    public FindQuestionServiceResponse findQuestion(Long questionId) {
        Question question = questionStoragePort.findById(questionId);
        Answer answer = answerStoragePort.findOneByQuestionId(questionId).orElse(null);
        return FindQuestionServiceResponse.of(question, answer);
    }
}

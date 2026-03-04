package com.odcloud.application.question.port.in;

import com.odcloud.application.question.service.find_question.FindQuestionResponse;

public interface FindQuestionUseCase {

    FindQuestionResponse findQuestion(Long questionId);
}

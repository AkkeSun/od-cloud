package com.odcloud.application.question.port.in;

import com.odcloud.application.question.service.find_question.FindQuestionServiceResponse;

public interface FindQuestionUseCase {

    FindQuestionServiceResponse findQuestion(Long questionId);
}

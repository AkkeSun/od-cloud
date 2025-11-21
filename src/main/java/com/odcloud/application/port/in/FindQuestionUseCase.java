package com.odcloud.application.port.in;

import com.odcloud.application.service.find_question.FindQuestionServiceResponse;

public interface FindQuestionUseCase {

    FindQuestionServiceResponse findQuestion(Long questionId);
}

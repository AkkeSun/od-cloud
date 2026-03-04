package com.odcloud.application.question.port.in;

import com.odcloud.application.question.service.find_question.FindQuestionsCommand;
import com.odcloud.application.question.service.find_questions.FindQuestionsResponse;

public interface FindQuestionsUseCase {

    FindQuestionsResponse findQuestions(FindQuestionsCommand command);
}

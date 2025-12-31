package com.odcloud.application.question.port.in;

import com.odcloud.application.question.port.in.command.FindQuestionsCommand;
import com.odcloud.application.question.service.find_questions.FindQuestionsServiceResponse;

public interface FindQuestionsUseCase {

    FindQuestionsServiceResponse findQuestions(FindQuestionsCommand command);
}

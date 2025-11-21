package com.odcloud.application.port.in;

import com.odcloud.application.port.in.command.FindQuestionsCommand;
import com.odcloud.application.service.find_questions.FindQuestionsServiceResponse;

public interface FindQuestionsUseCase {

    FindQuestionsServiceResponse findQuestions(FindQuestionsCommand command);
}

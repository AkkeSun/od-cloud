package com.odcloud.application.question.service.register_answer;

import com.odcloud.application.question.port.out.AnswerStoragePort;
import com.odcloud.application.question.port.out.QuestionStoragePort;
import com.odcloud.application.question.port.in.RegisterAnswerUseCase;
import com.odcloud.application.question.port.in.command.RegisterAnswerCommand;
import com.odcloud.domain.model.Answer;
import com.odcloud.domain.model.Question;
import com.odcloud.infrastructure.exception.CustomBusinessException;
import com.odcloud.infrastructure.exception.ErrorCode;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
class RegisterAnswerService implements RegisterAnswerUseCase {

    private final QuestionStoragePort questionStoragePort;
    private final AnswerStoragePort answerStoragePort;

    @Override
    @Transactional
    public RegisterAnswerServiceResponse registerAnswer(RegisterAnswerCommand command) {
        Question question = questionStoragePort.findById(command.questionId());

        if (answerStoragePort.existsByQuestionId(command.questionId())) {
            throw new CustomBusinessException(ErrorCode.Business_ALREADY_EXISTS_ANSWER);
        }

        answerStoragePort.save(Answer.create(command));
        question.markAsAnswered();
        questionStoragePort.save(question);

        // TODO: 문의자에게 푸시 알림 전송
        // NotificationPort.sendPushNotification(question.getWriterEmail(), "답변이 등록되었습니다.");
        return RegisterAnswerServiceResponse.ofSuccess();
    }
}

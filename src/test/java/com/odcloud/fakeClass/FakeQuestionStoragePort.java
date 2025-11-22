package com.odcloud.fakeClass;

import com.odcloud.application.port.out.QuestionStoragePort;
import com.odcloud.domain.model.Question;
import com.odcloud.infrastructure.exception.CustomBusinessException;
import com.odcloud.infrastructure.exception.ErrorCode;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;

@Slf4j
public class FakeQuestionStoragePort implements QuestionStoragePort {

    public List<Question> database = new ArrayList<>();
    public Long id = 0L;
    public boolean shouldThrowException = false;

    @Override
    public void save(Question question) {
        if (shouldThrowException) {
            throw new RuntimeException("Storage failure");
        }

        Question savedQuestion = Question.builder()
            .id(question.getId() == null ? ++id : question.getId())
            .writerEmail(question.getWriterEmail())
            .writerNickname(question.getWriterNickname())
            .title(question.getTitle())
            .content(question.getContent())
            .answered(question.getAnswered())
            .modDt(question.getModDt())
            .regDt(question.getRegDt())
            .build();

        database.removeIf(q -> q.getId() != null && q.getId().equals(savedQuestion.getId()));
        database.add(savedQuestion);
        log.info("FakeQuestionStoragePort saved: id={}, title={}", savedQuestion.getId(),
            question.getTitle());
    }

    @Override
    public Question findById(Long id) {
        return database.stream()
            .filter(question -> question.getId() != null && question.getId().equals(id))
            .findFirst()
            .orElseThrow(() -> new CustomBusinessException(ErrorCode.Business_NOT_FOUND_QUESTION));
    }

    @Override
    public Page<Question> findAll(int page, int size) {
        List<Question> sortedQuestions = database.stream()
            .sorted(Comparator.comparing(Question::getRegDt).reversed())
            .toList();

        int start = page * size;
        int end = Math.min(start + size, sortedQuestions.size());

        List<Question> content = start >= sortedQuestions.size() ? new ArrayList<>()
            : sortedQuestions.subList(start, end);

        return new PageImpl<>(content, PageRequest.of(page, size), sortedQuestions.size());
    }
}

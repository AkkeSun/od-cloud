package com.odcloud.application.port.out;

import com.odcloud.domain.model.Question;
import java.util.List;
import org.springframework.data.domain.Page;

public interface QuestionStoragePort {

    void save(Question question);

    Question findById(Long id);

    Page<Question> findAll(int page, int size);
}

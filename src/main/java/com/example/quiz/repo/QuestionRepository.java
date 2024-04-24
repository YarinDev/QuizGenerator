package com.example.quiz.repo;

import com.example.quiz.model.Question;
import org.springframework.data.repository.CrudRepository;
import java.util.List;

public interface QuestionRepository extends CrudRepository<Question, Long> {
    // Retrieve questions ordered by content
    List<Question> findAllByOrderByContentAsc();
}

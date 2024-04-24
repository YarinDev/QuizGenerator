package com.example.quiz.repo;

import com.example.quiz.model.Answer;
import org.springframework.data.repository.CrudRepository;
import java.util.List;

public interface AnswerRepository extends CrudRepository<Answer, Long> {
    // Retrieve answers ordered by letter
    List<Answer> findAllByOrderByLetterAsc();
}

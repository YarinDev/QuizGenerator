package com.example.quiz.repo;


import com.example.quiz.model.Quiz;
import org.springframework.data.repository.CrudRepository;

import java.util.List;

public interface QuizRepository extends CrudRepository<Quiz, Long> {
    // Define a method to retrieve quizzes with questions and answers, ordered by question ID and answer ID
    List<Quiz> findAllByOrderByQuestionsAndAnswersIdAsc();
}

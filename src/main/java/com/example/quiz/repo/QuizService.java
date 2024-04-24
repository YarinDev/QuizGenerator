package com.example.quiz.repo;

import com.example.quiz.model.Quiz;
import com.example.quiz.model.Question;
import com.example.quiz.model.Answer;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Map;
import java.util.Optional;

@Service
public class QuizService {

    @Autowired
    private QuizRepository repository;

    @Autowired
    private QuestionRepository questionRepository;

    @Autowired
    private AnswerRepository answerRepository;

    @Transactional
    public Quiz saveQuizWithQuestionsAndAnswers(Quiz quiz) {
        // Iterate through each entry in the map of questions and answers
        for (Map.Entry<Question, Answer> entry : quiz.getQuestionsAndAnswers().entrySet()) {
            Question question = entry.getKey();
            Answer answer = entry.getValue();

            // Save question and answer objects
            questionRepository.save(question);
            answerRepository.save(answer);
        }

        // Save the quiz after all questions and answers are saved
        return repository.save(quiz);
    }

    public Iterable<Quiz> all() {
        return repository.findAll();
    }

    public Optional<Quiz> findById(Long id) {
        return repository.findById(id);
    }

    public void delete(Quiz quiz) {
        repository.delete(quiz);
    }
}

package com.smartbacklog.service;

import com.smartbacklog.entity.Question;
import com.smartbacklog.entity.Scheme;
import com.smartbacklog.entity.Subject;
import com.smartbacklog.repository.QuestionRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
@Transactional
public class QuestionService {

    private final QuestionRepository questionRepository;

    public QuestionService(QuestionRepository questionRepository) {
        this.questionRepository = questionRepository;
    }

    public List<Question> getAllQuestions() {
        return questionRepository.findAll();
    }

    public Optional<Question> getQuestionById(Long id) {
        return questionRepository.findById(id);
    }

    public List<Question> getQuestionsBySubject(Subject subject) {
        return questionRepository.findBySubjectOrderByIdAsc(subject);
    }

    public List<Question> getQuestionsBySubjectAndScheme(Subject subject, Scheme scheme) {
        if (scheme != null) {
            return questionRepository.findBySubjectAndScheme(subject, scheme);
        }
        return questionRepository.findBySubjectOrderByIdAsc(subject);
    }

    public Question saveQuestion(Question question) {
        return questionRepository.save(question);
    }

    public void deleteQuestion(Long id) {
        questionRepository.deleteById(id);
    }

    public long getTotalQuestionsCount() {
        return questionRepository.count();
    }

    public long getQuestionsCountBySubject(Subject subject) {
        return questionRepository.countBySubject(subject);
    }
}

package com.smartbacklog.repository;

import com.smartbacklog.entity.QuizAttempt;
import com.smartbacklog.entity.StudentQuizAnswer;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface StudentQuizAnswerRepository extends JpaRepository<StudentQuizAnswer, Long> {
    List<StudentQuizAnswer> findByQuizAttempt(QuizAttempt quizAttempt);
}

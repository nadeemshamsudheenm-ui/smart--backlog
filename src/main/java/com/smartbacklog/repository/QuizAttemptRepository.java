package com.smartbacklog.repository;

import com.smartbacklog.entity.QuizAttempt;
import com.smartbacklog.entity.StudentProfile;
import com.smartbacklog.entity.Subject;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface QuizAttemptRepository extends JpaRepository<QuizAttempt, Long> {
    List<QuizAttempt> findByStudentOrderByAttemptedAtDesc(StudentProfile student);
    List<QuizAttempt> findByStudentAndSubjectOrderByAttemptedAtDesc(StudentProfile student, Subject subject);
    List<QuizAttempt> findBySubjectOrderByAttemptedAtDesc(Subject subject);
    List<QuizAttempt> findTop10ByOrderByAttemptedAtDesc();

    @Query("SELECT AVG(qa.scorePercentage) FROM QuizAttempt qa WHERE qa.student = :student")
    Double getAverageScoreByStudent(@Param("student") StudentProfile student);

    @Query("SELECT COUNT(qa) FROM QuizAttempt qa WHERE qa.student = :student AND qa.passed = true")
    long countPassedByStudent(@Param("student") StudentProfile student);

    long countByStudent(StudentProfile student);

    @Query("SELECT qa.subject.name, AVG(qa.scorePercentage) FROM QuizAttempt qa WHERE qa.student = :student GROUP BY qa.subject.name")
    List<Object[]> getSubjectAverageScoresByStudent(@Param("student") StudentProfile student);

    @Query("SELECT qa.subject.name, AVG(qa.scorePercentage) FROM QuizAttempt qa GROUP BY qa.subject.name")
    List<Object[]> getOverallSubjectAverageScores();
}

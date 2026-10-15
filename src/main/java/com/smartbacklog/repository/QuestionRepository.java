package com.smartbacklog.repository;

import com.smartbacklog.entity.Question;
import com.smartbacklog.entity.Scheme;
import com.smartbacklog.entity.Subject;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface QuestionRepository extends JpaRepository<Question, Long> {
    List<Question> findBySubject(Subject subject);
    List<Question> findBySubjectAndScheme(Subject subject, Scheme scheme);
    List<Question> findBySubjectOrderByIdAsc(Subject subject);
    long countBySubject(Subject subject);
}

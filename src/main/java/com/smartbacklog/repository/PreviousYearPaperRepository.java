package com.smartbacklog.repository;

import com.smartbacklog.entity.PreviousYearPaper;
import com.smartbacklog.entity.Scheme;
import com.smartbacklog.entity.Semester;
import com.smartbacklog.entity.Subject;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface PreviousYearPaperRepository extends JpaRepository<PreviousYearPaper, Long> {
    List<PreviousYearPaper> findBySubject(Subject subject);
    List<PreviousYearPaper> findBySemester(Semester semester);
    List<PreviousYearPaper> findByScheme(Scheme scheme);
    List<PreviousYearPaper> findBySemesterAndScheme(Semester semester, Scheme scheme);
    List<PreviousYearPaper> findBySubjectAndSemesterAndScheme(Subject subject, Semester semester, Scheme scheme);
    List<PreviousYearPaper> findTop10ByOrderByUploadedAtDesc();
    long countBySubject(Subject subject);
}

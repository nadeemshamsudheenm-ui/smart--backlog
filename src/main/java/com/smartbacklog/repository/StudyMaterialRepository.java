package com.smartbacklog.repository;

import com.smartbacklog.entity.Scheme;
import com.smartbacklog.entity.Semester;
import com.smartbacklog.entity.StudyMaterial;
import com.smartbacklog.entity.Subject;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface StudyMaterialRepository extends JpaRepository<StudyMaterial, Long> {
    List<StudyMaterial> findBySubject(Subject subject);
    List<StudyMaterial> findBySemester(Semester semester);
    List<StudyMaterial> findByScheme(Scheme scheme);
    List<StudyMaterial> findBySemesterAndScheme(Semester semester, Scheme scheme);
    List<StudyMaterial> findBySubjectAndSemesterAndScheme(Subject subject, Semester semester, Scheme scheme);
    List<StudyMaterial> findTop10ByOrderByUploadedAtDesc();
    long countBySubject(Subject subject);
}

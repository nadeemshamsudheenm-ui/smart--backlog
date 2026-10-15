package com.smartbacklog.repository;

import com.smartbacklog.entity.*;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface StudentProfileRepository extends JpaRepository<StudentProfile, Long> {
    Optional<StudentProfile> findByUser(User user);
    Optional<StudentProfile> findByRegisterNumber(String registerNumber);
    List<StudentProfile> findByParentUser(User parent);
    List<StudentProfile> findByAssignedTutor(User tutor);
    List<StudentProfile> findByCurrentSemester(Semester semester);
    List<StudentProfile> findByScheme(Scheme scheme);
    List<StudentProfile> findByCurrentSemesterAndScheme(Semester semester, Scheme scheme);

    @Query("SELECT sp FROM StudentProfile sp JOIN sp.supplementarySubjects s WHERE s.id = :subjectId")
    List<StudentProfile> findStudentsWithBacklogSubject(@Param("subjectId") Long subjectId);

    @Query("SELECT COUNT(sp) FROM StudentProfile sp JOIN sp.supplementarySubjects s")
    long countTotalBacklogs();
}

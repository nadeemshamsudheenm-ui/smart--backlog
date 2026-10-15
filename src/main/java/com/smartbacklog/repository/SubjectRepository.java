package com.smartbacklog.repository;

import com.smartbacklog.entity.Scheme;
import com.smartbacklog.entity.Semester;
import com.smartbacklog.entity.Subject;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface SubjectRepository extends JpaRepository<Subject, Long> {
    Optional<Subject> findByCode(String code);
    Optional<Subject> findByCodeAndScheme(String code, Scheme scheme);
    List<Subject> findBySemester(Semester semester);
    List<Subject> findByScheme(Scheme scheme);
    List<Subject> findBySemesterAndScheme(Semester semester, Scheme scheme);
    List<Subject> findByDepartment(String department);
    List<Subject> findAllByOrderByCodeAsc();
}

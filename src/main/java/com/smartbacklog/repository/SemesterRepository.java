package com.smartbacklog.repository;

import com.smartbacklog.entity.Semester;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface SemesterRepository extends JpaRepository<Semester, Long> {
    Optional<Semester> findByCode(String code);
    List<Semester> findAllByOrderBySemesterNumberAsc();
}

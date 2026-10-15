package com.smartbacklog.repository;

import com.smartbacklog.entity.Scheme;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface SchemeRepository extends JpaRepository<Scheme, Long> {
    Optional<Scheme> findByCode(String code);
    Optional<Scheme> findByName(String name);
    List<Scheme> findByActiveTrue();
}

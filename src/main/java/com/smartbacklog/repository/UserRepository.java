package com.smartbacklog.repository;

import com.smartbacklog.entity.Role;
import com.smartbacklog.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<User, Long> {
    Optional<User> findByUsername(String username);
    Optional<User> findByEmail(String email);
    List<User> findByRole(Role role);
    List<User> findByRoleOrderByFullNameAsc(Role role);
    boolean existsByUsername(String username);
    boolean existsByEmail(String email);
    long countByRole(Role role);
}

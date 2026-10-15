package com.smartbacklog.service;

import com.smartbacklog.entity.Role;
import com.smartbacklog.entity.User;
import com.smartbacklog.repository.UserRepository;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
@Transactional
public class UserService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    public UserService(UserRepository userRepository, PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
    }

    public List<User> findAll() {
        return userRepository.findAll();
    }

    public Optional<User> findById(Long id) {
        return userRepository.findById(id);
    }

    public Optional<User> findByUsername(String username) {
        return userRepository.findByUsername(username);
    }

    public List<User> findByRole(Role role) {
        return userRepository.findByRoleOrderByFullNameAsc(role);
    }

    public User createUser(String username, String rawPassword, String fullName, Role role, String email, String phone) {
        if (userRepository.existsByUsername(username)) {
            throw new IllegalArgumentException("Username already exists: " + username);
        }
        User user = new User();
        user.setUsername(username.trim());
        user.setPassword(passwordEncoder.encode(rawPassword));
        user.setFullName(fullName.trim());
        user.setRole(role);
        user.setEmail(email != null ? email.trim() : null);
        user.setPhone(phone != null ? phone.trim() : null);
        user.setActive(true);
        return userRepository.save(user);
    }

    public User updateUser(Long id, String fullName, String email, String phone, Boolean active, String newPassword) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("User not found with id: " + id));

        if (fullName != null && !fullName.isBlank()) {
            user.setFullName(fullName.trim());
        }
        if (email != null) {
            user.setEmail(email.trim());
        }
        if (phone != null) {
            user.setPhone(phone.trim());
        }
        if (active != null) {
            user.setActive(active);
        }
        if (newPassword != null && !newPassword.isBlank()) {
            user.setPassword(passwordEncoder.encode(newPassword));
        }
        return userRepository.save(user);
    }

    public void deleteUser(Long id) {
        userRepository.deleteById(id);
    }

    public long countByRole(Role role) {
        return userRepository.countByRole(role);
    }
}

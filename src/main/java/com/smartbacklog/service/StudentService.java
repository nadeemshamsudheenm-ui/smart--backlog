package com.smartbacklog.service;

import com.smartbacklog.entity.*;
import com.smartbacklog.repository.StudentProfileRepository;
import com.smartbacklog.repository.UserRepository;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;

@Service
@Transactional
public class StudentService {

    private final StudentProfileRepository studentProfileRepository;
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    public StudentService(StudentProfileRepository studentProfileRepository,
                          UserRepository userRepository,
                          PasswordEncoder passwordEncoder) {
        this.studentProfileRepository = studentProfileRepository;
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
    }

    public List<StudentProfile> getAllStudents() {
        return studentProfileRepository.findAll();
    }

    public Optional<StudentProfile> getStudentById(Long id) {
        return studentProfileRepository.findById(id);
    }

    public Optional<StudentProfile> getStudentByUser(User user) {
        return studentProfileRepository.findByUser(user);
    }

    public Optional<StudentProfile> getStudentByUsername(String username) {
        return userRepository.findByUsername(username)
                .flatMap(studentProfileRepository::findByUser);
    }

    public Optional<StudentProfile> getStudentByRegisterNumber(String registerNumber) {
        return studentProfileRepository.findByRegisterNumber(registerNumber);
    }

    public List<StudentProfile> getStudentsByTutor(User tutor) {
        return studentProfileRepository.findByAssignedTutor(tutor);
    }

    public List<StudentProfile> getStudentsByParent(User parent) {
        return studentProfileRepository.findByParentUser(parent);
    }

    public List<StudentProfile> filterStudents(Semester semester, Scheme scheme, Long subjectId) {
        if (subjectId != null && subjectId > 0) {
            return studentProfileRepository.findStudentsWithBacklogSubject(subjectId);
        }
        if (semester != null && scheme != null) {
            return studentProfileRepository.findByCurrentSemesterAndScheme(semester, scheme);
        } else if (semester != null) {
            return studentProfileRepository.findByCurrentSemester(semester);
        } else if (scheme != null) {
            return studentProfileRepository.findByScheme(scheme);
        }
        return studentProfileRepository.findAll();
    }

    public StudentProfile registerStudent(String username, String password, String fullName,
                                         String email, String phone, String registerNumber,
                                         String department, Semester semester, Scheme scheme,
                                         Set<Subject> backlogSubjects) {
        if (userRepository.existsByUsername(username)) {
            throw new IllegalArgumentException("Username already exists: " + username);
        }
        if (studentProfileRepository.findByRegisterNumber(registerNumber).isPresent()) {
            throw new IllegalArgumentException("Register Number already registered: " + registerNumber);
        }

        User user = new User();
        user.setUsername(username.trim());
        user.setPassword(passwordEncoder.encode(password));
        user.setFullName(fullName.trim());
        user.setRole(Role.ROLE_STUDENT);
        user.setEmail(email != null ? email.trim() : null);
        user.setPhone(phone != null ? phone.trim() : null);
        user.setActive(true);
        User savedUser = userRepository.save(user);

        StudentProfile profile = new StudentProfile();
        profile.setUser(savedUser);
        profile.setRegisterNumber(registerNumber.trim().toUpperCase());
        profile.setDepartment(department != null ? department : "Computer Science and Engineering");
        profile.setCurrentSemester(semester);
        profile.setScheme(scheme);
        if (backlogSubjects != null) {
            profile.setSupplementarySubjects(new HashSet<>(backlogSubjects));
        }
        return studentProfileRepository.save(profile);
    }

    public StudentProfile updateStudentProfile(Long profileId, String fullName, String phone,
                                             Semester semester, Scheme scheme,
                                             Set<Subject> backlogSubjects, String notes) {
        StudentProfile profile = studentProfileRepository.findById(profileId)
                .orElseThrow(() -> new IllegalArgumentException("Student profile not found: " + profileId));

        if (fullName != null && !fullName.isBlank()) {
            profile.getUser().setFullName(fullName.trim());
        }
        if (phone != null) {
            profile.getUser().setPhone(phone.trim());
        }
        if (semester != null) {
            profile.setCurrentSemester(semester);
        }
        if (scheme != null) {
            profile.setScheme(scheme);
        }
        if (backlogSubjects != null) {
            profile.setSupplementarySubjects(backlogSubjects);
        }
        if (notes != null) {
            profile.setNotes(notes);
        }
        return studentProfileRepository.save(profile);
    }

    public void assignTutor(Long studentProfileId, User tutor) {
        StudentProfile profile = studentProfileRepository.findById(studentProfileId)
                .orElseThrow(() -> new IllegalArgumentException("Student profile not found: " + studentProfileId));
        profile.setAssignedTutor(tutor);
        studentProfileRepository.save(profile);
    }

    public void assignParent(Long studentProfileId, User parent) {
        StudentProfile profile = studentProfileRepository.findById(studentProfileId)
                .orElseThrow(() -> new IllegalArgumentException("Student profile not found: " + studentProfileId));
        profile.setParentUser(parent);
        studentProfileRepository.save(profile);
    }

    public long getTotalStudentsCount() {
        return studentProfileRepository.count();
    }

    public long getTotalBacklogsCount() {
        return studentProfileRepository.countTotalBacklogs();
    }
}

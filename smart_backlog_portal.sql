-- =========================================================================
-- Smart Backlog Clearance Portal - MySQL Database Schema & Sample Data
-- Database Name: smart_backlog_portal
-- Compatible with MySQL 8.0+
-- =========================================================================

CREATE DATABASE IF NOT EXISTS smart_backlog_portal
    CHARACTER SET utf8mb4
    COLLATE utf8mb4_unicode_ci;

USE smart_backlog_portal;

SET FOREIGN_KEY_CHECKS = 0;

-- 1. Schemes Table
DROP TABLE IF EXISTS schemes;
CREATE TABLE schemes (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    name VARCHAR(50) NOT NULL UNIQUE,
    code VARCHAR(20) NOT NULL UNIQUE,
    description VARCHAR(255),
    active BOOLEAN NOT NULL DEFAULT TRUE
) ENGINE=InnoDB;

-- 2. Semesters Table
DROP TABLE IF EXISTS semesters;
CREATE TABLE semesters (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    name VARCHAR(20) NOT NULL UNIQUE,
    code VARCHAR(10) NOT NULL,
    semester_number INT NOT NULL
) ENGINE=InnoDB;

-- 3. Subjects Table
DROP TABLE IF EXISTS subjects;
CREATE TABLE subjects (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    code VARCHAR(30) NOT NULL,
    name VARCHAR(150) NOT NULL,
    department VARCHAR(100),
    semester_id BIGINT NOT NULL,
    scheme_id BIGINT NOT NULL,
    credits INT NOT NULL DEFAULT 4,
    description VARCHAR(500),
    CONSTRAINT fk_subject_semester FOREIGN KEY (semester_id) REFERENCES semesters(id) ON DELETE RESTRICT,
    CONSTRAINT fk_subject_scheme FOREIGN KEY (scheme_id) REFERENCES schemes(id) ON DELETE RESTRICT
) ENGINE=InnoDB;

-- 4. Users Table
DROP TABLE IF EXISTS users;
CREATE TABLE users (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    username VARCHAR(100) NOT NULL UNIQUE,
    password VARCHAR(255) NOT NULL,
    full_name VARCHAR(150) NOT NULL,
    role VARCHAR(30) NOT NULL,
    email VARCHAR(150),
    phone VARCHAR(20),
    active BOOLEAN NOT NULL DEFAULT TRUE,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
) ENGINE=InnoDB;

-- 5. Student Profiles Table
DROP TABLE IF EXISTS student_profiles;
CREATE TABLE student_profiles (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    user_id BIGINT NOT NULL UNIQUE,
    register_number VARCHAR(50) NOT NULL UNIQUE,
    department VARCHAR(100) DEFAULT 'Computer Science and Engineering',
    current_semester_id BIGINT,
    scheme_id BIGINT,
    parent_user_id BIGINT,
    assigned_tutor_id BIGINT,
    notes VARCHAR(500),
    CONSTRAINT fk_profile_user FOREIGN KEY (user_id) REFERENCES users(id) ON DELETE CASCADE,
    CONSTRAINT fk_profile_semester FOREIGN KEY (current_semester_id) REFERENCES semesters(id) ON DELETE SET NULL,
    CONSTRAINT fk_profile_scheme FOREIGN KEY (scheme_id) REFERENCES schemes(id) ON DELETE SET NULL,
    CONSTRAINT fk_profile_parent FOREIGN KEY (parent_user_id) REFERENCES users(id) ON DELETE SET NULL,
    CONSTRAINT fk_profile_tutor FOREIGN KEY (assigned_tutor_id) REFERENCES users(id) ON DELETE SET NULL
) ENGINE=InnoDB;

-- 6. Student Supplementary Subjects Junction Table
DROP TABLE IF EXISTS student_supplementary_subjects;
CREATE TABLE student_supplementary_subjects (
    student_profile_id BIGINT NOT NULL,
    subject_id BIGINT NOT NULL,
    PRIMARY KEY (student_profile_id, subject_id),
    CONSTRAINT fk_sss_student FOREIGN KEY (student_profile_id) REFERENCES student_profiles(id) ON DELETE CASCADE,
    CONSTRAINT fk_sss_subject FOREIGN KEY (subject_id) REFERENCES subjects(id) ON DELETE CASCADE
) ENGINE=InnoDB;

-- 7. Study Materials Table
DROP TABLE IF EXISTS study_materials;
CREATE TABLE study_materials (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    title VARCHAR(200) NOT NULL,
    description VARCHAR(1000),
    subject_id BIGINT NOT NULL,
    semester_id BIGINT NOT NULL,
    scheme_id BIGINT NOT NULL,
    file_type VARCHAR(20) DEFAULT 'PDF',
    file_name VARCHAR(255) NOT NULL,
    file_path VARCHAR(500),
    file_size BIGINT DEFAULT 0,
    content_summary TEXT,
    uploaded_by_id BIGINT,
    uploaded_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    download_count INT NOT NULL DEFAULT 0,
    CONSTRAINT fk_material_subject FOREIGN KEY (subject_id) REFERENCES subjects(id) ON DELETE CASCADE,
    CONSTRAINT fk_material_semester FOREIGN KEY (semester_id) REFERENCES semesters(id) ON DELETE RESTRICT,
    CONSTRAINT fk_material_scheme FOREIGN KEY (scheme_id) REFERENCES schemes(id) ON DELETE RESTRICT,
    CONSTRAINT fk_material_uploader FOREIGN KEY (uploaded_by_id) REFERENCES users(id) ON DELETE SET NULL
) ENGINE=InnoDB;

-- 8. Previous Year Question Papers Table
DROP TABLE IF EXISTS previous_year_papers;
CREATE TABLE previous_year_papers (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    title VARCHAR(200) NOT NULL,
    exam_session VARCHAR(100),
    subject_id BIGINT NOT NULL,
    semester_id BIGINT NOT NULL,
    scheme_id BIGINT NOT NULL,
    file_name VARCHAR(255) NOT NULL,
    file_path VARCHAR(500),
    file_size BIGINT DEFAULT 0,
    exam_pattern_notes VARCHAR(1000),
    uploaded_by_id BIGINT,
    uploaded_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    download_count INT NOT NULL DEFAULT 0,
    CONSTRAINT fk_paper_subject FOREIGN KEY (subject_id) REFERENCES subjects(id) ON DELETE CASCADE,
    CONSTRAINT fk_paper_semester FOREIGN KEY (semester_id) REFERENCES semesters(id) ON DELETE RESTRICT,
    CONSTRAINT fk_paper_scheme FOREIGN KEY (scheme_id) REFERENCES schemes(id) ON DELETE RESTRICT,
    CONSTRAINT fk_paper_uploader FOREIGN KEY (uploaded_by_id) REFERENCES users(id) ON DELETE SET NULL
) ENGINE=InnoDB;

-- 9. Question Bank Table (MCQ practice questions)
DROP TABLE IF EXISTS questions;
CREATE TABLE questions (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    subject_id BIGINT NOT NULL,
    scheme_id BIGINT,
    question_text VARCHAR(1000) NOT NULL,
    option_a VARCHAR(500) NOT NULL,
    option_b VARCHAR(500) NOT NULL,
    option_c VARCHAR(500) NOT NULL,
    option_d VARCHAR(500) NOT NULL,
    correct_option VARCHAR(5) NOT NULL,
    explanation VARCHAR(1000),
    difficulty_level VARCHAR(20) DEFAULT 'MEDIUM',
    source_exam VARCHAR(150),
    created_by_id BIGINT,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT fk_question_subject FOREIGN KEY (subject_id) REFERENCES subjects(id) ON DELETE CASCADE,
    CONSTRAINT fk_question_scheme FOREIGN KEY (scheme_id) REFERENCES schemes(id) ON DELETE SET NULL,
    CONSTRAINT fk_question_creator FOREIGN KEY (created_by_id) REFERENCES users(id) ON DELETE SET NULL
) ENGINE=InnoDB;

-- 10. Quiz Attempts Table
DROP TABLE IF EXISTS quiz_attempts;
CREATE TABLE quiz_attempts (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    student_profile_id BIGINT NOT NULL,
    subject_id BIGINT NOT NULL,
    total_questions INT NOT NULL,
    correct_answers INT NOT NULL,
    wrong_answers INT NOT NULL,
    score_percentage DOUBLE NOT NULL,
    passed BOOLEAN NOT NULL,
    time_taken_seconds INT NOT NULL DEFAULT 0,
    attempted_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT fk_attempt_student FOREIGN KEY (student_profile_id) REFERENCES student_profiles(id) ON DELETE CASCADE,
    CONSTRAINT fk_attempt_subject FOREIGN KEY (subject_id) REFERENCES subjects(id) ON DELETE CASCADE
) ENGINE=InnoDB;

-- 11. Student Quiz Answers Table
DROP TABLE IF EXISTS student_quiz_answers;
CREATE TABLE student_quiz_answers (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    quiz_attempt_id BIGINT NOT NULL,
    question_id BIGINT NOT NULL,
    selected_option VARCHAR(5),
    correct BOOLEAN NOT NULL,
    CONSTRAINT fk_sqa_attempt FOREIGN KEY (quiz_attempt_id) REFERENCES quiz_attempts(id) ON DELETE CASCADE,
    CONSTRAINT fk_sqa_question FOREIGN KEY (question_id) REFERENCES questions(id) ON DELETE CASCADE
) ENGINE=InnoDB;

-- 12. Notifications Table
DROP TABLE IF EXISTS notifications;
CREATE TABLE notifications (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    recipient_id BIGINT,
    target_role VARCHAR(30),
    title VARCHAR(200) NOT NULL,
    message VARCHAR(1000) NOT NULL,
    type VARCHAR(50) DEFAULT 'ANNOUNCEMENT',
    is_read BOOLEAN NOT NULL DEFAULT FALSE,
    link_url VARCHAR(255),
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT fk_notification_recipient FOREIGN KEY (recipient_id) REFERENCES users(id) ON DELETE CASCADE
) ENGINE=InnoDB;

SET FOREIGN_KEY_CHECKS = 1;

-- =========================================================================
-- SEED DATA INSERTION
-- Password for all demo accounts is encrypted with BCrypt
-- admin -> admin123, hod_cs -> hod123, tutor_ramesh -> tutor123,
-- student_arun -> student123, parent_kumar -> parent123
-- =========================================================================

-- Schemes
INSERT INTO schemes (id, name, code, description, active) VALUES
(1, '2019 Scheme', 'SCH-2019', 'B.Tech Regulations 2019 Curriculum with Choice Based Credit System', TRUE),
(2, '2024 Scheme', 'SCH-2024', 'B.Tech Regulations 2024 NEP-Aligned Dynamic Curriculum', TRUE);

-- Semesters
INSERT INTO semesters (id, name, code, semester_number) VALUES
(1, 'Semester 1', 'S1', 1),
(2, 'Semester 2', 'S2', 2),
(3, 'Semester 3', 'S3', 3),
(4, 'Semester 4', 'S4', 4),
(5, 'Semester 5', 'S5', 5),
(6, 'Semester 6', 'S6', 6),
(7, 'Semester 7', 'S7', 7),
(8, 'Semester 8', 'S8', 8);

-- Subjects
INSERT INTO subjects (id, code, name, department, semester_id, scheme_id, credits, description) VALUES
(1, 'MAT101', 'Linear Algebra & Calculus', 'Basic Sciences', 1, 1, 4, 'Eigenvalues, Cayley-Hamilton theorem, Multivariable calculus'),
(2, 'PHT100', 'Engineering Physics', 'Basic Sciences', 1, 1, 4, 'Wave optics, Lasers, Quantum mechanics'),
(3, 'EST102', 'Programming in C', 'Engineering Sciences', 2, 1, 4, 'Pointers, structures, file handling'),
(4, 'CST201', 'Data Structures', 'Computer Science and Engineering', 3, 1, 4, 'Stacks, Queues, Linked Lists, Trees, Graphs, Sorting algorithms'),
(5, 'MAT203', 'Discrete Mathematical Structures', 'Computer Science and Engineering', 3, 1, 4, 'Graph theory, combinatorics, relations and lattices'),
(6, 'CST202', 'Computer Organization & Architecture', 'Computer Science and Engineering', 4, 1, 4, 'ALU design, pipelining, cache memory hierarchy'),
(7, 'CST204', 'Database Management Systems', 'Computer Science and Engineering', 4, 1, 4, 'ER models, relational algebra, Normalization (1NF to BCNF), SQL'),
(8, 'CST301', 'Formal Languages & Automata Theory', 'Computer Science and Engineering', 5, 1, 4, 'DFA, NFA, CFG, Pushdown automata, Turing machines'),
(9, 'CST303', 'Computer Networks', 'Computer Science and Engineering', 5, 1, 4, 'TCP/IP stack, routing protocols, flow and congestion control'),
(10, 'MATH101', 'Matrices and Advanced Calculus', 'Basic Sciences', 1, 2, 4, 'NEP 2024 Matrix algebra and differential forms'),
(11, 'CSE101', 'Problem Solving with Python', 'Computer Science and Engineering', 2, 2, 4, 'Python idiomatic programming and computational thinking'),
(12, 'CSE201', 'Data Structures and Algorithms', 'Computer Science and Engineering', 3, 2, 4, 'NEP 2024 Advanced structures, amortized analysis, Balanced BSTs');

-- Demo Users
-- Passwords:
-- admin123   -> $2a$10$w82tG.mHkW6bH6vO1G1NVe9qE7tWbcvS4GlnpPZZO4Qc5wB3s1k8K
-- hod123     -> $2a$10$N2G48mO3bTjM3hZg9u002O5P6W4m6T.P8rZ7q1xUeA2sY9kP.jV1C
-- tutor123   -> $2a$10$E5zX9.7JkI8WbY8L3tT4vOB8s2J5h.q0y4gB9T1wV2xP7uR4kM8.a
-- student123 -> $2a$10$8lR8tZ.N3gA5tW0yK2J.9O3l5a0hB4wQ6sP8vT1uY2mN4kL9xP3g2
-- parent123  -> $2a$10$4tW8vL2jR9sP1bX5tZ.3AOk7s2hQ4m8P0yV6wT3uN7kJ9lM1xR4.O
-- (Spring Boot application's DataInitializer also encodes raw passwords using BCryptPasswordEncoder dynamically)

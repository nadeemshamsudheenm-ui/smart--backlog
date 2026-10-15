package com.smartbacklog.entity;

import jakarta.persistence.*;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "quiz_attempts")
public class QuizAttempt {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "student_profile_id", nullable = false)
    private StudentProfile student;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "subject_id", nullable = false)
    private Subject subject;

    @Column(nullable = false)
    private int totalQuestions;

    @Column(nullable = false)
    private int correctAnswers;

    @Column(nullable = false)
    private int wrongAnswers;

    @Column(nullable = false)
    private double scorePercentage;

    @Column(nullable = false)
    private boolean passed;

    @Column(nullable = false)
    private int timeTakenSeconds = 0;

    @Column(nullable = false)
    private LocalDateTime attemptedAt = LocalDateTime.now();

    @OneToMany(mappedBy = "quizAttempt", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.LAZY)
    private List<StudentQuizAnswer> answers = new ArrayList<>();

    public QuizAttempt() {}

    public QuizAttempt(StudentProfile student, Subject subject, int totalQuestions, int correctAnswers, int wrongAnswers,
                       double scorePercentage, boolean passed, int timeTakenSeconds) {
        this.student = student;
        this.subject = subject;
        this.totalQuestions = totalQuestions;
        this.correctAnswers = correctAnswers;
        this.wrongAnswers = wrongAnswers;
        this.scorePercentage = scorePercentage;
        this.passed = passed;
        this.timeTakenSeconds = timeTakenSeconds;
        this.attemptedAt = LocalDateTime.now();
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public StudentProfile getStudent() {
        return student;
    }

    public void setStudent(StudentProfile student) {
        this.student = student;
    }

    public Subject getSubject() {
        return subject;
    }

    public void setSubject(Subject subject) {
        this.subject = subject;
    }

    public int getTotalQuestions() {
        return totalQuestions;
    }

    public void setTotalQuestions(int totalQuestions) {
        this.totalQuestions = totalQuestions;
    }

    public int getCorrectAnswers() {
        return correctAnswers;
    }

    public void setCorrectAnswers(int correctAnswers) {
        this.correctAnswers = correctAnswers;
    }

    public int getWrongAnswers() {
        return wrongAnswers;
    }

    public void setWrongAnswers(int wrongAnswers) {
        this.wrongAnswers = wrongAnswers;
    }

    public double getScorePercentage() {
        return scorePercentage;
    }

    public void setScorePercentage(double scorePercentage) {
        this.scorePercentage = scorePercentage;
    }

    public boolean isPassed() {
        return passed;
    }

    public void setPassed(boolean passed) {
        this.passed = passed;
    }

    public int getTimeTakenSeconds() {
        return timeTakenSeconds;
    }

    public void setTimeTakenSeconds(int timeTakenSeconds) {
        this.timeTakenSeconds = timeTakenSeconds;
    }

    public LocalDateTime getAttemptedAt() {
        return attemptedAt;
    }

    public void setAttemptedAt(LocalDateTime attemptedAt) {
        this.attemptedAt = attemptedAt;
    }

    public List<StudentQuizAnswer> getAnswers() {
        return answers;
    }

    public void setAnswers(List<StudentQuizAnswer> answers) {
        this.answers = answers;
    }

    public void addAnswer(StudentQuizAnswer answer) {
        answers.add(answer);
        answer.setQuizAttempt(this);
    }

    public String getFormattedDuration() {
        int minutes = timeTakenSeconds / 60;
        int seconds = timeTakenSeconds % 60;
        return String.format("%02d:%02d", minutes, seconds);
    }
}

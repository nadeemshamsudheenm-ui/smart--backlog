package com.smartbacklog.entity;

import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "questions")
public class Question {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "subject_id", nullable = false)
    private Subject subject;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "scheme_id")
    private Scheme scheme;

    @Column(nullable = false, length = 1000)
    private String questionText;

    @Column(nullable = false, length = 500)
    private String optionA;

    @Column(nullable = false, length = 500)
    private String optionB;

    @Column(nullable = false, length = 500)
    private String optionC;

    @Column(nullable = false, length = 500)
    private String optionD;

    @Column(nullable = false, length = 5)
    private String correctOption; // "A", "B", "C", "D"

    @Column(length = 1000)
    private String explanation;

    @Column(length = 20)
    private String difficultyLevel = "MEDIUM"; // "EASY", "MEDIUM", "HARD"

    @Column(length = 150)
    private String sourceExam; // e.g. "Dec 2023 University Exam Part A"

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "created_by_id")
    private User createdBy;

    @Column(nullable = false)
    private LocalDateTime createdAt = LocalDateTime.now();

    public Question() {}

    public Question(Subject subject, Scheme scheme, String questionText,
                    String optionA, String optionB, String optionC, String optionD,
                    String correctOption, String explanation, String difficultyLevel,
                    String sourceExam, User createdBy) {
        this.subject = subject;
        this.scheme = scheme;
        this.questionText = questionText;
        this.optionA = optionA;
        this.optionB = optionB;
        this.optionC = optionC;
        this.optionD = optionD;
        this.correctOption = correctOption;
        this.explanation = explanation;
        this.difficultyLevel = difficultyLevel;
        this.sourceExam = sourceExam;
        this.createdBy = createdBy;
        this.createdAt = LocalDateTime.now();
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Subject getSubject() {
        return subject;
    }

    public void setSubject(Subject subject) {
        this.subject = subject;
    }

    public Scheme getScheme() {
        return scheme;
    }

    public void setScheme(Scheme scheme) {
        this.scheme = scheme;
    }

    public String getQuestionText() {
        return questionText;
    }

    public void setQuestionText(String questionText) {
        this.questionText = questionText;
    }

    public String getOptionA() {
        return optionA;
    }

    public void setOptionA(String optionA) {
        this.optionA = optionA;
    }

    public String getOptionB() {
        return optionB;
    }

    public void setOptionB(String optionB) {
        this.optionB = optionB;
    }

    public String getOptionC() {
        return optionC;
    }

    public void setOptionC(String optionC) {
        this.optionC = optionC;
    }

    public String getOptionD() {
        return optionD;
    }

    public void setOptionD(String optionD) {
        this.optionD = optionD;
    }

    public String getCorrectOption() {
        return correctOption;
    }

    public void setCorrectOption(String correctOption) {
        this.correctOption = correctOption;
    }

    public String getExplanation() {
        return explanation;
    }

    public void setExplanation(String explanation) {
        this.explanation = explanation;
    }

    public String getDifficultyLevel() {
        return difficultyLevel;
    }

    public void setDifficultyLevel(String difficultyLevel) {
        this.difficultyLevel = difficultyLevel;
    }

    public String getSourceExam() {
        return sourceExam;
    }

    public void setSourceExam(String sourceExam) {
        this.sourceExam = sourceExam;
    }

    public User getCreatedBy() {
        return createdBy;
    }

    public void setCreatedBy(User createdBy) {
        this.createdBy = createdBy;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }

    public String getCorrectOptionText() {
        if ("A".equalsIgnoreCase(correctOption)) return optionA;
        if ("B".equalsIgnoreCase(correctOption)) return optionB;
        if ("C".equalsIgnoreCase(correctOption)) return optionC;
        if ("D".equalsIgnoreCase(correctOption)) return optionD;
        return "";
    }
}

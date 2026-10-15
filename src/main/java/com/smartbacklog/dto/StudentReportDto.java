package com.smartbacklog.dto;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class StudentReportDto {
    private String studentName;
    private String registerNumber;
    private String department;
    private String schemeName;
    private String semesterName;
    private int totalAttempts = 0;
    private int passedAttempts = 0;
    private double averageScore = 0.0;
    private double highestScore = 0.0;
    private double passRate = 0.0;

    private List<String> weakSubjects = new ArrayList<>();
    private List<String> strongSubjects = new ArrayList<>();
    private Map<String, Double> subjectAverageScores = new HashMap<>();

    // For Chart.js timeline
    private List<String> attemptDates = new ArrayList<>();
    private List<Double> attemptScores = new ArrayList<>();
    private List<String> attemptSubjects = new ArrayList<>();

    public StudentReportDto() {}

    public String getStudentName() {
        return studentName;
    }

    public void setStudentName(String studentName) {
        this.studentName = studentName;
    }

    public String getRegisterNumber() {
        return registerNumber;
    }

    public void setRegisterNumber(String registerNumber) {
        this.registerNumber = registerNumber;
    }

    public String getDepartment() {
        return department;
    }

    public void setDepartment(String department) {
        this.department = department;
    }

    public String getSchemeName() {
        return schemeName;
    }

    public void setSchemeName(String schemeName) {
        this.schemeName = schemeName;
    }

    public String getSemesterName() {
        return semesterName;
    }

    public void setSemesterName(String semesterName) {
        this.semesterName = semesterName;
    }

    public int getTotalAttempts() {
        return totalAttempts;
    }

    public void setTotalAttempts(int totalAttempts) {
        this.totalAttempts = totalAttempts;
    }

    public int getPassedAttempts() {
        return passedAttempts;
    }

    public void setPassedAttempts(int passedAttempts) {
        this.passedAttempts = passedAttempts;
    }

    public double getAverageScore() {
        return averageScore;
    }

    public void setAverageScore(double averageScore) {
        this.averageScore = averageScore;
    }

    public double getHighestScore() {
        return highestScore;
    }

    public void setHighestScore(double highestScore) {
        this.highestScore = highestScore;
    }

    public double getPassRate() {
        return passRate;
    }

    public void setPassRate(double passRate) {
        this.passRate = passRate;
    }

    public List<String> getWeakSubjects() {
        return weakSubjects;
    }

    public void setWeakSubjects(List<String> weakSubjects) {
        this.weakSubjects = weakSubjects;
    }

    public List<String> getStrongSubjects() {
        return strongSubjects;
    }

    public void setStrongSubjects(List<String> strongSubjects) {
        this.strongSubjects = strongSubjects;
    }

    public Map<String, Double> getSubjectAverageScores() {
        return subjectAverageScores;
    }

    public void setSubjectAverageScores(Map<String, Double> subjectAverageScores) {
        this.subjectAverageScores = subjectAverageScores;
    }

    public List<String> getAttemptDates() {
        return attemptDates;
    }

    public void setAttemptDates(List<String> attemptDates) {
        this.attemptDates = attemptDates;
    }

    public List<Double> getAttemptScores() {
        return attemptScores;
    }

    public void setAttemptScores(List<Double> attemptScores) {
        this.attemptScores = attemptScores;
    }

    public List<String> getAttemptSubjects() {
        return attemptSubjects;
    }

    public void setAttemptSubjects(List<String> attemptSubjects) {
        this.attemptSubjects = attemptSubjects;
    }
}

package com.smartbacklog.dto;

import java.util.HashMap;
import java.util.Map;

public class DepartmentReportDto {
    private long totalStudents;
    private long totalBacklogs;
    private long totalQuizzesAttempted;
    private double overallPassRate;
    private Map<String, Long> studentsByScheme = new HashMap<>();
    private Map<String, Long> studentsBySemester = new HashMap<>();
    private Map<String, Double> subjectPerformance = new HashMap<>();
    private Map<String, Long> backlogsBySubject = new HashMap<>();

    public DepartmentReportDto() {}

    public long getTotalStudents() {
        return totalStudents;
    }

    public void setTotalStudents(long totalStudents) {
        this.totalStudents = totalStudents;
    }

    public long getTotalBacklogs() {
        return totalBacklogs;
    }

    public void setTotalBacklogs(long totalBacklogs) {
        this.totalBacklogs = totalBacklogs;
    }

    public long getTotalQuizzesAttempted() {
        return totalQuizzesAttempted;
    }

    public void setTotalQuizzesAttempted(long totalQuizzesAttempted) {
        this.totalQuizzesAttempted = totalQuizzesAttempted;
    }

    public double getOverallPassRate() {
        return overallPassRate;
    }

    public void setOverallPassRate(double overallPassRate) {
        this.overallPassRate = overallPassRate;
    }

    public Map<String, Long> getStudentsByScheme() {
        return studentsByScheme;
    }

    public void setStudentsByScheme(Map<String, Long> studentsByScheme) {
        this.studentsByScheme = studentsByScheme;
    }

    public Map<String, Long> getStudentsBySemester() {
        return studentsBySemester;
    }

    public void setStudentsBySemester(Map<String, Long> studentsBySemester) {
        this.studentsBySemester = studentsBySemester;
    }

    public Map<String, Double> getSubjectPerformance() {
        return subjectPerformance;
    }

    public void setSubjectPerformance(Map<String, Double> subjectPerformance) {
        this.subjectPerformance = subjectPerformance;
    }

    public Map<String, Long> getBacklogsBySubject() {
        return backlogsBySubject;
    }

    public void setBacklogsBySubject(Map<String, Long> backlogsBySubject) {
        this.backlogsBySubject = backlogsBySubject;
    }
}

package com.smartbacklog.service;

import com.smartbacklog.dto.DepartmentReportDto;
import com.smartbacklog.dto.StudentReportDto;
import com.smartbacklog.entity.*;
import com.smartbacklog.repository.QuizAttemptRepository;
import com.smartbacklog.repository.StudentProfileRepository;
import com.smartbacklog.repository.SubjectRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.format.DateTimeFormatter;
import java.util.*;

@Service
@Transactional(readOnly = true)
public class ReportService {

    private final QuizAttemptRepository quizAttemptRepository;
    private final StudentProfileRepository studentProfileRepository;
    private final SubjectRepository subjectRepository;

    public ReportService(QuizAttemptRepository quizAttemptRepository,
                         StudentProfileRepository studentProfileRepository,
                         SubjectRepository subjectRepository) {
        this.quizAttemptRepository = quizAttemptRepository;
        this.studentProfileRepository = studentProfileRepository;
        this.subjectRepository = subjectRepository;
    }

    public StudentReportDto generateStudentReport(StudentProfile student) {
        StudentReportDto report = new StudentReportDto();
        report.setStudentName(student.getUser().getFullName());
        report.setRegisterNumber(student.getRegisterNumber());
        report.setDepartment(student.getDepartment());
        report.setSchemeName(student.getScheme() != null ? student.getScheme().getName() : "N/A");
        report.setSemesterName(student.getCurrentSemester() != null ? student.getCurrentSemester().getName() : "N/A");

        List<QuizAttempt> attempts = quizAttemptRepository.findByStudentOrderByAttemptedAtDesc(student);
        report.setTotalAttempts(attempts.size());

        if (attempts.isEmpty()) {
            return report;
        }

        int passedCount = 0;
        double sumScore = 0.0;
        double maxScore = 0.0;

        Map<String, List<Double>> subjectScores = new HashMap<>();

        DateTimeFormatter dtf = DateTimeFormatter.ofPattern("dd MMM");

        // Reverse for chronological timeline
        List<QuizAttempt> chronological = new ArrayList<>(attempts);
        Collections.reverse(chronological);

        for (QuizAttempt a : chronological) {
            report.getAttemptDates().add(a.getAttemptedAt().format(dtf));
            report.getAttemptScores().add(a.getScorePercentage());
            report.getAttemptSubjects().add(a.getSubject().getName());
        }

        for (QuizAttempt a : attempts) {
            if (a.isPassed()) {
                passedCount++;
            }
            sumScore += a.getScorePercentage();
            if (a.getScorePercentage() > maxScore) {
                maxScore = a.getScorePercentage();
            }

            String subjName = a.getSubject().getName();
            subjectScores.computeIfAbsent(subjName, k -> new ArrayList<>()).add(a.getScorePercentage());
        }

        report.setPassedAttempts(passedCount);
        double avgScore = sumScore / attempts.size();
        report.setAverageScore(Math.round(avgScore * 10.0) / 10.0);
        report.setHighestScore(maxScore);
        double passRate = ((double) passedCount / attempts.size()) * 100.0;
        report.setPassRate(Math.round(passRate * 10.0) / 10.0);

        Map<String, Double> subjectAvg = new HashMap<>();
        List<String> weak = new ArrayList<>();
        List<String> strong = new ArrayList<>();

        for (Map.Entry<String, List<Double>> entry : subjectScores.entrySet()) {
            double sSum = 0;
            for (Double s : entry.getValue()) {
                sSum += s;
            }
            double sAvg = Math.round((sSum / entry.getValue().size()) * 10.0) / 10.0;
            subjectAvg.put(entry.getKey(), sAvg);

            if (sAvg < 50.0) {
                weak.add(entry.getKey() + " (" + sAvg + "%)");
            } else if (sAvg >= 70.0) {
                strong.add(entry.getKey() + " (" + sAvg + "%)");
            }
        }

        report.setSubjectAverageScores(subjectAvg);
        report.setWeakSubjects(weak);
        report.setStrongSubjects(strong);

        return report;
    }

    public DepartmentReportDto generateDepartmentReport() {
        DepartmentReportDto report = new DepartmentReportDto();
        List<StudentProfile> allStudents = studentProfileRepository.findAll();
        report.setTotalStudents(allStudents.size());

        long backlogsCount = 0;
        Map<String, Long> schemeMap = new HashMap<>();
        Map<String, Long> semesterMap = new HashMap<>();
        Map<String, Long> backlogSubjMap = new HashMap<>();

        for (StudentProfile sp : allStudents) {
            if (sp.getScheme() != null) {
                String sch = sp.getScheme().getName();
                schemeMap.put(sch, schemeMap.getOrDefault(sch, 0L) + 1);
            }
            if (sp.getCurrentSemester() != null) {
                String sem = sp.getCurrentSemester().getCode();
                semesterMap.put(sem, semesterMap.getOrDefault(sem, 0L) + 1);
            }
            if (sp.getSupplementarySubjects() != null) {
                backlogsCount += sp.getSupplementarySubjects().size();
                for (Subject s : sp.getSupplementarySubjects()) {
                    backlogSubjMap.put(s.getName(), backlogSubjMap.getOrDefault(s.getName(), 0L) + 1);
                }
            }
        }

        report.setTotalBacklogs(backlogsCount);
        report.setStudentsByScheme(schemeMap);
        report.setStudentsBySemester(semesterMap);
        report.setBacklogsBySubject(backlogSubjMap);

        List<QuizAttempt> allAttempts = quizAttemptRepository.findAll();
        report.setTotalQuizzesAttempted(allAttempts.size());

        if (!allAttempts.isEmpty()) {
            long passed = allAttempts.stream().filter(QuizAttempt::isPassed).count();
            double pRate = ((double) passed / allAttempts.size()) * 100.0;
            report.setOverallPassRate(Math.round(pRate * 10.0) / 10.0);

            Map<String, List<Double>> perfMap = new HashMap<>();
            for (QuizAttempt qa : allAttempts) {
                perfMap.computeIfAbsent(qa.getSubject().getName(), k -> new ArrayList<>()).add(qa.getScorePercentage());
            }
            Map<String, Double> subjAvgMap = new HashMap<>();
            for (Map.Entry<String, List<Double>> e : perfMap.entrySet()) {
                double avg = e.getValue().stream().mapToDouble(Double::doubleValue).average().orElse(0.0);
                subjAvgMap.put(e.getKey(), Math.round(avg * 10.0) / 10.0);
            }
            report.setSubjectPerformance(subjAvgMap);
        }

        return report;
    }
}

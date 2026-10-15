package com.smartbacklog;

import com.smartbacklog.dto.DepartmentReportDto;
import com.smartbacklog.dto.StudentReportDto;
import com.smartbacklog.entity.*;
import com.smartbacklog.repository.*;
import com.smartbacklog.service.*;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.*;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
class SmartBacklogPortalApplicationTests {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private SchemeRepository schemeRepository;

    @Autowired
    private SemesterRepository semesterRepository;

    @Autowired
    private SubjectRepository subjectRepository;

    @Autowired
    private StudentProfileRepository studentProfileRepository;

    @Autowired
    private QuestionRepository questionRepository;

    @Autowired
    private QuizAttemptRepository quizAttemptRepository;

    @Autowired
    private StudyMaterialRepository studyMaterialRepository;

    @Autowired
    private PreviousYearPaperRepository previousYearPaperRepository;

    @Autowired
    private QuizService quizService;

    @Autowired
    private ReportService reportService;

    @Autowired
    private NotificationService notificationService;

    @Test
    void contextLoads() {
        assertNotNull(userRepository);
    }

    @Test
    void testSeedDataInitialization() {
        // Verify Schemes
        assertTrue(schemeRepository.findByCode("SCH-2019").isPresent());
        assertTrue(schemeRepository.findByCode("SCH-2024").isPresent());

        // Verify Semesters
        assertEquals(8, semesterRepository.count());

        // Verify Users for all 5 roles
        assertTrue(userRepository.findByUsername("admin").isPresent());
        assertEquals(Role.ROLE_ADMIN, userRepository.findByUsername("admin").get().getRole());

        assertTrue(userRepository.findByUsername("hod_cs").isPresent());
        assertEquals(Role.ROLE_HOD, userRepository.findByUsername("hod_cs").get().getRole());

        assertTrue(userRepository.findByUsername("tutor_ramesh").isPresent());
        assertEquals(Role.ROLE_TUTOR, userRepository.findByUsername("tutor_ramesh").get().getRole());

        assertTrue(userRepository.findByUsername("student_arun").isPresent());
        assertEquals(Role.ROLE_STUDENT, userRepository.findByUsername("student_arun").get().getRole());

        assertTrue(userRepository.findByUsername("parent_kumar").isPresent());
        assertEquals(Role.ROLE_PARENT, userRepository.findByUsername("parent_kumar").get().getRole());

        // Verify Student Profile
        User arun = userRepository.findByUsername("student_arun").get();
        Optional<StudentProfile> arunProfileOpt = studentProfileRepository.findByUser(arun);
        assertTrue(arunProfileOpt.isPresent());
        StudentProfile arunProfile = arunProfileOpt.get();
        assertEquals("TVE21CS042", arunProfile.getRegisterNumber());
        assertEquals("2019 Scheme", arunProfile.getScheme().getName());
        assertTrue(arunProfile.getBacklogCount() >= 2);
    }

    @Test
    void testQuizSubmissionAndScoring() {
        User arunUser = userRepository.findByUsername("student_arun").get();
        StudentProfile arunProfile = studentProfileRepository.findByUser(arunUser).get();
        Subject cst201 = subjectRepository.findByCode("CST201").orElseThrow();

        List<Question> questions = questionRepository.findBySubjectOrderByIdAsc(cst201);
        assertFalse(questions.isEmpty());

        Map<Long, String> answers = new HashMap<>();
        // Answer 1st correctly, 2nd incorrectly
        answers.put(questions.get(0).getId(), questions.get(0).getCorrectOption());
        if (questions.size() > 1) {
            String wrongOpt = "A".equals(questions.get(1).getCorrectOption()) ? "B" : "A";
            answers.put(questions.get(1).getId(), wrongOpt);
        }

        QuizAttempt attempt = quizService.submitQuiz(arunProfile, cst201, answers, 120);
        assertNotNull(attempt);
        assertNotNull(attempt.getId());
        assertEquals(answers.size(), attempt.getTotalQuestions());
        assertTrue(attempt.getCorrectAnswers() >= 1);
        assertEquals(attempt.isPassed(), attempt.getScorePercentage() >= 50.0);

        // Verify notification sent
        long count = notificationService.getNotificationsForUser(arunUser).size();
        assertTrue(count > 0);
    }

    @Test
    void testProgressReporting() {
        User arunUser = userRepository.findByUsername("student_arun").get();
        StudentProfile arunProfile = studentProfileRepository.findByUser(arunUser).get();

        StudentReportDto studentReport = reportService.generateStudentReport(arunProfile);
        assertNotNull(studentReport);
        assertEquals("Arun Suresh", studentReport.getStudentName());
        assertTrue(studentReport.getTotalAttempts() > 0);
        assertTrue(studentReport.getAverageScore() > 0);

        DepartmentReportDto deptReport = reportService.generateDepartmentReport();
        assertNotNull(deptReport);
        assertTrue(deptReport.getTotalStudents() >= 2);
        assertTrue(deptReport.getTotalBacklogs() >= 3);
        assertTrue(deptReport.getStudentsByScheme().containsKey("2019 Scheme"));
    }
}

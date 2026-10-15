package com.smartbacklog.controller;

import com.smartbacklog.dto.StudentReportDto;
import com.smartbacklog.entity.*;
import com.smartbacklog.service.*;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.*;

@Controller
@RequestMapping("/student")
public class StudentController {

    private final UserService userService;
    private final StudentService studentService;
    private final AcademicService academicService;
    private final StudyMaterialService studyMaterialService;
    private final PreviousYearPaperService previousYearPaperService;
    private final QuestionService questionService;
    private final QuizService quizService;
    private final NotificationService notificationService;
    private final ReportService reportService;

    public StudentController(UserService userService,
                             StudentService studentService,
                             AcademicService academicService,
                             StudyMaterialService studyMaterialService,
                             PreviousYearPaperService previousYearPaperService,
                             QuestionService questionService,
                             QuizService quizService,
                             NotificationService notificationService,
                             ReportService reportService) {
        this.userService = userService;
        this.studentService = studentService;
        this.academicService = academicService;
        this.studyMaterialService = studyMaterialService;
        this.previousYearPaperService = previousYearPaperService;
        this.questionService = questionService;
        this.quizService = quizService;
        this.notificationService = notificationService;
        this.reportService = reportService;
    }

    private StudentProfile getStudent(UserDetails userDetails) {
        return studentService.getStudentByUsername(userDetails.getUsername())
                .orElse(null);
    }

    @GetMapping("/dashboard")
    public String dashboard(@AuthenticationPrincipal UserDetails userDetails, Model model) {
        StudentProfile student = getStudent(userDetails);
        if (student == null) {
            return "redirect:/login";
        }

        StudentReportDto report = reportService.generateStudentReport(student);
        List<QuizAttempt> recentAttempts = quizService.getAttemptsByStudent(student);

        model.addAttribute("currentUser", student.getUser());
        model.addAttribute("student", student);
        model.addAttribute("report", report);
        model.addAttribute("recentAttempts", recentAttempts.stream().limit(5).toList());
        model.addAttribute("unreadNotifications", notificationService.getUnreadCount(student.getUser()));
        return "student/dashboard";
    }

    @GetMapping("/profile")
    public String viewProfile(@AuthenticationPrincipal UserDetails userDetails, Model model) {
        StudentProfile student = getStudent(userDetails);
        model.addAttribute("currentUser", student.getUser());
        model.addAttribute("student", student);
        model.addAttribute("semesters", academicService.getAllSemesters());
        model.addAttribute("schemes", academicService.getActiveSchemes());
        model.addAttribute("allSubjects", academicService.getAllSubjects());
        return "student/profile";
    }

    @PostMapping("/profile/update")
    public String updateProfile(@AuthenticationPrincipal UserDetails userDetails,
                                @RequestParam("fullName") String fullName,
                                @RequestParam(value = "phone", required = false) String phone,
                                @RequestParam("semesterId") Long semesterId,
                                @RequestParam("schemeId") Long schemeId,
                                @RequestParam(value = "supplementarySubjectIds", required = false) List<Long> supplementarySubjectIds,
                                @RequestParam(value = "notes", required = false) String notes,
                                RedirectAttributes redirectAttributes) {
        try {
            StudentProfile student = getStudent(userDetails);
            Semester semester = academicService.getSemesterById(semesterId).orElseThrow();
            Scheme scheme = academicService.getSchemeById(schemeId).orElseThrow();

            Set<Subject> backlogSubjects = new HashSet<>();
            if (supplementarySubjectIds != null) {
                for (Long sId : supplementarySubjectIds) {
                    academicService.getSubjectById(sId).ifPresent(backlogSubjects::add);
                }
            }

            studentService.updateStudentProfile(student.getId(), fullName, phone, semester, scheme, backlogSubjects, notes);
            redirectAttributes.addFlashAttribute("successMessage", "Profile updated successfully.");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("errorMessage", "Error updating profile: " + e.getMessage());
        }
        return "redirect:/student/profile";
    }

    @GetMapping("/materials")
    public String studyMaterials(@RequestParam(value = "subjectId", required = false) Long subjectId,
                                 @RequestParam(value = "semesterId", required = false) Long semesterId,
                                 @RequestParam(value = "schemeId", required = false) Long schemeId,
                                 @AuthenticationPrincipal UserDetails userDetails,
                                 Model model) {
        StudentProfile student = getStudent(userDetails);
        model.addAttribute("currentUser", student.getUser());
        model.addAttribute("student", student);

        Semester sem = semesterId != null ? academicService.getSemesterById(semesterId).orElse(null) : null;
        Scheme sch = schemeId != null ? academicService.getSchemeById(schemeId).orElse(null) : student.getScheme();
        Subject sub = subjectId != null ? academicService.getSubjectById(subjectId).orElse(null) : null;

        model.addAttribute("materials", studyMaterialService.filterMaterials(sem, sch, sub));
        model.addAttribute("subjects", academicService.getAllSubjects());
        model.addAttribute("semesters", academicService.getAllSemesters());
        model.addAttribute("schemes", academicService.getActiveSchemes());
        model.addAttribute("selectedSubjectId", subjectId);
        return "student/materials";
    }

    @GetMapping("/papers")
    public String previousYearPapers(@RequestParam(value = "subjectId", required = false) Long subjectId,
                                     @RequestParam(value = "semesterId", required = false) Long semesterId,
                                     @RequestParam(value = "schemeId", required = false) Long schemeId,
                                     @AuthenticationPrincipal UserDetails userDetails,
                                     Model model) {
        StudentProfile student = getStudent(userDetails);
        model.addAttribute("currentUser", student.getUser());
        model.addAttribute("student", student);

        Semester sem = semesterId != null ? academicService.getSemesterById(semesterId).orElse(null) : null;
        Scheme sch = schemeId != null ? academicService.getSchemeById(schemeId).orElse(null) : student.getScheme();
        Subject sub = subjectId != null ? academicService.getSubjectById(subjectId).orElse(null) : null;

        model.addAttribute("papers", previousYearPaperService.filterPapers(sem, sch, sub));
        model.addAttribute("subjects", academicService.getAllSubjects());
        model.addAttribute("semesters", academicService.getAllSemesters());
        model.addAttribute("schemes", academicService.getActiveSchemes());
        model.addAttribute("selectedSubjectId", subjectId);
        return "student/papers";
    }

    @GetMapping("/quiz")
    public String quizList(@AuthenticationPrincipal UserDetails userDetails, Model model) {
        StudentProfile student = getStudent(userDetails);
        model.addAttribute("currentUser", student.getUser());
        model.addAttribute("student", student);

        // Map subjects to question counts
        Map<Long, Long> questionCounts = new HashMap<>();
        for (Subject s : academicService.getAllSubjects()) {
            questionCounts.put(s.getId(), questionService.getQuestionsCountBySubject(s));
        }
        model.addAttribute("questionCounts", questionCounts);
        model.addAttribute("allSubjects", academicService.getAllSubjects());
        return "student/quiz_list";
    }

    @GetMapping("/quiz/start/{subjectId}")
    public String startQuiz(@PathVariable("subjectId") Long subjectId,
                            @RequestParam(value = "timer", defaultValue = "true") boolean timerEnabled,
                            @RequestParam(value = "duration", defaultValue = "15") int durationMinutes,
                            @AuthenticationPrincipal UserDetails userDetails,
                            Model model,
                            RedirectAttributes redirectAttributes) {
        StudentProfile student = getStudent(userDetails);
        Subject subject = academicService.getSubjectById(subjectId).orElseThrow();

        List<Question> questions = quizService.getQuizQuestions(subject, 10);
        if (questions.isEmpty()) {
            redirectAttributes.addFlashAttribute("errorMessage", "No practice questions currently available for " + subject.getName() + ".");
            return "redirect:/student/quiz";
        }

        model.addAttribute("currentUser", student.getUser());
        model.addAttribute("student", student);
        model.addAttribute("subject", subject);
        model.addAttribute("questions", questions);
        model.addAttribute("timerEnabled", timerEnabled);
        model.addAttribute("durationMinutes", durationMinutes);
        return "student/quiz_attempt";
    }

    @PostMapping("/quiz/submit")
    public String submitQuiz(@RequestParam("subjectId") Long subjectId,
                             @RequestParam("timeTakenSeconds") int timeTakenSeconds,
                             HttpServletRequest request,
                             @AuthenticationPrincipal UserDetails userDetails) {
        StudentProfile student = getStudent(userDetails);
        Subject subject = academicService.getSubjectById(subjectId).orElseThrow();

        Map<Long, String> answers = new HashMap<>();
        Enumeration<String> parameterNames = request.getParameterNames();
        while (parameterNames.hasMoreElements()) {
            String paramName = parameterNames.nextElement();
            if (paramName.startsWith("question_")) {
                Long questionId = Long.parseLong(paramName.replace("question_", ""));
                String selectedOption = request.getParameter(paramName);
                answers.put(questionId, selectedOption);
            }
        }

        QuizAttempt attempt = quizService.submitQuiz(student, subject, answers, timeTakenSeconds);
        return "redirect:/student/quiz/result/" + attempt.getId();
    }

    @GetMapping("/quiz/result/{attemptId}")
    public String quizResult(@PathVariable("attemptId") Long attemptId,
                             @AuthenticationPrincipal UserDetails userDetails,
                             Model model) {
        StudentProfile student = getStudent(userDetails);
        QuizAttempt attempt = quizService.getAttemptById(attemptId).orElseThrow();
        List<StudentQuizAnswer> answers = quizService.getAnswersByAttempt(attempt);

        model.addAttribute("currentUser", student.getUser());
        model.addAttribute("student", student);
        model.addAttribute("attempt", attempt);
        model.addAttribute("answers", answers);
        return "student/quiz_result";
    }

    @GetMapping("/quiz/history")
    public String quizHistory(@AuthenticationPrincipal UserDetails userDetails, Model model) {
        StudentProfile student = getStudent(userDetails);
        List<QuizAttempt> attempts = quizService.getAttemptsByStudent(student);

        model.addAttribute("currentUser", student.getUser());
        model.addAttribute("student", student);
        model.addAttribute("attempts", attempts);
        return "student/quiz_history";
    }

    @GetMapping("/progress")
    public String progressReport(@AuthenticationPrincipal UserDetails userDetails, Model model) {
        StudentProfile student = getStudent(userDetails);
        StudentReportDto report = reportService.generateStudentReport(student);
        List<QuizAttempt> attempts = quizService.getAttemptsByStudent(student);

        model.addAttribute("currentUser", student.getUser());
        model.addAttribute("student", student);
        model.addAttribute("report", report);
        model.addAttribute("attempts", attempts);
        return "student/progress";
    }
}

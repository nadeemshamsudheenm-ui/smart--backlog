package com.smartbacklog.controller;

import com.smartbacklog.dto.StudentReportDto;
import com.smartbacklog.entity.*;
import com.smartbacklog.service.*;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Controller
@RequestMapping("/parent")
public class ParentController {

    private final UserService userService;
    private final StudentService studentService;
    private final QuizService quizService;
    private final NotificationService notificationService;
    private final ReportService reportService;

    public ParentController(UserService userService,
                            StudentService studentService,
                            QuizService quizService,
                            NotificationService notificationService,
                            ReportService reportService) {
        this.userService = userService;
        this.studentService = studentService;
        this.quizService = quizService;
        this.notificationService = notificationService;
        this.reportService = reportService;
    }

    private User getLoggedInUser(UserDetails userDetails) {
        return userService.findByUsername(userDetails.getUsername()).orElse(null);
    }

    @GetMapping("/dashboard")
    public String dashboard(@AuthenticationPrincipal UserDetails userDetails,
                            @RequestParam(value = "studentId", required = false) Long studentId,
                            Model model) {
        User parent = getLoggedInUser(userDetails);
        List<StudentProfile> wards = studentService.getStudentsByParent(parent);

        model.addAttribute("currentUser", parent);
        model.addAttribute("wards", wards);

        if (!wards.isEmpty()) {
            StudentProfile selectedWard = null;
            if (studentId != null) {
                selectedWard = wards.stream().filter(w -> w.getId().equals(studentId)).findFirst().orElse(wards.get(0));
            } else {
                selectedWard = wards.get(0);
            }

            StudentReportDto report = reportService.generateStudentReport(selectedWard);
            List<QuizAttempt> attempts = quizService.getAttemptsByStudent(selectedWard);

            model.addAttribute("ward", selectedWard);
            model.addAttribute("report", report);
            model.addAttribute("recentAttempts", attempts.stream().limit(5).toList());
        }

        model.addAttribute("unreadNotifications", notificationService.getUnreadCount(parent));
        return "parent/dashboard";
    }

    @GetMapping("/ward-progress")
    public String wardProgress(@AuthenticationPrincipal UserDetails userDetails,
                               @RequestParam(value = "studentId", required = false) Long studentId,
                               Model model) {
        User parent = getLoggedInUser(userDetails);
        List<StudentProfile> wards = studentService.getStudentsByParent(parent);

        model.addAttribute("currentUser", parent);
        model.addAttribute("wards", wards);

        if (!wards.isEmpty()) {
            StudentProfile selectedWard = (studentId != null) ?
                    wards.stream().filter(w -> w.getId().equals(studentId)).findFirst().orElse(wards.get(0)) : wards.get(0);

            StudentReportDto report = reportService.generateStudentReport(selectedWard);
            List<QuizAttempt> attempts = quizService.getAttemptsByStudent(selectedWard);

            model.addAttribute("ward", selectedWard);
            model.addAttribute("report", report);
            model.addAttribute("attempts", attempts);
        }

        return "parent/progress";
    }

    @GetMapping("/quiz/result/{attemptId}")
    public String viewQuizResult(@PathVariable("attemptId") Long attemptId,
                                 @AuthenticationPrincipal UserDetails userDetails,
                                 Model model) {
        User parent = getLoggedInUser(userDetails);
        QuizAttempt attempt = quizService.getAttemptById(attemptId).orElseThrow();
        List<StudentQuizAnswer> answers = quizService.getAnswersByAttempt(attempt);

        model.addAttribute("currentUser", parent);
        model.addAttribute("attempt", attempt);
        model.addAttribute("answers", answers);
        model.addAttribute("student", attempt.getStudent());
        return "student/quiz_result";
    }
}

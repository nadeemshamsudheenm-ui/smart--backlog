package com.smartbacklog.controller;

import com.smartbacklog.dto.DepartmentReportDto;
import com.smartbacklog.dto.StudentReportDto;
import com.smartbacklog.entity.*;
import com.smartbacklog.service.*;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.List;

@Controller
@RequestMapping("/hod")
public class HodController {

    private final UserService userService;
    private final StudentService studentService;
    private final AcademicService academicService;
    private final QuizService quizService;
    private final NotificationService notificationService;
    private final ReportService reportService;

    public HodController(UserService userService,
                         StudentService studentService,
                         AcademicService academicService,
                         QuizService quizService,
                         NotificationService notificationService,
                         ReportService reportService) {
        this.userService = userService;
        this.studentService = studentService;
        this.academicService = academicService;
        this.quizService = quizService;
        this.notificationService = notificationService;
        this.reportService = reportService;
    }

    private User getLoggedInUser(UserDetails userDetails) {
        return userService.findByUsername(userDetails.getUsername()).orElse(null);
    }

    @GetMapping("/dashboard")
    public String dashboard(@AuthenticationPrincipal UserDetails userDetails, Model model) {
        User user = getLoggedInUser(userDetails);
        DepartmentReportDto report = reportService.generateDepartmentReport();

        model.addAttribute("currentUser", user);
        model.addAttribute("departmentReport", report);
        model.addAttribute("students", studentService.getAllStudents());
        model.addAttribute("tutors", userService.findByRole(Role.ROLE_TUTOR));
        model.addAttribute("recentAttempts", quizService.getAllRecentAttempts());
        model.addAttribute("unreadNotifications", notificationService.getUnreadCount(user));
        return "hod/dashboard";
    }

    @GetMapping("/students")
    public String listStudents(@RequestParam(value = "semesterId", required = false) Long semesterId,
                               @RequestParam(value = "schemeId", required = false) Long schemeId,
                               @RequestParam(value = "subjectId", required = false) Long subjectId,
                               @AuthenticationPrincipal UserDetails userDetails,
                               Model model) {
        model.addAttribute("currentUser", getLoggedInUser(userDetails));
        Semester sem = semesterId != null ? academicService.getSemesterById(semesterId).orElse(null) : null;
        Scheme sch = schemeId != null ? academicService.getSchemeById(schemeId).orElse(null) : null;

        List<StudentProfile> students = studentService.filterStudents(sem, sch, subjectId);
        model.addAttribute("students", students);
        model.addAttribute("semesters", academicService.getAllSemesters());
        model.addAttribute("schemes", academicService.getActiveSchemes());
        model.addAttribute("subjects", academicService.getAllSubjects());
        model.addAttribute("selectedSemesterId", semesterId);
        model.addAttribute("selectedSchemeId", schemeId);
        model.addAttribute("selectedSubjectId", subjectId);
        return "hod/students";
    }

    @GetMapping("/students/{id}")
    public String studentDetail(@PathVariable("id") Long id,
                                @AuthenticationPrincipal UserDetails userDetails,
                                Model model) {
        StudentProfile student = studentService.getStudentById(id).orElseThrow();
        StudentReportDto report = reportService.generateStudentReport(student);

        model.addAttribute("currentUser", getLoggedInUser(userDetails));
        model.addAttribute("student", student);
        model.addAttribute("studentReport", report);
        model.addAttribute("quizAttempts", quizService.getAttemptsByStudent(student));
        model.addAttribute("tutors", userService.findByRole(Role.ROLE_TUTOR));
        return "hod/student_detail";
    }

    @PostMapping("/tutors/assign")
    public String assignTutor(@RequestParam("studentProfileId") Long studentProfileId,
                              @RequestParam("tutorId") Long tutorId,
                              RedirectAttributes redirectAttributes) {
        try {
            User tutor = userService.findById(tutorId).orElseThrow();
            studentService.assignTutor(studentProfileId, tutor);
            redirectAttributes.addFlashAttribute("successMessage", "Assigned tutor " + tutor.getFullName() + " successfully.");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("errorMessage", "Error assigning tutor: " + e.getMessage());
        }
        return "redirect:/hod/students/" + studentProfileId;
    }

    @GetMapping("/reports")
    public String academicReports(@AuthenticationPrincipal UserDetails userDetails, Model model) {
        User user = getLoggedInUser(userDetails);
        DepartmentReportDto report = reportService.generateDepartmentReport();

        model.addAttribute("currentUser", user);
        model.addAttribute("departmentReport", report);
        model.addAttribute("students", studentService.getAllStudents());
        return "hod/reports";
    }

    @PostMapping("/notifications/send")
    public String sendNotification(@RequestParam("title") String title,
                                   @RequestParam("message") String message,
                                   @RequestParam(value = "targetRole", required = false) Role targetRole,
                                   @RequestParam(value = "recipientId", required = false) Long recipientId,
                                   RedirectAttributes redirectAttributes) {
        try {
            User recipient = recipientId != null ? userService.findById(recipientId).orElse(null) : null;
            notificationService.createNotification(recipient, targetRole, title, message, "ACADEMIC_ALERT", "/hod/dashboard");
            redirectAttributes.addFlashAttribute("successMessage", "Academic notice sent.");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("errorMessage", "Error sending notice: " + e.getMessage());
        }
        return "redirect:/hod/reports";
    }
}

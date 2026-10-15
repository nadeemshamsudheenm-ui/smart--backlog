package com.smartbacklog.controller;

import com.smartbacklog.dto.MaterialUploadDto;
import com.smartbacklog.dto.QuestionDto;
import com.smartbacklog.dto.StudentReportDto;
import com.smartbacklog.entity.*;
import com.smartbacklog.service.*;
import jakarta.validation.Valid;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.List;

@Controller
@RequestMapping("/tutor")
public class TutorController {

    private final UserService userService;
    private final StudentService studentService;
    private final AcademicService academicService;
    private final StudyMaterialService studyMaterialService;
    private final QuestionService questionService;
    private final QuizService quizService;
    private final NotificationService notificationService;
    private final ReportService reportService;

    public TutorController(UserService userService,
                           StudentService studentService,
                           AcademicService academicService,
                           StudyMaterialService studyMaterialService,
                           QuestionService questionService,
                           QuizService quizService,
                           NotificationService notificationService,
                           ReportService reportService) {
        this.userService = userService;
        this.studentService = studentService;
        this.academicService = academicService;
        this.studyMaterialService = studyMaterialService;
        this.questionService = questionService;
        this.quizService = quizService;
        this.notificationService = notificationService;
        this.reportService = reportService;
    }

    private User getLoggedInUser(UserDetails userDetails) {
        return userService.findByUsername(userDetails.getUsername()).orElse(null);
    }

    @GetMapping("/dashboard")
    public String dashboard(@AuthenticationPrincipal UserDetails userDetails, Model model) {
        User tutor = getLoggedInUser(userDetails);
        List<StudentProfile> students = studentService.getStudentsByTutor(tutor);

        model.addAttribute("currentUser", tutor);
        model.addAttribute("students", students);
        model.addAttribute("totalAssignedStudents", students.size());
        model.addAttribute("totalMaterials", studyMaterialService.getTotalMaterialsCount());
        model.addAttribute("totalQuestions", questionService.getTotalQuestionsCount());
        model.addAttribute("unreadNotifications", notificationService.getUnreadCount(tutor));
        return "tutor/dashboard";
    }

    @GetMapping("/students")
    public String listStudents(@AuthenticationPrincipal UserDetails userDetails, Model model) {
        User tutor = getLoggedInUser(userDetails);
        List<StudentProfile> students = studentService.getStudentsByTutor(tutor);

        model.addAttribute("currentUser", tutor);
        model.addAttribute("students", students);
        return "tutor/students";
    }

    @GetMapping("/student/{id}")
    public String studentDetail(@PathVariable("id") Long id,
                                @AuthenticationPrincipal UserDetails userDetails,
                                Model model) {
        User tutor = getLoggedInUser(userDetails);
        StudentProfile student = studentService.getStudentById(id).orElseThrow();
        StudentReportDto report = reportService.generateStudentReport(student);
        List<QuizAttempt> attempts = quizService.getAttemptsByStudent(student);

        model.addAttribute("currentUser", tutor);
        model.addAttribute("student", student);
        model.addAttribute("report", report);
        model.addAttribute("attempts", attempts);
        return "tutor/student_detail";
    }

    @GetMapping("/materials")
    public String listMaterials(@AuthenticationPrincipal UserDetails userDetails, Model model) {
        User tutor = getLoggedInUser(userDetails);
        model.addAttribute("currentUser", tutor);
        model.addAttribute("materials", studyMaterialService.getAllMaterials());
        model.addAttribute("subjects", academicService.getAllSubjects());
        model.addAttribute("semesters", academicService.getAllSemesters());
        model.addAttribute("schemes", academicService.getActiveSchemes());
        return "tutor/materials";
    }

    @PostMapping("/materials/create")
    public String createMaterial(@Valid @ModelAttribute MaterialUploadDto dto,
                                 BindingResult bindingResult,
                                 @AuthenticationPrincipal UserDetails userDetails,
                                 RedirectAttributes redirectAttributes) {
        try {
            Subject subject = academicService.getSubjectById(dto.getSubjectId()).orElseThrow();
            Semester semester = dto.getSemesterId() != null ? academicService.getSemesterById(dto.getSemesterId()).orElse(subject.getSemester()) : subject.getSemester();
            Scheme scheme = dto.getSchemeId() != null ? academicService.getSchemeById(dto.getSchemeId()).orElse(subject.getScheme()) : subject.getScheme();
            User tutor = getLoggedInUser(userDetails);

            String fileName = "TutorNotes_" + subject.getCode() + "_" + System.currentTimeMillis() + ".pdf";
            long fileSize = 1500000L;
            String filePath = "uploads/" + fileName;

            if (dto.getFile() != null && !dto.getFile().isEmpty()) {
                fileName = dto.getFile().getOriginalFilename();
                fileSize = dto.getFile().getSize();
                saveUploadedFile(dto.getFile(), fileName);
                filePath = "uploads/" + fileName;
            }

            StudyMaterial material = new StudyMaterial(
                    dto.getTitle(), dto.getDescription(), subject, semester, scheme,
                    dto.getFileType() != null ? dto.getFileType() : "PDF",
                    fileName, filePath, fileSize, dto.getContentSummary(), tutor
            );
            studyMaterialService.saveMaterial(material);
            redirectAttributes.addFlashAttribute("successMessage", "Study resource added successfully for your students.");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("errorMessage", "Error: " + e.getMessage());
        }
        return "redirect:/tutor/materials";
    }

    @GetMapping("/questions")
    public String listQuestions(@RequestParam(value = "subjectId", required = false) Long subjectId,
                                @AuthenticationPrincipal UserDetails userDetails,
                                Model model) {
        User tutor = getLoggedInUser(userDetails);
        Subject subject = subjectId != null ? academicService.getSubjectById(subjectId).orElse(null) : null;

        List<Question> questions = subject != null ? questionService.getQuestionsBySubject(subject) : questionService.getAllQuestions();
        model.addAttribute("currentUser", tutor);
        model.addAttribute("questions", questions);
        model.addAttribute("subjects", academicService.getAllSubjects());
        model.addAttribute("schemes", academicService.getActiveSchemes());
        model.addAttribute("selectedSubjectId", subjectId);
        return "tutor/questions";
    }

    @PostMapping("/questions/save")
    public String saveQuestion(@Valid @ModelAttribute("questionDto") QuestionDto dto,
                               @AuthenticationPrincipal UserDetails userDetails,
                               RedirectAttributes redirectAttributes) {
        try {
            Subject subject = academicService.getSubjectById(dto.getSubjectId()).orElseThrow();
            Scheme scheme = dto.getSchemeId() != null ? academicService.getSchemeById(dto.getSchemeId()).orElse(subject.getScheme()) : subject.getScheme();
            User tutor = getLoggedInUser(userDetails);

            Question question = new Question(
                    subject, scheme, dto.getQuestionText(),
                    dto.getOptionA(), dto.getOptionB(), dto.getOptionC(), dto.getOptionD(),
                    dto.getCorrectOption().trim().toUpperCase(),
                    dto.getExplanation(), dto.getDifficultyLevel(), dto.getSourceExam(), tutor
            );
            questionService.saveQuestion(question);
            redirectAttributes.addFlashAttribute("successMessage", "Practice question added successfully.");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("errorMessage", "Error saving question: " + e.getMessage());
        }
        return "redirect:/tutor/questions";
    }

    @PostMapping("/notifications/send")
    public String sendNotification(@RequestParam("recipientId") Long recipientId,
                                   @RequestParam("title") String title,
                                   @RequestParam("message") String message,
                                   RedirectAttributes redirectAttributes) {
        try {
            User student = userService.findById(recipientId).orElseThrow();
            notificationService.createNotification(student, null, title, message, "TUTOR_ADVICE", "/student/dashboard");
            redirectAttributes.addFlashAttribute("successMessage", "Study advice sent to student.");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("errorMessage", "Error: " + e.getMessage());
        }
        return "redirect:/tutor/student/" + recipientId;
    }

    private void saveUploadedFile(MultipartFile file, String fileName) throws IOException {
        Path uploadPath = Paths.get("src/main/resources/static/uploads");
        if (!Files.exists(uploadPath)) {
            Files.createDirectories(uploadPath);
        }
        Path targetPath = uploadPath.resolve(fileName);
        Files.copy(file.getInputStream(), targetPath, StandardCopyOption.REPLACE_EXISTING);
    }
}

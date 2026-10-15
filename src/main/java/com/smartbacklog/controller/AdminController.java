package com.smartbacklog.controller;

import com.smartbacklog.dto.MaterialUploadDto;
import com.smartbacklog.dto.PaperUploadDto;
import com.smartbacklog.dto.QuestionDto;
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
@RequestMapping("/admin")
public class AdminController {

    private final UserService userService;
    private final StudentService studentService;
    private final AcademicService academicService;
    private final StudyMaterialService studyMaterialService;
    private final PreviousYearPaperService previousYearPaperService;
    private final QuestionService questionService;
    private final QuizService quizService;
    private final NotificationService notificationService;

    public AdminController(UserService userService,
                           StudentService studentService,
                           AcademicService academicService,
                           StudyMaterialService studyMaterialService,
                           PreviousYearPaperService previousYearPaperService,
                           QuestionService questionService,
                           QuizService quizService,
                           NotificationService notificationService) {
        this.userService = userService;
        this.studentService = studentService;
        this.academicService = academicService;
        this.studyMaterialService = studyMaterialService;
        this.previousYearPaperService = previousYearPaperService;
        this.questionService = questionService;
        this.quizService = quizService;
        this.notificationService = notificationService;
    }

    private User getLoggedInUser(UserDetails userDetails) {
        return userService.findByUsername(userDetails.getUsername()).orElse(null);
    }

    @GetMapping("/dashboard")
    public String dashboard(@AuthenticationPrincipal UserDetails userDetails, Model model) {
        User user = getLoggedInUser(userDetails);
        model.addAttribute("currentUser", user);
        model.addAttribute("totalStudents", studentService.getTotalStudentsCount());
        model.addAttribute("totalTutors", userService.countByRole(Role.ROLE_TUTOR));
        model.addAttribute("totalHod", userService.countByRole(Role.ROLE_HOD));
        model.addAttribute("totalParents", userService.countByRole(Role.ROLE_PARENT));
        model.addAttribute("totalSubjects", academicService.getTotalSubjectsCount());
        model.addAttribute("totalMaterials", studyMaterialService.getTotalMaterialsCount());
        model.addAttribute("totalPapers", previousYearPaperService.getTotalPapersCount());
        model.addAttribute("totalQuestions", questionService.getTotalQuestionsCount());
        model.addAttribute("totalQuizAttempts", quizService.getTotalQuizAttemptsCount());
        model.addAttribute("totalBacklogs", studentService.getTotalBacklogsCount());

        model.addAttribute("recentAttempts", quizService.getAllRecentAttempts());
        model.addAttribute("recentStudents", studentService.getAllStudents());
        model.addAttribute("unreadNotifications", notificationService.getUnreadCount(user));
        return "admin/dashboard";
    }

    // User Management
    @GetMapping("/users")
    public String listUsers(@RequestParam(value = "role", required = false) Role role,
                            @AuthenticationPrincipal UserDetails userDetails,
                            Model model) {
        model.addAttribute("currentUser", getLoggedInUser(userDetails));
        List<User> users = (role != null) ? userService.findByRole(role) : userService.findAll();
        model.addAttribute("users", users);
        model.addAttribute("selectedRole", role);
        model.addAttribute("roles", Role.values());
        return "admin/users";
    }

    @PostMapping("/users/create")
    public String createUser(@RequestParam("username") String username,
                             @RequestParam("password") String password,
                             @RequestParam("fullName") String fullName,
                             @RequestParam("role") Role role,
                             @RequestParam(value = "email", required = false) String email,
                             @RequestParam(value = "phone", required = false) String phone,
                             RedirectAttributes redirectAttributes) {
        try {
            userService.createUser(username, password, fullName, role, email, phone);
            redirectAttributes.addFlashAttribute("successMessage", "User account created successfully for: " + fullName);
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("errorMessage", "Error: " + e.getMessage());
        }
        return "redirect:/admin/users";
    }

    @PostMapping("/users/update")
    public String updateUser(@RequestParam("id") Long id,
                             @RequestParam("fullName") String fullName,
                             @RequestParam(value = "email", required = false) String email,
                             @RequestParam(value = "phone", required = false) String phone,
                             @RequestParam(value = "active", defaultValue = "false") boolean active,
                             @RequestParam(value = "newPassword", required = false) String newPassword,
                             RedirectAttributes redirectAttributes) {
        try {
            userService.updateUser(id, fullName, email, phone, active, newPassword);
            redirectAttributes.addFlashAttribute("successMessage", "User updated successfully.");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("errorMessage", "Error: " + e.getMessage());
        }
        return "redirect:/admin/users";
    }

    @PostMapping("/users/delete/{id}")
    public String deleteUser(@PathVariable("id") Long id, RedirectAttributes redirectAttributes) {
        try {
            userService.deleteUser(id);
            redirectAttributes.addFlashAttribute("successMessage", "User deleted successfully.");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("errorMessage", "Could not delete user: " + e.getMessage());
        }
        return "redirect:/admin/users";
    }

    // Subjects Management
    @GetMapping("/subjects")
    public String listSubjects(@RequestParam(value = "semesterId", required = false) Long semesterId,
                               @RequestParam(value = "schemeId", required = false) Long schemeId,
                               @AuthenticationPrincipal UserDetails userDetails,
                               Model model) {
        model.addAttribute("currentUser", getLoggedInUser(userDetails));
        Semester semester = semesterId != null ? academicService.getSemesterById(semesterId).orElse(null) : null;
        Scheme scheme = schemeId != null ? academicService.getSchemeById(schemeId).orElse(null) : null;

        model.addAttribute("subjects", academicService.getSubjectsBySemesterAndScheme(semester, scheme));
        model.addAttribute("schemes", academicService.getActiveSchemes());
        model.addAttribute("semesters", academicService.getAllSemesters());
        model.addAttribute("selectedSemesterId", semesterId);
        model.addAttribute("selectedSchemeId", schemeId);
        return "admin/subjects";
    }

    @PostMapping("/subjects/create")
    public String createSubject(@RequestParam("code") String code,
                                @RequestParam("name") String name,
                                @RequestParam(value = "department", defaultValue = "Computer Science and Engineering") String department,
                                @RequestParam("semesterId") Long semesterId,
                                @RequestParam("schemeId") Long schemeId,
                                @RequestParam(value = "credits", defaultValue = "4") int credits,
                                @RequestParam(value = "description", required = false) String description,
                                RedirectAttributes redirectAttributes) {
        try {
            Semester semester = academicService.getSemesterById(semesterId).orElseThrow();
            Scheme scheme = academicService.getSchemeById(schemeId).orElseThrow();
            Subject subject = new Subject(code.trim().toUpperCase(), name.trim(), department, semester, scheme, credits, description);
            academicService.saveSubject(subject);
            redirectAttributes.addFlashAttribute("successMessage", "Subject " + code + " added successfully.");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("errorMessage", "Error: " + e.getMessage());
        }
        return "redirect:/admin/subjects";
    }

    @PostMapping("/subjects/delete/{id}")
    public String deleteSubject(@PathVariable("id") Long id, RedirectAttributes redirectAttributes) {
        try {
            academicService.deleteSubject(id);
            redirectAttributes.addFlashAttribute("successMessage", "Subject deleted successfully.");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("errorMessage", "Cannot delete subject: linked to students or materials.");
        }
        return "redirect:/admin/subjects";
    }

    // Study Materials Management
    @GetMapping("/materials")
    public String listMaterials(@RequestParam(value = "semesterId", required = false) Long semesterId,
                                @RequestParam(value = "schemeId", required = false) Long schemeId,
                                @RequestParam(value = "subjectId", required = false) Long subjectId,
                                @AuthenticationPrincipal UserDetails userDetails,
                                Model model) {
        model.addAttribute("currentUser", getLoggedInUser(userDetails));
        Semester sem = semesterId != null ? academicService.getSemesterById(semesterId).orElse(null) : null;
        Scheme sch = schemeId != null ? academicService.getSchemeById(schemeId).orElse(null) : null;
        Subject sub = subjectId != null ? academicService.getSubjectById(subjectId).orElse(null) : null;

        model.addAttribute("materials", studyMaterialService.filterMaterials(sem, sch, sub));
        model.addAttribute("subjects", academicService.getAllSubjects());
        model.addAttribute("schemes", academicService.getActiveSchemes());
        model.addAttribute("semesters", academicService.getAllSemesters());
        return "admin/materials";
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
            User adminUser = getLoggedInUser(userDetails);

            String fileName = "Notes_" + subject.getCode() + "_" + System.currentTimeMillis() + ".pdf";
            long fileSize = 1024000L;
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
                    fileName, filePath, fileSize, dto.getContentSummary(), adminUser
            );
            studyMaterialService.saveMaterial(material);
            redirectAttributes.addFlashAttribute("successMessage", "Study material uploaded successfully.");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("errorMessage", "Error: " + e.getMessage());
        }
        return "redirect:/admin/materials";
    }

    @PostMapping("/materials/delete/{id}")
    public String deleteMaterial(@PathVariable("id") Long id, RedirectAttributes redirectAttributes) {
        try {
            studyMaterialService.deleteMaterial(id);
            redirectAttributes.addFlashAttribute("successMessage", "Study material deleted.");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("errorMessage", "Could not delete material: " + e.getMessage());
        }
        return "redirect:/admin/materials";
    }

    // Previous Year Papers Management
    @GetMapping("/papers")
    public String listPapers(@RequestParam(value = "semesterId", required = false) Long semesterId,
                             @RequestParam(value = "schemeId", required = false) Long schemeId,
                             @RequestParam(value = "subjectId", required = false) Long subjectId,
                             @AuthenticationPrincipal UserDetails userDetails,
                             Model model) {
        model.addAttribute("currentUser", getLoggedInUser(userDetails));
        Semester sem = semesterId != null ? academicService.getSemesterById(semesterId).orElse(null) : null;
        Scheme sch = schemeId != null ? academicService.getSchemeById(schemeId).orElse(null) : null;
        Subject sub = subjectId != null ? academicService.getSubjectById(subjectId).orElse(null) : null;

        model.addAttribute("papers", previousYearPaperService.filterPapers(sem, sch, sub));
        model.addAttribute("subjects", academicService.getAllSubjects());
        model.addAttribute("schemes", academicService.getActiveSchemes());
        model.addAttribute("semesters", academicService.getAllSemesters());
        return "admin/papers";
    }

    @PostMapping("/papers/create")
    public String createPaper(@Valid @ModelAttribute PaperUploadDto dto,
                              BindingResult bindingResult,
                              @AuthenticationPrincipal UserDetails userDetails,
                              RedirectAttributes redirectAttributes) {
        try {
            Subject subject = academicService.getSubjectById(dto.getSubjectId()).orElseThrow();
            Semester semester = dto.getSemesterId() != null ? academicService.getSemesterById(dto.getSemesterId()).orElse(subject.getSemester()) : subject.getSemester();
            Scheme scheme = dto.getSchemeId() != null ? academicService.getSchemeById(dto.getSchemeId()).orElse(subject.getScheme()) : subject.getScheme();
            User adminUser = getLoggedInUser(userDetails);

            String fileName = "PYQ_" + subject.getCode() + "_" + System.currentTimeMillis() + ".pdf";
            long fileSize = 1204000L;
            String filePath = "uploads/" + fileName;

            if (dto.getFile() != null && !dto.getFile().isEmpty()) {
                fileName = dto.getFile().getOriginalFilename();
                fileSize = dto.getFile().getSize();
                saveUploadedFile(dto.getFile(), fileName);
                filePath = "uploads/" + fileName;
            }

            PreviousYearPaper paper = new PreviousYearPaper(
                    dto.getTitle(), dto.getExamSession(), subject, semester, scheme,
                    fileName, filePath, fileSize, dto.getExamPatternNotes(), adminUser
            );
            previousYearPaperService.savePaper(paper);
            redirectAttributes.addFlashAttribute("successMessage", "Previous Year Question Paper uploaded successfully.");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("errorMessage", "Error: " + e.getMessage());
        }
        return "redirect:/admin/papers";
    }

    @PostMapping("/papers/delete/{id}")
    public String deletePaper(@PathVariable("id") Long id, RedirectAttributes redirectAttributes) {
        try {
            previousYearPaperService.deletePaper(id);
            redirectAttributes.addFlashAttribute("successMessage", "Question paper deleted.");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("errorMessage", "Could not delete paper: " + e.getMessage());
        }
        return "redirect:/admin/papers";
    }

    // Questions / MCQ Bank Management
    @GetMapping("/questions")
    public String listQuestions(@RequestParam(value = "subjectId", required = false) Long subjectId,
                                @AuthenticationPrincipal UserDetails userDetails,
                                Model model) {
        model.addAttribute("currentUser", getLoggedInUser(userDetails));
        Subject subject = subjectId != null ? academicService.getSubjectById(subjectId).orElse(null) : null;

        List<Question> questions = subject != null ? questionService.getQuestionsBySubject(subject) : questionService.getAllQuestions();
        model.addAttribute("questions", questions);
        model.addAttribute("subjects", academicService.getAllSubjects());
        model.addAttribute("selectedSubjectId", subjectId);
        return "admin/questions";
    }

    @GetMapping("/questions/create")
    public String showCreateQuestionForm(Model model, @AuthenticationPrincipal UserDetails userDetails) {
        model.addAttribute("currentUser", getLoggedInUser(userDetails));
        model.addAttribute("questionDto", new QuestionDto());
        model.addAttribute("subjects", academicService.getAllSubjects());
        model.addAttribute("schemes", academicService.getActiveSchemes());
        return "admin/question_form";
    }

    @PostMapping("/questions/save")
    public String saveQuestion(@Valid @ModelAttribute("questionDto") QuestionDto dto,
                               BindingResult bindingResult,
                               @AuthenticationPrincipal UserDetails userDetails,
                               RedirectAttributes redirectAttributes,
                               Model model) {
        if (bindingResult.hasErrors()) {
            model.addAttribute("currentUser", getLoggedInUser(userDetails));
            model.addAttribute("subjects", academicService.getAllSubjects());
            model.addAttribute("schemes", academicService.getActiveSchemes());
            return "admin/question_form";
        }

        try {
            Subject subject = academicService.getSubjectById(dto.getSubjectId()).orElseThrow();
            Scheme scheme = dto.getSchemeId() != null ? academicService.getSchemeById(dto.getSchemeId()).orElse(subject.getScheme()) : subject.getScheme();
            User user = getLoggedInUser(userDetails);

            Question question;
            if (dto.getId() != null) {
                question = questionService.getQuestionById(dto.getId()).orElse(new Question());
            } else {
                question = new Question();
                question.setCreatedBy(user);
            }

            question.setSubject(subject);
            question.setScheme(scheme);
            question.setQuestionText(dto.getQuestionText());
            question.setOptionA(dto.getOptionA());
            question.setOptionB(dto.getOptionB());
            question.setOptionC(dto.getOptionC());
            question.setOptionD(dto.getOptionD());
            question.setCorrectOption(dto.getCorrectOption().trim().toUpperCase());
            question.setExplanation(dto.getExplanation());
            question.setDifficultyLevel(dto.getDifficultyLevel());
            question.setSourceExam(dto.getSourceExam());

            questionService.saveQuestion(question);
            redirectAttributes.addFlashAttribute("successMessage", "Question saved to practice bank.");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("errorMessage", "Error saving question: " + e.getMessage());
        }
        return "redirect:/admin/questions";
    }

    @GetMapping("/questions/edit/{id}")
    public String editQuestion(@PathVariable("id") Long id, Model model, @AuthenticationPrincipal UserDetails userDetails) {
        Question q = questionService.getQuestionById(id).orElseThrow();
        QuestionDto dto = new QuestionDto();
        dto.setId(q.getId());
        dto.setSubjectId(q.getSubject().getId());
        dto.setSchemeId(q.getScheme() != null ? q.getScheme().getId() : null);
        dto.setQuestionText(q.getQuestionText());
        dto.setOptionA(q.getOptionA());
        dto.setOptionB(q.getOptionB());
        dto.setOptionC(q.getOptionC());
        dto.setOptionD(q.getOptionD());
        dto.setCorrectOption(q.getCorrectOption());
        dto.setExplanation(q.getExplanation());
        dto.setDifficultyLevel(q.getDifficultyLevel());
        dto.setSourceExam(q.getSourceExam());

        model.addAttribute("currentUser", getLoggedInUser(userDetails));
        model.addAttribute("questionDto", dto);
        model.addAttribute("subjects", academicService.getAllSubjects());
        model.addAttribute("schemes", academicService.getActiveSchemes());
        return "admin/question_form";
    }

    @PostMapping("/questions/delete/{id}")
    public String deleteQuestion(@PathVariable("id") Long id, RedirectAttributes redirectAttributes) {
        try {
            questionService.deleteQuestion(id);
            redirectAttributes.addFlashAttribute("successMessage", "Question deleted.");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("errorMessage", "Could not delete question: " + e.getMessage());
        }
        return "redirect:/admin/questions";
    }

    // Notifications Management
    @GetMapping("/notifications")
    public String listNotifications(@AuthenticationPrincipal UserDetails userDetails, Model model) {
        model.addAttribute("currentUser", getLoggedInUser(userDetails));
        model.addAttribute("notifications", notificationService.getAllRecentNotifications());
        model.addAttribute("roles", Role.values());
        model.addAttribute("users", userService.findAll());
        return "admin/notifications";
    }

    @PostMapping("/notifications/send")
    public String sendNotification(@RequestParam("title") String title,
                                   @RequestParam("message") String message,
                                   @RequestParam(value = "targetRole", required = false) Role targetRole,
                                   @RequestParam(value = "recipientId", required = false) Long recipientId,
                                   @RequestParam(value = "type", defaultValue = "ANNOUNCEMENT") String type,
                                   @RequestParam(value = "linkUrl", required = false) String linkUrl,
                                   RedirectAttributes redirectAttributes) {
        try {
            User recipient = recipientId != null ? userService.findById(recipientId).orElse(null) : null;
            notificationService.createNotification(recipient, targetRole, title, message, type, linkUrl);
            redirectAttributes.addFlashAttribute("successMessage", "Notification sent successfully.");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("errorMessage", "Error sending notification: " + e.getMessage());
        }
        return "redirect:/admin/notifications";
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

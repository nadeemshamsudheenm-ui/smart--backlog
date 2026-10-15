package com.smartbacklog.controller;

import com.smartbacklog.dto.StudentRegistrationDto;
import com.smartbacklog.entity.Role;
import com.smartbacklog.entity.Scheme;
import com.smartbacklog.entity.Semester;
import com.smartbacklog.entity.Subject;
import com.smartbacklog.service.AcademicService;
import com.smartbacklog.service.StudentService;
import com.smartbacklog.service.UserService;
import jakarta.validation.Valid;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Controller
public class AuthController {

    private final StudentService studentService;
    private final AcademicService academicService;

    public AuthController(StudentService studentService,
                          AcademicService academicService) {
        this.studentService = studentService;
        this.academicService = academicService;
    }

    @GetMapping("/")
    public String index(Authentication authentication) {
        if (authentication != null && authentication.isAuthenticated()
                && !"anonymousUser".equals(authentication.getPrincipal())) {
            for (GrantedAuthority auth : authentication.getAuthorities()) {
                String role = auth.getAuthority();
                if ("ROLE_ADMIN".equals(role)) return "redirect:/admin/dashboard";
                if ("ROLE_HOD".equals(role)) return "redirect:/hod/dashboard";
                if ("ROLE_STUDENT".equals(role)) return "redirect:/student/dashboard";
                if ("ROLE_PARENT".equals(role)) return "redirect:/parent/dashboard";
                if ("ROLE_TUTOR".equals(role)) return "redirect:/tutor/dashboard";
            }
        }
        return "redirect:/login";
    }

    @GetMapping("/login")
    public String login(Authentication authentication, Model model) {
        if (authentication != null && authentication.isAuthenticated()
                && !"anonymousUser".equals(authentication.getPrincipal())) {
            return "redirect:/";
        }
        return "auth/login";
    }

    @GetMapping("/register")
    public String showRegistrationForm(Model model) {
        model.addAttribute("studentDto", new StudentRegistrationDto());
        model.addAttribute("schemes", academicService.getActiveSchemes());
        model.addAttribute("semesters", academicService.getAllSemesters());
        model.addAttribute("subjects", academicService.getAllSubjects());
        return "auth/register";
    }

    @PostMapping("/register")
    public String registerStudent(@Valid @ModelAttribute("studentDto") StudentRegistrationDto dto,
                                  BindingResult bindingResult,
                                  Model model) {
        if (bindingResult.hasErrors()) {
            model.addAttribute("schemes", academicService.getActiveSchemes());
            model.addAttribute("semesters", academicService.getAllSemesters());
            model.addAttribute("subjects", academicService.getAllSubjects());
            return "auth/register";
        }

        try {
            Semester semester = academicService.getSemesterById(dto.getSemesterId())
                    .orElseThrow(() -> new IllegalArgumentException("Invalid Semester selected"));
            Scheme scheme = academicService.getSchemeById(dto.getSchemeId())
                    .orElseThrow(() -> new IllegalArgumentException("Invalid Academic Scheme selected"));

            Set<Subject> backlogSubjects = new HashSet<>();
            if (dto.getSupplementarySubjectIds() != null) {
                for (Long sId : dto.getSupplementarySubjectIds()) {
                    academicService.getSubjectById(sId).ifPresent(backlogSubjects::add);
                }
            }

            studentService.registerStudent(
                    dto.getUsername(),
                    dto.getPassword(),
                    dto.getFullName(),
                    dto.getEmail(),
                    dto.getPhone(),
                    dto.getRegisterNumber(),
                    dto.getDepartment(),
                    semester,
                    scheme,
                    backlogSubjects
            );

            return "redirect:/login?registered=true";
        } catch (Exception e) {
            model.addAttribute("errorMessage", e.getMessage());
            model.addAttribute("schemes", academicService.getActiveSchemes());
            model.addAttribute("semesters", academicService.getAllSemesters());
            model.addAttribute("subjects", academicService.getAllSubjects());
            return "auth/register";
        }
    }
}

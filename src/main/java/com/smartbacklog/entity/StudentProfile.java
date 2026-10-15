package com.smartbacklog.entity;

import jakarta.persistence.*;
import java.util.HashSet;
import java.util.Set;

@Entity
@Table(name = "student_profiles")
public class StudentProfile {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @OneToOne(fetch = FetchType.EAGER, cascade = CascadeType.ALL)
    @JoinColumn(name = "user_id", nullable = false, unique = true)
    private User user;

    @Column(nullable = false, unique = true, length = 50)
    private String registerNumber;

    @Column(length = 100)
    private String department = "Computer Science and Engineering";

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "current_semester_id")
    private Semester currentSemester;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "scheme_id")
    private Scheme scheme;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "parent_user_id")
    private User parentUser;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "assigned_tutor_id")
    private User assignedTutor;

    @ManyToMany(fetch = FetchType.EAGER)
    @JoinTable(
        name = "student_supplementary_subjects",
        joinColumns = @JoinColumn(name = "student_profile_id"),
        inverseJoinColumns = @JoinColumn(name = "subject_id")
    )
    private Set<Subject> supplementarySubjects = new HashSet<>();

    @Column(length = 500)
    private String notes;

    public StudentProfile() {}

    public StudentProfile(User user, String registerNumber, String department, Semester currentSemester, Scheme scheme) {
        this.user = user;
        this.registerNumber = registerNumber;
        this.department = department;
        this.currentSemester = currentSemester;
        this.scheme = scheme;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
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

    public Semester getCurrentSemester() {
        return currentSemester;
    }

    public void setCurrentSemester(Semester currentSemester) {
        this.currentSemester = currentSemester;
    }

    public Scheme getScheme() {
        return scheme;
    }

    public void setScheme(Scheme scheme) {
        this.scheme = scheme;
    }

    public User getParentUser() {
        return parentUser;
    }

    public void setParentUser(User parentUser) {
        this.parentUser = parentUser;
    }

    public User getAssignedTutor() {
        return assignedTutor;
    }

    public void setAssignedTutor(User assignedTutor) {
        this.assignedTutor = assignedTutor;
    }

    public Set<Subject> getSupplementarySubjects() {
        return supplementarySubjects;
    }

    public void setSupplementarySubjects(Set<Subject> supplementarySubjects) {
        this.supplementarySubjects = supplementarySubjects;
    }

    public String getNotes() {
        return notes;
    }

    public void setNotes(String notes) {
        this.notes = notes;
    }

    public int getBacklogCount() {
        return supplementarySubjects != null ? supplementarySubjects.size() : 0;
    }
}

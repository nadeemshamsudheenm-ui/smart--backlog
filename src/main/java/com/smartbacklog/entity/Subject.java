package com.smartbacklog.entity;

import jakarta.persistence.*;

@Entity
@Table(name = "subjects")
public class Subject {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, length = 30)
    private String code; // e.g. "CST201"

    @Column(nullable = false, length = 150)
    private String name; // e.g. "Data Structures"

    @Column(length = 100)
    private String department; // "Computer Science and Engineering"

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "semester_id", nullable = false)
    private Semester semester;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "scheme_id", nullable = false)
    private Scheme scheme;

    @Column(nullable = false)
    private int credits = 4;

    @Column(length = 500)
    private String description;

    public Subject() {}

    public Subject(String code, String name, String department, Semester semester, Scheme scheme, int credits, String description) {
        this.code = code;
        this.name = name;
        this.department = department;
        this.semester = semester;
        this.scheme = scheme;
        this.credits = credits;
        this.description = description;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDepartment() {
        return department;
    }

    public void setDepartment(String department) {
        this.department = department;
    }

    public Semester getSemester() {
        return semester;
    }

    public void setSemester(Semester semester) {
        this.semester = semester;
    }

    public Scheme getScheme() {
        return scheme;
    }

    public void setScheme(Scheme scheme) {
        this.scheme = scheme;
    }

    public int getCredits() {
        return credits;
    }

    public void setCredits(int credits) {
        this.credits = credits;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getDisplayName() {
        return code + " - " + name + " (" + (scheme != null ? scheme.getName() : "") + ")";
    }
}

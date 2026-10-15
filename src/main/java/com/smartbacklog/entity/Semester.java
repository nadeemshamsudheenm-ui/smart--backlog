package com.smartbacklog.entity;

import jakarta.persistence.*;

@Entity
@Table(name = "semesters")
public class Semester {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true, length = 20)
    private String name; // "Semester 1 (S1)", "Semester 2 (S2)", etc.

    @Column(nullable = false, length = 10)
    private String code; // "S1", "S2", etc.

    @Column(nullable = false)
    private int semesterNumber; // 1 to 8

    public Semester() {}

    public Semester(String name, String code, int semesterNumber) {
        this.name = name;
        this.code = code;
        this.semesterNumber = semesterNumber;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public int getSemesterNumber() {
        return semesterNumber;
    }

    public void setSemesterNumber(int semesterNumber) {
        this.semesterNumber = semesterNumber;
    }
}

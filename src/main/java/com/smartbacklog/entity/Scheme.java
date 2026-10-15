package com.smartbacklog.entity;

import jakarta.persistence.*;

@Entity
@Table(name = "schemes")
public class Scheme {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true, length = 50)
    private String name; // e.g. "2019 Scheme", "2024 Scheme"

    @Column(nullable = false, unique = true, length = 20)
    private String code; // e.g. "SCH-2019", "SCH-2024"

    @Column(length = 255)
    private String description;

    @Column(nullable = false)
    private boolean active = true;

    public Scheme() {}

    public Scheme(String name, String code, String description) {
        this.name = name;
        this.code = code;
        this.description = description;
        this.active = true;
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

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public boolean isActive() {
        return active;
    }

    public void setActive(boolean active) {
        this.active = active;
    }
}

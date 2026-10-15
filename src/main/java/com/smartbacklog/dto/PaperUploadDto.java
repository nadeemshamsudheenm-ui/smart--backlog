package com.smartbacklog.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import org.springframework.web.multipart.MultipartFile;

public class PaperUploadDto {

    @NotBlank(message = "Title is required")
    private String title;

    private String examSession;

    @NotNull(message = "Subject is required")
    private Long subjectId;

    private Long semesterId;
    private Long schemeId;

    private String examPatternNotes;
    private MultipartFile file;

    public PaperUploadDto() {}

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getExamSession() {
        return examSession;
    }

    public void setExamSession(String examSession) {
        this.examSession = examSession;
    }

    public Long getSubjectId() {
        return subjectId;
    }

    public void setSubjectId(Long subjectId) {
        this.subjectId = subjectId;
    }

    public Long getSemesterId() {
        return semesterId;
    }

    public void setSemesterId(Long semesterId) {
        this.semesterId = semesterId;
    }

    public Long getSchemeId() {
        return schemeId;
    }

    public void setSchemeId(Long schemeId) {
        this.schemeId = schemeId;
    }

    public String getExamPatternNotes() {
        return examPatternNotes;
    }

    public void setExamPatternNotes(String examPatternNotes) {
        this.examPatternNotes = examPatternNotes;
    }

    public MultipartFile getFile() {
        return file;
    }

    public void setFile(MultipartFile file) {
        this.file = file;
    }
}

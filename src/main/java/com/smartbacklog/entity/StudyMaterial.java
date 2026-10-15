package com.smartbacklog.entity;

import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "study_materials")
public class StudyMaterial {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, length = 200)
    private String title;

    @Column(length = 1000)
    private String description;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "subject_id", nullable = false)
    private Subject subject;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "semester_id", nullable = false)
    private Semester semester;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "scheme_id", nullable = false)
    private Scheme scheme;

    @Column(length = 20)
    private String fileType = "PDF"; // "PDF", "NOTES", "MODULE_SUMMARY"

    @Column(nullable = false, length = 255)
    private String fileName;

    @Column(length = 500)
    private String filePath;

    private long fileSize = 0; // in bytes

    @Lob
    @Column(columnDefinition = "TEXT")
    private String contentSummary; // Key topics summary / revision notes

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "uploaded_by_id")
    private User uploadedBy;

    @Column(nullable = false)
    private LocalDateTime uploadedAt = LocalDateTime.now();

    @Column(nullable = false)
    private int downloadCount = 0;

    public StudyMaterial() {}

    public StudyMaterial(String title, String description, Subject subject, Semester semester, Scheme scheme,
                         String fileType, String fileName, String filePath, long fileSize, String contentSummary, User uploadedBy) {
        this.title = title;
        this.description = description;
        this.subject = subject;
        this.semester = semester;
        this.scheme = scheme;
        this.fileType = fileType;
        this.fileName = fileName;
        this.filePath = filePath;
        this.fileSize = fileSize;
        this.contentSummary = contentSummary;
        this.uploadedBy = uploadedBy;
        this.uploadedAt = LocalDateTime.now();
        this.downloadCount = 0;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public Subject getSubject() {
        return subject;
    }

    public void setSubject(Subject subject) {
        this.subject = subject;
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

    public String getFileType() {
        return fileType;
    }

    public void setFileType(String fileType) {
        this.fileType = fileType;
    }

    public String getFileName() {
        return fileName;
    }

    public void setFileName(String fileName) {
        this.fileName = fileName;
    }

    public String getFilePath() {
        return filePath;
    }

    public void setFilePath(String filePath) {
        this.filePath = filePath;
    }

    public long getFileSize() {
        return fileSize;
    }

    public void setFileSize(long fileSize) {
        this.fileSize = fileSize;
    }

    public String getContentSummary() {
        return contentSummary;
    }

    public void setContentSummary(String contentSummary) {
        this.contentSummary = contentSummary;
    }

    public User getUploadedBy() {
        return uploadedBy;
    }

    public void setUploadedBy(User uploadedBy) {
        this.uploadedBy = uploadedBy;
    }

    public LocalDateTime getUploadedAt() {
        return uploadedAt;
    }

    public void setUploadedAt(LocalDateTime uploadedAt) {
        this.uploadedAt = uploadedAt;
    }

    public int getDownloadCount() {
        return downloadCount;
    }

    public void setDownloadCount(int downloadCount) {
        this.downloadCount = downloadCount;
    }

    public String getFormattedFileSize() {
        if (fileSize < 1024) return fileSize + " B";
        long kb = fileSize / 1024;
        if (kb < 1024) return kb + " KB";
        return String.format("%.1f MB", kb / 1024.0);
    }
}

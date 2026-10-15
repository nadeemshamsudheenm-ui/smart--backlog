package com.smartbacklog.controller;

import com.smartbacklog.entity.PreviousYearPaper;
import com.smartbacklog.entity.StudyMaterial;
import com.smartbacklog.service.PreviousYearPaperService;
import com.smartbacklog.service.StudyMaterialService;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.core.io.FileSystemResource;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

import java.io.File;
import java.nio.charset.StandardCharsets;

@Controller
public class FileDownloadController {

    private final StudyMaterialService studyMaterialService;
    private final PreviousYearPaperService previousYearPaperService;

    public FileDownloadController(StudyMaterialService studyMaterialService,
                                  PreviousYearPaperService previousYearPaperService) {
        this.studyMaterialService = studyMaterialService;
        this.previousYearPaperService = previousYearPaperService;
    }

    @GetMapping("/download/material/{id}")
    public ResponseEntity<Resource> downloadMaterial(@PathVariable("id") Long id) {
        StudyMaterial material = studyMaterialService.getMaterialById(id).orElseThrow();
        studyMaterialService.incrementDownloadCount(id);

        File file = new File("src/main/resources/static/" + material.getFilePath());
        if (file.exists()) {
            return ResponseEntity.ok()
                    .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"" + material.getFileName() + "\"")
                    .contentType(MediaType.APPLICATION_PDF)
                    .contentLength(file.length())
                    .body(new FileSystemResource(file));
        }

        // Fallback dynamic resource
        String content = "%PDF-1.4\nSmart Backlog Clearance Portal - Study Material\nTitle: " + material.getTitle()
                + "\nSubject: " + material.getSubject().getName()
                + "\nKey Notes: " + material.getContentSummary();
        ByteArrayResource resource = new ByteArrayResource(content.getBytes(StandardCharsets.UTF_8));
        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"" + material.getFileName() + "\"")
                .contentType(MediaType.APPLICATION_PDF)
                .body(resource);
    }

    @GetMapping("/view/material/{id}")
    public ResponseEntity<Resource> viewMaterial(@PathVariable("id") Long id) {
        StudyMaterial material = studyMaterialService.getMaterialById(id).orElseThrow();

        File file = new File("src/main/resources/static/" + material.getFilePath());
        if (file.exists()) {
            return ResponseEntity.ok()
                    .header(HttpHeaders.CONTENT_DISPOSITION, "inline; filename=\"" + material.getFileName() + "\"")
                    .contentType(MediaType.APPLICATION_PDF)
                    .body(new FileSystemResource(file));
        }

        String content = "Smart Backlog Clearance Portal\nStudy Material: " + material.getTitle()
                + "\n\nDescription: " + material.getDescription()
                + "\n\nSummary & Revision Notes:\n" + material.getContentSummary();
        ByteArrayResource resource = new ByteArrayResource(content.getBytes(StandardCharsets.UTF_8));
        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_DISPOSITION, "inline; filename=\"" + material.getFileName() + ".txt\"")
                .contentType(MediaType.TEXT_PLAIN)
                .body(resource);
    }

    @GetMapping("/download/paper/{id}")
    public ResponseEntity<Resource> downloadPaper(@PathVariable("id") Long id) {
        PreviousYearPaper paper = previousYearPaperService.getPaperById(id).orElseThrow();
        previousYearPaperService.incrementDownloadCount(id);

        File file = new File("src/main/resources/static/" + paper.getFilePath());
        if (file.exists()) {
            return ResponseEntity.ok()
                    .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"" + paper.getFileName() + "\"")
                    .contentType(MediaType.APPLICATION_PDF)
                    .contentLength(file.length())
                    .body(new FileSystemResource(file));
        }

        String content = "%PDF-1.4\nSmart Backlog Clearance Portal - University Question Paper\nTitle: " + paper.getTitle()
                + "\nSubject: " + paper.getSubject().getName()
                + "\nExam Session: " + paper.getExamSession()
                + "\nPattern: " + paper.getExamPatternNotes();
        ByteArrayResource resource = new ByteArrayResource(content.getBytes(StandardCharsets.UTF_8));
        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"" + paper.getFileName() + "\"")
                .contentType(MediaType.APPLICATION_PDF)
                .body(resource);
    }

    @GetMapping("/view/paper/{id}")
    public ResponseEntity<Resource> viewPaper(@PathVariable("id") Long id) {
        PreviousYearPaper paper = previousYearPaperService.getPaperById(id).orElseThrow();

        File file = new File("src/main/resources/static/" + paper.getFilePath());
        if (file.exists()) {
            return ResponseEntity.ok()
                    .header(HttpHeaders.CONTENT_DISPOSITION, "inline; filename=\"" + paper.getFileName() + "\"")
                    .contentType(MediaType.APPLICATION_PDF)
                    .body(new FileSystemResource(file));
        }

        String content = "Smart Backlog Clearance Portal - Previous Year Question Paper\n"
                + "Paper: " + paper.getTitle() + "\n"
                + "Subject: " + paper.getSubject().getName() + " (" + paper.getSubject().getCode() + ")\n"
                + "Exam Session: " + paper.getExamSession() + "\n"
                + "Exam Pattern Notes:\n" + paper.getExamPatternNotes();
        ByteArrayResource resource = new ByteArrayResource(content.getBytes(StandardCharsets.UTF_8));
        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_DISPOSITION, "inline; filename=\"" + paper.getFileName() + ".txt\"")
                .contentType(MediaType.TEXT_PLAIN)
                .body(resource);
    }
}

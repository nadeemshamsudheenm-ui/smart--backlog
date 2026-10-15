package com.smartbacklog.service;

import com.smartbacklog.entity.PreviousYearPaper;
import com.smartbacklog.entity.Scheme;
import com.smartbacklog.entity.Semester;
import com.smartbacklog.entity.Subject;
import com.smartbacklog.repository.PreviousYearPaperRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
@Transactional
public class PreviousYearPaperService {

    private final PreviousYearPaperRepository previousYearPaperRepository;

    public PreviousYearPaperService(PreviousYearPaperRepository previousYearPaperRepository) {
        this.previousYearPaperRepository = previousYearPaperRepository;
    }

    public List<PreviousYearPaper> getAllPapers() {
        return previousYearPaperRepository.findAll();
    }

    public Optional<PreviousYearPaper> getPaperById(Long id) {
        return previousYearPaperRepository.findById(id);
    }

    public List<PreviousYearPaper> filterPapers(Semester semester, Scheme scheme, Subject subject) {
        if (subject != null && semester != null && scheme != null) {
            return previousYearPaperRepository.findBySubjectAndSemesterAndScheme(subject, semester, scheme);
        } else if (subject != null) {
            return previousYearPaperRepository.findBySubject(subject);
        } else if (semester != null && scheme != null) {
            return previousYearPaperRepository.findBySemesterAndScheme(semester, scheme);
        } else if (semester != null) {
            return previousYearPaperRepository.findBySemester(semester);
        } else if (scheme != null) {
            return previousYearPaperRepository.findByScheme(scheme);
        }
        return previousYearPaperRepository.findAll();
    }

    public List<PreviousYearPaper> getRecentPapers() {
        return previousYearPaperRepository.findTop10ByOrderByUploadedAtDesc();
    }

    public PreviousYearPaper savePaper(PreviousYearPaper paper) {
        return previousYearPaperRepository.save(paper);
    }

    public void incrementDownloadCount(Long id) {
        previousYearPaperRepository.findById(id).ifPresent(p -> {
            p.setDownloadCount(p.getDownloadCount() + 1);
            previousYearPaperRepository.save(p);
        });
    }

    public void deletePaper(Long id) {
        previousYearPaperRepository.deleteById(id);
    }

    public long getTotalPapersCount() {
        return previousYearPaperRepository.count();
    }
}

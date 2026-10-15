package com.smartbacklog.service;

import com.smartbacklog.entity.Scheme;
import com.smartbacklog.entity.Semester;
import com.smartbacklog.entity.StudyMaterial;
import com.smartbacklog.entity.Subject;
import com.smartbacklog.repository.StudyMaterialRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
@Transactional
public class StudyMaterialService {

    private final StudyMaterialRepository studyMaterialRepository;

    public StudyMaterialService(StudyMaterialRepository studyMaterialRepository) {
        this.studyMaterialRepository = studyMaterialRepository;
    }

    public List<StudyMaterial> getAllMaterials() {
        return studyMaterialRepository.findAll();
    }

    public Optional<StudyMaterial> getMaterialById(Long id) {
        return studyMaterialRepository.findById(id);
    }

    public List<StudyMaterial> filterMaterials(Semester semester, Scheme scheme, Subject subject) {
        if (subject != null && semester != null && scheme != null) {
            return studyMaterialRepository.findBySubjectAndSemesterAndScheme(subject, semester, scheme);
        } else if (subject != null) {
            return studyMaterialRepository.findBySubject(subject);
        } else if (semester != null && scheme != null) {
            return studyMaterialRepository.findBySemesterAndScheme(semester, scheme);
        } else if (semester != null) {
            return studyMaterialRepository.findBySemester(semester);
        } else if (scheme != null) {
            return studyMaterialRepository.findByScheme(scheme);
        }
        return studyMaterialRepository.findAll();
    }

    public List<StudyMaterial> getRecentMaterials() {
        return studyMaterialRepository.findTop10ByOrderByUploadedAtDesc();
    }

    public StudyMaterial saveMaterial(StudyMaterial material) {
        return studyMaterialRepository.save(material);
    }

    public void incrementDownloadCount(Long id) {
        studyMaterialRepository.findById(id).ifPresent(m -> {
            m.setDownloadCount(m.getDownloadCount() + 1);
            studyMaterialRepository.save(m);
        });
    }

    public void deleteMaterial(Long id) {
        studyMaterialRepository.deleteById(id);
    }

    public long getTotalMaterialsCount() {
        return studyMaterialRepository.count();
    }
}

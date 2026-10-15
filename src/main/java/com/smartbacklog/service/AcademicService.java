package com.smartbacklog.service;

import com.smartbacklog.entity.Scheme;
import com.smartbacklog.entity.Semester;
import com.smartbacklog.entity.Subject;
import com.smartbacklog.repository.SchemeRepository;
import com.smartbacklog.repository.SemesterRepository;
import com.smartbacklog.repository.SubjectRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
@Transactional
public class AcademicService {

    private final SchemeRepository schemeRepository;
    private final SemesterRepository semesterRepository;
    private final SubjectRepository subjectRepository;

    public AcademicService(SchemeRepository schemeRepository,
                           SemesterRepository semesterRepository,
                           SubjectRepository subjectRepository) {
        this.schemeRepository = schemeRepository;
        this.semesterRepository = semesterRepository;
        this.subjectRepository = subjectRepository;
    }

    // Schemes
    public List<Scheme> getAllSchemes() {
        return schemeRepository.findAll();
    }

    public List<Scheme> getActiveSchemes() {
        return schemeRepository.findByActiveTrue();
    }

    public Optional<Scheme> getSchemeById(Long id) {
        return schemeRepository.findById(id);
    }

    public Scheme saveScheme(Scheme scheme) {
        return schemeRepository.save(scheme);
    }

    // Semesters
    public List<Semester> getAllSemesters() {
        return semesterRepository.findAllByOrderBySemesterNumberAsc();
    }

    public Optional<Semester> getSemesterById(Long id) {
        return semesterRepository.findById(id);
    }

    public Optional<Semester> getSemesterByCode(String code) {
        return semesterRepository.findByCode(code);
    }

    // Subjects
    public List<Subject> getAllSubjects() {
        return subjectRepository.findAllByOrderByCodeAsc();
    }

    public Optional<Subject> getSubjectById(Long id) {
        return subjectRepository.findById(id);
    }

    public List<Subject> getSubjectsBySemester(Semester semester) {
        return subjectRepository.findBySemester(semester);
    }

    public List<Subject> getSubjectsByScheme(Scheme scheme) {
        return subjectRepository.findByScheme(scheme);
    }

    public List<Subject> getSubjectsBySemesterAndScheme(Semester semester, Scheme scheme) {
        if (semester != null && scheme != null) {
            return subjectRepository.findBySemesterAndScheme(semester, scheme);
        } else if (semester != null) {
            return subjectRepository.findBySemester(semester);
        } else if (scheme != null) {
            return subjectRepository.findByScheme(scheme);
        }
        return subjectRepository.findAllByOrderByCodeAsc();
    }

    public Subject saveSubject(Subject subject) {
        return subjectRepository.save(subject);
    }

    public void deleteSubject(Long id) {
        subjectRepository.deleteById(id);
    }

    public long getTotalSubjectsCount() {
        return subjectRepository.count();
    }
}

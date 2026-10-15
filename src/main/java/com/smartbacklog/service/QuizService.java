package com.smartbacklog.service;

import com.smartbacklog.entity.*;
import com.smartbacklog.repository.QuestionRepository;
import com.smartbacklog.repository.QuizAttemptRepository;
import com.smartbacklog.repository.StudentQuizAnswerRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.*;

@Service
@Transactional
public class QuizService {

    private final QuizAttemptRepository quizAttemptRepository;
    private final StudentQuizAnswerRepository studentQuizAnswerRepository;
    private final QuestionRepository questionRepository;
    private final NotificationService notificationService;

    public QuizService(QuizAttemptRepository quizAttemptRepository,
                       StudentQuizAnswerRepository studentQuizAnswerRepository,
                       QuestionRepository questionRepository,
                       NotificationService notificationService) {
        this.quizAttemptRepository = quizAttemptRepository;
        this.studentQuizAnswerRepository = studentQuizAnswerRepository;
        this.questionRepository = questionRepository;
        this.notificationService = notificationService;
    }

    public List<Question> getQuizQuestions(Subject subject, int maxQuestions) {
        List<Question> allQuestions = questionRepository.findBySubjectOrderByIdAsc(subject);
        if (allQuestions.isEmpty()) {
            return Collections.emptyList();
        }
        if (maxQuestions <= 0 || allQuestions.size() <= maxQuestions) {
            return allQuestions;
        }
        // Shuffle or pick first maxQuestions
        List<Question> selected = new ArrayList<>(allQuestions);
        Collections.shuffle(selected);
        return selected.subList(0, maxQuestions);
    }

    public QuizAttempt submitQuiz(StudentProfile student, Subject subject,
                                  Map<Long, String> selectedAnswers, int timeTakenSeconds) {
        List<Question> questions = new ArrayList<>();
        for (Long qId : selectedAnswers.keySet()) {
            questionRepository.findById(qId).ifPresent(questions::add);
        }

        if (questions.isEmpty()) {
            // If empty map, fetch subject questions
            questions = questionRepository.findBySubjectOrderByIdAsc(subject);
        }

        int total = questions.size();
        int correct = 0;
        int wrong = 0;

        List<StudentQuizAnswer> answersToSave = new ArrayList<>();

        for (Question q : questions) {
            String selected = selectedAnswers.get(q.getId());
            boolean isCorrect = selected != null && selected.trim().equalsIgnoreCase(q.getCorrectOption().trim());
            if (isCorrect) {
                correct++;
            } else {
                wrong++;
            }
            StudentQuizAnswer answer = new StudentQuizAnswer();
            answer.setQuestion(q);
            answer.setSelectedOption(selected != null ? selected.trim().toUpperCase() : null);
            answer.setCorrect(isCorrect);
            answersToSave.add(answer);
        }

        double percentage = total > 0 ? ((double) correct / total) * 100.0 : 0.0;
        percentage = Math.round(percentage * 10.0) / 10.0;
        boolean passed = percentage >= 50.0;

        QuizAttempt attempt = new QuizAttempt();
        attempt.setStudent(student);
        attempt.setSubject(subject);
        attempt.setTotalQuestions(total);
        attempt.setCorrectAnswers(correct);
        attempt.setWrongAnswers(wrong);
        attempt.setScorePercentage(percentage);
        attempt.setPassed(passed);
        attempt.setTimeTakenSeconds(timeTakenSeconds);
        attempt.setAttemptedAt(LocalDateTime.now());

        QuizAttempt savedAttempt = quizAttemptRepository.save(attempt);

        for (StudentQuizAnswer a : answersToSave) {
            a.setQuizAttempt(savedAttempt);
            studentQuizAnswerRepository.save(a);
        }
        savedAttempt.setAnswers(answersToSave);

        // Send notifications
        // 1. To student
        String studentMsg = String.format("You scored %.1f%% (%d/%d) in %s practice quiz. Status: %s.",
                percentage, correct, total, subject.getName(), passed ? "PASSED" : "NEEDS IMPROVEMENT");
        notificationService.createNotification(student.getUser(), null,
                "Quiz Completed: " + subject.getCode(), studentMsg, "QUIZ_RESULT",
                "/student/quiz/result/" + savedAttempt.getId());

        // 2. To tutor if assigned
        if (student.getAssignedTutor() != null) {
            String tutorMsg = String.format("Student %s (%s) scored %.1f%% in %s practice quiz.",
                    student.getUser().getFullName(), student.getRegisterNumber(), percentage, subject.getName());
            notificationService.createNotification(student.getAssignedTutor(), null,
                    "Student Quiz Result: " + student.getUser().getFullName(), tutorMsg, "QUIZ_RESULT",
                    "/tutor/student/" + student.getId());
        }

        // 3. To parent if linked
        if (student.getParentUser() != null) {
            String parentMsg = String.format("Your ward %s completed a practice quiz in %s with a score of %.1f%% (%s).",
                    student.getUser().getFullName(), subject.getName(), percentage, passed ? "Passed" : "Needs Revision");
            notificationService.createNotification(student.getParentUser(), null,
                    "Ward Performance Update", parentMsg, "QUIZ_RESULT",
                    "/parent/quiz/result/" + savedAttempt.getId());
        }

        return savedAttempt;
    }

    public Optional<QuizAttempt> getAttemptById(Long id) {
        return quizAttemptRepository.findById(id);
    }

    public List<StudentQuizAnswer> getAnswersByAttempt(QuizAttempt attempt) {
        return studentQuizAnswerRepository.findByQuizAttempt(attempt);
    }

    public List<QuizAttempt> getAttemptsByStudent(StudentProfile student) {
        return quizAttemptRepository.findByStudentOrderByAttemptedAtDesc(student);
    }

    public List<QuizAttempt> getAttemptsBySubject(Subject subject) {
        return quizAttemptRepository.findBySubjectOrderByAttemptedAtDesc(subject);
    }

    public List<QuizAttempt> getAllRecentAttempts() {
        return quizAttemptRepository.findTop10ByOrderByAttemptedAtDesc();
    }

    public long getTotalQuizAttemptsCount() {
        return quizAttemptRepository.count();
    }
}

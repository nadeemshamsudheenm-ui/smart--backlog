package com.smartbacklog.config;

import com.smartbacklog.entity.*;
import com.smartbacklog.repository.*;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.time.LocalDateTime;
import java.util.*;

@Component
public class DataInitializer implements CommandLineRunner {

    private final UserRepository userRepository;
    private final SchemeRepository schemeRepository;
    private final SemesterRepository semesterRepository;
    private final SubjectRepository subjectRepository;
    private final StudentProfileRepository studentProfileRepository;
    private final StudyMaterialRepository studyMaterialRepository;
    private final PreviousYearPaperRepository previousYearPaperRepository;
    private final QuestionRepository questionRepository;
    private final QuizAttemptRepository quizAttemptRepository;
    private final StudentQuizAnswerRepository studentQuizAnswerRepository;
    private final NotificationRepository notificationRepository;
    private final PasswordEncoder passwordEncoder;

    public DataInitializer(UserRepository userRepository,
                           SchemeRepository schemeRepository,
                           SemesterRepository semesterRepository,
                           SubjectRepository subjectRepository,
                           StudentProfileRepository studentProfileRepository,
                           StudyMaterialRepository studyMaterialRepository,
                           PreviousYearPaperRepository previousYearPaperRepository,
                           QuestionRepository questionRepository,
                           QuizAttemptRepository quizAttemptRepository,
                           StudentQuizAnswerRepository studentQuizAnswerRepository,
                           NotificationRepository notificationRepository,
                           PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.schemeRepository = schemeRepository;
        this.semesterRepository = semesterRepository;
        this.subjectRepository = subjectRepository;
        this.studentProfileRepository = studentProfileRepository;
        this.studyMaterialRepository = studyMaterialRepository;
        this.previousYearPaperRepository = previousYearPaperRepository;
        this.questionRepository = questionRepository;
        this.quizAttemptRepository = quizAttemptRepository;
        this.studentQuizAnswerRepository = studentQuizAnswerRepository;
        this.notificationRepository = notificationRepository;
        this.passwordEncoder = passwordEncoder;
    }

    @Override
    @Transactional
    public void run(String... args) throws Exception {
        if (schemeRepository.count() > 0) {
            return; // Data already initialized
        }

        System.out.println(">>> Initializing Smart Backlog Clearance Portal Database Seed Data...");

        // 1. Schemes
        Scheme scheme2019 = schemeRepository.save(new Scheme("2019 Scheme", "SCH-2019", "B.Tech Regulations 2019 Curriculum with Choice Based Credit System"));
        Scheme scheme2024 = schemeRepository.save(new Scheme("2024 Scheme", "SCH-2024", "B.Tech Regulations 2024 NEP-Aligned Dynamic Curriculum"));

        // 2. Semesters S1 - S8
        Semester s1 = semesterRepository.save(new Semester("Semester 1", "S1", 1));
        Semester s2 = semesterRepository.save(new Semester("Semester 2", "S2", 2));
        Semester s3 = semesterRepository.save(new Semester("Semester 3", "S3", 3));
        Semester s4 = semesterRepository.save(new Semester("Semester 4", "S4", 4));
        Semester s5 = semesterRepository.save(new Semester("Semester 5", "S5", 5));
        Semester s6 = semesterRepository.save(new Semester("Semester 6", "S6", 6));
        Semester s7 = semesterRepository.save(new Semester("Semester 7", "S7", 7));
        Semester s8 = semesterRepository.save(new Semester("Semester 8", "S8", 8));

        // 3. Subjects - 2019 Scheme
        Subject mat101 = subjectRepository.save(new Subject("MAT101", "Linear Algebra & Calculus", "Basic Sciences", s1, scheme2019, 4, "Eigenvalues, Matrix Diagonalization, Multivariable Calculus, Taylor Series"));
        Subject pht100 = subjectRepository.save(new Subject("PHT100", "Engineering Physics", "Basic Sciences", s1, scheme2019, 4, "Oscillations, Wave Optics, Quantum Mechanics, Laser Physics"));
        Subject est102 = subjectRepository.save(new Subject("EST102", "Programming in C", "Engineering Sciences", s2, scheme2019, 4, "Control structures, Pointers, Arrays, Structs, File I/O"));
        Subject cst201 = subjectRepository.save(new Subject("CST201", "Data Structures", "Computer Science and Engineering", s3, scheme2019, 4, "Linear and non-linear data structures, Sorting, Searching, Graphs, Trees"));
        Subject mat203 = subjectRepository.save(new Subject("MAT203", "Discrete Mathematical Structures", "Computer Science and Engineering", s3, scheme2019, 4, "Set theory, Relations, Graph theory, Combinatorics, Logic"));
        Subject cst202 = subjectRepository.save(new Subject("CST202", "Computer Organization & Architecture", "Computer Science and Engineering", s4, scheme2019, 4, "Processor design, Memory hierarchy, Pipelining, Instruction set architectures"));
        Subject cst204 = subjectRepository.save(new Subject("CST204", "Database Management Systems", "Computer Science and Engineering", s4, scheme2019, 4, "ER modeling, Relational algebra, SQL, Normalization, ACID transactions"));
        Subject cst301 = subjectRepository.save(new Subject("CST301", "Formal Languages & Automata Theory", "Computer Science and Engineering", s5, scheme2019, 4, "DFA, NFA, Regular expressions, CFG, Pushdown automata, Turing machines"));
        Subject cst303 = subjectRepository.save(new Subject("CST303", "Computer Networks", "Computer Science and Engineering", s5, scheme2019, 4, "OSI & TCP/IP stack, Routing protocols, Congestion control, Application protocols"));
        Subject cst306 = subjectRepository.save(new Subject("CST306", "Algorithm Analysis & Design", "Computer Science and Engineering", s6, scheme2019, 4, "Divide & conquer, Greedy method, Dynamic programming, NP-completeness"));

        // Subjects - 2024 Scheme
        Subject math101 = subjectRepository.save(new Subject("MATH101", "Matrices and Advanced Calculus", "Basic Sciences", s1, scheme2024, 4, "NEP 2024 Linear algebra, differential equations, Fourier transforms"));
        Subject cse101 = subjectRepository.save(new Subject("CSE101", "Problem Solving with Python", "Computer Science and Engineering", s2, scheme2024, 4, "Python idioms, functional programming, data handling"));
        Subject cse201 = subjectRepository.save(new Subject("CSE201", "Data Structures and Algorithms", "Computer Science and Engineering", s3, scheme2024, 4, "Modern algorithm design, balanced search trees, graph algorithms"));
        Subject cse202 = subjectRepository.save(new Subject("CSE202", "Operating Systems & Virtualization", "Computer Science and Engineering", s4, scheme2024, 4, "Kernel internals, thread scheduling, containerization concepts"));

        // 4. Users for all 5 roles
        // Admin
        User adminUser = userRepository.save(new User(
                "admin", passwordEncoder.encode("admin123"), "Dr. K. S. Radhakrishnan (Admin)",
                Role.ROLE_ADMIN, "admin@smartbacklog.edu", "+91 9447101010"));

        // HOD
        User hodUser = userRepository.save(new User(
                "hod_cs", passwordEncoder.encode("hod123"), "Prof. Anitha Thomas (HOD CSE)",
                Role.ROLE_HOD, "hod.cs@smartbacklog.edu", "+91 9447202020"));

        // Tutors
        User tutorRamesh = userRepository.save(new User(
                "tutor_ramesh", passwordEncoder.encode("tutor123"), "Prof. Ramesh Menon (Faculty Advisor)",
                Role.ROLE_TUTOR, "ramesh.menon@smartbacklog.edu", "+91 9447303030"));

        User tutorDeepa = userRepository.save(new User(
                "tutor_deepa", passwordEncoder.encode("tutor123"), "Dr. Deepa Nair (Supplementary Coordinator)",
                Role.ROLE_TUTOR, "deepa.nair@smartbacklog.edu", "+91 9447404040"));

        // Parents
        User parentKumar = userRepository.save(new User(
                "parent_kumar", passwordEncoder.encode("parent123"), "Mr. Suresh Kumar",
                Role.ROLE_PARENT, "suresh.kumar@gmail.com", "+91 9847123456"));

        User parentPriya = userRepository.save(new User(
                "parent_priya", passwordEncoder.encode("parent123"), "Mrs. Priya Varma",
                Role.ROLE_PARENT, "priya.varma@gmail.com", "+91 9847654321"));

        // Students
        // Student 1: Arun Suresh (2019 Scheme, S5, Backlogs in CST201, MAT101, CST204)
        User studentArunUser = userRepository.save(new User(
                "student_arun", passwordEncoder.encode("student123"), "Arun Suresh",
                Role.ROLE_STUDENT, "arun.suresh@student.edu", "+91 8590112233"));

        StudentProfile arunProfile = new StudentProfile(
                studentArunUser, "TVE21CS042", "Computer Science and Engineering", s5, scheme2019);
        arunProfile.setParentUser(parentKumar);
        arunProfile.setAssignedTutor(tutorRamesh);
        arunProfile.setSupplementarySubjects(new HashSet<>(Arrays.asList(cst201, mat101, cst204)));
        arunProfile.setNotes("Targeting June supplementary clearance for S1/S3 papers. Attending tutor revision sessions.");
        studentProfileRepository.save(arunProfile);

        // Student 2: Sneha Varma (2024 Scheme, S3, Backlogs in CSE201, CSE101)
        User studentSnehaUser = userRepository.save(new User(
                "student_sneha", passwordEncoder.encode("student123"), "Sneha Varma",
                Role.ROLE_STUDENT, "sneha.varma@student.edu", "+91 8590445566"));

        StudentProfile snehaProfile = new StudentProfile(
                studentSnehaUser, "TVE23CS089", "Computer Science and Engineering", s3, scheme2024);
        snehaProfile.setParentUser(parentPriya);
        snehaProfile.setAssignedTutor(tutorDeepa);
        snehaProfile.setSupplementarySubjects(new HashSet<>(Arrays.asList(cse201, cse101)));
        snehaProfile.setNotes("NEP 2024 Scheme candidate. Needs practice in Data Structures algorithm complexity.");
        studentProfileRepository.save(snehaProfile);

        // Student 3: Rahul Krishnan (2019 Scheme, S6, Backlogs in CST301, CST202)
        User studentRahulUser = userRepository.save(new User(
                "student_rahul", passwordEncoder.encode("student123"), "Rahul Krishnan",
                Role.ROLE_STUDENT, "rahul.k@student.edu", "+91 8590778899"));

        StudentProfile rahulProfile = new StudentProfile(
                studentRahulUser, "TVE21CS077", "Computer Science and Engineering", s6, scheme2019);
        rahulProfile.setAssignedTutor(tutorRamesh);
        rahulProfile.setSupplementarySubjects(new HashSet<>(Arrays.asList(cst301, cst202)));
        rahulProfile.setNotes("Cleared 2 backlogs last semester; preparing for Automata Theory.");
        studentProfileRepository.save(rahulProfile);

        // Prepare local uploads folder with realistic dummy PDF files
        createSampleFile("CST201_Data_Structures_High_Yield_Notes.pdf", "Smart Backlog Clearance Portal - Study Material: CST201 Data Structures Complete Module 1-5 Summary");
        createSampleFile("MAT101_Calculus_Formula_Sheet.pdf", "Smart Backlog Clearance Portal - Formula Sheet & Solved Derivations for MAT101 Calculus");
        createSampleFile("CST204_DBMS_Revision_Kit.pdf", "Smart Backlog Clearance Portal - CST204 Database Systems Normalization & SQL Queries Revision Sheet");
        createSampleFile("CSE201_NEP2024_Algorithm_Notes.pdf", "Smart Backlog Clearance Portal - CSE201 Advanced Data Structures & Algorithm Design");
        createSampleFile("KTU_CST201_Dec_2023_Question_Paper.pdf", "KTU University Supplementary Examination Dec 2023 - CST201 Data Structures Question Paper");
        createSampleFile("KTU_MAT101_Dec_2023_Question_Paper.pdf", "KTU University Supplementary Examination Dec 2023 - MAT101 Linear Algebra & Calculus Question Paper");
        createSampleFile("KTU_CST204_May_2024_Question_Paper.pdf", "KTU University Examination May 2024 - CST204 Database Management Systems Question Paper");

        // 5. Seed Study Materials
        studyMaterialRepository.save(new StudyMaterial(
                "CST201: Module 1 to 5 Comprehensive Supplementary Notes",
                "Complete handwritten & typed revision kit covering Stacks, Queues, Linked Lists, Binary Trees, AVL Trees, Hashing, and Graph Traversals.",
                cst201, s3, scheme2019, "PDF", "CST201_Data_Structures_High_Yield_Notes.pdf",
                "uploads/CST201_Data_Structures_High_Yield_Notes.pdf", 2458900L,
                "Key Topics Covered: Infix to Postfix conversion, AVL tree rotations (LL, RR, LR, RL), Dijkstra's algorithm, BFS vs DFS complexity.",
                tutorRamesh));

        studyMaterialRepository.save(new StudyMaterial(
                "MAT101: Linear Algebra & Calculus Formula Handout",
                "Essential formulas, Cayley-Hamilton theorem steps, Eigenvalues & Eigenvectors shortcut methods, Taylor & Maclaurin series.",
                mat101, s1, scheme2019, "PDF", "MAT101_Calculus_Formula_Sheet.pdf",
                "uploads/MAT101_Calculus_Formula_Sheet.pdf", 1823000L,
                "Key Formulas: Quadratic form reduction to canonical form, Lagrange multipliers, Double integration in polar coordinates.",
                adminUser));

        studyMaterialRepository.save(new StudyMaterial(
                "CST204: DBMS Normalization & SQL Practice Bank",
                "Step-by-step 1NF to BCNF normalization guide with previous 5 years solved university questions.",
                cst204, s4, scheme2019, "PDF", "CST204_DBMS_Revision_Kit.pdf",
                "uploads/CST204_DBMS_Revision_Kit.pdf", 3120000L,
                "Key Topics Covered: Functional dependency closure, Canonical cover, Lossless join decomposition, SQL Nested queries and Joins.",
                tutorDeepa));

        studyMaterialRepository.save(new StudyMaterial(
                "CSE201: NEP 2024 Advanced Algorithm & Data Structures Guide",
                "New 2024 Scheme specialized module covering Amortized analysis, Red-Black Trees, and Dynamic Programming.",
                cse201, s3, scheme2024, "PDF", "CSE201_NEP2024_Algorithm_Notes.pdf",
                "uploads/CSE201_NEP2024_Algorithm_Notes.pdf", 2890000L,
                "Key Topics Covered: Big-O proofs, Master theorem cases, 0/1 Knapsack, Longest Common Subsequence.",
                tutorDeepa));

        // 6. Seed Previous Year Question Papers (PYQs)
        previousYearPaperRepository.save(new PreviousYearPaper(
                "CST201 Data Structures - December 2023 Supplementary Paper",
                "December 2023 Supplementary", cst201, s3, scheme2019,
                "KTU_CST201_Dec_2023_Question_Paper.pdf", "uploads/KTU_CST201_Dec_2023_Question_Paper.pdf",
                1420000L, "Part A: 10 questions x 3 marks = 30 marks. Part B: 5 modules x 14 marks = 70 marks.",
                tutorRamesh));

        previousYearPaperRepository.save(new PreviousYearPaper(
                "MAT101 Linear Algebra & Calculus - December 2023 Supplementary Paper",
                "December 2023 Supplementary", mat101, s1, scheme2019,
                "KTU_MAT101_Dec_2023_Question_Paper.pdf", "uploads/KTU_MAT101_Dec_2023_Question_Paper.pdf",
                1560000L, "Includes detailed step-by-step solutions in appendix.",
                adminUser));

        previousYearPaperRepository.save(new PreviousYearPaper(
                "CST204 Database Management Systems - May 2024 Regular & Supplementary Paper",
                "May 2024 Regular/Supplementary", cst204, s4, scheme2019,
                "KTU_CST204_May_2024_Question_Paper.pdf", "uploads/KTU_CST204_May_2024_Question_Paper.pdf",
                1380000L, "Focuses heavily on Transaction ACID properties and BCNF decomposition proofs.",
                tutorDeepa));

        // 7. Seed Question Bank for MCQ Practice Quizzes
        // CST201 Questions
        questionRepository.save(new Question(
                cst201, scheme2019,
                "Which data structure is primarily used to evaluate an infix arithmetic expression or convert it to postfix form?",
                "Queue", "Stack", "Binary Search Tree", "Linked List",
                "B",
                "A Stack (LIFO) is the standard data structure used in Dijkstra's Shunting-yard algorithm for infix to postfix conversion and expression evaluation.",
                "EASY", "Dec 2023 Supplementary Exam Q1", tutorRamesh));

        questionRepository.save(new Question(
                cst201, scheme2019,
                "What is the worst-case time complexity of searching an element in an unbalanced Binary Search Tree (BST) with N nodes?",
                "O(1)", "O(log N)", "O(N)", "O(N log N)",
                "C",
                "In a skewed BST (where elements are inserted in ascending or descending order), the tree degenerates into a linked list, yielding worst-case time complexity of O(N).",
                "MEDIUM", "June 2023 Exam Q4", tutorRamesh));

        questionRepository.save(new Question(
                cst201, scheme2019,
                "In an AVL tree, what are the allowed balance factors for any node?",
                "0 only", "-1, 0, or +1", "-2 to +2", "Any positive integer",
                "B",
                "An AVL tree is a self-balancing binary search tree where the difference between heights of left and right subtrees (Balance Factor = Height(L) - Height(R)) is -1, 0, or +1.",
                "MEDIUM", "Dec 2022 Supplementary Exam Q6", tutorRamesh));

        questionRepository.save(new Question(
                cst201, scheme2019,
                "Which sorting algorithm has the best average-case performance with O(N log N) and is typically implemented in-place?",
                "Merge Sort", "Bubble Sort", "Quick Sort", "Selection Sort",
                "C",
                "Quick Sort has an average-case time complexity of O(N log N) and sorts in-place with minimal additional stack memory.",
                "MEDIUM", "KTU Model Paper 2023", tutorRamesh));

        questionRepository.save(new Question(
                cst201, scheme2019,
                "Which of the following graph traversal algorithms uses a Queue data structure to visit vertices level by level?",
                "Depth First Search (DFS)", "Breadth First Search (BFS)", "Topological Sort", "Prim's Algorithm",
                "B",
                "Breadth First Search (BFS) explores all neighboring vertices at the present depth before moving to the next level, inherently using a FIFO Queue.",
                "EASY", "Dec 2021 Supplementary Q2", tutorRamesh));

        questionRepository.save(new Question(
                cst201, scheme2019,
                "What is the maximum number of nodes in a full binary tree of height H (root is at height 0)?",
                "2^H - 1", "2^(H+1) - 1", "2^H", "2^(H-1)",
                "B",
                "A full binary tree of height H has sum of 2^i for i=0 to H, which evaluates to 2^(H+1) - 1 nodes.",
                "HARD", "University Exam Dec 2023 Module 3", tutorRamesh));

        // MAT101 Questions
        questionRepository.save(new Question(
                mat101, scheme2019,
                "If A is an n x n square matrix with eigenvalues λ1, λ2, ..., λn, what is the determinant of A?",
                "Sum of eigenvalues (λ1 + λ2 + ... + λn)",
                "Product of eigenvalues (λ1 * λ2 * ... * λn)",
                "Average of eigenvalues",
                "Trace of A",
                "B",
                "The determinant of any square matrix is equal to the product of all its eigenvalues, while the trace is the sum of its eigenvalues.",
                "EASY", "KTU Dec 2023 Part A Q1", adminUser));

        questionRepository.save(new Question(
                mat101, scheme2019,
                "According to the Cayley-Hamilton theorem, every square matrix satisfies which of the following?",
                "Its own characteristic equation",
                "Its inverse transpose",
                "The Cauchy-Riemann conditions",
                "Euler's theorem",
                "A",
                "The Cayley-Hamilton theorem states that substituting a square matrix A into its own characteristic polynomial P(λ) = det(A - λI) yields the zero matrix P(A) = 0.",
                "MEDIUM", "KTU June 2023 Part A Q3", adminUser));

        questionRepository.save(new Question(
                mat101, scheme2019,
                "What is the condition for a vector field F to be conservative?",
                "div F = 0",
                "curl F = 0",
                "grad F = 0",
                "laplacian F = 0",
                "B",
                "A continuous vector field F is conservative in an open simply-connected domain if and only if curl F = ∇ x F = 0.",
                "HARD", "KTU Dec 2022 Part B Q11", adminUser));

        // CST204 Questions (DBMS)
        questionRepository.save(new Question(
                cst204, scheme2019,
                "Which normal form eliminates partial functional dependencies on a composite primary key?",
                "First Normal Form (1NF)",
                "Second Normal Form (2NF)",
                "Third Normal Form (3NF)",
                "Boyce-Codd Normal Form (BCNF)",
                "B",
                "A relation is in 2NF if it is in 1NF and no non-prime attribute is functionally dependent on any proper subset of any candidate key (no partial dependencies).",
                "MEDIUM", "KTU May 2024 Q5", tutorDeepa));

        questionRepository.save(new Question(
                cst204, scheme2019,
                "Which ACID property guarantees that all operations in a database transaction are completed successfully, or none are applied?",
                "Atomicity", "Consistency", "Isolation", "Durability",
                "A",
                "Atomicity ensures that a transaction is treated as an indivisible unit: either all updates are committed, or in the case of failure, everything is rolled back.",
                "EASY", "KTU Dec 2023 Q2", tutorDeepa));

        questionRepository.save(new Question(
                cst204, scheme2019,
                "In relational algebra, which operator is equivalent to the SQL WHERE clause?",
                "Projection (π)", "Selection (σ)", "Cartesian Product (x)", "Join (⨝)",
                "B",
                "The Selection operator (represented by Greek letter sigma σ) selects tuples that satisfy a given predicate, identical to the SQL WHERE clause.",
                "EASY", "KTU May 2023 Q3", tutorDeepa));

        // CSE201 Questions (2024 Scheme)
        questionRepository.save(new Question(
                cse201, scheme2024,
                "In Python and modern algorithmic analysis, what is the amortized time complexity of appending an element to a dynamic array (like list.append)?",
                "O(1)", "O(log N)", "O(N)", "O(N^2)",
                "A",
                "Although occasional doubling of array capacity takes O(N), over a sequence of N insertions the total cost is O(N), making the amortized cost O(1) per operation.",
                "MEDIUM", "2024 Scheme Model Paper Q1", tutorDeepa));

        questionRepository.save(new Question(
                cse201, scheme2024,
                "Which data structure is optimal for implementing a Priority Queue with efficient extract-min and insert operations?",
                "Doubly Linked List", "Binary Heap", "Hash Table", "Circular Queue",
                "B",
                "A Binary Heap allows both insertion and extract-min/max in O(log N) time, making it the ideal underlying structure for Priority Queues.",
                "MEDIUM", "2024 Scheme Model Paper Q3", tutorDeepa));

        // 8. Seed Realistic Completed Quiz Attempts for Arun and Sneha
        // Attempt 1 for Arun in CST201 (Score: 83.3% - Passed)
        QuizAttempt attempt1 = new QuizAttempt(arunProfile, cst201, 6, 5, 1, 83.3, true, 420);
        attempt1.setAttemptedAt(LocalDateTime.now().minusDays(5));
        QuizAttempt savedAttempt1 = quizAttemptRepository.save(attempt1);

        List<Question> cst201List = questionRepository.findBySubjectOrderByIdAsc(cst201);
        for (int i = 0; i < cst201List.size(); i++) {
            Question q = cst201List.get(i);
            boolean isCorrect = i != 1; // question 2 wrong for demonstration
            String selected = isCorrect ? q.getCorrectOption() : "A";
            studentQuizAnswerRepository.save(new StudentQuizAnswer(savedAttempt1, q, selected, isCorrect));
        }

        // Attempt 2 for Arun in MAT101 (Score: 33.3% - Failed / Needs Improvement)
        QuizAttempt attempt2 = new QuizAttempt(arunProfile, mat101, 3, 1, 2, 33.3, false, 310);
        attempt2.setAttemptedAt(LocalDateTime.now().minusDays(2));
        QuizAttempt savedAttempt2 = quizAttemptRepository.save(attempt2);

        List<Question> mat101List = questionRepository.findBySubjectOrderByIdAsc(mat101);
        for (int i = 0; i < mat101List.size(); i++) {
            Question q = mat101List.get(i);
            boolean isCorrect = i == 0;
            String selected = isCorrect ? q.getCorrectOption() : "C";
            studentQuizAnswerRepository.save(new StudentQuizAnswer(savedAttempt2, q, selected, isCorrect));
        }

        // Attempt 3 for Arun in CST204 (Score: 100.0% - Passed)
        QuizAttempt attempt3 = new QuizAttempt(arunProfile, cst204, 3, 3, 0, 100.0, true, 260);
        attempt3.setAttemptedAt(LocalDateTime.now().minusHours(18));
        QuizAttempt savedAttempt3 = quizAttemptRepository.save(attempt3);

        List<Question> cst204List = questionRepository.findBySubjectOrderByIdAsc(cst204);
        for (Question q : cst204List) {
            studentQuizAnswerRepository.save(new StudentQuizAnswer(savedAttempt3, q, q.getCorrectOption(), true));
        }

        // Attempt for Sneha in CSE201 (Score: 100.0%)
        QuizAttempt attemptSneha = new QuizAttempt(snehaProfile, cse201, 2, 2, 0, 100.0, true, 180);
        attemptSneha.setAttemptedAt(LocalDateTime.now().minusDays(1));
        QuizAttempt savedAttemptSneha = quizAttemptRepository.save(attemptSneha);

        List<Question> cse201List = questionRepository.findBySubjectOrderByIdAsc(cse201);
        for (Question q : cse201List) {
            studentQuizAnswerRepository.save(new StudentQuizAnswer(savedAttemptSneha, q, q.getCorrectOption(), true));
        }

        // 9. Seed In-App Notifications
        notificationRepository.save(new Notification(
                null, Role.ROLE_STUDENT,
                "University Supplementary Exam Registration Notification",
                "Supplementary examination registration for 2019 and 2024 schemes is now open on the university portal. Last date without fine is the 30th of this month.",
                "EXAM_ALERT", "/student/dashboard"));

        notificationRepository.save(new Notification(
                null, null,
                "New High Yield Study Materials Uploaded",
                "Module-wise revision notes and previous-year question papers have been uploaded for CST201 (Data Structures) and CST204 (DBMS). Check the materials repository.",
                "MATERIAL_UPLOAD", "/student/materials"));

        notificationRepository.save(new Notification(
                studentArunUser, null,
                "Practice Quiz Result: CST204 Database Management Systems",
                "Congratulations Arun! You scored 100.0% (3/3) in the CST204 practice quiz.",
                "QUIZ_RESULT", "/student/quiz/result/" + savedAttempt3.getId()));

        notificationRepository.save(new Notification(
                parentKumar, null,
                "Ward Progress Alert: Arun Suresh",
                "Your ward Arun Suresh has completed practice tests in Data Structures (83.3%) and DBMS (100.0%). Review his full progress report.",
                "QUIZ_RESULT", "/parent/dashboard"));

        notificationRepository.save(new Notification(
                tutorRamesh, null,
                "Student Backlog Clearance Update: TVE21CS042",
                "Student Arun Suresh has attempted 3 practice tests with an overall pass rate of 66.7%. Weak subject identified: MAT101 Calculus.",
                "QUIZ_RESULT", "/tutor/dashboard"));

        notificationRepository.save(new Notification(
                hodUser, null,
                "Department Backlog Analytics Generated",
                "Monthly supplementary preparation status report is ready for review. 3 students currently tracked across 2019 and 2024 schemes.",
                "ANNOUNCEMENT", "/hod/reports"));

        System.out.println(">>> Seed Data successfully initialized for Smart Backlog Clearance Portal!");
    }

    private void createSampleFile(String fileName, String sampleContent) {
        try {
            File uploadDir = new File("src/main/resources/static/uploads");
            if (!uploadDir.exists()) {
                uploadDir.mkdirs();
            }
            File file = new File(uploadDir, fileName);
            if (!file.exists()) {
                try (FileWriter writer = new FileWriter(file)) {
                    writer.write("%PDF-1.4\n% Smart Backlog Clearance Portal Document\n");
                    writer.write("Document Title: " + fileName + "\n");
                    writer.write("Content: " + sampleContent + "\n");
                    writer.write("Timestamp: " + LocalDateTime.now() + "\n");
                    writer.write("%%EOF\n");
                }
            }
        } catch (IOException e) {
            System.err.println("Could not create sample file: " + e.getMessage());
        }
    }
}

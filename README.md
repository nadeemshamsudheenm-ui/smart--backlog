# Smart Backlog Clearance Portal 🎓

A complete, production-ready Java web application built using **Java 17, Spring Boot 3.2, Spring Security 6, Spring Data JPA, Hibernate, MySQL, Thymeleaf, Bootstrap 5, and Maven**.

The portal is designed specifically for B.Tech supplementary examination students to prepare for university examinations through organized study materials, previous-year question papers (PYQs), interactive MCQ practice quizzes with automatic score calculation, progress monitoring, and role-based multi-user workflows.

---

## 🌟 Key Features

1. **Role-Based Access Control (5 Roles)**:
   - **Admin**: Complete system management (users, academic schemes, semesters, subjects, materials, PYQs, questions, broadcast alerts).
   - **HOD (Head of Department)**: Academic monitoring, student classification roster, faculty advisor assignments, department-wide progress reports.
   - **Student**: Registration, profile management, scheme classification (2019 & 2024 schemes), study materials repository, previous year papers download, interactive timed MCQ practice quizzes, immediate scoring, question-by-question review with explanations, performance analytics charts.
   - **Parent**: Dedicated portal to monitor linked ward's backlog clearance status, mock quiz scores, and academic progress.
   - **Tutor / Faculty Advisor**: Manage assigned students, upload high-yield study notes, contribute practice questions, and send personalized study advice.

2. **Student Classification System**:
   - Supports both **2019 Scheme** and new **2024 Scheme** (NEP-aligned).
   - Configurable classification across Semesters (S1 to S8) and B.Tech supplementary subjects.

3. **Study Materials & University Previous-Year Papers (PYQ)**:
   - Upload and download PDF study notes, module summaries, and university question papers.
   - Fast filtering by subject, semester, and academic scheme.
   - Built-in secure preview and download counters.

4. **Interactive Practice MCQ Quiz System**:
   - Subject-wise quizzes based on university supplementary question patterns.
   - Optional live countdown timer with auto-submit on expiration.
   - Automatic score calculation, percentage evaluation, and pass/fail benchmark (50%).
   - Immediate feedback with correct/incorrect highlighting and academic solution explanations.
   - Attempt history and score tracking.

5. **Progress Monitoring & Visual Reports**:
   - Integrated **Chart.js** data visualizations.
   - Score improvement timeline (line chart).
   - Subject-wise mastery and proficiency (bar chart).
   - Department-wide scheme distribution (doughnut chart) and backlog density.
   - Automatic identification of weak subjects (< 50%) and strong subjects (>= 70%).

6. **In-App Notifications**:
   - Instant in-app alerts for quiz completion, test scores, exam announcements, and tutor advice with read/unread tracking.

---

## 🔑 Test Login Credentials (All 5 Roles)

| Role | Username | Password | Full Name / Description |
| :--- | :--- | :--- | :--- |
| **Admin** | `admin` | `admin123` | Dr. K. S. Radhakrishnan (System Admin) |
| **HOD (CSE)** | `hod_cs` | `hod123` | Prof. Anitha Thomas (Head of Department) |
| **Faculty Tutor** | `tutor_ramesh` | `tutor123` | Prof. Ramesh Menon (Senior Faculty Advisor) |
| **Faculty Tutor** | `tutor_deepa` | `tutor123` | Dr. Deepa Nair (Supplementary Coordinator) |
| **Student (2019 Scheme)** | `student_arun` | `student123` | Arun Suresh (Reg No: TVE21CS042, S5) |
| **Student (2024 Scheme)** | `student_sneha` | `student123` | Sneha Varma (Reg No: TVE23CS089, S3) |
| **Parent** | `parent_kumar` | `parent123` | Mr. Suresh Kumar (Linked to Arun Suresh) |

> 💡 **Tip**: On the login page (`/login`), you can click any of the **1-Click Quick Demo Chips** to autofill credentials instantly!

---

## 🛠️ Technology Stack

- **Backend**: Java 17, Spring Boot 3.2.5
- **Security**: Spring Security 6 with BCrypt password hashing & role-based authorization
- **Persistence**: Spring Data JPA, Hibernate 6
- **Database**: MySQL 8.0+ (with out-of-the-box H2 MySQL-compatibility fallback mode)
- **Frontend**: Thymeleaf, HTML5, CSS3, JavaScript (Vanilla ES6)
- **Styling**: Bootstrap 5.3, Bootstrap Icons 1.11, Custom Academic Theme
- **Data Visualization**: Chart.js 4.4
- **Build Tool**: Apache Maven 3.9+

---

## 🚀 How to Run the Application

### Option 1: Instant Out-of-the-Box Run (Default In-Memory MySQL-Compatible Mode)

The application is pre-configured to run immediately with zero external database setup required. It uses an in-memory MySQL-compatible database seeded with complete demo data (users, schemes, subjects, questions, attempts, materials, and notifications).

Open your terminal in the project directory and execute:

```bash
mvn spring-boot:run
```

Once started, open your browser and navigate to:
```
http://localhost:8080
```

---

### Option 2: Running with MySQL Database (`smart_backlog_portal`)

#### Step 1: Create the MySQL Database
Log in to your MySQL server (via MySQL Workbench, HeidiSQL, or MySQL CLI):

```sql
CREATE DATABASE smart_backlog_portal CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;
```

#### Step 2: (Optional) Import Complete SQL Schema & Seed Data
You can import the provided `smart_backlog_portal.sql` script into MySQL:

```bash
mysql -u root -p smart_backlog_portal < smart_backlog_portal.sql
```

#### Step 3: Configure Database Credentials
Edit `src/main/resources/application-mysql.properties` if your MySQL username or password differs:

```properties
spring.datasource.url=jdbc:mysql://localhost:3306/smart_backlog_portal?createDatabaseIfNotExist=true&useSSL=false&allowPublicKeyRetrieval=true&serverTimezone=UTC
spring.datasource.username=root
spring.datasource.password=root
```

#### Step 4: Run with the MySQL Profile

```bash
mvn spring-boot:run -Dspring-boot.run.profiles=mysql
```

---

## 📂 Project Structure

```
c:\Users\USER\Documents\Smartbacklog
├── pom.xml
├── README.md
├── smart_backlog_portal.sql             # Complete MySQL DDL & DML Schema + Seed Script
└── src
    └── main
        ├── java/com/smartbacklog
        │   ├── SmartBacklogApplication.java
        │   ├── config
        │   │   ├── SecurityConfig.java
        │   │   ├── CustomAuthenticationSuccessHandler.java
        │   │   └── DataInitializer.java
        │   ├── controller
        │   │   ├── AuthController.java
        │   │   ├── AdminController.java
        │   │   ├── HodController.java
        │   │   ├── StudentController.java
        │   │   ├── ParentController.java
        │   │   ├── TutorController.java
        │   │   ├── FileDownloadController.java
        │   │   └── NotificationController.java
        │   ├── dto
        │   │   ├── StudentRegistrationDto.java
        │   │   ├── StudentReportDto.java
        │   │   ├── DepartmentReportDto.java
        │   │   ├── QuestionDto.java
        │   │   ├── MaterialUploadDto.java
        │   │   └── PaperUploadDto.java
        │   ├── entity
        │   │   ├── Role.java
        │   │   ├── User.java
        │   │   ├── Scheme.java
        │   │   ├── Semester.java
        │   │   ├── Subject.java
        │   │   ├── StudentProfile.java
        │   │   ├── StudyMaterial.java
        │   │   ├── PreviousYearPaper.java
        │   │   ├── Question.java
        │   │   ├── QuizAttempt.java
        │   │   ├── StudentQuizAnswer.java
        │   │   └── Notification.java
        │   ├── repository
        │   │   └── [All Spring Data JPA Repositories]
        │   └── service
        │       ├── UserService.java
        │       ├── CustomUserDetailsService.java
        │       ├── AcademicService.java
        │       ├── StudentService.java
        │       ├── StudyMaterialService.java
        │       ├── PreviousYearPaperService.java
        │       ├── QuestionService.java
        │       ├── QuizService.java
        │       ├── NotificationService.java
        │       └── ReportService.java
        └── resources
            ├── application.properties          # Default out-of-the-box configuration
            ├── application-mysql.properties    # MySQL production configuration
            ├── static
            │   ├── css/style.css               # Modern responsive academic theme
            │   ├── js/main.js                  # Timer, animations, and chart helpers
            │   └── uploads/                    # Seeded study notes & past papers
            └── templates
                ├── auth/                       # Login & Student Registration
                ├── admin/                      # Admin Dashboard, Users, Subjects, Materials, PYQs, Questions
                ├── hod/                        # HOD Dashboard, Student Directory, Advisor Assign, Reports
                ├── student/                    # Student Dashboard, Profile, Materials, PYQs, Quiz, Results, Analytics
                ├── parent/                     # Parent Dashboard, Ward Progress
                ├── tutor/                      # Tutor Dashboard, Students, Notes Upload, Question Bank
                ├── common/                     # Notifications Feed
                ├── fragments/                  # Reusable Navbar, Sidebar, Header, Footer
                └── error/                      # 403, 404, 500 error pages
```

---

## 🧪 Testing Workflows

1. **Student Workflow**:
   - Log in as `student_arun` / `student123`.
   - View your registered backlogs (CST201, MAT101, CST204).
   - Go to **Practice MCQ Quizzes** -> Click **Start Practice Quiz** for CST201 or CST204.
   - Answer the multiple-choice questions while the countdown timer runs.
   - Submit quiz to see automatic score calculation, pass/fail status, and detailed explanations for every option.
   - Visit **My Progress & Analytics** to see your score trajectory on Chart.js.

2. **HOD Workflow**:
   - Log in as `hod_cs` / `hod123`.
   - View department overview, backlog statistics, and scheme breakdown (2019 vs 2024).
   - Go to **Supplementary Students** -> Filter by scheme or subject -> Click **Monitor** on any student to assign/reassign faculty advisors.
   - Visit **Progress Reports** to inspect department charts and send academic alerts.

3. **Parent Workflow**:
   - Log in as `parent_kumar` / `parent123`.
   - View linked ward Arun Suresh's backlogs and quiz performance.
   - Check weak subject alerts and review past quiz attempts.

4. **Tutor Workflow**:
   - Log in as `tutor_ramesh` / `tutor123`.
   - Monitor assigned students and send direct study guidance notifications.
   - Upload new revision notes or contribute new MCQs to the question bank.

5. **Admin Workflow**:
   - Log in as `admin` / `admin123`.
   - Create and manage user accounts across all roles.
   - Manage subjects, upload materials and PYQ papers, edit question bank items, and broadcast announcements.

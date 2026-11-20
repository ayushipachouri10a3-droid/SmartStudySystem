# Smart Study & Coding Assessment System (Core Java)

An academic desktop assessment platform engineered entirely in **Core Java** using **Java Swing** and **Java AWT**. Designed for Computer Science & Engineering (CSE) students.

---

## 1. Technology Requirements Compliance

- **Language:** Standard Java (JDK 8 / 11 / 17 / 21+)
- **GUI Framework:** Java Swing (`JFrame`, `JPanel`, `CardLayout`, `JProgressBar`, `JScrollPane`, `ButtonGroup`, `JRadioButton`, `JTextArea`, `JTextField`)
- **Graphics Engine:** Java AWT (`Graphics2D`, anti-aliased geometry, `RoundRectangle2D`)
- **Timing:** `javax.swing.Timer` (asynchronous, non-blocking)
- **Data Structures:** Java Collections Framework (`ArrayList`, `HashMap`, `LinkedHashMap`, `LinkedHashSet`, `ArrayDeque`, `Collections.shuffle()`)
- **Dependencies:** **Zero (0)** — 100% native standard JDK.

---

## 2. Compilation and Execution

Open command prompt or terminal in the project directory:

```bash
cd c:\Users\ayush\OneDrive\Documents\coding\SmartStudyProject
```

### Compile:
```bash
javac -encoding UTF-8 SmartStudySystem.java
```

### Run:
```bash
java SmartStudySystem
```

---

## 3. Implemented Modules

1. **Clean Top-Bar Desktop Layout (No Slide Window)**:
   - Unified top header bar featuring application logo, branding, and navigation buttons (`[Dashboard]`, `[Study Timer]`, `[Assessment]`, `[Results / Review]`, `[About Project]`).
   - Active section indicator and clean screen transitions.

2. **Dashboard**:
   - Live metrics: Total Question Pool (170), Tests Attempted, Personal Best, and Average Accuracy.
   - Quick action buttons to launch assessment or study timer.
   - Curriculum overview badges across all 17 CS disciplines.

3. **Study Timer**:
   - 25-minute Pomodoro default, preset buttons (15m, 25m, 45m, 60m), and custom input (1 to 180 min).
   - Large 68pt countdown clock, animated progress bar, status indicators (`Ready`, `Running`, `Paused`, `Completed`), and completion alert beep.

4. **170-Question Pool (17 CS Disciplines, 10 Questions Each)**:
   - Covers: Programming Fundamentals, Java, C, C++, Data Structures, Algorithms, OOP, DBMS, SQL, Operating Systems, Computer Networks, Computer Organization & Architecture, Software Engineering, Computer Graphics, Cybersecurity Fundamentals, Web Fundamentals, and Discrete Mathematics.
   - Includes MCQs, True/False, Fill in the Blanks, Short Answer, and Coding Logic.

5. **Random Question Generation Algorithm**:
   - Selects 10 questions per test.
   - Ensures representation from 10 distinct subjects in each test.
   - Maintains balanced question types (4 MCQs, 2 True/False, 1 Fill, 1 Short, 2 Coding Logic).
   - Rolling history queue of the last 40 questions to prevent repeating questions in consecutive attempts.
   - Shuffles MCQ options while preserving correct answers.

6. **CBT Assessment Screen**:
   - 10-Question Palette (1..10) allowing instant jump navigation with color-coded answer states (Green = Answered, Slate = Unanswered, Blue = Current).
   - 15-minute countdown timer with auto-submit on timeout.
   - High-contrast question card and dynamic response inputs.
   - Response persistence during forward/backward navigation.

7. **Results & Analytics**:
   - Score out of 10, accuracy percentage, and performance rating tiers.
   - Metric breakdown: Correct, Incorrect, Unanswered, Time Taken.
   - Question-by-question review with your answer, correct answer, status, and engineering explanations plus sample code.

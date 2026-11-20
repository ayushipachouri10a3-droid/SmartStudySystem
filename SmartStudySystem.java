import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import java.awt.*;
import java.awt.event.*;
import java.awt.geom.RoundRectangle2D;
import java.util.*;
import java.util.List;

/**
 * SMART STUDY & CODING ASSESSMENT SYSTEM (v2.0 Compact Pro)
 * Core Java Desktop Application (Swing & AWT Only)
 * Clean Top-Bar Desktop Layout (No Sliding Window / No Sidebar)
 */
public class SmartStudySystem extends JFrame {

        // --- Modern Theme Colors & Fonts ---
        public static final Color BG_DARK = new Color(15, 23, 42); // #0F172A Dark Slate Navy
        public static final Color CARD_BG = new Color(30, 41, 59); // #1E293B Card Background
        public static final Color CARD_BORDER = new Color(51, 65, 85); // #334155 Subtle Border
        public static final Color ACCENT_BLUE = new Color(37, 99, 235); // #2563EB Primary Blue
        public static final Color ACCENT_CYAN = new Color(6, 182, 212); // #06B6D4 Vibrant Cyan
        public static final Color COLOR_GREEN = new Color(16, 185, 129); // #10B981 Emerald
        public static final Color COLOR_RED = new Color(239, 68, 68); // #EF4444 Red
        public static final Color COLOR_AMBER = new Color(245, 158, 11); // #F59E0B Amber
        public static final Color TEXT_LIGHT = new Color(248, 250, 252); // #F8FAFC Text
        public static final Color TEXT_MUTED = new Color(148, 163, 184); // #94A3B8 Muted Text

        public static final Font FONT_TITLE = new Font("Segoe UI", Font.BOLD, 20);
        public static final Font FONT_HEADING = new Font("Segoe UI", Font.BOLD, 16);
        public static final Font FONT_BODY = new Font("Segoe UI", Font.PLAIN, 13);
        public static final Font FONT_BODY_BOLD = new Font("Segoe UI", Font.BOLD, 13);
        public static final Font FONT_SMALL = new Font("Segoe UI", Font.PLAIN, 11);
        public static final Font FONT_MONO = new Font("Consolas", Font.PLAIN, 13);

        // --- Core Managers & State ---
        private final CardLayout cardLayout = new CardLayout();
        private final JPanel mainPanel = new JPanel(cardLayout);
        private final Map<String, JButton> topNavButtons = new LinkedHashMap<>();

        private final QuestionBank questionBank = new QuestionBank();
        private final AssessmentManager assessmentManager = new AssessmentManager(questionBank);

        // Session Statistics
        private int totalTests = 0;
        private int highestScore = 0;
        private double cumulativeScore = 0;

        // View References
        private JPanel dashboardView, studyView, assessmentView, resultsView, aboutView;

        // Study Timer State
        private int studyTotalSec = 25 * 60;
        private int studyRemainSec = 25 * 60;
        private javax.swing.Timer studyTimer;
        private JLabel studyClockLabel, studyStatusLabel;
        private JProgressBar studyProgressBar;
        private JTextField customMinsField;
        private JButton studyStartBtn, studyPauseBtn, studyResumeBtn;
        private int completedSessions = 0;

        // Assessment State
        private int examIndex = 0;
        private int examRemainSec = 15 * 60;
        private javax.swing.Timer examTimer;
        private JLabel examTimerLabel, examCounterLabel, examSubjectLabel, examTypeLabel, examIdLabel;
        private JProgressBar examProgressBar;
        private JTextArea examQuestionArea;
        private JPanel examAnswersPanel;
        private ButtonGroup examRadioGroup;
        private final List<JButton> paletteButtons = new ArrayList<>();
        private JButton examPrevBtn, examNextBtn;

        // Results State
        private JLabel resScoreLabel, resTierLabel, resCorrectLabel, resIncorrectLabel, resUnansweredLabel,
                        resTimeLabel;
        private JPanel resReviewContainer;

        // Dashboard State
        private JLabel dashTestsLabel, dashHighLabel, dashAvgLabel;

        public SmartStudySystem() {
                super("Smart Study & Coding Assessment System (Core Java)");
                setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
                setSize(1080, 720);
                setMinimumSize(new Dimension(960, 640));
                setLocationRelativeTo(null);
                getContentPane().setBackground(BG_DARK);

                initViews();
                buildDesktopLayout();

                // Start initial assessment so questions are ready immediately
                assessmentManager.startNewTest();
                loadExamQuestion();

                showView("dashboard");
        }

        // ==========================================
        // TOP NAVIGATION (NO SLIDING WINDOW)
        // ==========================================
        private void buildDesktopLayout() {
                setLayout(new BorderLayout());

                // Top Header Bar
                JPanel topBar = new JPanel(new BorderLayout());
                topBar.setBackground(new Color(11, 17, 32));
                topBar.setBorder(BorderFactory.createCompoundBorder(
                                BorderFactory.createMatteBorder(0, 0, 1, 0, CARD_BORDER),
                                new EmptyBorder(12, 24, 12, 24)));

                // Brand Title on Left
                JPanel brand = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 0));
                brand.setOpaque(false);
                JLabel logo = new JLabel("SS");
                logo.setFont(new Font("Segoe UI", Font.BOLD, 15));
                logo.setForeground(Color.WHITE);
                JPanel logoBox = createRoundedBox(8, ACCENT_BLUE, null);
                logoBox.setPreferredSize(new Dimension(34, 34));
                logoBox.add(logo);

                JLabel brandTitle = new JLabel("SmartStudy System");
                brandTitle.setFont(new Font("Segoe UI", Font.BOLD, 17));
                brandTitle.setForeground(TEXT_LIGHT);

                JLabel brandSub = new JLabel("  |  Assessment & Focus Engine");
                brandSub.setFont(FONT_SMALL);
                brandSub.setForeground(ACCENT_CYAN);

                brand.add(logoBox);
                brand.add(brandTitle);
                brand.add(brandSub);
                topBar.add(brand, BorderLayout.WEST);

                // Top Navigation Buttons on Right
                JPanel nav = new JPanel(new FlowLayout(FlowLayout.RIGHT, 8, 0));
                nav.setOpaque(false);
                addTopNavBtn(nav, "dashboard", "Dashboard");
                addTopNavBtn(nav, "study", "Study Timer");
                addTopNavBtn(nav, "assessment", "Assessment");
                addTopNavBtn(nav, "results", "Results / Review");
                addTopNavBtn(nav, "about", "About Project");
                topBar.add(nav, BorderLayout.EAST);

                add(topBar, BorderLayout.NORTH);
                add(mainPanel, BorderLayout.CENTER);
        }

        private void addTopNavBtn(JPanel container, String cardName, String title) {
                JButton btn = new JButton(title);
                btn.setFont(FONT_BODY_BOLD);
                btn.setFocusPainted(false);
                btn.setContentAreaFilled(false);
                btn.setCursor(new Cursor(Cursor.HAND_CURSOR));
                btn.setBorder(new EmptyBorder(8, 14, 8, 14));
                btn.setForeground(TEXT_MUTED);

                btn.addActionListener(e -> showView(cardName));
                topNavButtons.put(cardName, btn);
                container.add(btn);
        }

        public void showView(String cardName) {
                // Highlight active top button
                for (Map.Entry<String, JButton> entry : topNavButtons.entrySet()) {
                        boolean active = entry.getKey().equals(cardName);
                        JButton b = entry.getValue();
                        b.setForeground(active ? ACCENT_CYAN : TEXT_MUTED);
                        b.setBorder(active ? BorderFactory.createMatteBorder(0, 0, 2, 0, ACCENT_CYAN)
                                        : new EmptyBorder(8, 14, 8, 14));
                }

                if ("dashboard".equals(cardName)) {
                        dashTestsLabel.setText(String.valueOf(totalTests));
                        dashHighLabel.setText(highestScore + " / 10");
                        double avg = totalTests == 0 ? 0.0 : (cumulativeScore / totalTests) * 10;
                        dashAvgLabel.setText(String.format("%.1f%%", avg));
                } else if ("assessment".equals(cardName)) {
                        if (assessmentManager.getCurrentSession() == null) {
                                assessmentManager.startNewTest();
                        }
                        loadExamQuestion();
                } else if ("results".equals(cardName)) {
                        AssessmentSession cur = assessmentManager.getCurrentSession();
                        if (cur != null)
                                displayResults(cur);
                }

                cardLayout.show(mainPanel, cardName);
        }

        private void initViews() {
                dashboardView = createDashboard();
                studyView = createStudyTimer();
                assessmentView = createAssessment();
                resultsView = createResults();
                aboutView = createAbout();

                mainPanel.add(dashboardView, "dashboard");
                mainPanel.add(studyView, "study");
                mainPanel.add(assessmentView, "assessment");
                mainPanel.add(resultsView, "results");
                mainPanel.add(aboutView, "about");
        }

        // ==========================================
        // 1. DASHBOARD VIEW
        // ==========================================
        private JPanel createDashboard() {
                JPanel root = new JPanel(new BorderLayout());
                root.setBackground(BG_DARK);
                root.setBorder(new EmptyBorder(20, 28, 20, 28));

                JPanel content = new JPanel();
                content.setLayout(new BoxLayout(content, BoxLayout.Y_AXIS));
                content.setOpaque(false);

                // Hero Card
                JPanel hero = createRoundedBox(12, CARD_BG, CARD_BORDER);
                hero.setLayout(new BorderLayout());
                hero.setBorder(new EmptyBorder(20, 24, 20, 24));
                hero.setMaximumSize(new Dimension(Integer.MAX_VALUE, 140));

                JPanel heroText = new JPanel();
                heroText.setLayout(new BoxLayout(heroText, BoxLayout.Y_AXIS));
                heroText.setOpaque(false);

                JLabel hTag = new JLabel("ACADEMIC ASSESSMENT PLATFORM");
                hTag.setFont(new Font("Segoe UI", Font.BOLD, 11));
                hTag.setForeground(ACCENT_CYAN);

                JLabel hTitle = new JLabel("Smart Study & Coding Assessment System");
                hTitle.setFont(new Font("Segoe UI", Font.BOLD, 22));
                hTitle.setForeground(TEXT_LIGHT);

                JLabel hDesc = new JLabel(
                                "Practice with 170+ categorized CS questions across 17 subjects, track focus with Pomodoro timer, and assess performance.");
                hDesc.setFont(FONT_BODY);
                hDesc.setForeground(TEXT_MUTED);

                heroText.add(hTag);
                heroText.add(Box.createVerticalStrut(4));
                heroText.add(hTitle);
                heroText.add(Box.createVerticalStrut(6));
                heroText.add(hDesc);

                JPanel heroActions = new JPanel(new FlowLayout(FlowLayout.RIGHT, 12, 10));
                heroActions.setOpaque(false);
                JButton startExam = createButton("Start Assessment", ACCENT_BLUE, Color.WHITE);
                startExam.addActionListener(e -> {
                        assessmentManager.startNewTest();
                        examIndex = 0;
                        examRemainSec = 15 * 60;
                        showView("assessment");
                });
                JButton startStudy = createButton("Study Timer", CARD_BG, TEXT_LIGHT);
                startStudy.setBorder(BorderFactory.createLineBorder(CARD_BORDER, 1));
                startStudy.addActionListener(e -> showView("study"));

                heroActions.add(startExam);
                heroActions.add(startStudy);

                hero.add(heroText, BorderLayout.CENTER);
                hero.add(heroActions, BorderLayout.SOUTH);
                content.add(hero);
                content.add(Box.createVerticalStrut(16));

                // Metric Cards Grid
                JPanel metrics = new JPanel(new GridLayout(1, 4, 14, 0));
                metrics.setOpaque(false);
                metrics.setMaximumSize(new Dimension(Integer.MAX_VALUE, 85));

                dashTestsLabel = new JLabel("0");
                dashTestsLabel.setFont(new Font("Segoe UI", Font.BOLD, 22));
                dashTestsLabel.setForeground(TEXT_LIGHT);

                dashHighLabel = new JLabel("0 / 10");
                dashHighLabel.setFont(new Font("Segoe UI", Font.BOLD, 22));
                dashHighLabel.setForeground(COLOR_GREEN);

                dashAvgLabel = new JLabel("0.0%");
                dashAvgLabel.setFont(new Font("Segoe UI", Font.BOLD, 22));
                dashAvgLabel.setForeground(COLOR_AMBER);

                JLabel poolLabel = new JLabel("170");
                poolLabel.setFont(new Font("Segoe UI", Font.BOLD, 22));
                poolLabel.setForeground(ACCENT_CYAN);

                metrics.add(createMiniStat("QUESTION POOL", poolLabel, "17 CS Disciplines"));
                metrics.add(createMiniStat("TESTS ATTEMPTED", dashTestsLabel, "Session Total"));
                metrics.add(createMiniStat("HIGHEST SCORE", dashHighLabel, "Personal Best"));
                metrics.add(createMiniStat("AVERAGE ACCURACY", dashAvgLabel, "Overall Record"));

                content.add(metrics);
                content.add(Box.createVerticalStrut(16));

                // Two Column Section: Disciplines & Instructions
                JPanel twoCol = new JPanel(new GridLayout(1, 2, 16, 0));
                twoCol.setOpaque(false);

                // Subject Badges Box
                JPanel subCard = createRoundedBox(10, CARD_BG, CARD_BORDER);
                subCard.setLayout(new BorderLayout());
                subCard.setBorder(new EmptyBorder(16, 18, 16, 18));
                JLabel subHead = new JLabel("Curriculum & Subjects Covered");
                subHead.setFont(FONT_HEADING);
                subHead.setForeground(TEXT_LIGHT);
                subCard.add(subHead, BorderLayout.NORTH);

                JPanel badges = new JPanel(new FlowLayout(FlowLayout.LEFT, 6, 6));
                badges.setOpaque(false);
                for (String s : questionBank.getSubjects()) {
                        JPanel b = createRoundedBox(6, BG_DARK, CARD_BORDER);
                        b.setBorder(new EmptyBorder(4, 8, 4, 8));
                        JLabel l = new JLabel(s);
                        l.setFont(FONT_SMALL);
                        l.setForeground(TEXT_LIGHT);
                        b.add(l);
                        badges.add(b);
                }
                subCard.add(badges, BorderLayout.CENTER);

                // Exam Guidelines Box
                JPanel rulesCard = createRoundedBox(10, CARD_BG, CARD_BORDER);
                rulesCard.setLayout(new BoxLayout(rulesCard, BoxLayout.Y_AXIS));
                rulesCard.setBorder(new EmptyBorder(16, 18, 16, 18));
                JLabel rHead = new JLabel("Assessment Guidelines");
                rHead.setFont(FONT_HEADING);
                rHead.setForeground(TEXT_LIGHT);
                rulesCard.add(rHead);
                rulesCard.add(Box.createVerticalStrut(10));
                rulesCard.add(createBullet("• 10 questions randomized per attempt across 10 subjects."));
                rulesCard.add(createBullet("• Balanced mix of MCQ, True/False, Fill, Short, and Coding."));
                rulesCard.add(createBullet("• 15-minute countdown clock with auto-submission on timeout."));
                rulesCard.add(createBullet("• Question Palette (1..10) allows direct jump navigation."));
                rulesCard.add(createBullet("• Detailed technical review & sample solutions provided post-test."));

                twoCol.add(subCard);
                twoCol.add(rulesCard);
                content.add(twoCol);

                JScrollPane sp = new JScrollPane(content);
                sp.setBorder(null);
                sp.setOpaque(false);
                sp.getViewport().setOpaque(false);
                root.add(sp, BorderLayout.CENTER);
                return root;
        }

        private JPanel createMiniStat(String title, JLabel val, String sub) {
                JPanel p = createRoundedBox(10, CARD_BG, CARD_BORDER);
                p.setLayout(new BoxLayout(p, BoxLayout.Y_AXIS));
                p.setBorder(new EmptyBorder(10, 14, 10, 14));
                JLabel t = new JLabel(title);
                t.setFont(new Font("Segoe UI", Font.BOLD, 10));
                t.setForeground(TEXT_MUTED);
                JLabel s = new JLabel(sub);
                s.setFont(FONT_SMALL);
                s.setForeground(TEXT_MUTED);
                p.add(t);
                p.add(Box.createVerticalStrut(2));
                p.add(val);
                p.add(Box.createVerticalStrut(2));
                p.add(s);
                return p;
        }

        private JLabel createBullet(String text) {
                JLabel l = new JLabel(text);
                l.setFont(FONT_BODY);
                l.setForeground(TEXT_MUTED);
                l.setBorder(new EmptyBorder(2, 0, 4, 0));
                return l;
        }

        // ==========================================
        // 2. STUDY TIMER VIEW
        // ==========================================
        private JPanel createStudyTimer() {
                JPanel root = new JPanel(new BorderLayout());
                root.setBackground(BG_DARK);
                root.setBorder(new EmptyBorder(20, 28, 20, 28));

                JPanel container = new JPanel();
                container.setLayout(new BoxLayout(container, BoxLayout.Y_AXIS));
                container.setOpaque(false);

                JLabel title = new JLabel("Study Focus Session Timer");
                title.setFont(FONT_TITLE);
                title.setForeground(TEXT_LIGHT);
                title.setAlignmentX(Component.CENTER_ALIGNMENT);

                JLabel subtitle = new JLabel("Optimize concentration using customizable interval sessions.");
                subtitle.setFont(FONT_BODY);
                subtitle.setForeground(TEXT_MUTED);
                subtitle.setAlignmentX(Component.CENTER_ALIGNMENT);

                container.add(title);
                container.add(Box.createVerticalStrut(4));
                container.add(subtitle);
                container.add(Box.createVerticalStrut(20));

                // Clock Display Card
                JPanel clockCard = createRoundedBox(16, CARD_BG, CARD_BORDER);
                clockCard.setLayout(new BoxLayout(clockCard, BoxLayout.Y_AXIS));
                clockCard.setBorder(new EmptyBorder(24, 40, 24, 40));
                clockCard.setMaximumSize(new Dimension(500, 240));
                clockCard.setAlignmentX(Component.CENTER_ALIGNMENT);

                studyStatusLabel = new JLabel("● READY TO FOCUS");
                studyStatusLabel.setFont(new Font("Segoe UI", Font.BOLD, 12));
                studyStatusLabel.setForeground(ACCENT_CYAN);
                studyStatusLabel.setAlignmentX(Component.CENTER_ALIGNMENT);

                studyClockLabel = new JLabel("25:00");
                studyClockLabel.setFont(new Font("Segoe UI", Font.BOLD, 68));
                studyClockLabel.setForeground(TEXT_LIGHT);
                studyClockLabel.setAlignmentX(Component.CENTER_ALIGNMENT);

                studyProgressBar = new JProgressBar(0, 100);
                studyProgressBar.setValue(0);
                studyProgressBar.setPreferredSize(new Dimension(380, 6));
                studyProgressBar.setMaximumSize(new Dimension(380, 6));
                studyProgressBar.setForeground(ACCENT_CYAN);
                studyProgressBar.setBackground(CARD_BORDER);
                studyProgressBar.setBorderPainted(false);
                studyProgressBar.setAlignmentX(Component.CENTER_ALIGNMENT);

                clockCard.add(studyStatusLabel);
                clockCard.add(Box.createVerticalStrut(8));
                clockCard.add(studyClockLabel);
                clockCard.add(Box.createVerticalStrut(10));
                clockCard.add(studyProgressBar);

                container.add(clockCard);
                container.add(Box.createVerticalStrut(18));

                // Quick Presets
                JPanel presets = new JPanel(new FlowLayout(FlowLayout.CENTER, 10, 0));
                presets.setOpaque(false);
                addPresetBtn(presets, "15 Min Sprint", 15);
                addPresetBtn(presets, "25 Min Pomodoro", 25);
                addPresetBtn(presets, "45 Min Deep Work", 45);
                addPresetBtn(presets, "60 Min Lecture", 60);
                container.add(presets);
                container.add(Box.createVerticalStrut(14));

                // Custom Duration Input
                JPanel customRow = new JPanel(new FlowLayout(FlowLayout.CENTER, 10, 0));
                customRow.setOpaque(false);
                JLabel cLabel = new JLabel("Custom Duration (1 - 180 min):");
                cLabel.setFont(FONT_BODY);
                cLabel.setForeground(TEXT_LIGHT);

                customMinsField = new JTextField("25", 4);
                customMinsField.setFont(FONT_BODY_BOLD);
                customMinsField.setHorizontalAlignment(JTextField.CENTER);
                customMinsField.setBackground(CARD_BG);
                customMinsField.setForeground(Color.WHITE);
                customMinsField.setCaretColor(Color.WHITE);
                customMinsField.setBorder(BorderFactory.createCompoundBorder(
                                BorderFactory.createLineBorder(CARD_BORDER, 1),
                                new EmptyBorder(6, 8, 6, 8)));

                JButton setBtn = createButton("Set Time", CARD_BG, TEXT_LIGHT);
                setBtn.setBorder(BorderFactory.createLineBorder(CARD_BORDER, 1));
                setBtn.addActionListener(e -> applyCustomDuration());

                customRow.add(cLabel);
                customRow.add(customMinsField);
                customRow.add(setBtn);
                container.add(customRow);
                container.add(Box.createVerticalStrut(18));

                // Control Buttons
                JPanel controls = new JPanel(new FlowLayout(FlowLayout.CENTER, 12, 0));
                controls.setOpaque(false);

                studyStartBtn = createButton("Start Session", ACCENT_BLUE, Color.WHITE);
                studyPauseBtn = createButton("Pause", CARD_BG, TEXT_LIGHT);
                studyPauseBtn.setBorder(BorderFactory.createLineBorder(CARD_BORDER, 1));
                studyPauseBtn.setEnabled(false);
                studyResumeBtn = createButton("Resume", ACCENT_BLUE, Color.WHITE);
                studyResumeBtn.setEnabled(false);
                JButton studyResetBtn = createButton("Reset", CARD_BG, TEXT_LIGHT);
                studyResetBtn.setBorder(BorderFactory.createLineBorder(CARD_BORDER, 1));

                studyTimer = new javax.swing.Timer(1000, e -> onStudyTick());

                studyStartBtn.addActionListener(e -> {
                        studyTimer.start();
                        studyStatusLabel.setText("● SESSION IN PROGRESS");
                        studyStatusLabel.setForeground(COLOR_GREEN);
                        studyStartBtn.setEnabled(false);
                        studyPauseBtn.setEnabled(true);
                        studyResumeBtn.setEnabled(false);
                });

                studyPauseBtn.addActionListener(e -> {
                        studyTimer.stop();
                        studyStatusLabel.setText("● SESSION PAUSED");
                        studyStatusLabel.setForeground(COLOR_AMBER);
                        studyPauseBtn.setEnabled(false);
                        studyResumeBtn.setEnabled(true);
                });

                studyResumeBtn.addActionListener(e -> {
                        studyTimer.start();
                        studyStatusLabel.setText("● SESSION IN PROGRESS");
                        studyStatusLabel.setForeground(COLOR_GREEN);
                        studyPauseBtn.setEnabled(true);
                        studyResumeBtn.setEnabled(false);
                });

                studyResetBtn.addActionListener(e -> {
                        studyTimer.stop();
                        studyRemainSec = studyTotalSec;
                        updateStudyClock();
                        studyStatusLabel.setText("● READY TO FOCUS");
                        studyStatusLabel.setForeground(ACCENT_CYAN);
                        studyStartBtn.setEnabled(true);
                        studyPauseBtn.setEnabled(false);
                        studyResumeBtn.setEnabled(false);
                        studyProgressBar.setValue(0);
                });

                controls.add(studyStartBtn);
                controls.add(studyPauseBtn);
                controls.add(studyResumeBtn);
                controls.add(studyResetBtn);
                container.add(controls);

                root.add(container, BorderLayout.CENTER);
                return root;
        }

        private void addPresetBtn(JPanel p, String text, int mins) {
                JButton b = createButton(text, CARD_BG, TEXT_LIGHT);
                b.setBorder(BorderFactory.createLineBorder(CARD_BORDER, 1));
                b.setFont(FONT_SMALL);
                b.addActionListener(e -> {
                        customMinsField.setText(String.valueOf(mins));
                        studyTimer.stop();
                        studyTotalSec = mins * 60;
                        studyRemainSec = studyTotalSec;
                        updateStudyClock();
                        studyStatusLabel.setText("● READY TO FOCUS");
                        studyStatusLabel.setForeground(ACCENT_CYAN);
                        studyStartBtn.setEnabled(true);
                        studyPauseBtn.setEnabled(false);
                        studyResumeBtn.setEnabled(false);
                        studyProgressBar.setValue(0);
                });
                p.add(b);
        }

        private void applyCustomDuration() {
                try {
                        int m = Integer.parseInt(customMinsField.getText().trim());
                        if (m < 1 || m > 180) {
                                JOptionPane.showMessageDialog(this, "Enter minutes between 1 and 180.");
                                return;
                        }
                        studyTimer.stop();
                        studyTotalSec = m * 60;
                        studyRemainSec = studyTotalSec;
                        updateStudyClock();
                        studyStatusLabel.setText("● READY TO FOCUS");
                        studyStatusLabel.setForeground(ACCENT_CYAN);
                        studyStartBtn.setEnabled(true);
                        studyPauseBtn.setEnabled(false);
                        studyResumeBtn.setEnabled(false);
                        studyProgressBar.setValue(0);
                } catch (NumberFormatException ex) {
                        JOptionPane.showMessageDialog(this, "Please enter a valid number.");
                }
        }

        private void onStudyTick() {
                if (studyRemainSec > 0) {
                        studyRemainSec--;
                        updateStudyClock();
                        int prog = (int) (((double) (studyTotalSec - studyRemainSec) / studyTotalSec) * 100);
                        studyProgressBar.setValue(prog);
                } else {
                        studyTimer.stop();
                        studyStatusLabel.setText("● COMPLETED!");
                        studyStatusLabel.setForeground(COLOR_GREEN);
                        studyStartBtn.setEnabled(true);
                        studyPauseBtn.setEnabled(false);
                        studyResumeBtn.setEnabled(false);
                        Toolkit.getDefaultToolkit().beep();
                        JOptionPane.showMessageDialog(this, "Study session completed! Take a 5-minute break.");
                }
        }

        private void updateStudyClock() {
                int m = studyRemainSec / 60;
                int s = studyRemainSec % 60;
                studyClockLabel.setText(String.format("%02d:%02d", m, s));
        }

        // ==========================================
        // 3. ASSESSMENT VIEW (CBT Palette + Questions)
        // ==========================================
        private JPanel createAssessment() {
                JPanel root = new JPanel(new BorderLayout(0, 10));
                root.setBackground(BG_DARK);
                root.setBorder(new EmptyBorder(16, 24, 16, 24));

                // Top Info Bar
                JPanel top = new JPanel(new BorderLayout());
                top.setOpaque(false);

                JPanel infoLeft = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 0));
                infoLeft.setOpaque(false);
                examSubjectLabel = new JLabel("Subject: Computer Science");
                examSubjectLabel.setFont(FONT_BODY_BOLD);
                examSubjectLabel.setForeground(ACCENT_CYAN);

                examTypeLabel = new JLabel("[ MCQ ]");
                examTypeLabel.setFont(FONT_SMALL);
                examTypeLabel.setForeground(COLOR_AMBER);

                infoLeft.add(examSubjectLabel);
                infoLeft.add(examTypeLabel);

                JPanel infoRight = new JPanel(new FlowLayout(FlowLayout.RIGHT, 12, 0));
                infoRight.setOpaque(false);
                examCounterLabel = new JLabel("Question 1 of 10");
                examCounterLabel.setFont(FONT_BODY_BOLD);
                examCounterLabel.setForeground(TEXT_LIGHT);

                examTimerLabel = new JLabel("15:00");
                examTimerLabel.setFont(new Font("Segoe UI", Font.BOLD, 17));
                examTimerLabel.setForeground(ACCENT_CYAN);

                infoRight.add(examCounterLabel);
                infoRight.add(examTimerLabel);

                top.add(infoLeft, BorderLayout.WEST);
                top.add(infoRight, BorderLayout.EAST);

                examProgressBar = new JProgressBar(0, 10);
                examProgressBar.setValue(1);
                examProgressBar.setPreferredSize(new Dimension(Integer.MAX_VALUE, 4));
                examProgressBar.setForeground(ACCENT_BLUE);
                examProgressBar.setBackground(CARD_BORDER);
                examProgressBar.setBorderPainted(false);

                JPanel headerBox = new JPanel(new BorderLayout(0, 6));
                headerBox.setOpaque(false);
                headerBox.add(top, BorderLayout.NORTH);
                headerBox.add(examProgressBar, BorderLayout.SOUTH);
                root.add(headerBox, BorderLayout.NORTH);

                // Center Question & Answers Card
                JPanel centerCard = createRoundedBox(12, CARD_BG, CARD_BORDER);
                centerCard.setLayout(new BorderLayout(0, 12));
                centerCard.setBorder(new EmptyBorder(18, 22, 18, 22));

                // Question Statement Area
                JPanel qBox = new JPanel(new BorderLayout(0, 8));
                qBox.setOpaque(false);
                examIdLabel = new JLabel("QUESTION #1  |  ID: CS-101");
                examIdLabel.setFont(new Font("Segoe UI", Font.BOLD, 11));
                examIdLabel.setForeground(TEXT_MUTED);

                examQuestionArea = new JTextArea(3, 40);
                examQuestionArea.setFont(new Font("Segoe UI", Font.BOLD, 15));
                examQuestionArea.setForeground(TEXT_LIGHT);
                examQuestionArea.setBackground(new Color(20, 29, 47));
                examQuestionArea.setEditable(false);
                examQuestionArea.setLineWrap(true);
                examQuestionArea.setWrapStyleWord(true);
                examQuestionArea.setBorder(BorderFactory.createCompoundBorder(
                                BorderFactory.createLineBorder(CARD_BORDER, 1),
                                new EmptyBorder(10, 14, 10, 14)));

                qBox.add(examIdLabel, BorderLayout.NORTH);
                qBox.add(examQuestionArea, BorderLayout.CENTER);
                centerCard.add(qBox, BorderLayout.NORTH);

                // Dynamic Answer Section
                examAnswersPanel = new JPanel();
                examAnswersPanel.setLayout(new BoxLayout(examAnswersPanel, BoxLayout.Y_AXIS));
                examAnswersPanel.setOpaque(false);

                JScrollPane answerScroll = new JScrollPane(examAnswersPanel);
                answerScroll.setBorder(null);
                answerScroll.setOpaque(false);
                answerScroll.getViewport().setOpaque(false);
                centerCard.add(answerScroll, BorderLayout.CENTER);

                root.add(centerCard, BorderLayout.CENTER);

                // Bottom Bar: CBT Palette + Nav Controls
                JPanel bottom = new JPanel(new BorderLayout(10, 0));
                bottom.setOpaque(false);
                bottom.setBorder(new EmptyBorder(8, 0, 0, 0));

                JPanel palette = new JPanel(new FlowLayout(FlowLayout.LEFT, 5, 0));
                palette.setOpaque(false);
                for (int i = 0; i < 10; i++) {
                        final int idx = i;
                        JButton btn = new JButton(String.valueOf(i + 1));
                        btn.setFont(new Font("Segoe UI", Font.BOLD, 12));
                        btn.setPreferredSize(new Dimension(34, 34));
                        btn.setFocusPainted(false);
                        btn.setCursor(new Cursor(Cursor.HAND_CURSOR));
                        btn.addActionListener(e -> {
                                examIndex = idx;
                                loadExamQuestion();
                        });
                        paletteButtons.add(btn);
                        palette.add(btn);
                }
                bottom.add(palette, BorderLayout.WEST);

                JPanel nav = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 0));
                nav.setOpaque(false);

                examPrevBtn = createButton("← Previous", CARD_BG, TEXT_LIGHT);
                examPrevBtn.setBorder(BorderFactory.createLineBorder(CARD_BORDER, 1));
                examPrevBtn.addActionListener(e -> {
                        if (examIndex > 0) {
                                examIndex--;
                                loadExamQuestion();
                        }
                });

                examNextBtn = createButton("Next →", ACCENT_BLUE, Color.WHITE);
                examNextBtn.addActionListener(e -> {
                        if (examIndex < 9) {
                                examIndex++;
                                loadExamQuestion();
                        }
                });

                JButton submitBtn = createButton("Submit Test ✓", COLOR_GREEN, Color.WHITE);
                submitBtn.addActionListener(e -> confirmSubmitExam());

                nav.add(examPrevBtn);
                nav.add(examNextBtn);
                nav.add(submitBtn);
                bottom.add(nav, BorderLayout.EAST);

                root.add(bottom, BorderLayout.SOUTH);

                // 15-Minute Exam Countdown Timer
                examTimer = new javax.swing.Timer(1000, e -> {
                        if (examRemainSec > 0) {
                                examRemainSec--;
                                int m = examRemainSec / 60;
                                int s = examRemainSec % 60;
                                examTimerLabel.setText(String.format("%02d:%02d", m, s));
                                if (examRemainSec <= 180)
                                        examTimerLabel.setForeground(COLOR_RED);
                        } else {
                                examTimer.stop();
                                JOptionPane.showMessageDialog(this, "Time expired! Submitting test automatically.");
                                finishAssessment();
                        }
                });

                return root;
        }

        private void loadExamQuestion() {
                AssessmentSession session = assessmentManager.getCurrentSession();
                if (session == null || session.getQuestionCount() == 0)
                        return;

                Question q = session.getQuestion(examIndex);

                // Update Header & Question Prompt
                examCounterLabel.setText("Question " + (examIndex + 1) + " of 10");
                examProgressBar.setValue(examIndex + 1);
                examSubjectLabel.setText("Subject: " + q.getSubject());
                examTypeLabel.setText("[ " + q.getType() + " ]");
                examIdLabel.setText("QUESTION #" + (examIndex + 1) + "  |  ID: " + q.getId());
                examQuestionArea.setText(q.getQuestionText());

                // Update Dynamic Answers
                examAnswersPanel.removeAll();
                examRadioGroup = new ButtonGroup();
                String savedAns = session.getResponse(examIndex);

                if (q.getType().equals("MCQ") || q.getType().equals("TRUE_FALSE")) {
                        List<String> opts = session.getDisplayOptions(examIndex);
                        if (opts == null)
                                opts = q.getOptions();

                        final List<JPanel> optCards = new ArrayList<>();
                        for (int i = 0; i < opts.size(); i++) {
                                final String opt = opts.get(i);
                                char pfx = (char) ('A' + i);

                                final JPanel card = createRoundedBox(8, new Color(20, 29, 47), CARD_BORDER);
                                card.setLayout(new BorderLayout(10, 0));
                                card.setBorder(new EmptyBorder(10, 14, 10, 14));
                                card.setMaximumSize(new Dimension(Integer.MAX_VALUE, 46));
                                card.setAlignmentX(Component.LEFT_ALIGNMENT);
                                card.setCursor(new Cursor(Cursor.HAND_CURSOR));

                                final JRadioButton rb = new JRadioButton(pfx + ".   " + opt);
                                rb.setFont(FONT_BODY);
                                rb.setForeground(TEXT_LIGHT);
                                rb.setOpaque(false);
                                rb.setFocusPainted(false);

                                if (opt.equalsIgnoreCase(savedAns)) {
                                        rb.setSelected(true);
                                        card.setBackground(new Color(30, 58, 100));
                                }

                                // Click card or radio button to select
                                MouseAdapter selectAction = new MouseAdapter() {
                                        @Override
                                        public void mousePressed(MouseEvent e) {
                                                rb.setSelected(true);
                                                session.setResponse(examIndex, opt);
                                                for (JPanel c : optCards)
                                                        c.setBackground(new Color(20, 29, 47));
                                                card.setBackground(new Color(30, 58, 100));
                                                updateExamPalette();
                                        }
                                };
                                card.addMouseListener(selectAction);
                                rb.addActionListener(e -> {
                                        session.setResponse(examIndex, opt);
                                        for (JPanel c : optCards)
                                                c.setBackground(new Color(20, 29, 47));
                                        card.setBackground(new Color(30, 58, 100));
                                        updateExamPalette();
                                });

                                optCards.add(card);
                                examRadioGroup.add(rb);
                                card.add(rb, BorderLayout.CENTER);

                                examAnswersPanel.add(card);
                                examAnswersPanel.add(Box.createVerticalStrut(8));
                        }
                } else if (q.getType().equals("CODING")) {
                        JLabel hint = new JLabel("Write programming logic / pseudocode solution:");
                        hint.setFont(FONT_SMALL);
                        hint.setForeground(ACCENT_CYAN);

                        JTextArea codeBox = new JTextArea(8, 40);
                        codeBox.setFont(FONT_MONO);
                        codeBox.setForeground(new Color(226, 232, 240));
                        codeBox.setBackground(new Color(15, 23, 42));
                        codeBox.setCaretColor(Color.WHITE);
                        codeBox.setText(savedAns);
                        codeBox.setLineWrap(true);
                        codeBox.setBorder(new EmptyBorder(8, 10, 8, 10));

                        codeBox.getDocument().addDocumentListener(new DocumentListener() {
                                public void insertUpdate(DocumentEvent e) {
                                        save();
                                }

                                public void removeUpdate(DocumentEvent e) {
                                        save();
                                }

                                public void changedUpdate(DocumentEvent e) {
                                        save();
                                }

                                private void save() {
                                        session.setResponse(examIndex, codeBox.getText().trim());
                                        updateExamPalette();
                                }
                        });

                        JScrollPane csp = new JScrollPane(codeBox);
                        csp.setBorder(BorderFactory.createLineBorder(CARD_BORDER, 1));
                        csp.setAlignmentX(Component.LEFT_ALIGNMENT);

                        examAnswersPanel.add(hint);
                        examAnswersPanel.add(Box.createVerticalStrut(6));
                        examAnswersPanel.add(csp);
                } else {
                        // Fill in the blanks or Short Answer
                        JLabel hint = new JLabel(q.getType().equals("FILL") ? "Type the exact missing term below:"
                                        : "Provide a concise technical answer (1-3 words):");
                        hint.setFont(FONT_SMALL);
                        hint.setForeground(TEXT_MUTED);

                        JTextField input = new JTextField(savedAns);
                        input.setFont(new Font("Segoe UI", Font.PLAIN, 15));
                        input.setForeground(Color.WHITE);
                        input.setBackground(new Color(15, 23, 42));
                        input.setCaretColor(Color.WHITE);
                        input.setBorder(BorderFactory.createCompoundBorder(
                                        BorderFactory.createLineBorder(CARD_BORDER, 1),
                                        new EmptyBorder(10, 14, 10, 14)));
                        input.setMaximumSize(new Dimension(Integer.MAX_VALUE, 44));
                        input.setAlignmentX(Component.LEFT_ALIGNMENT);

                        input.getDocument().addDocumentListener(new DocumentListener() {
                                public void insertUpdate(DocumentEvent e) {
                                        save();
                                }

                                public void removeUpdate(DocumentEvent e) {
                                        save();
                                }

                                public void changedUpdate(DocumentEvent e) {
                                        save();
                                }

                                private void save() {
                                        session.setResponse(examIndex, input.getText().trim());
                                        updateExamPalette();
                                }
                        });

                        examAnswersPanel.add(hint);
                        examAnswersPanel.add(Box.createVerticalStrut(6));
                        examAnswersPanel.add(input);
                }

                examPrevBtn.setEnabled(examIndex > 0);
                examNextBtn.setText(examIndex == 9 ? "Review →" : "Next →");

                examAnswersPanel.revalidate();
                examAnswersPanel.repaint();
                assessmentView.revalidate();
                assessmentView.repaint();

                updateExamPalette();
                if (!examTimer.isRunning())
                        examTimer.start();
        }

        private void updateExamPalette() {
                AssessmentSession session = assessmentManager.getCurrentSession();
                if (session == null)
                        return;

                for (int i = 0; i < paletteButtons.size(); i++) {
                        JButton b = paletteButtons.get(i);
                        boolean cur = (i == examIndex);
                        boolean ans = session.isAnswered(i);

                        if (cur) {
                                b.setBackground(ACCENT_BLUE);
                                b.setForeground(Color.WHITE);
                                b.setBorder(BorderFactory.createLineBorder(ACCENT_CYAN, 2));
                        } else if (ans) {
                                b.setBackground(new Color(6, 95, 70));
                                b.setForeground(Color.WHITE);
                                b.setBorder(BorderFactory.createLineBorder(COLOR_GREEN, 1));
                        } else {
                                b.setBackground(CARD_BG);
                                b.setForeground(TEXT_MUTED);
                                b.setBorder(BorderFactory.createLineBorder(CARD_BORDER, 1));
                        }
                }
        }

        private void confirmSubmitExam() {
                AssessmentSession session = assessmentManager.getCurrentSession();
                int answered = session != null ? session.getAnsweredCount() : 0;
                int unans = 10 - answered;

                String msg = String.format(
                                "You have answered %d of 10 questions (%d unanswered).\nDo you want to submit your assessment?",
                                answered, unans);
                int opt = JOptionPane.showConfirmDialog(this, msg, "Submit Assessment", JOptionPane.YES_NO_OPTION);
                if (opt == JOptionPane.YES_OPTION) {
                        finishAssessment();
                }
        }

        private void finishAssessment() {
                examTimer.stop();
                AssessmentSession session = assessmentManager.getCurrentSession();
                if (session != null) {
                        totalTests++;
                        int sc = session.calculateScore();
                        cumulativeScore += sc;
                        if (sc > highestScore)
                                highestScore = sc;
                        displayResults(session);
                        showView("results");
                }
        }

        // ==========================================
        // 4. RESULTS VIEW
        // ==========================================
        private JPanel createResults() {
                JPanel root = new JPanel(new BorderLayout());
                root.setBackground(BG_DARK);
                root.setBorder(new EmptyBorder(18, 24, 18, 24));

                JPanel content = new JPanel();
                content.setLayout(new BoxLayout(content, BoxLayout.Y_AXIS));
                content.setOpaque(false);

                // Score Hero
                JPanel hero = createRoundedBox(12, CARD_BG, CARD_BORDER);
                hero.setLayout(new BorderLayout());
                hero.setBorder(new EmptyBorder(18, 22, 18, 22));
                hero.setMaximumSize(new Dimension(Integer.MAX_VALUE, 120));

                JPanel left = new JPanel();
                left.setLayout(new BoxLayout(left, BoxLayout.Y_AXIS));
                left.setOpaque(false);

                JLabel hTag = new JLabel("EVALUATION REPORT");
                hTag.setFont(new Font("Segoe UI", Font.BOLD, 10));
                hTag.setForeground(ACCENT_CYAN);

                resScoreLabel = new JLabel("Score: -- / 10");
                resScoreLabel.setFont(new Font("Segoe UI", Font.BOLD, 26));
                resScoreLabel.setForeground(TEXT_LIGHT);

                resTierLabel = new JLabel("Status Pending");
                resTierLabel.setFont(FONT_BODY_BOLD);
                resTierLabel.setForeground(COLOR_GREEN);

                left.add(hTag);
                left.add(Box.createVerticalStrut(2));
                left.add(resScoreLabel);
                left.add(Box.createVerticalStrut(4));
                left.add(resTierLabel);

                JPanel right = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 0));
                right.setOpaque(false);

                JButton retake = createButton("Retake Test (New Questions)", ACCENT_BLUE, Color.WHITE);
                retake.addActionListener(e -> {
                        assessmentManager.startNewTest();
                        examIndex = 0;
                        examRemainSec = 15 * 60;
                        showView("assessment");
                });

                JButton home = createButton("Back to Dashboard", CARD_BG, TEXT_LIGHT);
                home.setBorder(BorderFactory.createLineBorder(CARD_BORDER, 1));
                home.addActionListener(e -> showView("dashboard"));

                right.add(retake);
                right.add(home);

                hero.add(left, BorderLayout.CENTER);
                hero.add(right, BorderLayout.EAST);
                content.add(hero);
                content.add(Box.createVerticalStrut(14));

                // Metric Row
                JPanel stats = new JPanel(new GridLayout(1, 4, 12, 0));
                stats.setOpaque(false);
                stats.setMaximumSize(new Dimension(Integer.MAX_VALUE, 75));

                resCorrectLabel = new JLabel("0");
                resCorrectLabel.setFont(new Font("Segoe UI", Font.BOLD, 20));
                resCorrectLabel.setForeground(COLOR_GREEN);

                resIncorrectLabel = new JLabel("0");
                resIncorrectLabel.setFont(new Font("Segoe UI", Font.BOLD, 20));
                resIncorrectLabel.setForeground(COLOR_RED);

                resUnansweredLabel = new JLabel("0");
                resUnansweredLabel.setFont(new Font("Segoe UI", Font.BOLD, 20));
                resUnansweredLabel.setForeground(TEXT_MUTED);

                resTimeLabel = new JLabel("15:00");
                resTimeLabel.setFont(new Font("Segoe UI", Font.BOLD, 20));
                resTimeLabel.setForeground(ACCENT_CYAN);

                stats.add(createMiniStat("CORRECT", resCorrectLabel, "1 Mark Each"));
                stats.add(createMiniStat("INCORRECT", resIncorrectLabel, "0 Marks"));
                stats.add(createMiniStat("UNANSWERED", resUnansweredLabel, "Skipped"));
                stats.add(createMiniStat("TIME TAKEN", resTimeLabel, "Exam Duration"));

                content.add(stats);
                content.add(Box.createVerticalStrut(16));

                // Question-by-Question Review List
                JLabel rHead = new JLabel("Detailed Answer & Technical Explanations Review");
                rHead.setFont(FONT_HEADING);
                rHead.setForeground(TEXT_LIGHT);
                content.add(rHead);
                content.add(Box.createVerticalStrut(10));

                resReviewContainer = new JPanel();
                resReviewContainer.setLayout(new BoxLayout(resReviewContainer, BoxLayout.Y_AXIS));
                resReviewContainer.setOpaque(false);
                content.add(resReviewContainer);

                JScrollPane sp = new JScrollPane(content);
                sp.setBorder(null);
                sp.setOpaque(false);
                sp.getViewport().setOpaque(false);
                sp.getVerticalScrollBar().setUnitIncrement(16);

                root.add(sp, BorderLayout.CENTER);
                return root;
        }

        private void displayResults(AssessmentSession session) {
                int score = session.calculateScore();
                int total = session.getQuestionCount();
                int ans = session.getAnsweredCount();
                int unans = total - ans;
                int incorr = total - score - unans;

                resScoreLabel.setText(String.format("Your Score: %d / %d  (%.0f%%)", score, total,
                                ((double) score / total) * 100));

                if (score >= 9) {
                        resTierLabel.setText("Outstanding! Exceptional technical mastery demonstrated.");
                        resTierLabel.setForeground(COLOR_GREEN);
                } else if (score >= 7) {
                        resTierLabel.setText("Very Good! Solid engineering fundamentals and logic.");
                        resTierLabel.setForeground(ACCENT_CYAN);
                } else if (score >= 5) {
                        resTierLabel.setText("Satisfactory. Review the detailed explanations below to improve.");
                        resTierLabel.setForeground(COLOR_AMBER);
                } else {
                        resTierLabel.setText("Needs Practice. Review foundational topics and retake the test.");
                        resTierLabel.setForeground(COLOR_RED);
                }

                resCorrectLabel.setText(String.valueOf(score));
                resIncorrectLabel.setText(String.valueOf(incorr));
                resUnansweredLabel.setText(String.valueOf(unans));

                int elapsed = (15 * 60) - examRemainSec;
                resTimeLabel.setText(String.format("%02d:%02d", elapsed / 60, elapsed % 60));

                resReviewContainer.removeAll();
                for (int i = 0; i < total; i++) {
                        Question q = session.getQuestion(i);
                        String userAns = session.getResponse(i);
                        boolean correct = session.isCorrect(i);
                        boolean answered = session.isAnswered(i);

                        JPanel card = createRoundedBox(10, CARD_BG, CARD_BORDER);
                        card.setLayout(new BoxLayout(card, BoxLayout.Y_AXIS));
                        card.setBorder(new EmptyBorder(14, 18, 14, 18));

                        JPanel top = new JPanel(new BorderLayout());
                        top.setOpaque(false);
                        JLabel num = new JLabel(
                                        String.format("Question %d: [%s] • %s", (i + 1), q.getSubject(), q.getType()));
                        num.setFont(FONT_BODY_BOLD);
                        num.setForeground(TEXT_LIGHT);

                        JLabel stat = new JLabel(!answered ? "○ SKIPPED" : correct ? "✓ CORRECT" : "✗ INCORRECT");
                        stat.setFont(new Font("Segoe UI", Font.BOLD, 11));
                        stat.setForeground(!answered ? TEXT_MUTED : correct ? COLOR_GREEN : COLOR_RED);

                        top.add(num, BorderLayout.WEST);
                        top.add(stat, BorderLayout.EAST);
                        card.add(top);
                        card.add(Box.createVerticalStrut(8));

                        JLabel qText = new JLabel("<html><b>Q:</b> " + q.getQuestionText() + "</html>");
                        qText.setFont(FONT_BODY);
                        qText.setForeground(TEXT_LIGHT);
                        card.add(qText);
                        card.add(Box.createVerticalStrut(4));

                        String dispUser = answered ? userAns : "<None>";
                        JLabel uAns = new JLabel("<html><b>Your Answer:</b> <font color='" +
                                        (correct ? "#10B981" : answered ? "#EF4444" : "#94A3B8") + "'>"
                                        + escape(dispUser) + "</font></html>");
                        uAns.setFont(FONT_BODY);
                        card.add(uAns);
                        card.add(Box.createVerticalStrut(2));

                        JLabel cAns = new JLabel("<html><b>Correct Answer:</b> <font color='#38BDF8'>"
                                        + escape(q.getCorrectAnswer()) + "</font></html>");
                        cAns.setFont(FONT_BODY);
                        card.add(cAns);
                        card.add(Box.createVerticalStrut(4));

                        JLabel exp = new JLabel("<html><b>Explanation:</b> " + escape(q.getExplanation()) + "</html>");
                        exp.setFont(FONT_BODY);
                        exp.setForeground(TEXT_MUTED);
                        card.add(exp);

                        if (q.getType().equals("CODING") && q.getSampleSolution() != null) {
                                card.add(Box.createVerticalStrut(6));
                                JLabel sTitle = new JLabel("Sample Code Approach:");
                                sTitle.setFont(FONT_SMALL);
                                sTitle.setForeground(ACCENT_CYAN);
                                card.add(sTitle);

                                JTextArea sCode = new JTextArea(q.getSampleSolution());
                                sCode.setFont(FONT_MONO);
                                sCode.setEditable(false);
                                sCode.setBackground(new Color(15, 23, 42));
                                sCode.setForeground(new Color(203, 213, 225));
                                sCode.setBorder(new EmptyBorder(6, 10, 6, 10));
                                card.add(sCode);
                        }

                        resReviewContainer.add(card);
                        resReviewContainer.add(Box.createVerticalStrut(10));
                }

                resReviewContainer.revalidate();
                resReviewContainer.repaint();
        }

        private String escape(String s) {
                if (s == null)
                        return "";
                return s.replace("&", "&amp;").replace("<", "&lt;").replace(">", "&gt;").replace("\n", "<br/>");
        }

        // ==========================================
        // 5. ABOUT PROJECT VIEW
        // ==========================================
        private JPanel createAbout() {
                JPanel root = new JPanel(new BorderLayout());
                root.setBackground(BG_DARK);
                root.setBorder(new EmptyBorder(22, 28, 22, 28));

                JPanel content = new JPanel();
                content.setLayout(new BoxLayout(content, BoxLayout.Y_AXIS));
                content.setOpaque(false);

                JPanel box = createRoundedBox(12, CARD_BG, CARD_BORDER);
                box.setLayout(new BoxLayout(box, BoxLayout.Y_AXIS));
                box.setBorder(new EmptyBorder(22, 24, 22, 24));

                JLabel title = new JLabel("Smart Study & Coding Assessment System");
                title.setFont(FONT_TITLE);
                title.setForeground(TEXT_LIGHT);

                JLabel sub = new JLabel("B.Tech Computer Science & Engineering Academic Project");
                sub.setFont(FONT_BODY_BOLD);
                sub.setForeground(ACCENT_CYAN);

                box.add(title);
                box.add(Box.createVerticalStrut(4));
                box.add(sub);
                box.add(Box.createVerticalStrut(16));

                box.add(createBullet(
                                "• Technology Stack: Standard Core Java (JDK 8 / 11 / 17 / 21+), Swing, and AWT."));
                box.add(createBullet("• Zero Third-Party Dependencies: Runs purely with standard JDK (javac & java)."));
                box.add(createBullet(
                                "• UI Architecture: Clean modern top-bar navigation (No sliding sidebar windows)."));
                box.add(createBullet(
                                "• Question Bank Size: 170 technically verified questions spanning 17 CS subjects."));
                box.add(createBullet(
                                "• Advanced Randomization: Guaranteed non-repeating tests, 10 distinct subjects per test."));
                box.add(createBullet("• Study Timer: Asynchronous non-blocking countdown with Pomodoro presets."));
                box.add(createBullet(
                                "• Coding Logic Verification: Transparent algorithmic pattern and keyword matching."));

                box.add(Box.createVerticalStrut(16));
                JLabel runInfo = new JLabel("<html><b>Compilation & Execution:</b><br/>" +
                                "<font face='Consolas' color='#38BDF8'>javac -encoding UTF-8 SmartStudySystem.java</font><br/>"
                                +
                                "<font face='Consolas' color='#38BDF8'>java SmartStudySystem</font></html>");
                runInfo.setFont(FONT_BODY);
                runInfo.setForeground(TEXT_MUTED);
                box.add(runInfo);

                content.add(box);
                root.add(content, BorderLayout.CENTER);
                return root;
        }

        // --- Common Helper Factory Methods ---
        private JPanel createRoundedBox(int radius, Color bg, Color border) {
                JPanel p = new JPanel() {
                        @Override
                        protected void paintComponent(Graphics g) {
                                Graphics2D g2 = (Graphics2D) g.create();
                                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                                if (bg != null) {
                                        g2.setColor(bg);
                                        g2.fill(new RoundRectangle2D.Float(0, 0, getWidth(), getHeight(), radius,
                                                        radius));
                                }
                                if (border != null) {
                                        g2.setColor(border);
                                        g2.draw(new RoundRectangle2D.Float(0.5f, 0.5f, getWidth() - 1, getHeight() - 1,
                                                        radius, radius));
                                }
                                g2.dispose();
                                super.paintComponent(g);
                        }
                };
                p.setOpaque(false);
                return p;
        }

        private JButton createButton(String text, Color bg, Color fg) {
                JButton b = new JButton(text);
                b.setFont(FONT_BODY_BOLD);
                b.setBackground(bg);
                b.setForeground(fg);
                b.setFocusPainted(false);
                b.setCursor(new Cursor(Cursor.HAND_CURSOR));
                b.setBorder(new EmptyBorder(8, 16, 8, 16));
                return b;
        }

        public static void main(String[] args) {
                System.setProperty("awt.useSystemAAFontSettings", "on");
                System.setProperty("swing.aatext", "true");
                SwingUtilities.invokeLater(() -> new SmartStudySystem().setVisible(true));
        }
}

// =========================================================================
// QUESTION DATA MODEL
// =========================================================================
class Question {
        private final String id, subject, type, questionText, correctAnswer, explanation, sampleSolution;
        private final List<String> options;
        private final String[] keywords;

        public Question(String id, String subject, String type, String questionText,
                        List<String> options, String correctAnswer, String explanation,
                        String[] keywords, String sampleSolution) {
                this.id = id;
                this.subject = subject;
                this.type = type;
                this.questionText = questionText;
                this.options = options != null ? options : new ArrayList<>();
                this.correctAnswer = correctAnswer;
                this.explanation = explanation;
                this.keywords = keywords != null ? keywords : new String[0];
                this.sampleSolution = sampleSolution != null ? sampleSolution : "";
        }

        public String getId() {
                return id;
        }

        public String getSubject() {
                return subject;
        }

        public String getType() {
                return type;
        }

        public String getQuestionText() {
                return questionText;
        }

        public List<String> getOptions() {
                return new ArrayList<>(options);
        }

        public String getCorrectAnswer() {
                return correctAnswer;
        }

        public String getExplanation() {
                return explanation;
        }

        public String getSampleSolution() {
                return sampleSolution;
        }

        public boolean evaluateAnswer(String userAns) {
                if (userAns == null || userAns.trim().isEmpty())
                        return false;
                String clean = userAns.trim();

                if (type.equals("MCQ") || type.equals("TRUE_FALSE")) {
                        return clean.equalsIgnoreCase(correctAnswer.trim());
                } else if (type.equals("FILL") || type.equals("SHORT")) {
                        String u = clean.replaceAll("[^a-zA-Z0-9 ]", "").toLowerCase();
                        String a = correctAnswer.replaceAll("[^a-zA-Z0-9 ]", "").toLowerCase();
                        return u.equalsIgnoreCase(a) || clean.equalsIgnoreCase(correctAnswer);
                } else if (type.equals("CODING")) {
                        String lower = clean.toLowerCase();
                        if (keywords.length == 0)
                                return lower.contains(correctAnswer.toLowerCase());
                        int matched = 0;
                        for (String kw : keywords) {
                                if (lower.contains(kw.toLowerCase()))
                                        matched++;
                        }
                        return ((double) matched / keywords.length) >= 0.65;
                }
                return false;
        }
}

// =========================================================================
// ASSESSMENT SESSION
// =========================================================================
class AssessmentSession {
        private final List<Question> questions;
        private final Map<Integer, String> responses = new HashMap<>();
        private final Map<Integer, List<String>> displayOptions = new HashMap<>();

        public AssessmentSession(List<Question> questions) {
                this.questions = questions;
                for (int i = 0; i < questions.size(); i++) {
                        Question q = questions.get(i);
                        if (q.getType().equals("MCQ")) {
                                List<String> opts = q.getOptions();
                                Collections.shuffle(opts);
                                displayOptions.put(i, opts);
                        } else if (q.getType().equals("TRUE_FALSE")) {
                                displayOptions.put(i, Arrays.asList("True", "False"));
                        }
                }
        }

        public int getQuestionCount() {
                return questions.size();
        }

        public Question getQuestion(int i) {
                return questions.get(i);
        }

        public List<String> getDisplayOptions(int i) {
                return displayOptions.get(i);
        }

        public void setResponse(int i, String ans) {
                responses.put(i, ans != null ? ans.trim() : "");
        }

        public String getResponse(int i) {
                return responses.getOrDefault(i, "");
        }

        public boolean isAnswered(int i) {
                String s = responses.get(i);
                return s != null && !s.trim().isEmpty();
        }

        public boolean isCorrect(int i) {
                return questions.get(i).evaluateAnswer(getResponse(i));
        }

        public int calculateScore() {
                int s = 0;
                for (int i = 0; i < questions.size(); i++)
                        if (isCorrect(i))
                                s++;
                return s;
        }

        public int getAnsweredCount() {
                int c = 0;
                for (int i = 0; i < questions.size(); i++)
                        if (isAnswered(i))
                                c++;
                return c;
        }
}

// =========================================================================
// ASSESSMENT RANDOM GENERATOR
// =========================================================================
class AssessmentManager {
        private final QuestionBank bank;
        private final Deque<String> recentIds = new ArrayDeque<>();
        private AssessmentSession currentSession;

        public AssessmentManager(QuestionBank bank) {
                this.bank = bank;
        }

        public AssessmentSession getCurrentSession() {
                return currentSession;
        }

        public AssessmentSession startNewTest() {
                List<Question> selected = new ArrayList<>();
                List<Question> fresh = new ArrayList<>();
                List<Question> recent = new ArrayList<>();

                for (Question q : bank.getAllQuestions()) {
                        if (recentIds.contains(q.getId()))
                                recent.add(q);
                        else
                                fresh.add(q);
                }
                Collections.shuffle(fresh);
                Collections.shuffle(recent);

                // Map fresh questions by subject
                Map<String, List<Question>> subMap = new HashMap<>();
                for (Question q : fresh) {
                        subMap.computeIfAbsent(q.getSubject(), k -> new ArrayList<>()).add(q);
                }

                List<String> shuffledSubs = new ArrayList<>(bank.getSubjects());
                Collections.shuffle(shuffledSubs);

                // Target type ratios: 4 MCQ, 2 TRUE_FALSE, 1 FILL, 1 SHORT, 2 CODING
                Map<String, Integer> target = new LinkedHashMap<>();
                target.put("MCQ", 4);
                target.put("TRUE_FALSE", 2);
                target.put("FILL", 1);
                target.put("SHORT", 1);
                target.put("CODING", 2);

                Set<String> usedIds = new HashSet<>();
                Set<String> usedSubs = new HashSet<>();

                // Pick distinct subjects across desired types
                for (Map.Entry<String, Integer> entry : target.entrySet()) {
                        String type = entry.getKey();
                        int needed = entry.getValue();
                        int acquired = 0;

                        for (String sub : shuffledSubs) {
                                if (acquired >= needed)
                                        break;
                                if (usedSubs.contains(sub))
                                        continue;

                                List<Question> cands = subMap.get(sub);
                                if (cands != null) {
                                        for (Question cand : cands) {
                                                if (cand.getType().equals(type) && !usedIds.contains(cand.getId())) {
                                                        selected.add(cand);
                                                        usedIds.add(cand.getId());
                                                        usedSubs.add(sub);
                                                        acquired++;
                                                        break;
                                                }
                                        }
                                }
                        }
                }

                // Fill remaining from fresh pool
                if (selected.size() < 10) {
                        for (Question q : fresh) {
                                if (selected.size() >= 10)
                                        break;
                                if (!usedIds.contains(q.getId())) {
                                        selected.add(q);
                                        usedIds.add(q.getId());
                                }
                        }
                }

                // Fallback from recent pool
                if (selected.size() < 10) {
                        for (Question q : recent) {
                                if (selected.size() >= 10)
                                        break;
                                if (!usedIds.contains(q.getId())) {
                                        selected.add(q);
                                        usedIds.add(q.getId());
                                }
                        }
                }

                Collections.shuffle(selected);

                // Track recent questions to avoid repeats in consecutive tests
                for (Question q : selected) {
                        recentIds.addLast(q.getId());
                        if (recentIds.size() > 40)
                                recentIds.removeFirst();
                }

                currentSession = new AssessmentSession(selected);
                return currentSession;
        }
}

// =========================================================================
// CONCISE QUESTION BANK (170 QUESTIONS ACROSS 17 CS DISCIPLINES)
// =========================================================================
class QuestionBank {
        private final List<Question> list = new ArrayList<>();
        private final Set<String> subjects = new LinkedHashSet<>();

        public QuestionBank() {
                init();
        }

        public List<Question> getAllQuestions() {
                return list;
        }

        public Set<String> getSubjects() {
                return subjects;
        }

        private void add(String id, String sub, String type, String text, String opts, String ans, String exp,
                        String kw, String sol) {
                subjects.add(sub);
                List<String> oList = opts != null ? Arrays.asList(opts.split("\\|")) : null;
                String[] kList = kw != null ? kw.split(",") : null;
                list.add(new Question(id, sub, type, text, oList, ans, exp, kList, sol));
        }

        private void init() {
                // 1. Programming Fundamentals
                add("PF1", "Programming Fundamentals", "MCQ",
                                "What is the time complexity of binary search on a sorted array of size n?",
                                "O(1)|O(log n)|O(n)|O(n log n)", "O(log n)", "Halves the search space on each step.",
                                null, null);
                add("PF2", "Programming Fundamentals", "MCQ",
                                "Which programming paradigm organizes programs around data objects?",
                                "Functional|Procedural|Object-Oriented|Logic", "Object-Oriented",
                                "OOP binds data attributes and methods together.", null, null);
                add("PF3", "Programming Fundamentals", "TRUE_FALSE",
                                "In recursion, a base case is necessary to prevent stack overflow.", "True|False",
                                "True", "Without a base case, calls recurse until stack memory depletes.", null, null);
                add("PF4", "Programming Fundamentals", "TRUE_FALSE",
                                "An interpreter converts the entire program to machine code before execution.",
                                "True|False", "False", "An interpreter translates source code line by line at runtime.",
                                null, null);
                add("PF5", "Programming Fundamentals", "FILL",
                                "The process of locating and fixing bugs in source code is called _______.", null,
                                "debugging", "Debugging isolates and rectifies software errors.", null, null);
                add("PF6", "Programming Fundamentals", "SHORT", "What does IDE stand for in programming?", null,
                                "Integrated Development Environment", "Combines editor, compiler, and debugger.", null,
                                null);
                add("PF7", "Programming Fundamentals", "MCQ",
                                "Which operator returns the remainder of integer division?", "/|%|&|^", "%",
                                "Modulus operator calculates integer remainder.", null, null);
                add("PF8", "Programming Fundamentals", "FILL",
                                "A variable whose scope is restricted to the declaring block is a _______ variable.",
                                null, "local", "Local variables exist only within their enclosing scope.", null, null);
                add("PF9", "Programming Fundamentals", "CODING",
                                "Write logic to find maximum of three integers (a, b, c).", null,
                                "int max = a; if(b>max) max=b; if(c>max) max=c; return max;",
                                "Compare sequentially and store largest.", "if,max,>",
                                "int max = a;\nif (b > max) max = b;\nif (c > max) max = c;\nreturn max;");
                add("PF10", "Programming Fundamentals", "SHORT", "What is arithmetic overflow?", null,
                                "calculation exceeds storage limit",
                                "Occurs when result exceeds type representation capacity.", null, null);

                // 2. Java Programming
                add("JV1", "Java Programming", "MCQ",
                                "Which JVM memory area holds dynamically allocated class objects?",
                                "Method Area|Call Stack|Heap Memory|PC Register", "Heap Memory",
                                "Heap is allocated for object storage at runtime.", null, null);
                add("JV2", "Java Programming", "MCQ", "Which keyword prevents method overriding and class inheritance?",
                                "static|final|abstract|const", "final", "final marks methods and classes immutable.",
                                null, null);
                add("JV3", "Java Programming", "TRUE_FALSE",
                                "Java primitive data types like int and double are stored directly as object references.",
                                "True|False", "False", "Primitives are stored by value directly on the stack.", null,
                                null);
                add("JV4", "Java Programming", "TRUE_FALSE", "String objects in Java are immutable.", "True|False",
                                "True", "String modifications produce newly allocated string instances.", null, null);
                add("JV5", "Java Programming", "FILL", "The root class of all classes in Java is java.lang._______.",
                                null, "Object", "Every Java class directly or indirectly inherits from Object.", null,
                                null);
                add("JV6", "Java Programming", "SHORT", "What does JIT stand for in JVM?", null, "Just In Time",
                                "JIT compiles bytecode into native machine code at runtime.", null, null);
                add("JV7", "Java Programming", "MCQ",
                                "Which collection allows duplicate elements and maintains insertion order?",
                                "HashSet|ArrayList|TreeSet|HashMap", "ArrayList",
                                "ArrayList is a resizable array preserving insertion order.", null, null);
                add("JV8", "Java Programming", "FILL", "Which keyword is used to inherit a class in Java?", null,
                                "extends", "extends specifies class inheritance.", null, null);
                add("JV9", "Java Programming", "CODING",
                                "Write Java logic to reverse a String 's' without StringBuilder.reverse().", null,
                                "String r = \"\"; for(int i=s.length()-1; i>=0; i--) r += s.charAt(i); return r;",
                                "Iterate backwards from length-1 down to 0.", "for,charat,length",
                                "String r = \"\";\nfor(int i = s.length() - 1; i >= 0; i--) {\n    r += s.charAt(i);\n}\nreturn r;");
                add("JV10", "Java Programming", "SHORT", "What is the purpose of the volatile keyword in Java?", null,
                                "thread visibility", "Ensures writes are directly flushed to main memory.", null, null);

                // 3. C Programming
                add("CP1", "C Programming", "MCQ", "In standard C99, what is the default return type of main()?",
                                "void|int|char|float", "int", "Returns integer status code to the host OS.", null,
                                null);
                add("CP2", "C Programming", "MCQ", "Which function allocates uninitialized heap memory in C?",
                                "malloc()|calloc()|realloc()|free()", "malloc()",
                                "malloc allocates raw uninitialized memory.", null, null);
                add("CP3", "C Programming", "TRUE_FALSE", "C automatically performs runtime array boundary checks.",
                                "True|False", "False", "C performs no runtime array boundary verification.", null,
                                null);
                add("CP4", "C Programming", "TRUE_FALSE",
                                "A pointer pointing to deallocated memory is called a dangling pointer.", "True|False",
                                "True", "Dangling pointers point to invalid freed addresses.", null, null);
                add("CP5", "C Programming", "FILL", "The address-of unary operator in C is _______.", null, "&",
                                "Ampersand returns variable memory address.", null, null);
                add("CP6", "C Programming", "SHORT", "What terminates string literals in C?", null, "null character",
                                "Strings end with '\\0' null delimiter.", null, null);
                add("CP7", "C Programming", "MCQ", "What does the sizeof operator return in C?",
                                "Memory address|Size in bytes|Number of elements|Pointer count", "Size in bytes",
                                "sizeof computes byte storage of types.", null, null);
                add("CP8", "C Programming", "FILL",
                                "The memory deallocation function in C standard library is _______().", null, "free",
                                "free() releases heap allocated memory.", null, null);
                add("CP9", "C Programming", "CODING", "Write C logic to swap two integers using pointers *a and *b.",
                                null, "int temp = *a; *a = *b; *b = temp;",
                                "Use temp variable to swap dereferenced values.", "temp,*a,*b",
                                "int temp = *a;\n*a = *b;\n*b = temp;");
                add("CP10", "C Programming", "SHORT", "What is a memory leak in C?", null, "unreleased heap memory",
                                "Allocated memory that is never deallocated with free().", null, null);

                // 4. C++ Programming
                add("CPP1", "C++ Programming", "MCQ",
                                "Which mechanism enables runtime polymorphism using a vtable in C++?",
                                "Templates|Virtual functions|Friend functions|Inline functions", "Virtual functions",
                                "Virtual functions dispatch dynamically via vtable.", null, null);
                add("CPP2", "C++ Programming", "MCQ", "What does RAII stand for in modern C++?",
                                "Resource Acquisition Is Initialization|Runtime Allocation Interface|Resource Array Indexing|Recursive Application Interface",
                                "Resource Acquisition Is Initialization",
                                "Ties resource lifetime directly to object scope.", null, null);
                add("CPP3", "C++ Programming", "TRUE_FALSE",
                                "A class with at least one pure virtual function (= 0) is abstract.", "True|False",
                                "True", "Abstract classes cannot be directly instantiated.", null, null);
                add("CPP4", "C++ Programming", "TRUE_FALSE",
                                "std::unique_ptr permits shared multi-ownership of a heap pointer.", "True|False",
                                "False", "unique_ptr enforces sole exclusive ownership.", null, null);
                add("CPP5", "C++ Programming", "FILL",
                                "The operator used to access class-level or global scope in C++ is _______.", null,
                                "::", "Scope resolution operator :: resolves namespaces and classes.", null, null);
                add("CPP6", "C++ Programming", "SHORT",
                                "Which standard STL container represents a dynamically resizable array?", null,
                                "vector", "std::vector provides dynamic contiguous array storage.", null, null);
                add("CPP7", "C++ Programming", "MCQ",
                                "Why should base class destructors be virtual in polymorphic hierarchies?",
                                "Prevent memory leak|Speed up CPU|Enable inlining|Allow friend access",
                                "Prevent memory leak", "Ensures derived destructors are properly called.", null, null);
                add("CPP8", "C++ Programming", "FILL", "C++ templates are evaluated and generated at _______ time.",
                                null, "compile", "Templates generate specialized class/function code at compile time.",
                                null, null);
                add("CPP9", "C++ Programming", "CODING",
                                "Write C++ class 'Box' with private int width and public setter setWidth(int w).", null,
                                "class Box { private: int width; public: void setWidth(int w){ width = w; } };",
                                "Declare class with private and public sections.", "class,private,public,width",
                                "class Box {\nprivate:\n    int width;\npublic:\n    void setWidth(int w) { width = w; }\n};");
                add("CPP10", "C++ Programming", "SHORT", "What is function overloading in C++?", null,
                                "same name different parameters",
                                "Multiple functions sharing name with distinct signatures.", null, null);

                // 5. Data Structures
                add("DS1", "Data Structures", "MCQ",
                                "Which data structure operates on the Last-In-First-Out (LIFO) principle?",
                                "Queue|Stack|Array|Linked List", "Stack",
                                "Stack pushes and pops from a single end (top).", null, null);
                add("DS2", "Data Structures", "MCQ",
                                "What is the worst-case search time complexity in a singly linked list of size n?",
                                "O(1)|O(log n)|O(n)|O(n^2)", "O(n)", "Requires linear traversal from head to target.",
                                null, null);
                add("DS3", "Data Structures", "TRUE_FALSE",
                                "In a Max-Heap, every parent node has a key greater than or equal to its children.",
                                "True|False", "True", "Max-heap property mandates parent >= children.", null, null);
                add("DS4", "Data Structures", "TRUE_FALSE",
                                "Circular queues eliminate false overflow of linear array queues.", "True|False",
                                "True", "Circular wrapping reuses de-queued front positions.", null, null);
                add("DS5", "Data Structures", "FILL",
                                "A self-balancing BST with height difference of at most 1 is an _______ tree.", null,
                                "AVL", "AVL trees balance subtrees using node rotations.", null, null);
                add("DS6", "Data Structures", "SHORT",
                                "What is the maximum number of children of a node in a Binary Tree?", null, "2",
                                "Binary trees permit at most two children (left and right).", null, null);
                add("DS7", "Data Structures", "MCQ",
                                "Which data structure is primarily used to implement Breadth-First Search (BFS)?",
                                "Stack|Queue|Priority Queue|Tree", "Queue",
                                "BFS explores neighbors level-by-level using FIFO Queue.", null, null);
                add("DS8", "Data Structures", "FILL",
                                "Hash table collision resolution using linked lists is called separate _______.", null,
                                "chaining", "Separate chaining chains collided entries in lists.", null, null);
                add("DS9", "Data Structures", "CODING",
                                "Write logic to pop from an integer array stack 'stk', decrementing top.", null,
                                "if(top < 0) return -1; return stk[top--];", "Check underflow and decrement top index.",
                                "top,stk,return", "if (top < 0) return -1;\nreturn stk[top--];");
                add("DS10", "Data Structures", "SHORT",
                                "What is the time complexity to insert at the head of a linked list with known head pointer?",
                                null, "O(1)", "Only involves updating two pointer references.", null, null);

                // 6. Algorithms
                add("AL1", "Algorithms", "MCQ", "Which paradigm does Dijkstra's shortest path algorithm use?",
                                "Dynamic Programming|Greedy Approach|Divide and Conquer|Backtracking",
                                "Greedy Approach", "Greedily picks unvisited node with minimal tentative distance.",
                                null, null);
                add("AL2", "Algorithms", "MCQ", "What is the average time complexity of Merge Sort?",
                                "O(n)|O(n log n)|O(n^2)|O(log n)", "O(n log n)",
                                "Divides in halves and merges in linear time.", null, null);
                add("AL3", "Algorithms", "TRUE_FALSE",
                                "Quick Sort has O(n^2) worst case when poorly balanced pivots occur.", "True|False",
                                "True", "Sorted inputs with extremal pivots degenerate to O(n^2).", null, null);
                add("AL4", "Algorithms", "TRUE_FALSE",
                                "Dynamic programming is designed for independent, non-overlapping subproblems.",
                                "True|False", "False", "DP specifically solves overlapping subproblems by memoization.",
                                null, null);
                add("AL5", "Algorithms", "FILL",
                                "The asymptotic notation representing a tight upper and lower bound is Big-_______.",
                                null, "Theta", "Theta Θ denotes asymptotically tight growth bounds.", null, null);
                add("AL6", "Algorithms", "SHORT",
                                "What is the time complexity of building a heap from an unsorted array of size n?",
                                null, "O(n)", "Bottom-up heap construction runs in linear O(n) time.", null, null);
                add("AL7", "Algorithms", "MCQ", "Which sorting algorithm is intrinsically stable?",
                                "Quick Sort|Heap Sort|Merge Sort|Selection Sort", "Merge Sort",
                                "Preserves relative order of duplicate elements.", null, null);
                add("AL8", "Algorithms", "FILL",
                                "Kruskal's algorithm avoids graph cycles using Disjoint Set _______ (DSU).", null,
                                "Union", "DSU tracks connected components to reject cycles.", null, null);
                add("AL9", "Algorithms", "CODING", "Write binary search loop logic finding target in sorted array arr.",
                                null,
                                "int l=0, r=arr.length-1; while(l<=r){ int m=l+(r-l)/2; if(arr[m]==target) return m; if(arr[m]<target) l=m+1; else r=m-1; } return -1;",
                                "Halve search space each loop.", "while,mid,low,high",
                                "int l = 0, r = arr.length - 1;\nwhile(l <= r) {\n    int m = l + (r - l) / 2;\n    if (arr[m] == target) return m;\n    if (arr[m] < target) l = m + 1;\n    else r = m - 1;\n}\nreturn -1;");
                add("AL10", "Algorithms", "SHORT", "What algorithmic strategy is used in solving the N-Queens problem?",
                                null, "backtracking", "Incrementally constructs states and abandons invalid branches.",
                                null, null);

                // 7. Object-Oriented Programming
                add("OOP1", "Object-Oriented Programming", "MCQ",
                                "Which OOP concept bundles data and methods while hiding internal state?",
                                "Inheritance|Encapsulation|Polymorphism|Abstraction", "Encapsulation",
                                "Encapsulation controls access through access modifiers.", null, null);
                add("OOP2", "Object-Oriented Programming", "MCQ",
                                "Same method name with differing parameter lists in one class demonstrates:",
                                "Method Overriding|Method Overloading|Dynamic Binding|Shadowing", "Method Overloading",
                                "Compile-time static polymorphism.", null, null);
                add("OOP3", "Object-Oriented Programming", "TRUE_FALSE",
                                "Composition models a 'has-a' relationship between software classes.", "True|False",
                                "True", "Composition represents ownership of component objects.", null, null);
                add("OOP4", "Object-Oriented Programming", "TRUE_FALSE",
                                "Interfaces can be instantiated directly using the 'new' keyword.", "True|False",
                                "False", "Interfaces specify contracts requiring concrete implementing classes.", null,
                                null);
                add("OOP5", "Object-Oriented Programming", "FILL",
                                "A subclass providing specific implementation of a superclass method is method _______.",
                                null, "overriding", "Overriding enables runtime dynamic dispatch.", null, null);
                add("OOP6", "Object-Oriented Programming", "SHORT",
                                "What design pattern restricts a class to one single instance?", null, "Singleton",
                                "Singleton guarantees single global instance access.", null, null);
                add("OOP7", "Object-Oriented Programming", "MCQ",
                                "Which access modifier restricts visibility to package and subclasses only?",
                                "private|protected|public|default", "protected",
                                "Protected allows access inside package and derived classes.", null, null);
                add("OOP8", "Object-Oriented Programming", "FILL",
                                "The SOLID principle represented by 'O' is the Open/_______ Principle.", null, "Closed",
                                "Classes should be open for extension, closed for modification.", null, null);
                add("OOP9", "Object-Oriented Programming", "CODING",
                                "Write Java class header Student inheriting from Person calling super(name).", null,
                                "class Student extends Person { public Student(String name){ super(name); } }",
                                "Use extends and super().", "extends,person,super",
                                "class Student extends Person {\n    public Student(String name) {\n        super(name);\n    }\n}");
                add("OOP10", "Object-Oriented Programming", "SHORT",
                                "What is the difference between high cohesion and low cohesion?", null,
                                "high cohesion focuses on single task",
                                "High cohesion modules concentrate on one well-defined responsibility.", null, null);

                // 8. DBMS
                add("DB1", "DBMS", "MCQ", "Which ACID property guarantees 'all-or-nothing' transaction execution?",
                                "Atomicity|Consistency|Isolation|Durability", "Atomicity",
                                "If any transaction step fails, entire transaction rolls back.", null, null);
                add("DB2", "DBMS", "MCQ", "Which normal form removes partial dependency on candidate keys?",
                                "1NF|2NF|3NF|BCNF", "2NF",
                                "2NF mandates no non-prime attribute depends on subset of key.", null, null);
                add("DB3", "DBMS", "TRUE_FALSE",
                                "A candidate key is a minimal superkey capable of uniquely identifying tuples.",
                                "True|False", "True", "Candidate keys possess uniqueness and irreducibility.", null,
                                null);
                add("DB4", "DBMS", "TRUE_FALSE", "Deadlocks in database systems can be detected using Wait-For Graphs.",
                                "True|False", "True", "Cycles in wait-for graphs indicate deadlock conditions.", null,
                                null);
                add("DB5", "DBMS", "FILL",
                                "A key in one table referencing the primary key of another table is a _______ key.",
                                null, "foreign", "Foreign keys enforce relational referential integrity.", null, null);
                add("DB6", "DBMS", "SHORT", "What does 'D' in ACID properties stand for?", null, "Durability",
                                "Durability guarantees committed transactions survive system failures.", null, null);
                add("DB7", "DBMS", "MCQ", "Which tree data structure is widely used for DB index range searches?",
                                "AVL Tree|B+ Tree|Red-Black Tree|Binary Heap", "B+ Tree",
                                "B+ trees store values in linked leaf nodes for range scans.", null, null);
                add("DB8", "DBMS", "FILL",
                                "Reading uncommitted changes made by concurrent transactions is a _______ read.", null,
                                "dirty", "Dirty reads access dirty uncommitted data.", null, null);
                add("DB9", "DBMS", "CODING",
                                "Write pseudocode sequence for transaction execution with begin, commit, rollback.",
                                null, "BEGIN TRANSACTION; try { executeQuery(); COMMIT; } catch { ROLLBACK; }",
                                "Structure with BEGIN, COMMIT, and ROLLBACK.", "begin,commit,rollback",
                                "BEGIN TRANSACTION;\nTRY {\n    UPDATE Balance;\n    COMMIT;\n} CATCH {\n    ROLLBACK;\n}");
                add("DB10", "DBMS", "SHORT", "What is a shared lock in concurrency control?", null,
                                "read lock allowing concurrent reads",
                                "Allows multiple transactions to read without writes.", null, null);

                // 9. SQL
                add("SQL1", "SQL", "MCQ", "Which SQL clause filters records resulting from aggregate functions?",
                                "WHERE|HAVING|GROUP BY|ORDER BY", "HAVING",
                                "HAVING filters aggregated groups; WHERE filters rows.", null, null);
                add("SQL2", "SQL", "MCQ", "Which join returns all left table rows and matching right table rows?",
                                "INNER JOIN|LEFT JOIN|RIGHT JOIN|CROSS JOIN", "LEFT JOIN",
                                "LEFT JOIN preserves all records from left table.", null, null);
                add("SQL3", "SQL", "TRUE_FALSE", "In SQL, NULL value is equivalent to integer 0 or empty string ''.",
                                "True|False", "False", "NULL indicates missing/unknown value, checked by IS NULL.",
                                null, null);
                add("SQL4", "SQL", "TRUE_FALSE", "TRUNCATE is a DDL command that deallocates entire table data pages.",
                                "True|False", "True", "TRUNCATE resets table without per-row logging.", null, null);
                add("SQL5", "SQL", "FILL", "To remove duplicate tuples from a SELECT query, use SELECT _______.", null,
                                "DISTINCT", "SELECT DISTINCT removes duplicates.", null, null);
                add("SQL6", "SQL", "SHORT", "What SQL category do INSERT, UPDATE, and DELETE belong to?", null, "DML",
                                "Data Manipulation Language modifies stored data.", null, null);
                add("SQL7", "SQL", "MCQ", "Which constraint prevents duplicates while permitting nulls?",
                                "PRIMARY KEY|UNIQUE|NOT NULL|CHECK", "UNIQUE",
                                "UNIQUE enforces distinct non-null values.", null, null);
                add("SQL8", "SQL", "FILL",
                                "In SQL LIKE pattern matching, the wildcard for multiple arbitrary characters is _______.",
                                null, "%", "Percent symbol matches zero or more characters.", null, null);
                add("SQL9", "SQL", "CODING",
                                "Write SQL query selecting name, salary from employees where salary > 50000 order by salary desc.",
                                null, "SELECT name, salary FROM employees WHERE salary > 50000 ORDER BY salary DESC;",
                                "Standard SELECT with WHERE and ORDER BY DESC.", "select,from,where,order by,desc",
                                "SELECT name, salary\nFROM employees\nWHERE salary > 50000\nORDER BY salary DESC;");
                add("SQL10", "SQL", "SHORT", "What aggregate function calculates total sum of numeric column in SQL?",
                                null, "SUM", "SUM() totals numeric column values.", null, null);

                // 10. Operating Systems
                add("OS1", "Operating Systems", "MCQ", "Which is NOT one of the four Coffman deadlock conditions?",
                                "Mutual Exclusion|Hold and Wait|Preemption Allowed|Circular Wait", "Preemption Allowed",
                                "Deadlock requires 'No Preemption'.", null, null);
                add("OS2", "Operating Systems", "MCQ",
                                "Which scheduling algorithm minimizes average waiting time for batch jobs?",
                                "FCFS|Shortest Job First|Round Robin|Priority", "Shortest Job First",
                                "SJF is provably optimal for minimizing average wait time.", null, null);
                add("OS3", "Operating Systems", "TRUE_FALSE",
                                "Thrashing happens when the OS spends more time paging than executing instructions.",
                                "True|False", "True", "Thrashing collapses throughput due to frequent page swaps.",
                                null, null);
                add("OS4", "Operating Systems", "TRUE_FALSE",
                                "Threads of the same process share code, global data, and open file handles.",
                                "True|False", "True",
                                "Threads share heap and address space while keeping separate stacks.", null, null);
                add("OS5", "Operating Systems", "FILL",
                                "Deadlock avoidance in multi-resource environments uses the _______ algorithm.", null,
                                "Banker's", "Dijkstra's Banker's Algorithm checks for safe states.", null, null);
                add("OS6", "Operating Systems", "SHORT",
                                "What hardware cache accelerates virtual to physical address translations?", null,
                                "TLB", "Translation Lookaside Buffer caches page table lookups.", null, null);
                add("OS7", "Operating Systems", "MCQ", "What happens during an OS context switch?",
                                "CPU switches kernel mode only|Saves state of one process and loads another|Flushes memory|Restarts bus",
                                "Saves state of one process and loads another",
                                "Saves registers and PC into PCB and loads next process.", null, null);
                add("OS8", "Operating Systems", "FILL",
                                "An integer synchronization variable accessed atomically via wait() and signal() is a _______.",
                                null, "semaphore", "Semaphores regulate concurrent resource access.", null, null);
                add("OS9", "Operating Systems", "CODING",
                                "Write pseudocode sequence for consumer semaphore synchronization (mutex, full, empty).",
                                null, "wait(full); wait(mutex); item=buf.remove(); signal(mutex); signal(empty);",
                                "Consumer consumes full, protects mutex, signals empty.",
                                "wait,signal,mutex,full,empty",
                                "wait(full);\nwait(mutex);\nitem = buffer.remove();\nsignal(mutex);\nsignal(empty);");
                add("OS10", "Operating Systems", "SHORT", "What is a critical section in concurrency?", null,
                                "code accessing shared resource",
                                "Must be executed with mutual exclusion to prevent race conditions.", null, null);

                // 11. Computer Networks
                add("CN1", "Computer Networks", "MCQ",
                                "Which OSI layer provides reliable end-to-end transport and flow control?",
                                "Network Layer|Transport Layer|Data Link Layer|Session Layer", "Transport Layer",
                                "Layer 4 (TCP) manages reliable end-to-end delivery.", null, null);
                add("CN2", "Computer Networks", "MCQ", "What is the size of an IPv4 address in bits?",
                                "32 bits|64 bits|128 bits|16 bits", "32 bits",
                                "IPv4 uses 32 bits (4 octets). IPv6 uses 128 bits.", null, null);
                add("CN3", "Computer Networks", "TRUE_FALSE",
                                "TCP provides ordered delivery via sequence numbers, whereas UDP is connectionless.",
                                "True|False", "True",
                                "TCP guarantees ordered delivery; UDP sends independent datagrams.", null, null);
                add("CN4", "Computer Networks", "TRUE_FALSE",
                                "MAC addresses operate at Layer 3 (Network Layer) of OSI model.", "True|False", "False",
                                "MAC addresses are physical addresses at Layer 2 (Data Link).", null, null);
                add("CN5", "Computer Networks", "FILL",
                                "The system translating domain hostnames to IP addresses is _______.", null, "DNS",
                                "Domain Name System maps names to IP addresses.", null, null);
                add("CN6", "Computer Networks", "SHORT", "What is the standard port number for HTTPS traffic?", null,
                                "443", "HTTPS defaults to encrypted port 443; HTTP uses port 80.", null, null);
                add("CN7", "Computer Networks", "MCQ",
                                "Which protocol dynamically assigns IP addresses to connected clients?",
                                "DNS|DHCP|ARP|ICMP", "DHCP",
                                "Dynamic Host Configuration Protocol automatically assigns IPs.", null, null);
                add("CN8", "Computer Networks", "FILL",
                                "The protocol resolving IP address to local physical MAC address is _______.", null,
                                "ARP", "Address Resolution Protocol maps IP to MAC.", null, null);
                add("CN9", "Computer Networks", "CODING",
                                "Write sequence of TCP 3-way handshake packet flags exchanged.", null,
                                "Client -> Server: SYN; Server -> Client: SYN-ACK; Client -> Server: ACK;",
                                "Sequence: SYN, SYN-ACK, ACK.", "syn,ack,syn-ack",
                                "1. Client -> Server: SYN\n2. Server -> Client: SYN-ACK\n3. Client -> Server: ACK");
                add("CN10", "Computer Networks", "SHORT", "What is propagation delay in networks?", null,
                                "time for bit to travel over physical medium",
                                "Calculated as link distance divided by wave propagation speed.", null, null);

                // 12. Computer Organization & Architecture
                add("COA1", "Computer Organization and Architecture", "MCQ",
                                "Which CPU register stores the address of the next instruction to fetch?",
                                "Accumulator|Instruction Register|Program Counter|Memory Buffer", "Program Counter",
                                "PC holds memory address of next instruction.", null, null);
                add("COA2", "Computer Organization and Architecture", "MCQ",
                                "Which pipeline hazard occurs when instructions need data not yet written back?",
                                "Structural Hazard|Data Hazard|Control Hazard|Branch Hazard", "Data Hazard",
                                "Data hazards arise from uncommitted operand dependencies.", null, null);
                add("COA3", "Computer Organization and Architecture", "TRUE_FALSE",
                                "Von Neumann architecture stores program instructions and data in unified memory.",
                                "True|False", "True", "Shared memory bus for instructions and program data.", null,
                                null);
                add("COA4", "Computer Organization and Architecture", "TRUE_FALSE",
                                "SRAM is used for CPU cache because it does not require electrical refreshing.",
                                "True|False", "True", "SRAM uses flip-flop circuits avoiding dynamic refresh cycles.",
                                null, null);
                add("COA5", "Computer Organization and Architecture", "FILL",
                                "Direct block data transfers between I/O and RAM without CPU intervention is _______.",
                                null, "DMA", "Direct Memory Access offloads CPU from byte copying.", null, null);
                add("COA6", "Computer Organization and Architecture", "SHORT", "What does RISC stand for?", null,
                                "Reduced Instruction Set Computer", "Focuses on simple single-cycle instructions.",
                                null, null);
                add("COA7", "Computer Organization and Architecture", "MCQ",
                                "Which cache mapping allows a memory block to reside in any cache line?",
                                "Direct Mapped|Fully Associative|Set Associative|Sector", "Fully Associative",
                                "Any block maps to any line, minimizing conflicts.", null, null);
                add("COA8", "Computer Organization and Architecture", "FILL",
                                "Signed binary multiplication is accelerated using Booth's _______ algorithm.", null,
                                "multiplication", "Booth's algorithm optimizes signed two's complement multiplication.",
                                null, null);
                add("COA9", "Computer Organization and Architecture", "CODING",
                                "Write the 5 standard CPU instruction pipeline stages in order.", null,
                                "IF, ID, EX, MEM, WB", "Fetch, Decode, Execute, Memory, Write-back.", "if,id,ex,mem,wb",
                                "1. IF (Instruction Fetch)\n2. ID (Decode)\n3. EX (Execute)\n4. MEM (Memory)\n5. WB (Write Back)");
                add("COA10", "Computer Organization and Architecture", "SHORT",
                                "What is spatial locality in memory hierarchy?", null,
                                "accessing nearby memory addresses",
                                "High probability of accessing locations adjacent to recent memory references.", null,
                                null);

                // 13. Software Engineering
                add("SE1", "Software Engineering", "MCQ",
                                "Which agile framework uses Sprints, daily standups, and burndown charts?",
                                "Waterfall|Scrum|V-Model|Spiral", "Scrum",
                                "Scrum delivers software in time-boxed sprint iterations.", null, null);
                add("SE2", "Software Engineering", "MCQ",
                                "Testing individual methods or classes in complete isolation is:",
                                "Integration Testing|Unit Testing|System Testing|Acceptance Testing", "Unit Testing",
                                "Unit testing validates small software units independently.", null, null);
                add("SE3", "Software Engineering", "TRUE_FALSE",
                                "High cohesion and low coupling represent key goals of modular software design.",
                                "True|False", "True", "Maximizes module focus while minimizing interdependencies.",
                                null, null);
                add("SE4", "Software Engineering", "TRUE_FALSE",
                                "Regression testing ensures recent updates have not broken existing functionality.",
                                "True|False", "True", "Verifies changes did not introduce unintended side effects.",
                                null, null);
                add("SE5", "Software Engineering", "FILL",
                                "The metric measuring linearly independent paths in code is _______ complexity.", null,
                                "cyclomatic", "McCabe's cyclomatic complexity counts decision paths.", null, null);
                add("SE6", "Software Engineering", "SHORT", "What does SRS stand for in requirements engineering?",
                                null, "Software Requirements Specification",
                                "Defines complete functional and non-functional specifications.", null, null);
                add("SE7", "Software Engineering", "MCQ",
                                "Which lifecycle model places explicit emphasis on risk assessment in every cycle?",
                                "Waterfall|Spiral Model|V-Model|RAD", "Spiral Model",
                                "Boehm's Spiral Model repeats risk evaluation phases.", null, null);
                add("SE8", "Software Engineering", "FILL",
                                "In Git, merging a branch with no divergent commits is a _______ merge.", null,
                                "fast-forward", "Fast-forward moves HEAD pointer forward directly.", null, null);
                add("SE9", "Software Engineering", "CODING",
                                "Write sequence of 3 core stages in automated CI/CD pipeline.", null,
                                "Build -> Test -> Deploy", "Checkout, Build, Unit Tests, Deploy.", "build,test,deploy",
                                "Stage 1: Build\nStage 2: Test\nStage 3: Deploy");
                add("SE10", "Software Engineering", "SHORT", "What is Black-Box testing?", null,
                                "testing without knowledge of internal code",
                                "Focuses purely on inputs and expected outputs against specification.", null, null);

                // 14. Computer Graphics
                add("CG1", "Computer Graphics", "MCQ",
                                "Which algorithm draws raster lines using integer additions and subtractions only?",
                                "DDA Algorithm|Bresenham's Algorithm|Cohen-Sutherland|Sutherland-Hodgman",
                                "Bresenham's Algorithm", "Avoids floating point arithmetic using decision parameters.",
                                null, null);
                add("CG2", "Computer Graphics", "MCQ",
                                "What matrix dimension is required for 2D affine transformations using homogeneous coordinates?",
                                "2x2|3x3|4x4|1x3", "3x3",
                                "3x3 matrix enables translation, rotation, and scaling combination.", null, null);
                add("CG3", "Computer Graphics", "TRUE_FALSE",
                                "The Z-buffer algorithm resolves hidden surfaces by recording closest pixel depths.",
                                "True|False", "True",
                                "Depth buffer discards fragments further away than current pixel.", null, null);
                add("CG4", "Computer Graphics", "TRUE_FALSE",
                                "Anti-aliasing blends edge pixels to reduce jagged 'staircase' raster artifacts.",
                                "True|False", "True", "Smoothes rasterization discontinuities across pixel grids.",
                                null, null);
                add("CG5", "Computer Graphics", "FILL",
                                "The 2D clipping algorithm using 4-bit region outcodes is Cohen-_______.", null,
                                "Sutherland", "Cohen-Sutherland clips lines against viewports using 4-bit codes.", null,
                                null);
                add("CG6", "Computer Graphics", "SHORT",
                                "What primary additive color model is used in computer monitors?", null, "RGB",
                                "Red, Green, Blue additive light spectrum model.", null, null);
                add("CG7", "Computer Graphics", "MCQ",
                                "Which shading model interpolates surface normal vectors for specular highlights?",
                                "Flat Shading|Gouraud Shading|Phong Shading|Wireframe", "Phong Shading",
                                "Phong interpolates normal vectors to produce accurate highlights.", null, null);
                add("CG8", "Computer Graphics", "FILL",
                                "Rendering artifact where screen displays halves of two different frames is screen _______.",
                                null, "tearing", "Screen tearing occurs when GPU buffer updates mid-scanout.", null,
                                null);
                add("CG9", "Computer Graphics", "CODING",
                                "Write initial decision parameter P0 for midpoint circle algorithm of radius r.", null,
                                "int p = 1 - r; int x = 0, y = r;", "Initialize p = 1 - r, x = 0, y = r.", "p,r,x,y",
                                "int p = 1 - r;\nint x = 0, y = r;");
                add("CG10", "Computer Graphics", "SHORT", "What is ray tracing in 3D graphics?", null,
                                "simulating physical path of light rays",
                                "Simulates optical reflection, refraction, and shadows.", null, null);

                // 15. Cybersecurity Fundamentals
                add("CS1", "Cybersecurity Fundamentals", "MCQ",
                                "Which triad forms the cornerstone foundation of information security?",
                                "Confidentiality, Integrity, Availability|Control, Identification, Access|Crypto, Inspection, Audit|Cyber, Infrastructure, Auth",
                                "Confidentiality, Integrity, Availability",
                                "The CIA triad defines foundational security principles.", null, null);
                add("CS2", "Cybersecurity Fundamentals", "MCQ",
                                "Which asymmetric encryption algorithm relies on factoring large prime numbers?",
                                "AES|RSA|DES|SHA-256", "RSA",
                                "RSA security rests on difficulty of factoring large semiprimes.", null, null);
                add("CS3", "Cybersecurity Fundamentals", "TRUE_FALSE",
                                "Symmetric cryptography uses different keys for encryption and decryption.",
                                "True|False", "False", "Symmetric uses one shared secret key for both operations.",
                                null, null);
                add("CS4", "Cybersecurity Fundamentals", "TRUE_FALSE",
                                "SQL injection attacks can be prevented using parameterized prepared statements.",
                                "True|False", "True",
                                "Prepared statements treat user input as literal values, not code.", null, null);
                add("CS5", "Cybersecurity Fundamentals", "FILL",
                                "An attack flooding network targets with illegitimate distributed traffic is a _______ attack.",
                                null, "DDoS", "Distributed Denial of Service overwhelms server capacity.", null, null);
                add("CS6", "Cybersecurity Fundamentals", "SHORT", "What does 2FA stand for in user authentication?",
                                null, "Two Factor Authentication",
                                "Requires two distinct independent factors of identity proof.", null, null);
                add("CS7", "Cybersecurity Fundamentals", "MCQ",
                                "Which attack secretly intercepts and alters communication between two parties?",
                                "Man-in-the-Middle|Buffer Overflow|Cross-Site Scripting|Brute Force",
                                "Man-in-the-Middle", "MitM intercepts and relays traffic without parties' consent.",
                                null, null);
                add("CS8", "Cybersecurity Fundamentals", "FILL",
                                "A one-way deterministic mathematical function generating a fixed-size digest is a _______ function.",
                                null, "hash", "Cryptographic hashes (e.g. SHA-256) are irreversible digests.", null,
                                null);
                add("CS9", "Cybersecurity Fundamentals", "CODING",
                                "Write pseudocode showing password hashing with a cryptographic salt.", null,
                                "byte[] salt = genSalt(); String h = hash(password + salt); store(user, h, salt);",
                                "Append salt to password before hashing.", "salt,hash,password",
                                "byte[] salt = generateSalt();\nString hash = sha256(password + salt);\nsaveUser(username, hash, salt);");
                add("CS10", "Cybersecurity Fundamentals", "SHORT", "What is Phishing?", null,
                                "deceptive fraudulent social engineering",
                                "Impersonates legitimate entities to steal credentials.", null, null);

                // 16. Web and Internet Fundamentals
                add("WEB1", "Web and Internet Fundamentals", "MCQ",
                                "Which HTTP status code indicates a requested resource was not found on server?",
                                "200 OK|301 Redirect|404 Not Found|500 Server Error", "404 Not Found",
                                "404 indicates server cannot locate requested resource URI.", null, null);
                add("WEB2", "Web and Internet Fundamentals", "MCQ",
                                "Which standard HTTP verb is idempotent by specification?", "POST|GET|PATCH|CONNECT",
                                "GET", "Repeated GET calls produce identical server state without side effects.", null,
                                null);
                add("WEB3", "Web and Internet Fundamentals", "TRUE_FALSE",
                                "Cookies marked with the HttpOnly flag cannot be accessed via JavaScript document.cookie.",
                                "True|False", "True", "HttpOnly mitigates XSS session token theft.", null, null);
                add("WEB4", "Web and Internet Fundamentals", "TRUE_FALSE",
                                "The DOM represents an HTML webpage document as an in-memory tree hierarchy.",
                                "True|False", "True", "DOM parses markup into interactive programmable nodes.", null,
                                null);
                add("WEB5", "Web and Internet Fundamentals", "FILL",
                                "The browser security policy isolating scripts across different origins is _______ Origin Policy.",
                                null, "Same", "Same-Origin Policy restricts cross-domain DOM/cookie access.", null,
                                null);
                add("WEB6", "Web and Internet Fundamentals", "SHORT",
                                "What does REST stand for in web API architecture?", null,
                                "Representational State Transfer",
                                "REST uses stateless HTTP operations on resource endpoints.", null, null);
                add("WEB7", "Web and Internet Fundamentals", "MCQ",
                                "Which CSS layout is designed for one-dimensional linear container item alignment?",
                                "Flexbox|Grid|Table|Float", "Flexbox",
                                "Flexbox excels at linear row or column layouts.", null, null);
                add("WEB8", "Web and Internet Fundamentals", "FILL",
                                "The protocol providing full-duplex persistent bidirectional TCP communication is _______.",
                                null, "WebSocket",
                                "WebSocket provides low-latency real-time client-server communication.", null, null);
                add("WEB9", "Web and Internet Fundamentals", "CODING",
                                "Write JavaScript fetch API call retrieving JSON from 'url' with promise then handling.",
                                null, "fetch(url).then(r => r.json()).then(data => console.log(data));",
                                "Fetch, then parse json, then process data.", "fetch,then,json",
                                "fetch(url)\n  .then(res => res.json())\n  .then(data => console.log(data));");
                add("WEB10", "Web and Internet Fundamentals", "SHORT",
                                "What format is the standard lightweight data interchange syntax for web APIs?", null,
                                "JSON", "JavaScript Object Notation is standard API interchange format.", null, null);

                // 17. Discrete Mathematics
                add("DM1", "Discrete Mathematics", "MCQ",
                                "What is the cardinality of the power set of a set containing n elements?",
                                "n^2|2^n|2n|n!", "2^n",
                                "Every element has 2 choices (include/exclude), giving 2^n subsets.", null, null);
                add("DM2", "Discrete Mathematics", "MCQ",
                                "What condition guarantees an Eulerian circuit exists in a connected graph?",
                                "All vertices have even degree|Graph is bipartite|No cycles exist|All vertices have odd degree",
                                "All vertices have even degree",
                                "Eulerian circuit requires every vertex to have an even degree.", null, null);
                add("DM3", "Discrete Mathematics", "TRUE_FALSE",
                                "A relation that is reflexive, symmetric, and transitive is an equivalence relation.",
                                "True|False", "True", "Satisfies all three equivalence relation axioms.", null, null);
                add("DM4", "Discrete Mathematics", "TRUE_FALSE",
                                "Pigeonhole Principle: If k+1 items enter k bins, at least one bin holds >= 2 items.",
                                "True|False", "True", "Mathematical certainty when items strictly exceed containers.",
                                null, null);
                add("DM5", "Discrete Mathematics", "FILL",
                                "A compound logical proposition that is True under all truth assignments is a _______.",
                                null, "tautology", "Tautologies are unconditionally true propositional formulas.", null,
                                null);
                add("DM6", "Discrete Mathematics", "SHORT",
                                "What is the chromatic number of a connected bipartite graph with >= 1 edge?", null,
                                "2", "Bipartite graphs can be colored with exactly 2 colors.", null, null);
                add("DM7", "Discrete Mathematics", "MCQ", "How many edges are present in a tree with n vertices?",
                                "n|n - 1|n + 1|2n", "n - 1",
                                "A connected minimally acyclic tree of n vertices has n-1 edges.", null, null);
                add("DM8", "Discrete Mathematics", "FILL",
                                "The number of linear permutations of n distinct objects is given by n _______.", null,
                                "factorial", "Permutations of n distinct objects = n! factorial.", null, null);
                add("DM9", "Discrete Mathematics", "CODING", "Write recursive logic computing factorial of integer n.",
                                null, "if(n <= 1) return 1; return n * factorial(n - 1);",
                                "Base case returns 1 when n <= 1, else n * factorial(n-1).", "if,factorial,return",
                                "long factorial(int n) {\n    if (n <= 1) return 1;\n    return n * factorial(n - 1);\n}");
                add("DM10", "Discrete Mathematics", "SHORT", "What is a bijection between two sets?", null,
                                "one to one and onto", "A function that is both injective and surjective.", null, null);
        }
}

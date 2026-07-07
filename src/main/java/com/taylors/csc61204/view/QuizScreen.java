package com.taylors.csc61204.view;

import com.taylors.csc61204.model.MultipleChoiceQuestion;
import com.taylors.csc61204.model.Question;
import com.taylors.csc61204.model.Quiz;
import com.taylors.csc61204.model.ShortAnswerQuestion;
import com.taylors.csc61204.model.TrueFalseQuestion;

import javax.swing.*;
import java.awt.*;
import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;

/**
 * Renders one question at a time and collects the student's answers.
 * Dispatches on {@link Question} subclass to render the appropriate input widget:
 * radio buttons for multiple-choice, True/False radios for true-false,
 * a single text field for short-answer.
 */
public class QuizScreen extends JPanel {

    private final Quiz quiz;
    private final Consumer<List<String>> onSubmit;
    private final List<String> answers = new ArrayList<>();
    private int currentIndex = 0;

    private final JLabel progressLabel = new JLabel();
    private final JLabel promptLabel = new JLabel();
    private final JPanel optionsPanel = new JPanel();
    private final JButton nextButton = new JButton("Next");

    // Fresh input widgets per question; only one of these is populated at a time.
    private ButtonGroup optionGroup;
    private JTextField shortAnswerField;

    public QuizScreen(Quiz quiz, Consumer<List<String>> onSubmit) {
        this.quiz = quiz;
        this.onSubmit = onSubmit;
        for (int i = 0; i < quiz.size(); i++) {
            answers.add(null);
        }
        buildLayout();
        renderQuestion();
    }

    private void buildLayout() {
        setLayout(new BorderLayout(15, 15));
        setBorder(BorderFactory.createEmptyBorder(20, 30, 20, 30));

        JPanel top = new JPanel(new BorderLayout());
        progressLabel.setFont(progressLabel.getFont().deriveFont(Font.BOLD, 14f));
        top.add(progressLabel, BorderLayout.WEST);
        top.add(new JLabel(quiz.getTitle(), SwingConstants.RIGHT), BorderLayout.EAST);
        add(top, BorderLayout.NORTH);

        promptLabel.setFont(promptLabel.getFont().deriveFont(Font.PLAIN, 16f));
        optionsPanel.setLayout(new BoxLayout(optionsPanel, BoxLayout.Y_AXIS));

        JPanel centre = new JPanel(new BorderLayout(10, 20));
        centre.add(promptLabel, BorderLayout.NORTH);
        centre.add(optionsPanel, BorderLayout.CENTER);
        add(centre, BorderLayout.CENTER);

        JPanel bottom = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        nextButton.addActionListener(e -> handleNext());
        bottom.add(nextButton);
        add(bottom, BorderLayout.SOUTH);
    }

    private void renderQuestion() {
        Question q = quiz.getQuestion(currentIndex);
        progressLabel.setText("Question " + (currentIndex + 1) + " of " + quiz.size());
        promptLabel.setText("<html><body style='width: 500px'>" + q.getPrompt() + "</body></html>");

        optionsPanel.removeAll();
        optionGroup = null;
        shortAnswerField = null;

        // Subclass dispatch — the IS-A hierarchy carries through to the View.
        if (q instanceof MultipleChoiceQuestion mcq) {
            renderMultipleChoice(mcq);
        } else if (q instanceof TrueFalseQuestion) {
            renderTrueFalse();
        } else if (q instanceof ShortAnswerQuestion) {
            renderShortAnswer();
        } else {
            throw new IllegalStateException("Unsupported question type: " + q.getClass());
        }

        nextButton.setText(isLastQuestion() ? "Submit" : "Next");
        optionsPanel.revalidate();
        optionsPanel.repaint();
    }

    private void renderMultipleChoice(MultipleChoiceQuestion mcq) {
        optionGroup = new ButtonGroup();
        for (String opt : mcq.getOptions()) {
            JRadioButton btn = new JRadioButton(opt);
            btn.setActionCommand(opt);
            optionGroup.add(btn);
            optionsPanel.add(btn);
        }
    }

    private void renderTrueFalse() {
        // "true" / "false" match the action commands that TrueFalseQuestion.isCorrect expects.
        optionGroup = new ButtonGroup();
        JRadioButton trueBtn = new JRadioButton("True");
        trueBtn.setActionCommand("true");
        JRadioButton falseBtn = new JRadioButton("False");
        falseBtn.setActionCommand("false");
        optionGroup.add(trueBtn);
        optionGroup.add(falseBtn);
        optionsPanel.add(trueBtn);
        optionsPanel.add(falseBtn);
    }

    private void renderShortAnswer() {
        shortAnswerField = new JTextField();
        shortAnswerField.setMaximumSize(new Dimension(Integer.MAX_VALUE, 32));
        JLabel hint = new JLabel("Type your answer:");
        optionsPanel.add(hint);
        optionsPanel.add(Box.createVerticalStrut(6));
        optionsPanel.add(shortAnswerField);
    }

    private void handleNext() {
        String response = currentResponse();
        if (response == null || response.isBlank()) {
            JOptionPane.showMessageDialog(this,
                    "Please provide an answer before continuing.",
                    "No answer given", JOptionPane.WARNING_MESSAGE);
            return;
        }
        answers.set(currentIndex, response);
        if (isLastQuestion()) {
            onSubmit.accept(List.copyOf(answers));
        } else {
            currentIndex++;
            renderQuestion();
        }
    }

    /** Reads the currently-shown input widget for the active question type. */
    private String currentResponse() {
        if (shortAnswerField != null) {
            return shortAnswerField.getText();
        }
        if (optionGroup != null && optionGroup.getSelection() != null) {
            return optionGroup.getSelection().getActionCommand();
        }
        return null;
    }

    private boolean isLastQuestion() {
        return currentIndex == quiz.size() - 1;
    }
}

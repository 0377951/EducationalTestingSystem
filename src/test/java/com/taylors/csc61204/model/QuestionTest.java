package com.taylors.csc61204.model;

import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Base-class tests focused on shared {@link Question} contracts (fields,
 * validation) via a concrete subclass. Subclass-specific answer-checking rules
 * are tested in their own files.
 */
class QuestionTest {

    private static Question makeSample() {
        return new MultipleChoiceQuestion(
                "2 + 2?", List.of("3", "4"), "4", "Math", "easy");
    }

    @Test
    void getters_validQuestion_returnAllFields() {
        Question q = makeSample();
        assertAll(
                () -> assertEquals("2 + 2?", q.getPrompt()),
                () -> assertEquals("Math", q.getCategory()),
                () -> assertEquals("easy", q.getDifficulty())
        );
    }

    @Test
    void constructor_blankPrompt_throwsIllegalArgumentException() {
        assertThrows(IllegalArgumentException.class, () -> new MultipleChoiceQuestion(
                "", List.of("a", "b"), "a", "Math", "easy"));
    }

    @Test
    void constructor_nullPrompt_throwsIllegalArgumentException() {
        assertThrows(IllegalArgumentException.class, () -> new MultipleChoiceQuestion(
                null, List.of("a", "b"), "a", "Math", "easy"));
    }

    @Test
    void polymorphism_allSubclassesTreatedAsQuestion_uniformIsCorrect() {
        // The IS-A / polymorphism proof: three concrete subclasses, one interface.
        Question mcq = new MultipleChoiceQuestion(
                "Capital of France?", List.of("Paris", "Rome"), "Paris", "Geo", "easy");
        Question tf = new TrueFalseQuestion("Sky is blue.", true, "Sci", "easy");
        Question sa = new ShortAnswerQuestion("Chemical symbol for water?", "H2O", "Sci", "medium");

        List<Question> all = List.of(mcq, tf, sa);
        assertAll(
                () -> assertTrue(all.get(0).isCorrect("Paris")),
                () -> assertTrue(all.get(1).isCorrect("true")),
                () -> assertTrue(all.get(2).isCorrect("h2o"))
        );
    }
}

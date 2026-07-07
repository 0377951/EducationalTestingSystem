package com.taylors.csc61204.persistence;

import com.taylors.csc61204.model.MultipleChoiceQuestion;
import com.taylors.csc61204.model.Question;
import com.taylors.csc61204.model.QuizResult;
import com.taylors.csc61204.model.ShortAnswerQuestion;
import com.taylors.csc61204.model.StudentPerformance;
import com.taylors.csc61204.model.TrueFalseQuestion;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.MethodSource;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.*;

class JsonDataStoreTest {

    @TempDir
    Path tempDir;

    private JsonDataStore store;

    @BeforeEach
    void setUp() {
        store = new JsonDataStore(tempDir);
    }

    // ---------- Questions ----------

    @Test
    void loadQuestions_noFile_returnsEmptyList() throws Exception {
        assertTrue(store.loadQuestions().isEmpty());
    }

    @Test
    void saveThenLoadQuestions_multipleChoice_roundTripsCorrectly() throws Exception {
        MultipleChoiceQuestion original = new MultipleChoiceQuestion(
                "Capital of France?", List.of("Paris", "Rome"), "Paris", "Geo", "easy");
        store.saveQuestions(List.of(original));

        List<Question> loaded = store.loadQuestions();

        assertAll(
                () -> assertEquals(1, loaded.size()),
                () -> assertInstanceOf(MultipleChoiceQuestion.class, loaded.get(0)),
                () -> assertEquals("Capital of France?", loaded.get(0).getPrompt()),
                () -> assertTrue(loaded.get(0).isCorrect("Paris"))
        );
    }

    @Test
    void saveThenLoadQuestions_trueFalse_roundTripsCorrectly() throws Exception {
        store.saveQuestions(List.of(
                new TrueFalseQuestion("Sky blue?", true, "Sci", "easy")));

        Question loaded = store.loadQuestions().get(0);

        assertAll(
                () -> assertInstanceOf(TrueFalseQuestion.class, loaded),
                () -> assertTrue(loaded.isCorrect("true")),
                () -> assertFalse(loaded.isCorrect("false"))
        );
    }

    @Test
    void saveThenLoadQuestions_shortAnswer_roundTripsCorrectly() throws Exception {
        store.saveQuestions(List.of(
                new ShortAnswerQuestion("Symbol for water?", "H2O", "Sci", "medium")));

        Question loaded = store.loadQuestions().get(0);

        assertAll(
                () -> assertInstanceOf(ShortAnswerQuestion.class, loaded),
                () -> assertTrue(loaded.isCorrect("h2o"))
        );
    }

    static Stream<Question> allSubclasses() {
        return Stream.of(
                new MultipleChoiceQuestion("MCQ", List.of("a", "b"), "a", "Cat", "easy"),
                new TrueFalseQuestion("TF", true, "Cat", "easy"),
                new ShortAnswerQuestion("SA", "answer", "Cat", "easy"));
    }

    @ParameterizedTest
    @MethodSource("allSubclasses")
    void roundTrip_eachSubclass_preservesClassIdentity(Question original) throws Exception {
        store.saveQuestions(List.of(original));
        Question loaded = store.loadQuestions().get(0);
        assertEquals(original.getClass(), loaded.getClass());
    }

    @Test
    void loadQuestions_malformedJson_throwsDataStoreException() throws IOException {
        Files.writeString(tempDir.resolve("questions.json"), "not { valid json",
                StandardCharsets.UTF_8);
        assertThrows(DataStoreException.class, store::loadQuestions);
    }

    @Test
    void loadQuestions_unknownTypeDiscriminator_throwsDataStoreException() throws IOException {
        Files.writeString(tempDir.resolve("questions.json"),
                "[{\"type\":\"UNSUPPORTED\",\"prompt\":\"?\",\"category\":\"c\",\"difficulty\":\"easy\"}]",
                StandardCharsets.UTF_8);
        assertThrows(DataStoreException.class, store::loadQuestions);
    }

    @Test
    void saveQuestions_targetDirDoesNotExist_isCreatedAutomatically() throws Exception {
        Path nested = tempDir.resolve("nested").resolve("data");
        JsonDataStore nestedStore = new JsonDataStore(nested);
        nestedStore.saveQuestions(List.of(
                new MultipleChoiceQuestion("Q", List.of("a", "b"), "a", "Cat", "easy")));
        assertTrue(Files.exists(nested.resolve("questions.json")));
    }

    // ---------- Performance ----------

    @Test
    void loadPerformance_noFile_returnsEmptyOptional() throws Exception {
        assertEquals(Optional.empty(), store.loadPerformance("S-999"));
    }

    @Test
    void saveThenLoadPerformance_withAttempts_roundTripsAllFields() throws Exception {
        StudentPerformance perf = new StudentPerformance("S-001");
        LocalDateTime ts = LocalDateTime.of(2026, 6, 1, 10, 30);
        perf.record(new QuizResult("Q1", "S-001", 8, 10, "Math", ts));
        perf.record(new QuizResult("Q2", "S-001", 5, 10, "Science", ts.plusDays(1)));

        store.savePerformance(perf);
        StudentPerformance loaded = store.loadPerformance("S-001").orElseThrow();

        assertAll(
                () -> assertEquals("S-001", loaded.getStudentId()),
                () -> assertEquals(2, loaded.attemptCount()),
                () -> assertEquals(65.0, loaded.averageScorePercent(), 0.001),
                () -> assertEquals(ts, loaded.getHistory().get(0).getCompletedAt())
        );
    }

    @Test
    void saveThenLoadPerformance_emptyHistory_roundTripsCleanly() throws Exception {
        store.savePerformance(new StudentPerformance("S-002"));
        StudentPerformance loaded = store.loadPerformance("S-002").orElseThrow();
        assertEquals(0, loaded.attemptCount());
    }

    @Test
    void loadPerformance_malformedTimestamp_throwsDataStoreException() throws IOException {
        Files.writeString(tempDir.resolve("performance-S-001.json"),
                "{\"studentId\":\"S-001\",\"history\":[{\"quizId\":\"Q\","
                        + "\"correctCount\":1,\"totalCount\":1,\"category\":\"c\","
                        + "\"completedAt\":\"not-a-date\"}]}",
                StandardCharsets.UTF_8);
        assertThrows(DataStoreException.class, () -> store.loadPerformance("S-001"));
    }
}

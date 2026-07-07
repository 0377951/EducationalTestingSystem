package com.taylors.csc61204.persistence;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.taylors.csc61204.model.MultipleChoiceQuestion;
import com.taylors.csc61204.model.Question;
import com.taylors.csc61204.model.QuizResult;
import com.taylors.csc61204.model.ShortAnswerQuestion;
import com.taylors.csc61204.model.StudentPerformance;
import com.taylors.csc61204.model.TrueFalseQuestion;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.LocalDateTime;
import java.time.format.DateTimeParseException;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Optional;

/**
 * File-backed {@link DataStore} that reads and writes JSON documents from a
 * caller-supplied data directory. UTF-8, pretty-printed, one file per concern.
 * <p>
 * <b>Layer discipline:</b> the model classes are Jackson-annotation-free.
 * All serialisation logic lives here — the mapping between JSON and each
 * {@link Question} subclass is dispatched on an explicit {@code "type"}
 * discriminator so new subclasses can be added without breaking old files.
 */
public class JsonDataStore implements DataStore {

    static final String TYPE_MCQ = "MULTIPLE_CHOICE";
    static final String TYPE_TF = "TRUE_FALSE";
    static final String TYPE_SHORT = "SHORT_ANSWER";

    private static final String QUESTIONS_FILE = "questions.json";
    private static final String PERFORMANCE_PREFIX = "performance-";
    private static final String PERFORMANCE_SUFFIX = ".json";

    private final Path dataDir;
    private final ObjectMapper mapper;

    public JsonDataStore(Path dataDir) {
        this.dataDir = Objects.requireNonNull(dataDir, "dataDir cannot be null");
        this.mapper = new ObjectMapper();
    }

    // ---------- Questions ----------

    @Override
    public List<Question> loadQuestions() throws DataStoreException {
        Path file = dataDir.resolve(QUESTIONS_FILE);
        if (!Files.exists(file)) {
            return List.of();
        }
        try {
            JsonNode root = mapper.readTree(readUtf8(file));
            if (!root.isArray()) {
                throw new DataStoreException("questions.json root must be a JSON array");
            }
            List<Question> out = new ArrayList<>();
            for (JsonNode node : root) {
                out.add(readQuestion(node));
            }
            return List.copyOf(out);
        } catch (IOException e) {
            throw new DataStoreException("Failed to read " + file, e);
        }
    }

    @Override
    public void saveQuestions(List<Question> questions) throws DataStoreException {
        Objects.requireNonNull(questions, "questions cannot be null");
        ensureDirExists();
        ArrayNode array = mapper.createArrayNode();
        for (Question q : questions) {
            array.add(writeQuestion(q));
        }
        writeJson(dataDir.resolve(QUESTIONS_FILE), array);
    }

    private Question readQuestion(JsonNode node) throws DataStoreException {
        String type = node.path("type").asText();
        String prompt = node.path("prompt").asText();
        String category = node.path("category").asText();
        String difficulty = node.path("difficulty").asText();
        try {
            return switch (type) {
                case TYPE_MCQ -> readMcq(node, prompt, category, difficulty);
                case TYPE_TF -> new TrueFalseQuestion(
                        prompt, node.path("correctAnswer").asBoolean(), category, difficulty);
                case TYPE_SHORT -> new ShortAnswerQuestion(
                        prompt, node.path("expectedAnswer").asText(), category, difficulty);
                default -> throw new DataStoreException("Unknown question type: " + type);
            };
        } catch (IllegalArgumentException e) {
            throw new DataStoreException("Malformed question entry: " + e.getMessage(), e);
        }
    }

    private MultipleChoiceQuestion readMcq(JsonNode node, String prompt,
                                           String category, String difficulty) {
        List<String> options = new ArrayList<>();
        for (JsonNode opt : node.path("options")) {
            options.add(opt.asText());
        }
        return new MultipleChoiceQuestion(
                prompt, options, node.path("correctAnswer").asText(), category, difficulty);
    }

    private ObjectNode writeQuestion(Question q) {
        ObjectNode node = mapper.createObjectNode();
        node.put("prompt", q.getPrompt());
        node.put("category", q.getCategory());
        node.put("difficulty", q.getDifficulty());
        if (q instanceof MultipleChoiceQuestion m) {
            node.put("type", TYPE_MCQ);
            ArrayNode opts = node.putArray("options");
            m.getOptions().forEach(opts::add);
            node.put("correctAnswer", m.getCorrectAnswer());
        } else if (q instanceof TrueFalseQuestion t) {
            node.put("type", TYPE_TF);
            node.put("correctAnswer", t.getCorrectAnswer());
        } else if (q instanceof ShortAnswerQuestion s) {
            node.put("type", TYPE_SHORT);
            node.put("expectedAnswer", s.getExpectedAnswer());
        } else {
            throw new IllegalStateException("Unsupported Question subclass: " + q.getClass());
        }
        return node;
    }

    // ---------- Performance ----------

    @Override
    public Optional<StudentPerformance> loadPerformance(String studentId) throws DataStoreException {
        Objects.requireNonNull(studentId, "studentId cannot be null");
        Path file = performanceFile(studentId);
        if (!Files.exists(file)) {
            return Optional.empty();
        }
        try {
            JsonNode root = mapper.readTree(readUtf8(file));
            StudentPerformance perf = new StudentPerformance(root.path("studentId").asText());
            for (JsonNode r : root.path("history")) {
                perf.record(readResult(r, studentId));
            }
            return Optional.of(perf);
        } catch (IOException | DateTimeParseException e) {
            throw new DataStoreException("Failed to read " + file, e);
        }
    }

    @Override
    public void savePerformance(StudentPerformance performance) throws DataStoreException {
        Objects.requireNonNull(performance, "performance cannot be null");
        ensureDirExists();
        ObjectNode root = mapper.createObjectNode();
        root.put("studentId", performance.getStudentId());
        ArrayNode history = root.putArray("history");
        for (QuizResult r : performance.getHistory()) {
            history.add(writeResult(r));
        }
        writeJson(performanceFile(performance.getStudentId()), root);
    }

    private QuizResult readResult(JsonNode node, String studentId) {
        return new QuizResult(
                node.path("quizId").asText(),
                studentId,
                node.path("correctCount").asInt(),
                node.path("totalCount").asInt(),
                node.path("category").asText(),
                LocalDateTime.parse(node.path("completedAt").asText()));
    }

    private ObjectNode writeResult(QuizResult r) {
        ObjectNode node = mapper.createObjectNode();
        node.put("quizId", r.getQuizId());
        node.put("correctCount", r.getCorrectCount());
        node.put("totalCount", r.getTotalCount());
        node.put("category", r.getCategory());
        node.put("completedAt", r.getCompletedAt().toString()); // ISO-8601
        return node;
    }

    private Path performanceFile(String studentId) {
        return dataDir.resolve(PERFORMANCE_PREFIX + studentId + PERFORMANCE_SUFFIX);
    }

    // ---------- I/O helpers ----------

    private String readUtf8(Path file) throws IOException {
        return Files.readString(file, StandardCharsets.UTF_8);
    }

    private void writeJson(Path file, JsonNode node) throws DataStoreException {
        try {
            String pretty = mapper.writerWithDefaultPrettyPrinter().writeValueAsString(node);
            Files.writeString(file, pretty, StandardCharsets.UTF_8);
        } catch (IOException e) {
            throw new DataStoreException("Failed to write " + file, e);
        }
    }

    private void ensureDirExists() throws DataStoreException {
        try {
            Files.createDirectories(dataDir);
        } catch (IOException e) {
            throw new DataStoreException("Failed to create data directory " + dataDir, e);
        }
    }
}

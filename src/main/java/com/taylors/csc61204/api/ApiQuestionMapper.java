package com.taylors.csc61204.api;

import com.fasterxml.jackson.databind.JsonNode;
import com.taylors.csc61204.model.Question;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Random;

/**
 * Maps the Open Trivia DB JSON response shape into the application's
 * {@link Question} domain model. Kept separate from the HTTP client so that
 * each class has a single responsibility (SRP).
 */
public class ApiQuestionMapper {

    private final Random random;

    public ApiQuestionMapper() {
        this(new Random());
    }

    /** Test constructor — accepts seeded Random for deterministic shuffling. */
    public ApiQuestionMapper(Random random) {
        this.random = random;
    }

    /**
     * Parses the full Trivia DB response and returns the contained questions.
     *
     * @param root the root JSON node of the response
     * @return list of {@link Question} (empty if no results)
     * @throws ApiException if the response has a non-zero response_code
     */
    public List<Question> mapResponse(JsonNode root) throws ApiException {
        int responseCode = root.path("response_code").asInt(-1);
        if (responseCode != 0) {
            throw new ApiException(
                    "Trivia API returned response_code " + responseCode, responseCode);
        }
        JsonNode results = root.path("results");
        List<Question> questions = new ArrayList<>();
        for (JsonNode node : results) {
            questions.add(mapSingle(node));
        }
        return List.copyOf(questions);
    }

    private Question mapSingle(JsonNode node) {
        String prompt = decodeHtml(node.path("question").asText());
        String category = node.path("category").asText("Unknown");
        String difficulty = node.path("difficulty").asText("medium");
        String correctAnswer = decodeHtml(node.path("correct_answer").asText());

        List<String> options = new ArrayList<>();
        options.add(correctAnswer);
        for (JsonNode wrong : node.path("incorrect_answers")) {
            options.add(decodeHtml(wrong.asText()));
        }
        Collections.shuffle(options, random);
        int correctIndex = options.indexOf(correctAnswer);

        return new Question(prompt, options, correctIndex, category, difficulty);
    }

    /** Trivia DB returns HTML entities — decode the common ones. */
    private String decodeHtml(String text) {
        return text
                .replace("&quot;", "\"")
                .replace("&#039;", "'")
                .replace("&amp;", "&")
                .replace("&lt;", "<")
                .replace("&gt;", ">");
    }
}

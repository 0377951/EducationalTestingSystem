package com.taylors.csc61204.api;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.taylors.csc61204.model.Question;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.time.Duration;
import java.util.List;
import java.util.Objects;

/**
 * Client for the Open Trivia DB public REST API.
 * <p>
 * Endpoint base: <a href="https://opentdb.com/api.php">https://opentdb.com/api.php</a>
 * <p>
 * Handles the HTTP transport layer only — JSON parsing is delegated to
 * {@link ApiQuestionMapper} (SRP). All network and protocol errors are wrapped
 * as {@link ApiException} so callers never see raw exceptions.
 */
public class TriviaApiClient {

    private static final String DEFAULT_BASE_URL = "https://opentdb.com/api.php";
    private static final int HTTP_OK_MIN = 200;
    private static final int HTTP_OK_MAX = 299;
    private static final int TIMEOUT_SECONDS = 10;

    private final String baseUrl;
    private final HttpClient httpClient;
    private final ObjectMapper objectMapper;
    private final ApiQuestionMapper questionMapper;

    public TriviaApiClient() {
        this(DEFAULT_BASE_URL,
             HttpClient.newBuilder().connectTimeout(Duration.ofSeconds(TIMEOUT_SECONDS)).build(),
             new ObjectMapper(),
             new ApiQuestionMapper());
    }

    /** Test constructor — allows injection of a mock HttpClient. */
    public TriviaApiClient(String baseUrl, HttpClient httpClient,
                           ObjectMapper objectMapper, ApiQuestionMapper questionMapper) {
        this.baseUrl = Objects.requireNonNull(baseUrl);
        this.httpClient = Objects.requireNonNull(httpClient);
        this.objectMapper = Objects.requireNonNull(objectMapper);
        this.questionMapper = Objects.requireNonNull(questionMapper);
    }

    /**
     * Fetches questions from the trivia API.
     *
     * @param amount number of questions to request (1–50)
     * @param difficulty optional difficulty filter ("easy"|"medium"|"hard"), null for any
     * @return list of parsed {@link Question}s
     * @throws ApiException on any network or protocol error
     */
    public List<Question> fetchQuestions(int amount, String difficulty) throws ApiException {
        if (amount < 1 || amount > 50) {
            throw new IllegalArgumentException("amount must be between 1 and 50");
        }
        URI uri = buildUri(amount, difficulty);
        HttpResponse<String> response = sendRequest(uri);
        return parseResponse(response);
    }

    /**
     * Lightweight check used for graceful degradation: returns false if the API
     * cannot be reached, true otherwise.
     */
    public boolean isAvailable() {
        try {
            fetchQuestions(1, null);
            return true;
        } catch (Exception e) {
            return false;
        }
    }

    private URI buildUri(int amount, String difficulty) {
        StringBuilder url = new StringBuilder(baseUrl).append("?amount=").append(amount);
        if (difficulty != null && !difficulty.isBlank()) {
            url.append("&difficulty=").append(difficulty);
        }
        return URI.create(url.toString());
    }

    private HttpResponse<String> sendRequest(URI uri) throws ApiException {
        HttpRequest request = HttpRequest.newBuilder(uri)
                .timeout(Duration.ofSeconds(TIMEOUT_SECONDS))
                .GET()
                .build();
        try {
            return httpClient.send(request, HttpResponse.BodyHandlers.ofString());
        } catch (IOException | InterruptedException e) {
            Thread.currentThread().interrupt();
            throw new ApiException("Unable to reach trivia API", e);
        }
    }

    private List<Question> parseResponse(HttpResponse<String> response) throws ApiException {
        int status = response.statusCode();
        if (status < HTTP_OK_MIN || status > HTTP_OK_MAX) {
            throw new ApiException("Trivia API HTTP error", status);
        }
        try {
            JsonNode root = objectMapper.readTree(response.body());
            return questionMapper.mapResponse(root);
        } catch (IOException e) {
            throw new ApiException("Malformed JSON from trivia API", e);
        }
    }
}

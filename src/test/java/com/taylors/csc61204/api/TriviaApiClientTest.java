package com.taylors.csc61204.api;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

import java.io.IOException;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.Random;

import static org.junit.jupiter.api.Assertions.*;

class TriviaApiClientTest {

    private FakeHttpClient fakeHttp;
    private TriviaApiClient client;

    @BeforeEach
    void setUp() {
        fakeHttp = new FakeHttpClient();
        client = new TriviaApiClient(
                "https://opentdb.com/api.php",
                fakeHttp,
                new ObjectMapper(),
                new ApiQuestionMapper(new Random(1)));
    }

    @Test
    void fetchQuestions_successfulResponse_returnsQuestions() throws Exception {
        fakeHttp.respondWith(200, """
                {"response_code":0,"results":[{
                  "category":"Math","difficulty":"easy","question":"2+2?",
                  "correct_answer":"4","incorrect_answers":["3","5","6"]
                }]}""");

        assertEquals(1, client.fetchQuestions(1, "easy").size());
    }

    @Test
    void fetchQuestions_status404_throwsApiExceptionWithStatusCode() {
        fakeHttp.respondWith(404, "Not Found");
        ApiException ex = assertThrows(ApiException.class,
                () -> client.fetchQuestions(5, null));
        assertEquals(404, ex.getStatusCode());
    }

    @Test
    void fetchQuestions_status500_throwsApiExceptionWithStatusCode() {
        fakeHttp.respondWith(500, "Server Error");
        ApiException ex = assertThrows(ApiException.class,
                () -> client.fetchQuestions(5, null));
        assertEquals(500, ex.getStatusCode());
    }

    @Test
    void fetchQuestions_networkFailure_throwsApiExceptionAsNetworkFailure() {
        fakeHttp.throwOnSend(new IOException("connection refused"));
        ApiException ex = assertThrows(ApiException.class,
                () -> client.fetchQuestions(5, null));
        assertTrue(ex.isNetworkFailure());
    }

    @Test
    void fetchQuestions_malformedJson_throwsApiException() {
        fakeHttp.respondWith(200, "this is not json {{{");
        assertThrows(ApiException.class, () -> client.fetchQuestions(1, null));
    }

    @ParameterizedTest
    @ValueSource(ints = {0, -1, 51, 100})
    void fetchQuestions_invalidAmount_throwsIllegalArgumentException(int badAmount) {
        assertThrows(IllegalArgumentException.class,
                () -> client.fetchQuestions(badAmount, null));
    }

    @Test
    void isAvailable_workingApi_returnsTrue() {
        fakeHttp.respondWith(200, "{\"response_code\":0,\"results\":[]}");
        assertTrue(client.isAvailable());
    }

    @Test
    void isAvailable_apiDown_returnsFalseForGracefulDegradation() {
        fakeHttp.throwOnSend(new IOException("down"));
        assertFalse(client.isAvailable());
    }

    /** Minimal fake HttpClient — avoids real network calls in tests. */
    private static class FakeHttpClient extends HttpClient {
        private int status = 200;
        private String body = "";
        private IOException toThrow;

        void respondWith(int status, String body) {
            this.status = status;
            this.body = body;
            this.toThrow = null;
        }

        void throwOnSend(IOException ex) {
            this.toThrow = ex;
        }

        @Override
        @SuppressWarnings("unchecked")
        public <T> HttpResponse<T> send(HttpRequest request,
                                        HttpResponse.BodyHandler<T> handler) throws IOException {
            if (toThrow != null) throw toThrow;
            return (HttpResponse<T>) new FakeHttpResponse(status, body);
        }

        // The remaining methods are unused — defaulted to throw.
        @Override public java.util.Optional<java.net.CookieHandler> cookieHandler() { return java.util.Optional.empty(); }
        @Override public java.util.Optional<java.time.Duration> connectTimeout() { return java.util.Optional.empty(); }
        @Override public Redirect followRedirects() { return Redirect.NEVER; }
        @Override public java.util.Optional<java.net.ProxySelector> proxy() { return java.util.Optional.empty(); }
        @Override public javax.net.ssl.SSLContext sslContext() { return null; }
        @Override public javax.net.ssl.SSLParameters sslParameters() { return null; }
        @Override public java.util.Optional<java.net.Authenticator> authenticator() { return java.util.Optional.empty(); }
        @Override public Version version() { return Version.HTTP_2; }
        @Override public java.util.Optional<java.util.concurrent.Executor> executor() { return java.util.Optional.empty(); }
        @Override public <T> java.util.concurrent.CompletableFuture<HttpResponse<T>> sendAsync(
                HttpRequest req, HttpResponse.BodyHandler<T> h) { throw new UnsupportedOperationException(); }
        @Override public <T> java.util.concurrent.CompletableFuture<HttpResponse<T>> sendAsync(
                HttpRequest req, HttpResponse.BodyHandler<T> h, HttpResponse.PushPromiseHandler<T> p) {
            throw new UnsupportedOperationException(); }
    }

    private record FakeHttpResponse(int status, String body) implements HttpResponse<String> {
        @Override public int statusCode() { return status; }
        @Override public String body() { return body; }
        @Override public HttpRequest request() { return null; }
        @Override public java.util.Optional<HttpResponse<String>> previousResponse() { return java.util.Optional.empty(); }
        @Override public java.net.http.HttpHeaders headers() { return java.net.http.HttpHeaders.of(java.util.Map.of(), (a, b) -> true); }
        @Override public java.util.Optional<javax.net.ssl.SSLSession> sslSession() { return java.util.Optional.empty(); }
        @Override public java.net.URI uri() { return null; }
        @Override public HttpClient.Version version() { return HttpClient.Version.HTTP_2; }
    }
}

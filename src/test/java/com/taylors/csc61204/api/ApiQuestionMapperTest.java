package com.taylors.csc61204.api;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.taylors.csc61204.model.MultipleChoiceQuestion;
import com.taylors.csc61204.model.Question;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

import java.util.List;
import java.util.Random;

import static org.junit.jupiter.api.Assertions.*;

class ApiQuestionMapperTest {

    private ObjectMapper json;
    private ApiQuestionMapper mapper;

    @BeforeEach
    void setUp() {
        json = new ObjectMapper();
        mapper = new ApiQuestionMapper(new Random(42));
    }

    @Test
    void mapResponse_validSingleQuestion_returnsOneMultipleChoiceQuestion() throws Exception {
        String body = """
                {"response_code":0,"results":[{
                  "category":"Science",
                  "difficulty":"easy",
                  "question":"What is H2O?",
                  "correct_answer":"Water",
                  "incorrect_answers":["Hydrogen","Oxygen","Salt"]
                }]}""";
        JsonNode root = json.readTree(body);

        List<Question> result = mapper.mapResponse(root);
        MultipleChoiceQuestion q = (MultipleChoiceQuestion) result.get(0);

        assertAll(
                () -> assertEquals(1, result.size()),
                () -> assertEquals("What is H2O?", q.getPrompt()),
                () -> assertEquals("Science", q.getCategory()),
                () -> assertEquals(4, q.getOptions().size())
        );
    }

    @Test
    void mapResponse_emptyResults_returnsEmptyList() throws Exception {
        JsonNode root = json.readTree("{\"response_code\":0,\"results\":[]}");
        assertTrue(mapper.mapResponse(root).isEmpty());
    }

    @ParameterizedTest
    @ValueSource(ints = {1, 2, 3, 4})
    void mapResponse_nonZeroResponseCode_throwsApiException(int badCode) throws Exception {
        String body = "{\"response_code\":" + badCode + ",\"results\":[]}";
        JsonNode root = json.readTree(body);
        assertThrows(ApiException.class, () -> mapper.mapResponse(root));
    }

    @Test
    void mapResponse_htmlEntitiesInQuestion_areDecoded() throws Exception {
        String body = """
                {"response_code":0,"results":[{
                  "category":"Lit",
                  "difficulty":"easy",
                  "question":"What did he say &quot;hello&quot;?",
                  "correct_answer":"Yes &amp; No",
                  "incorrect_answers":["A","B","C"]
                }]}""";
        JsonNode root = json.readTree(body);
        MultipleChoiceQuestion q = (MultipleChoiceQuestion) mapper.mapResponse(root).get(0);
        assertAll(
                () -> assertEquals("What did he say \"hello\"?", q.getPrompt()),
                () -> assertTrue(q.getOptions().contains("Yes & No"))
        );
    }

    @Test
    void mapResponse_correctAnswer_isMarkedCorrectByPolymorphicIsCorrect() throws Exception {
        String body = """
                {"response_code":0,"results":[{
                  "category":"Math",
                  "difficulty":"easy",
                  "question":"2+2?",
                  "correct_answer":"4",
                  "incorrect_answers":["3","5","6"]
                }]}""";
        JsonNode root = json.readTree(body);
        Question q = mapper.mapResponse(root).get(0);
        assertTrue(q.isCorrect("4"));
    }
}

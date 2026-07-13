# Architecture

Every production class in the codebase, its layer, and its single responsibility.
Used by the team when writing the report and preparing for individual Q&A.

## Model layer — `com.taylors.csc61204.model`

| Class | Responsibility |
|---|---|
| `Question` | Abstract root of the question hierarchy. Holds prompt/category/difficulty. Declares `boolean isCorrect(String response)`. |
| `MultipleChoiceQuestion extends Question` | Multiple-choice question. Correct if the response equals the stored correct answer. |
| `TrueFalseQuestion extends Question` | True/false question. Parses the response as a boolean and compares. |
| `ShortAnswerQuestion extends Question` | Short-answer question. Case-insensitive, trimmed string match. |
| `Quiz` | Immutable collection of questions with title, time limit, and a polymorphic `grade(List<String>)` method. |
| `QuestionBank` | Categorised in-memory pool of questions; supports filter-by-category and filter-by-difficulty. |
| `StudentPerformance` | A student's quiz history plus derived metrics (average score, weak categories). |
| `QuizResult` | Immutable record of a single attempt (score, category, ISO-8601 timestamp). |

## Pattern layer — `com.taylors.csc61204.pattern`

| Class | Responsibility |
|---|---|
| `builder.QuizBuilder` | Fluent builder for `Quiz` — hides the multi-step construction (pick questions, apply metadata, validate). Demonstrates the Single Responsibility Principle. |
| `strategy.QuestionSelectionStrategy` | Strategy interface. One method: `select(bank, count)`. Demonstrates the Open/Closed Principle. |
| `strategy.RandomSelectionStrategy` | Uniform random selection (seedable for reproducible tests). |
| `strategy.DifficultyBasedStrategy` | Picks questions of a target difficulty level. |
| `strategy.WeaknessFocusedStrategy` | Adaptive: inspects a `StudentPerformance`, picks from that student's weak categories. Falls back to a supplied strategy (random by default) when history is empty. `QuizController` uses it with a `DifficultyBasedStrategy` fallback. |

## API layer — `com.taylors.csc61204.api`

| Class | Responsibility |
|---|---|
| `TriviaApiClient` | HTTP transport for Open Trivia DB. Uses `HttpClient` behind the scenes; injectable for test mocking. Returns `List<Question>` or throws `ApiException`. |
| `ApiQuestionMapper` | Converts Open Trivia DB JSON into `MultipleChoiceQuestion` instances. Decodes HTML entities. Kept separate from `TriviaApiClient` (SRP). |
| `ApiException` | Checked exception carrying either an HTTP status code or a network-failure flag. |

## Persistence layer — `com.taylors.csc61204.persistence`

| Class | Responsibility |
|---|---|
| `DataStore` | Storage abstraction — 4 methods: load/save questions, load/save performance. Demonstrates the Dependency Inversion Principle. |
| `JsonDataStore` | File-backed implementation. UTF-8 JSON, pretty-printed, ISO-8601 timestamps, `type` discriminator for `Question` subclass reconstruction. |
| `DataStoreException` | Checked exception with user-friendly message (never surfaces a stack trace). |

## Service layer — `com.taylors.csc61204.service`

| Class | Responsibility |
|---|---|
| `QuizService` | Orchestrates quiz generation (via Builder + Strategy), grading, and API refresh. The only class the controller calls for quiz operations. |
| `FeedbackService` | Interprets a `StudentPerformance` into human-readable text (per-attempt summary, recommendation, progress report). |

## Controller layer — `com.taylors.csc61204.controller`

| Class | Responsibility |
|---|---|
| `QuizController` | Wires the View to Services and the DataStore. Validates input, catches errors, persists results, coordinates screen transitions. |

## View layer — `com.taylors.csc61204.view`

| Class | Responsibility |
|---|---|
| `MainFrame` | Top-level `JFrame` hosting a `CardLayout` that swaps between screens. Knows nothing about business logic. |
| `StartScreen` | Landing screen — difficulty + question count picker. |
| `QuizScreen` | Renders one MCQ question at a time; collects answers; submits. |
| `ResultsScreen` | Shows score, feedback summary, and progress report; offers retry. |

## Bootstrap

| Class | Responsibility |
|---|---|
| `App` | Entry point. Constructs the object graph (DataStore → Bank → Services → Frame → Controller) and starts the Swing event-dispatch thread. |

## MVC mapping (defensible in Q&A)

| Layer | Packages |
|---|---|
| **Model** | `model/`, `pattern/`, `service/`, `persistence/`, `api/` |
| **View** | `view/` (Swing screens only, no business logic) |
| **Controller** | `controller/QuizController` (the only class allowed to call services from the GUI side) |

## SOLID demonstrations (for report Section 4)

| Principle | Concrete example |
|---|---|
| **S**RP | `QuizBuilder` — construction logic separated from `Quiz` representation. `ApiQuestionMapper` — parsing separated from HTTP transport. `FeedbackService` — interpretation separated from `QuizService` grading. |
| **O**CP | `QuestionSelectionStrategy` — added `WeaknessFocusedStrategy` in PR #8 without modifying any existing class. |
| **L**SP | Every `Question` subclass is substitutable for the base: `Quiz.grade()` accepts them uniformly via `isCorrect(String)`. |
| **I**SP | `DataStore` has only the 4 methods callers need; `QuestionSelectionStrategy` has one method. No fat interfaces. |
| **D**IP | `QuizController` depends on the `DataStore` interface, not `JsonDataStore`. `TriviaApiClient` depends on `HttpClient` (injectable). Services depend on abstractions. |

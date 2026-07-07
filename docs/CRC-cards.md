# CRC Cards

Class–Responsibility–Collaborator cards for the four architecturally-significant
classes in the system. Used in the design section of the report.

---

## Quiz

**Responsibilities**
- Hold an ordered, immutable list of `Question`s along with title, id, and time limit
- Grade a list of student responses polymorphically, returning the number of correct answers
- Validate its own construction (non-blank id/title, ≥ 1 question, positive time limit)

**Collaborators**
- `Question` — delegates answer checking to each question's own `isCorrect(String)`
- `QuizBuilder` — the only class expected to construct `Quiz` in production code
- `QuizService` — asks `Quiz.grade(...)` to score a submission

---

## QuizBuilder

**Responsibilities**
- Provide a fluent API (`withTitle`, `withCount`, `withStrategy`, …) for constructing a `Quiz`
- Apply sensible defaults when settings are not supplied
- Delegate question picking to the currently-configured `QuestionSelectionStrategy`
- Validate the final configuration before returning a `Quiz`

**Collaborators**
- `QuestionBank` — source pool of questions
- `QuestionSelectionStrategy` — decides *which* questions to pick
- `Quiz` — the product being built

---

## QuestionSelectionStrategy

**Responsibilities**
- Given a `QuestionBank` and a desired count, return exactly that many questions selected according to a specific algorithm
- Throw `IllegalArgumentException` when the pool cannot satisfy the request

**Collaborators**
- `QuestionBank` — read-only source of questions
- `Question` — the objects being returned
- `StudentPerformance` — consulted by the adaptive `WeaknessFocusedStrategy` implementation
- `QuizBuilder` — the primary caller

---

## QuizController

**Responsibilities**
- Own the flow between the three GUI screens (Start → Quiz → Results)
- Validate user input from the View before invoking any service
- Route submissions to `QuizService` for grading, then persist the result via `DataStore`
- Convert service/API/persistence exceptions into user-friendly dialogs — never a raw stack trace

**Collaborators**
- `MainFrame` — the View surface it drives
- `QuizService` — quiz generation, grading, API refresh
- `FeedbackService` — text summaries and recommendations
- `StudentPerformance` — the in-memory record it updates on each submission
- `DataStore` — persistence port for saving progress

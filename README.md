# Educational Testing System

CSC61204 Software Construction — group assignment, Scenario 2.
Aligned with UN SDG 4 (Quality Education), targets 4.1 and 4.6.

A desktop quiz application that generates quizzes from a categorised question
bank, evaluates answers, tracks a student's performance across sessions, and
delivers personalised feedback that focuses future quizzes on weak topics.

## Features

- **Categorised question bank** with three question types (multiple-choice,
  true/false, short-answer)
- **Builder pattern** for fluent quiz construction
- **Strategy pattern** for pluggable question-selection algorithms
  (`Random`, `DifficultyBased`, `WeaknessFocused`)
- **External API integration** with [Open Trivia DB](https://opentdb.com/api.php)
  — fetches fresh questions on demand, with graceful degradation to the seed
  bank when the API is unavailable
- **JSON persistence** — questions and per-student performance survive restarts
- **Swing GUI** with three screens (Start / Quiz / Results) and MVC separation
- **148+ JUnit 5 tests**, JaCoCo coverage ≥ 75%, GitHub Actions CI

## Build & run

Prerequisites: JDK 25, Maven 3.9+ (IntelliJ IDEA bundles Maven; install JDK 25 via `brew install --cask temurin@25` or IntelliJ's built-in JDK downloader).

```bash
mvn clean package
java -jar target/educational-testing-system-1.0.0.jar
```

The app works offline on first launch using the seed questions in
`data/questions.json`. When you take a quiz, an internet connection lets the
app top up the bank from Open Trivia DB; without one, the seed questions are
used and a friendly message is shown.

## Opening in IntelliJ IDEA

1. **File → Open** and select the `pom.xml` at the project root
2. When prompted, choose **Open as Project**
3. Enable Maven auto-import if it isn't already
4. Set a Run Configuration pointing at
   `src/main/java/com/taylors/csc61204/App.java`
5. From the Maven tool window, run
   `clean → test → jacoco:report` to see coverage in
   `target/site/jacoco/index.html`

## Project layout

```
src/main/java/com/taylors/csc61204/
├── App.java                # Entry point + dependency injection
├── model/                  # Question hierarchy, Quiz, Student, results
├── pattern/
│   ├── builder/            # QuizBuilder (Builder pattern)
│   └── strategy/           # QuestionSelectionStrategy + 3 implementations
├── api/                    # Open Trivia DB client + mapper + exception
├── persistence/            # DataStore interface + JsonDataStore
├── service/                # QuizService + FeedbackService
├── controller/             # QuizController (MVC)
└── view/                   # Swing screens (MVC)

data/
└── questions.json          # Seed question bank (18 questions, 3 types, 6 categories)

docs/
├── UML.md                  # Class diagram (Mermaid)
└── CRC-cards.md            # Class–Responsibility–Collaborator cards

.github/workflows/ci.yml    # Maven build + tests on push / PR
```

Detailed class-by-class responsibilities live in
[`ARCHITECTURE.md`](ARCHITECTURE.md).

## Testing

```bash
mvn test                    # run all tests
mvn verify                  # tests + JaCoCo coverage gate
```

The Swing presentation layer (`view/`, `controller/`, `App`) is excluded from
the JaCoCo coverage gate. Those classes cannot be instantiated on the headless
GitHub Actions runner and are verified via the live demo and screenshots
required by Section 5.1 of the report.

One test is intentionally `@Disabled` to document a known limitation
(short-answer semantic equivalence not supported). See
`ShortAnswerQuestionTest.isCorrect_shortAnswerSemanticEquivalent_returnsTrue`.

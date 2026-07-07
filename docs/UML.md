# UML Class Diagram

Rendered with Mermaid. GitHub renders this natively in the web view.
Satisfies the rubric requirement for a UML with ≥ 6 classes showing IS-A and HAS-A relationships.

## Core diagram

```mermaid
classDiagram
    class Question {
        <<abstract>>
        -String prompt
        -String category
        -String difficulty
        +isCorrect(response) boolean
    }

    class MultipleChoiceQuestion {
        -List~String~ options
        -String correctAnswer
        +isCorrect(response) boolean
    }

    class TrueFalseQuestion {
        -boolean correctAnswer
        +isCorrect(response) boolean
    }

    class ShortAnswerQuestion {
        -String expectedAnswer
        +isCorrect(response) boolean
    }

    class Quiz {
        -String quizId
        -String title
        -int timeLimitSeconds
        +grade(responses) int
    }

    class QuestionBank {
        +add(Question)
        +byCategory(String) List~Question~
        +byDifficulty(String) List~Question~
    }

    class QuizBuilder {
        -QuestionBank bank
        -QuestionSelectionStrategy strategy
        +withStrategy(...) QuizBuilder
        +withCount(int) QuizBuilder
        +build() Quiz
    }

    class QuestionSelectionStrategy {
        <<interface>>
        +select(bank, count) List~Question~
    }

    class RandomSelectionStrategy
    class DifficultyBasedStrategy
    class WeaknessFocusedStrategy

    class Student {
        -String studentId
        -List~QuizResult~ history
        +record(QuizResult)
        +averageScorePercent() double
        +weakCategories() List~String~
    }

    class QuizResult {
        -String quizId
        -int correctCount
        -int totalCount
        -String category
        -LocalDateTime completedAt
    }

    %% IS-A relationships
    Question <|-- MultipleChoiceQuestion
    Question <|-- TrueFalseQuestion
    Question <|-- ShortAnswerQuestion
    QuestionSelectionStrategy <|.. RandomSelectionStrategy
    QuestionSelectionStrategy <|.. DifficultyBasedStrategy
    QuestionSelectionStrategy <|.. WeaknessFocusedStrategy

    %% HAS-A relationships
    Quiz "1" o-- "1..*" Question : contains
    QuestionBank "1" o-- "*" Question : holds
    Student "1" o-- "*" QuizResult : history
    QuizBuilder --> QuestionBank : reads
    QuizBuilder --> QuestionSelectionStrategy : uses
    WeaknessFocusedStrategy --> Student : consults
```

> **Note:** `Student` in this diagram maps to the class named
> `StudentPerformance` in the code — the diagram uses the shorter conceptual
> name for clarity, but the responsibilities and fields are identical.

## Relationship inventory (for report Section 3.1)

| Type | Instances |
|---|---|
| **IS-A** | `MultipleChoiceQuestion` / `TrueFalseQuestion` / `ShortAnswerQuestion` all extend `Question`. `RandomSelectionStrategy` / `DifficultyBasedStrategy` / `WeaknessFocusedStrategy` all implement `QuestionSelectionStrategy`. |
| **HAS-A (composition)** | `Quiz` has a list of `Question`. `QuestionBank` has a list of `Question`. `Student` has a list of `QuizResult`. |
| **HAS-A (association)** | `QuizBuilder` uses `QuestionBank` and `QuestionSelectionStrategy`. `WeaknessFocusedStrategy` reads from `Student`. |

Ten named classes appear in the diagram — comfortably above the rubric's
minimum of six.

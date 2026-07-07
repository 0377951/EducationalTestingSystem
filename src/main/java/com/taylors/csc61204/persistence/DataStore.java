package com.taylors.csc61204.persistence;

import com.taylors.csc61204.model.Question;
import com.taylors.csc61204.model.StudentPerformance;

import java.util.List;
import java.util.Optional;

/**
 * Abstract persistence port. Kept as an interface so the storage backend
 * (currently a JSON file store; could be swapped for SQLite or a REST backend
 * without touching services or controllers) is a plug-in choice.
 * <p>
 * This is the Dependency Inversion Principle in the codebase: services depend
 * on this abstraction, not on the concrete implementation.
 */
public interface DataStore {

    /** Load all persisted questions. Returns an empty list if the store is empty. */
    List<Question> loadQuestions() throws DataStoreException;

    /** Persist the given questions, replacing any previous contents. */
    void saveQuestions(List<Question> questions) throws DataStoreException;

    /**
     * Load a student's performance history. Returns {@link Optional#empty()}
     * if no record exists for that student (i.e. first visit).
     */
    Optional<StudentPerformance> loadPerformance(String studentId) throws DataStoreException;

    /** Persist a student's performance history. */
    void savePerformance(StudentPerformance performance) throws DataStoreException;
}

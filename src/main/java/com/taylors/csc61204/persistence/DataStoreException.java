package com.taylors.csc61204.persistence;

/**
 * Checked exception raised by {@link DataStore} operations. Wraps the underlying
 * {@link java.io.IOException} or JSON-parsing failure with a message suitable
 * for surfacing to the user (never a raw stack trace).
 */
public class DataStoreException extends Exception {

    public DataStoreException(String message, Throwable cause) {
        super(message, cause);
    }

    public DataStoreException(String message) {
        super(message);
    }
}

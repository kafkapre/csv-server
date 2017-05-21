package org.kafkapre.csvserver.persistence.api;

public class DataProcessingPersistenceException extends PersistenceException {

    private static final long serialVersionUID = 1319996664823848752L;

    public DataProcessingPersistenceException() {
    }

    public DataProcessingPersistenceException(String s) {
        super(s);
    }

    public DataProcessingPersistenceException(String s, Throwable throwable) {
        super(s, throwable);
    }

    public DataProcessingPersistenceException(Throwable throwable) {
        super(throwable);
    }

    public DataProcessingPersistenceException(String s, Throwable throwable, boolean b, boolean b1) {
        super(s, throwable, b, b1);
    }
}

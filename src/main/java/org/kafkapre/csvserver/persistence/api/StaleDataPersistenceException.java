package org.kafkapre.csvserver.persistence.api;

public class StaleDataPersistenceException extends PersistenceException{

    private static final long serialVersionUID = 1748621210215571839L;

    public StaleDataPersistenceException() {
    }

    public StaleDataPersistenceException(String s) {
        super(s);
    }

    public StaleDataPersistenceException(String s, Throwable throwable) {
        super(s, throwable);
    }

    public StaleDataPersistenceException(Throwable throwable) {
        super(throwable);
    }

    public StaleDataPersistenceException(String s, Throwable throwable, boolean b, boolean b1) {
        super(s, throwable, b, b1);
    }
}

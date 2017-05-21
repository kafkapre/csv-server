package org.kafkapre.csvserver.persistence.api;

public class ConnectionPersistenceException extends PersistenceException {

    private static final long serialVersionUID = 3397745086706241142L;

    public ConnectionPersistenceException() {
        this("Persistence connection failed.");
    }

    public ConnectionPersistenceException(String s) {
        super(s);
    }

    public ConnectionPersistenceException(String s, Throwable throwable) {
        super(s, throwable);
    }

    public ConnectionPersistenceException(Throwable throwable) {
        super(throwable);
    }

    public ConnectionPersistenceException(String s, Throwable throwable, boolean b, boolean b1) {
        super(s, throwable, b, b1);
    }
}

package org.kafkapre.csvserver.persistence.api;


public abstract class PersistenceException extends Exception {

    private static final long serialVersionUID = 4160570300765574900L;

    public PersistenceException() {
    }

    public PersistenceException(String s) {
        super(s);
    }

    public PersistenceException(String s, Throwable throwable) {
        super(s, throwable);
    }

    public PersistenceException(Throwable throwable) {
        super(throwable);
    }

    public PersistenceException(String s, Throwable throwable, boolean b, boolean b1) {
        super(s, throwable, b, b1);
    }
}

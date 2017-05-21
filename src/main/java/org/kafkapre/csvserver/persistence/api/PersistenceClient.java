package org.kafkapre.csvserver.persistence.api;

import org.kafkapre.csvserver.model.CsvDocument;
import org.kafkapre.csvserver.model.CsvWrapper;

import java.util.List;
import java.util.Optional;
import java.util.Set;


public interface PersistenceClient {

    Set<String> getAllIds() throws ConnectionPersistenceException;

    void storeCsvWrapper(CsvWrapper csvWrapper) throws PersistenceException;

    Optional<CsvDocument> getCsvDocument(int id) throws PersistenceException;

    List<String> getAllLines(int id) throws ConnectionPersistenceException;

    List<String> getLines(int id, int from, int to) throws ConnectionPersistenceException;

    boolean ping();
}

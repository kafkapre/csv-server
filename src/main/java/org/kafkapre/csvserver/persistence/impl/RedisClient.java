package org.kafkapre.csvserver.persistence.impl;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.kafkapre.csvserver.model.CsvDocument;
import org.kafkapre.csvserver.model.CsvWrapper;
import org.kafkapre.csvserver.persistence.api.ConnectionPersistenceException;
import org.kafkapre.csvserver.persistence.api.DataProcessingPersistenceException;
import org.kafkapre.csvserver.persistence.api.PersistenceClient;
import org.kafkapre.csvserver.persistence.api.PersistenceException;
import org.kafkapre.csvserver.persistence.api.StaleDataPersistenceException;
import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisPool;
import redis.clients.jedis.Transaction;

import java.io.IOException;
import java.util.List;
import java.util.Optional;
import java.util.Set;

public class RedisClient implements PersistenceClient {

    private final JedisPool pool;
    private final ObjectMapper mapper;

    public RedisClient(String host, int port) {
        this.pool = new JedisPool(host, port);
        this.mapper = new ObjectMapper();
    }

    @Override
    public void finalize() {
        pool.destroy();
    }


    @Override
    public Set<String> getAllIds() throws ConnectionPersistenceException {
        try (Jedis jedis = pool.getResource()) {
            return jedis.smembers(CsvDocument.determinePath(""));
        } catch (RuntimeException ex) {
            throw new ConnectionPersistenceException();
        }
    }

    @Override
    public void storeCsvWrapper(CsvWrapper csvWrapper) throws PersistenceException {
        String json = csvDocumentToJson(csvWrapper.getCsvDocument());
        String[] linesArr = csvWrapper.getLines().toArray(new String[csvWrapper.getLines().size()]);

        String linesKey = csvWrapper.getCsvDocument().getLinesPath();
        String documentKey = csvWrapper.getCsvDocument().getPath();

        try (Jedis jedis = pool.getResource()) {
            jedis.watch(linesKey, documentKey);    // watches until exec is called
            Transaction t = jedis.multi();
            try {
                t.del(linesKey);
                t.set(documentKey, json);
                t.sadd(CsvDocument.determinePath(""), documentKey);
                t.rpush(linesKey, linesArr);
                List<Object> res = t.exec();
                if (res == null || res.size() != 4) {
                    throw new StaleDataPersistenceException("Csv data with id [%] cannot be stored, because they are updated now.");
                }
            } catch (RuntimeException ex) {
                t.discard();
                throw new ConnectionPersistenceException();
            }
        }
    }

    @Override
    public Optional<CsvDocument> getCsvDocument(int id) throws PersistenceException {
        try (Jedis jedis = pool.getResource()) {
            String str = jedis.get(CsvDocument.determinePath(id));
            return Optional.ofNullable(csvDocumentFromJson(str));
        } catch (RuntimeException ex) {
            throw new ConnectionPersistenceException(ex);
        }
    }

    @Override
    public List<String> getAllLines(int id) throws ConnectionPersistenceException {
        return getLines(id, 0, -1);
    }

    @Override
    public List<String> getLines(int id, int from, int to) throws ConnectionPersistenceException {
        if (from < 0) {
            from = 0;
        }
        try (Jedis jedis = pool.getResource()) {
            return jedis.lrange(CsvDocument.determineLinesPath(id), from, to);
        } catch (RuntimeException ex) {
            throw new ConnectionPersistenceException();
        }
    }

    @Override
    public boolean ping() {
        String response;
        try (Jedis jedis = pool.getResource()) {
            response = jedis.ping();
        }
        return "PONG".equalsIgnoreCase(response);
    }

    private String csvDocumentToJson(CsvDocument csvDocument) throws DataProcessingPersistenceException {
        if(csvDocument == null){
            return null;
        }
        try {
            return mapper.writeValueAsString(csvDocument);
        } catch (JsonProcessingException e) {
            throw new DataProcessingPersistenceException(e);

        }
    }

    private CsvDocument csvDocumentFromJson(String str) throws DataProcessingPersistenceException {
        if(str == null || str.isEmpty()){
            return null;
        }
        try {
            return mapper.readValue(str, CsvDocument.class);
        } catch (IOException e) {
            throw new DataProcessingPersistenceException(e);
        }
    }


}

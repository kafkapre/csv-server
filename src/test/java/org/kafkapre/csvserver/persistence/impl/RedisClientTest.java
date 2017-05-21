package org.kafkapre.csvserver.persistence.impl;

import org.junit.Before;
import org.junit.Test;
import org.kafkapre.csvserver.AbstractRedisTest;
import org.kafkapre.csvserver.model.CsvDocument;
import org.kafkapre.csvserver.model.CsvWrapper;
import org.kafkapre.csvserver.model.CsvWrapperFactory;
import org.kafkapre.csvserver.persistence.api.ConnectionPersistenceException;
import org.kafkapre.csvserver.persistence.api.PersistenceClient;
import org.kafkapre.csvserver.persistence.api.PersistenceException;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;


public class RedisClientTest extends AbstractRedisTest {

    private PersistenceClient redisClient;

    @Before
    public void setUp() {
        redisClient = new RedisClient("localhost", port);
    }

    @Test
    public void simpleTest() throws PersistenceException {
        String[] lines =  new String[10];
        for (int i = 0; i < 10; i++) {
            lines[i] = "line " + i;
        }
        String[] headers = new String[]{"name", "surename"};
        CsvWrapper csvWrapper = CsvWrapperFactory.build(1, headers, lines);

        redisClient.storeCsvWrapper(csvWrapper);

        assertThat(redisClient.getAllLines(csvWrapper.getCsvDocument().getId())).containsExactly(lines);

        Optional actualCsvMetadata = redisClient.getCsvDocument(csvWrapper.getCsvDocument().getId());
        assertThat(actualCsvMetadata).isPresent();
        assertThat(actualCsvMetadata.get()).isEqualTo(csvWrapper.getCsvDocument());

        assertThat(redisClient.getLines(csvWrapper.getCsvDocument().getId(),2,3)).containsExactly("line 2", "line 3");
        assertThat(redisClient.getLines(csvWrapper.getCsvDocument().getId(),0,30)).containsExactly(lines);
        assertThat(redisClient.getLines(csvWrapper.getCsvDocument().getId(),-1,30)).containsExactly(lines);
    }

    @Test
    public void simpleUpdateTest() throws PersistenceException {
        List<String> lines =  new ArrayList<>();
        for (int i = 0; i < 10; i++) {
            lines.add("line " + i);
        }
        String[] headers = new String[]{"name", "surename"};
        CsvWrapper csvWrapper = CsvWrapperFactory.build(1, headers, lines);

        redisClient.storeCsvWrapper(csvWrapper);

        assertThat(redisClient.getAllLines(csvWrapper.getCsvDocument().getId())).hasSize(10);
        assertThat(redisClient.getAllLines(csvWrapper.getCsvDocument().getId())).containsOnlyElementsOf(lines);

        Optional actualCsvMetadata = redisClient.getCsvDocument(csvWrapper.getCsvDocument().getId());
        assertThat(actualCsvMetadata).isPresent();
        assertThat(actualCsvMetadata.get()).isEqualTo(csvWrapper.getCsvDocument());

        lines.remove(7);
        lines.remove(5);
        lines.remove(1);

        headers = new String[]{"name", "surename", "age"};
        csvWrapper = CsvWrapperFactory.build(1, headers, lines);

        redisClient.storeCsvWrapper(csvWrapper);
        assertThat(redisClient.getAllLines(csvWrapper.getCsvDocument().getId())).hasSize(7);
        assertThat(redisClient.getAllLines(csvWrapper.getCsvDocument().getId())).containsOnlyElementsOf(lines);

        actualCsvMetadata = redisClient.getCsvDocument(csvWrapper.getCsvDocument().getId());
        assertThat(actualCsvMetadata).isPresent();
        assertThat(actualCsvMetadata.get()).isEqualTo(csvWrapper.getCsvDocument());
    }

    @Test
    public void linesNotFoundTest() throws ConnectionPersistenceException {
        List<String> resList = redisClient.getAllLines(-1);
        assertThat(resList).isNotNull();
        assertThat(resList).hasSize(0);
    }

    @Test
    public void metadataNotFoundTest() throws PersistenceException {
        Optional<CsvDocument> res = redisClient.getCsvDocument(-1);

        assertThat(res).isNotPresent();
    }


    @Test(expected = ConnectionPersistenceException.class)
    public void connectionFailTest() throws ConnectionPersistenceException {
        RedisClient client = new RedisClient("some-no-existing-host", port);

        assertThat(client.getAllLines(-1)).hasSize(1);
    }

}
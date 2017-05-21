package org.kafkapre.csvserver.model;

import org.apache.commons.lang3.Validate;

import java.util.concurrent.atomic.AtomicInteger;

public class CsvDocumentFactory {

    public static final AtomicInteger counter = new AtomicInteger(1);

    public static CsvDocument build(int id, String[] headers) {
        return build(id, headers, 0);
    }

    public static CsvDocument build(String[] headers) {
        return build(headers,0);
    }

    public static CsvDocument build(Integer id, String[] headers, int linesCount) {
        Validate.notNull(id);
        Validate.inclusiveBetween(0, Integer.MAX_VALUE, (int) id);
        Validate.notEmpty(headers);
        Validate.inclusiveBetween(0, Integer.MAX_VALUE, linesCount);

        return new CsvDocument(id, headers, linesCount);
    }

    public static CsvDocument build(String[] headers, int linesCount) {
        Validate.notEmpty(headers);
        Validate.inclusiveBetween(0, Integer.MAX_VALUE, linesCount);

        return new CsvDocument(counter.getAndIncrement(), headers, linesCount);
    }

}

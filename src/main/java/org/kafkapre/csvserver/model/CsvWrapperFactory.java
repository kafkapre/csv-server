package org.kafkapre.csvserver.model;


import org.apache.commons.lang3.Validate;

import java.util.Arrays;
import java.util.List;

public class CsvWrapperFactory {

    public static CsvWrapper build(CsvDocument metadata, List<String> lines) {
        Validate.notNull(metadata);
        Validate.notEmpty(metadata.getHeaders());
        Validate.inclusiveBetween(0, Integer.MAX_VALUE, metadata.getId());
        Validate.notNull(lines);

        metadata.setLinesCount(lines.size());
        return new CsvWrapper(metadata, lines);
    }

    public static CsvWrapper build(String[] headers, List<String> lines) {
        Validate.notNull(lines);

        CsvDocument metadata = CsvDocumentFactory.build(headers, lines.size());
        return new CsvWrapper(metadata, lines);
    }

    public static CsvWrapper build(int id, String[] headers, List<String> lines) {
        Validate.notNull(lines);

        CsvDocument metadata = CsvDocumentFactory.build(id, headers, lines.size());
        return new CsvWrapper(metadata, lines);
    }

    public static CsvWrapper build(int id, String[] headers, String[] lines) {
        Validate.notNull(lines);

        CsvDocument metadata = CsvDocumentFactory.build(id, headers, lines.length);
        return new CsvWrapper(metadata, Arrays.asList(lines));
    }

}

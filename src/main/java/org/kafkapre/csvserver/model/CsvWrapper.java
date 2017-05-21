package org.kafkapre.csvserver.model;


import java.util.List;

public class CsvWrapper {

    private CsvDocument csvDocument;

    private List<String> lines;

    public CsvWrapper() {
    }

    CsvWrapper(CsvDocument csvDocument, List<String> lines) {
        this.csvDocument = csvDocument;
        this.lines = lines;
    }

    public CsvDocument getCsvDocument() {
        return csvDocument;
    }

    public List<String> getLines() {
        return lines;
    }

}

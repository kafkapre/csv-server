package org.kafkapre.csvserver.model;


import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlElementWrapper;
import javax.xml.bind.annotation.XmlRootElement;
import java.util.Arrays;

import static org.kafkapre.csvserver.resources.ResourcesPaths.CSV_RESOURCE_PATH;

@XmlRootElement
public class CsvDocument {

    @JsonProperty
    @XmlElement
    private int id;

    // anotations are on getter
    private int linesCount;

    @JsonIgnore
    private String path;

    @JsonIgnore
    private String linesPath;

    @JsonProperty
//    @XmlElement
    @XmlElementWrapper(name="headers")
    @XmlElement(name="header")
    private String[] headers;


    public static String determinePath(int id) {
        return determinePath(String.valueOf(id));
    }

    public static String determinePath(String id) {
        return CSV_RESOURCE_PATH + "/" + id;
    }

    public static String determineLinesPath(int id) {
        return determinePath(id) + "/lines";
    }


    public CsvDocument() {
        // Jackson deserialization
    }

    CsvDocument(int id, String[] headers, int linesCount) {
        this.id = id;
        this.headers = headers;
        this.linesCount = linesCount;
    }

    public int getId() {
        return id;
    }

    @JsonProperty // (access = JsonProperty.Access.WRITE_ONLY) - bug: does not work, so there is workaround @JsonIgnore on property which is not used
    @XmlElement
    public String getPath() {
        return determinePath(id);
    }

    @JsonProperty
    @XmlElement
    public String getLinesPath() {
        return determineLinesPath(id);
    }

    @JsonProperty
    @XmlElement
    public int getLinesCount() {
        return linesCount;
    }

    public String[] getHeaders() {
        return headers;
    }

    void setLinesCount(int linesCount) { // package private
        this.linesCount = linesCount;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof CsvDocument)) {
            return false;
        }

        CsvDocument that = (CsvDocument) o;

        if (id != that.id) {
            return false;
        }
        if (linesCount != that.linesCount) {
            return false;
        }
        // Probably incorrect - comparing Object[] arrays with Arrays.equals
        return Arrays.equals(headers, that.headers);

    }

    @Override
    public int hashCode() {
        int result = id;
        result = 31 * result + linesCount;
        result = 31 * result + Arrays.hashCode(headers);
        return result;
    }
}

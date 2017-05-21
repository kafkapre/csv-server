package org.kafkapre.csvserver.model;

import com.fasterxml.jackson.annotation.JsonProperty;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlElementWrapper;
import javax.xml.bind.annotation.XmlRootElement;
import java.util.ArrayList;
import java.util.List;

@XmlRootElement()
public class LineCollection {

    static private class Line {
        @JsonProperty
        @XmlElement
        private int num;

        @JsonProperty
        @XmlElement
        private String content;

        public Line() {}

        public Line(int num, String content) {
            this.num = num;
            this.content = content;
        }
    }

    @JsonProperty
    @XmlElementWrapper(name="lines")
    @XmlElement(name="lines")
    private List<Line> lines;

    public LineCollection(){}

    public LineCollection(List<String> lines){
        this.lines = new ArrayList<>();
        for (int i = 0; i < lines.size(); i++) {
            this.lines.add(new Line(i, lines.get(i)));
        }
    }
}

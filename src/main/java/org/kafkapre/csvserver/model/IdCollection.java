package org.kafkapre.csvserver.model;


import com.fasterxml.jackson.annotation.JsonProperty;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlElementWrapper;
import javax.xml.bind.annotation.XmlRootElement;
import java.util.Set;

@XmlRootElement
public class IdCollection {

    @JsonProperty
    @XmlElementWrapper(name="ids")
    @XmlElement(name="id")
    private Set<String> ids;

    public IdCollection(){}

    public IdCollection(Set<String> ids){
        this.ids = ids;
    }

}

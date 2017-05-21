package org.kafkapre.csvserver.configuration;


import com.fasterxml.jackson.annotation.JsonProperty;
import io.dropwizard.Configuration;
import org.hibernate.validator.constraints.NotEmpty;

public class ServerConfiguration extends Configuration {

    @NotEmpty
    @JsonProperty
    private String csvSeparator = ";";

    @NotEmpty
    @JsonProperty
    private String redisHost = "localhost";

    @JsonProperty
    private int redisPort = 6379;

    public String getCsvSeparator() {
        return csvSeparator;
    }

    public String getRedisHost() {
        return redisHost;
    }

    public int getRedisPort() {
        return redisPort;
    }
}

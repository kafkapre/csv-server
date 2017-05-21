package org.kafkapre.csvserver.healthecks;

import org.kafkapre.csvserver.persistence.api.PersistenceClient;

public class ServerHealthCheck extends com.codahale.metrics.health.HealthCheck {
    PersistenceClient persistenceClient;

    public ServerHealthCheck(PersistenceClient persistenceClient) {
        this.persistenceClient = persistenceClient;
    }

    // path: hostname:port/healthcheck
    @Override
    protected Result check() throws Exception {
        if (!persistenceClient.ping()) {
            return Result.unhealthy("Persistence is broken.");
        }
        return Result.healthy();
    }
}
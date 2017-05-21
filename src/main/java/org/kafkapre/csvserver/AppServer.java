package org.kafkapre.csvserver;


import io.dropwizard.Application;
import io.dropwizard.setup.Environment;
import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;
import org.kafkapre.csvserver.configuration.ServerConfiguration;
import org.kafkapre.csvserver.healthecks.ServerHealthCheck;
import org.kafkapre.csvserver.persistence.api.PersistenceClient;
import org.kafkapre.csvserver.persistence.impl.RedisClient;
import org.kafkapre.csvserver.resources.CsvDataResource;
import org.kafkapre.csvserver.resources.RootResource;

import java.util.Optional;

import static java.lang.Thread.sleep;

public class AppServer extends Application<ServerConfiguration> {

    private static final Logger logger = LogManager.getLogger(AppServer.class);

    @Override
    public void run(ServerConfiguration configuration, Environment environment) throws Exception {
        PersistenceClient persistenceClient = connectToRedisOrExit(configuration);

        environment.healthChecks().register("template", new ServerHealthCheck(persistenceClient));

        environment.jersey().register(new RootResource());
        environment.jersey().register(new CsvDataResource(configuration, persistenceClient));
    }

    private PersistenceClient connectToRedisOrExit(ServerConfiguration conf) throws InterruptedException {
        Optional<PersistenceClient> client = connectToRedis(conf);
        if (client.isPresent()) {
            return client.get();
        }
        logger.error("Redis cannot be connected. Server exit.");
        System.exit(1);
        return null;
    }

    private Optional<PersistenceClient> connectToRedis(ServerConfiguration conf) throws InterruptedException {
        PersistenceClient client = new RedisClient(conf.getRedisHost(), conf.getRedisPort());
        final int attempts = 10;
        final int sleepDuration = 1000;
        for (int i = 0; i < attempts; i++) {
            if (client.ping()) {
                logger.info("Redis client connected to Redis server.");
                return Optional.of(client);
            }
            logger.warn(String.format("Attempt [%d] to connect to redis failed. i will try it " +
                    "again in [{}] millis.", i, sleepDuration));
            sleep(sleepDuration);
        }
        return Optional.empty();
    }


}

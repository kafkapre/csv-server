package org.kafkapre.csvserver;


import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.kafkapre.csvserver.persistence.impl.RedisClient;
import redis.embedded.RedisServer;

import java.io.IOException;

public abstract class AbstractRedisTest {

    protected static int port = 6370;
    protected static RedisServer redisServer;

    @BeforeClass
    public static void classSetUp() throws Exception {
        redisServer = new RedisServer(port);
        redisServer.start();
    }

    @AfterClass
    public static void classDestroy() {
        redisServer.stop();
    }

}

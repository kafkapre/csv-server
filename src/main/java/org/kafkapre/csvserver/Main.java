package org.kafkapre.csvserver;

import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;
import org.kafkapre.csvserver.resources.RootResource;

public class Main {

    private static final Logger logger = LogManager.getLogger(RootResource.class);

    public static void main(String[] args) {
        try {
            new AppServer().run(args);
        } catch (Exception ex) {
            logger.error("Server failed. ", ex);
            System.exit(1);
        }
    }

}

package com.mlops;

import org.glassfish.grizzly.http.server.HttpServer;
import org.glassfish.jersey.grizzly2.httpserver.GrizzlyHttpServerFactory;
import org.glassfish.jersey.server.ResourceConfig;

import java.io.IOException;
import java.net.URI;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Main entry point for the MLOps Pipeline Management API.
 * Starts an embedded Grizzly HTTP server with Jersey configured.
 */
public class Main {

    private static final Logger LOGGER = Logger.getLogger(Main.class.getName());
    public static final String BASE_URI = "http://localhost:8080/api/v1/";

    /**
     * Creates and configures the Grizzly HTTP server with Jersey.
     *
     * @return the configured HttpServer instance
     */
    public static HttpServer startServer() {
        // Scan the com.mlops package for JAX-RS resources and providers
        final ResourceConfig config = new ResourceConfig().packages("com.mlops");

        return GrizzlyHttpServerFactory.createHttpServer(URI.create(BASE_URI), config);
    }

    /**
     * Main method — launches the server and waits for user input to shut down.
     */
    public static void main(String[] args) throws IOException {
        final HttpServer server = startServer();

        LOGGER.log(Level.INFO, "MLOps API started at {0}", BASE_URI);
        System.out.println("MLOps Pipeline Management API is running.");
        System.out.println("Base URI: " + BASE_URI);
        System.out.println("Press Enter to stop the server...");

        System.in.read();
        server.shutdownNow();
        LOGGER.info("Server stopped.");
    }
}

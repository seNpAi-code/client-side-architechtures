package com.mlops;

import jakarta.ws.rs.ApplicationPath;
import org.glassfish.jersey.server.ResourceConfig;

/**
 * JAX-RS Application configuration.
 * Maps all API resources under the /api/v1 base path.
 * Uses Jersey's ResourceConfig for package scanning and provider registration.
 */
@ApplicationPath("/api/v1")
public class MLOpsApplication extends ResourceConfig {

    public MLOpsApplication() {
        // Register all resources and providers in the com.mlops package
        packages("com.mlops");
    }
}

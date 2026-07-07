package com.mlops.resource;

import jakarta.ws.rs.GET;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.core.MediaType;
import java.util.LinkedHashMap;
import java.util.Map;

/**
 * Discovery endpoint for the MLOps API.
 * Returns metadata about the API including version, contact, and resource links.
 */
@Path("/api/v1")
@Produces(MediaType.APPLICATION_JSON)
public class DiscoveryResource {

    /**
     * GET /api/v1
     * Returns API metadata as a JSON object.
     */
    @GET
    public Map<String, Object> getApiInfo() {
        Map<String, Object> info = new LinkedHashMap<>();
        info.put("apiName", "MLOps Pipeline Management API");
        info.put("version", "1.0");
        info.put("description", "RESTful API for managing ML Workspaces and Models");
        info.put("contact", "mlops-support@university.ac.uk");

        // Primary resource links
        Map<String, String> links = new LinkedHashMap<>();
        links.put("workspaces", "/api/v1/workspaces");
        links.put("models", "/api/v1/models");
        info.put("resources", links);

        return info;
    }
}

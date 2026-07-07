package com.mlops.resource;

import com.mlops.data.DataStore;
import com.mlops.exception.WorkspaceNotEmptyException;
import com.mlops.model.MLWorkspace;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.CacheControl;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Path("/workspaces")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
public class WorkspaceResource {

    private final DataStore dataStore = DataStore.getInstance();

    @GET
    public Response getAllWorkspaces() {
        List<MLWorkspace> workspaces = new ArrayList<>(dataStore.getWorkspaces().values());
        
        // Implementing Cache-Control header as per conceptual question
        CacheControl cc = new CacheControl();
        cc.setMaxAge(60); // cache for 60 seconds
        cc.setPrivate(false);
        
        return Response.ok(workspaces).cacheControl(cc).build();
    }

    @POST
    public Response createWorkspace(MLWorkspace workspace) {
        if (workspace.getId() == null || workspace.getId().isEmpty()) {
            workspace.setId(UUID.randomUUID().toString());
        }
        dataStore.getWorkspaces().put(workspace.getId(), workspace);
        return Response.status(Response.Status.CREATED).entity(workspace).build();
    }

    @GET
    @Path("/{workspaceId}")
    public Response getWorkspace(@PathParam("workspaceId") String workspaceId) {
        MLWorkspace workspace = dataStore.getWorkspaces().get(workspaceId);
        if (workspace == null) {
            return Response.status(Response.Status.NOT_FOUND).build();
        }
        return Response.ok(workspace).build();
    }

    /**
     * HEAD method to verify existence without downloading the entire body.
     * This directly addresses the conceptual question in Part 2.
     */
    @HEAD
    @Path("/{workspaceId}")
    public Response checkWorkspaceExists(@PathParam("workspaceId") String workspaceId) {
        if (dataStore.getWorkspaces().containsKey(workspaceId)) {
            return Response.ok().build();
        } else {
            return Response.status(Response.Status.NOT_FOUND).build();
        }
    }

    @DELETE
    @Path("/{workspaceId}")
    public Response deleteWorkspace(@PathParam("workspaceId") String workspaceId) {
        MLWorkspace workspace = dataStore.getWorkspaces().get(workspaceId);
        if (workspace == null) {
            return Response.status(Response.Status.NOT_FOUND).build();
        }

        // Business Logic Constraint: Prevent data orphans
        if (workspace.getModelIds() != null && !workspace.getModelIds().isEmpty()) {
            throw new WorkspaceNotEmptyException("Workspace cannot be deleted because it has models assigned to it.");
        }

        dataStore.getWorkspaces().remove(workspaceId);
        return Response.noContent().build();
    }
}

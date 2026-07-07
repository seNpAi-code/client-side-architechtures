package com.mlops.resource;

import com.mlops.data.DataStore;
import com.mlops.exception.LinkedWorkspaceNotFoundException;
import com.mlops.model.MLWorkspace;
import com.mlops.model.MachineLearningModel;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Path("/models")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
public class ModelResource {

    private final DataStore dataStore = DataStore.getInstance();

    @POST
    public Response createModel(MachineLearningModel model) {
        // Dependency Validation: Ensure workspaceId exists
        if (model.getWorkspaceId() == null || !dataStore.getWorkspaces().containsKey(model.getWorkspaceId())) {
            throw new LinkedWorkspaceNotFoundException("The specified workspaceId does not exist.");
        }

        if (model.getId() == null || model.getId().isEmpty()) {
            model.setId(UUID.randomUUID().toString());
        }

        // Default status
        if (model.getStatus() == null || model.getStatus().isEmpty()) {
            model.setStatus("TRAINING");
        }

        dataStore.getModels().put(model.getId(), model);
        
        // Link model to the workspace
        MLWorkspace workspace = dataStore.getWorkspaces().get(model.getWorkspaceId());
        workspace.getModelIds().add(model.getId());

        return Response.status(Response.Status.CREATED).entity(model).build();
    }

    @GET
    public Response getModels(@QueryParam("status") String status) {
        List<MachineLearningModel> models = new ArrayList<>(dataStore.getModels().values());

        // Filtered Retrieval
        if (status != null && !status.isEmpty()) {
            models = models.stream()
                    .filter(m -> status.equalsIgnoreCase(m.getStatus()))
                    .collect(Collectors.toList());
        }

        return Response.ok(models).build();
    }

    /**
     * Sub-resource locator for evaluation metrics.
     */
    @Path("/{modelId}/metrics")
    public EvaluationMetricResource getEvaluationMetricResource(@PathParam("modelId") String modelId) {
        return new EvaluationMetricResource(modelId);
    }
}

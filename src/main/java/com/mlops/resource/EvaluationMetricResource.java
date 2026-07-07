package com.mlops.resource;

import com.mlops.data.DataStore;
import com.mlops.exception.ModelDeprecatedException;
import com.mlops.model.EvaluationMetric;
import com.mlops.model.MachineLearningModel;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.POST;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

/**
 * Sub-resource for handling Evaluation Metrics.
 * This class is instantiated by ModelResource.
 */
// The @Produces annotation is placed here to answer the conceptual question about class-level vs method-level annotations.
@Produces(MediaType.APPLICATION_JSON)
public class EvaluationMetricResource {

    private final DataStore dataStore = DataStore.getInstance();
    private final String modelId;

    public EvaluationMetricResource(String modelId) {
        this.modelId = modelId;
    }

    @GET
    public Response getMetrics() {
        List<EvaluationMetric> metrics = dataStore.getModelMetrics().getOrDefault(modelId, new ArrayList<>());
        return Response.ok(metrics).build();
    }

    @POST
    public Response appendMetric(EvaluationMetric metric) {
        MachineLearningModel model = dataStore.getModels().get(modelId);
        if (model == null) {
            return Response.status(Response.Status.NOT_FOUND).build();
        }

        // State Constraint: Cannot add metrics to a deprecated model
        if ("DEPRECATED".equalsIgnoreCase(model.getStatus())) {
            throw new ModelDeprecatedException("Cannot add metrics to a deprecated model.");
        }

        if (metric.getId() == null || metric.getId().isEmpty()) {
            metric.setId(UUID.randomUUID().toString());
        }

        // Add to history
        dataStore.getModelMetrics().computeIfAbsent(modelId, k -> new ArrayList<>()).add(metric);

        // Side Effect: Update parent model's latestAccuracy
        model.setLatestAccuracy(metric.getAccuracyScore());

        return Response.status(Response.Status.CREATED).entity(metric).build();
    }
}

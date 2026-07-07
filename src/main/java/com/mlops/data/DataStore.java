package com.mlops.data;

import com.mlops.model.EvaluationMetric;
import com.mlops.model.MLWorkspace;
import com.mlops.model.MachineLearningModel;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * In-memory data store for the MLOps API.
 * Uses ConcurrentHashMap for thread safety in a concurrent environment.
 */
public class DataStore {
    private static final DataStore instance = new DataStore();

    // In-memory tables
    private final Map<String, MLWorkspace> workspaces = new ConcurrentHashMap<>();
    private final Map<String, MachineLearningModel> models = new ConcurrentHashMap<>();
    // Maps a modelId to a list of EvaluationMetrics
    private final Map<String, List<EvaluationMetric>> modelMetrics = new ConcurrentHashMap<>();

    private DataStore() {
        // Private constructor for Singleton
    }

    public static DataStore getInstance() {
        return instance;
    }

    public Map<String, MLWorkspace> getWorkspaces() {
        return workspaces;
    }

    public Map<String, MachineLearningModel> getModels() {
        return models;
    }

    public Map<String, List<EvaluationMetric>> getModelMetrics() {
        return modelMetrics;
    }
}

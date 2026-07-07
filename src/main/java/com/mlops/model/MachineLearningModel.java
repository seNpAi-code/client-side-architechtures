package com.mlops.model;

public class MachineLearningModel {
    private String id; // Unique identifier, e.g., "MOD-8832"
    private String framework; // Category, e.g., "TensorFlow", "PyTorch", "Scikit-Learn"
    private String status; // Current state: "TRAINING", "DEPLOYED", or "DEPRECATED"
    private double latestAccuracy; // The most recent accuracy score recorded
    private String workspaceId; // Foreign key linking to the Workspace hosting the model

    public MachineLearningModel() {
    }

    public MachineLearningModel(String id, String framework, String status, String workspaceId) {
        this.id = id;
        this.framework = framework;
        this.status = status;
        this.workspaceId = workspaceId;
        this.latestAccuracy = 0.0;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getFramework() {
        return framework;
    }

    public void setFramework(String framework) {
        this.framework = framework;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public double getLatestAccuracy() {
        return latestAccuracy;
    }

    public void setLatestAccuracy(double latestAccuracy) {
        this.latestAccuracy = latestAccuracy;
    }

    public String getWorkspaceId() {
        return workspaceId;
    }

    public void setWorkspaceId(String workspaceId) {
        this.workspaceId = workspaceId;
    }
}

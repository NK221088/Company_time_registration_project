package dtu.time_manager.app;

import java.util.ArrayList;
import java.util.List;

public class Project {
    private String projectName;
    private String projectID;
    private String startDate;
    private String endDate;
    private List<Activity> activities;

    // Constructor with project name
    public Project(String projectName, String projectID) {
        this.projectName = projectName;
        this.projectID = projectID;
        this.startDate = ""; // Initializes the start date to be an empty string
        this.endDate = ""; // Initializes the end date to be an empty string
        this.activities = new ArrayList<>(); // Initializes the list to be empty
    }

    // Constructor without project name, delegates to the main one
    public Project(String projectID) {
        this("", projectID);
    }

    public String getProjectName() {
        return projectName;
    }

    public String getProjectID() {return projectID;}

    public void setProjectName(String projectName) {
        this.projectName = projectName;
    }

    public void setProjectStartDate(String startDate) {
        this.startDate = startDate;
    }

    public void setProjectEndDate(String endDate) {
        this.endDate = endDate;
    }

    public String getStartDate() {
        return startDate;
    }

    public String getEndDate() {
        return endDate;
    }

    public void addActivity(Activity activity) {
        activities.add(activity);
    }

    public List<Activity> getActivities() {
        return activities;
    }

    public String toString() {
        return projectName;
    }


}

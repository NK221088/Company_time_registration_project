package dtu.time_manager.app;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;


public class Project {
    private String projectName;
    private String projectID;
    private LocalDate startDate;
    private LocalDate endDate;
    private List<Activity> activities = new ArrayList<>();

    public Project(String projectName) {
        this.projectName = projectName;
        projectID = formatID(TimeManager.incProjectCount());
    }

    public static Project exampleProject(String projectName) {
        Project project = new Project(projectName);
        project.setProjectStartDate(LocalDate.parse("2025-01-01"));
        project.setProjectEndDate(LocalDate.parse("2025-01-08"));
        try {
            project.addActivity(new Activity("Activity 1"));
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
        return project;
    }

    public void setProjectName(String projectName) {
        this.projectName = projectName;
    }
    public String getProjectName() {
        return projectName;
    }

    public void setProjectID(String projectID) {
        this.projectID = projectID;
    }
    public String getProjectID() { return projectID; }

    public void setProjectStartDate(LocalDate startDate) {
        this.startDate = startDate;
    }
    public void setProjectEndDate(LocalDate endDate) {
        this.endDate = endDate;
    }
    public LocalDate getStartDate() {
        return startDate;
    }
    public LocalDate getEndDate() {
        return endDate;
    }


    public boolean activityDuplicateExists(String activityName) {
        return activities.stream().anyMatch(existingActivity ->
                existingActivity.getActivityName().equals(activityName));
    }
    public void addActivity(Activity activity) throws Exception {
        if (!activityDuplicateExists(activity.getActivityName())) {
            activities.add(activity);
        } else {
            throw new Exception(
                    "An activity with name '" + activity.getActivityName() +
                    "' already exists within '" + this.projectName +
                    "' two activities cannot exist with the same name within the same project.");
        }
    }
    public List<Activity> getActivities() {
        return activities;
    }

    private String formatID(int count) {
        return "25" + String.format("%03d", count);
    }

    public String toString() {
        if (Objects.equals(projectName, "")) {
            return projectID;
        } else {
            return projectName;
        }
    }
}

package dtu.time_manager.domain;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

public class Project {
    private String projectID;
    private String projectName;
    private User projectLead;
    private LocalDate startDate;
    private LocalDate endDate;
    private List<Activity> activities;

    public Project(String projectName) {
        this.projectName = projectName;
        this.activities = new ArrayList<>();
    }

    public static Project exampleProject(String name, int activityCount) {
        Project project = new Project(name);
        for (int i = 0; i < activityCount; i++) {
            project.addActivity(new Activity("Activity " + (i + 1)));
        }
        return project;
    }

    public String getProjectID() {
        return projectID;
    }

    public void setProjectID(String projectID) {
        this.projectID = projectID;
    }

    public String getProjectName() {
        return projectName;
    }

    public User getProjectLead() {
        return projectLead;
    }

    public void setProjectLead(User projectLead) {
        this.projectLead = projectLead;
    }

    public LocalDate getStartDate() {
        return startDate;
    }

    public void setStartDate(LocalDate startDate) {
        this.startDate = startDate;
    }

    public LocalDate getEndDate() {
        return endDate;
    }

    public void setEndDate(LocalDate endDate) {
        this.endDate = endDate;
    }

    public List<Activity> getActivities() {
        return new ArrayList<>(activities);
    }

    public void addActivity(Activity activity) {
        if (!activities.contains(activity)) {
            activities.add(activity);
        }
    }

    public void removeActivity(Activity activity) {
        activities.remove(activity);
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (obj == null || getClass() != obj.getClass()) return false;
        Project project = (Project) obj;
        return projectName.equals(project.projectName);
    }

    @Override
    public int hashCode() {
        return projectName.hashCode();
    }
} 
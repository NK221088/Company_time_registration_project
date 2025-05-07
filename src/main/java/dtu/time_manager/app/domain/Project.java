package dtu.time_manager.app.domain;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import dtu.time_manager.app.domain.Activity;


public class Project {
    private String projectName;
    private String projectID;
    private LocalDate startDate;
    private LocalDate endDate;
    private User projectLead;
    private ArrayList<Activity> activities;
    private boolean isFinalized;

    public Project(String projectName, String projectID) {
        this.projectName = projectName;
        this.projectID = projectID;
        this.activities = new ArrayList<>();
    }
    public void setProjectID(String projectID) {
        this.projectID = projectID;
    }

    public void assignProjectLead(User projectLead) {
        this.projectLead = projectLead;
    }

    public User getProjectLead() {
        return this.projectLead;
    }
    public void setProjectLead(User projectLead) {}

    public void setProjectName(String projectName) {
        this.projectName = projectName;
    }

    public String getProjectName() {
        return projectName;
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

    public ArrayList<Activity> getActivities() {return activities;}


    public void addActivity(Activity activity) {
        if (!activities.contains(activity)) {
            activities.add(activity);
        }
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (obj == null || getClass() != obj.getClass()) return false;
        Project project = (Project) obj;
        return projectName.equals(project.projectName);
    }

    public Activity getActivityFromName(String activityName) {
        return getActivities().stream().filter(activity -> activity.getActivityName().equals(activityName)).findFirst().orElse(null);
    }

    public String toString() {
        if (Objects.equals(projectName, "")) {
            return projectID;
        } else {
            return projectName;
        }
    }

    public boolean getFinalized() {
        return this.isFinalized;
    }

    public void setFinalized() {
        this.isFinalized = true;
    }

    public void setActivityAsFinalized(Activity activity) {
        activity.setActivityAsFinalized();
        boolean allFinalized = activities.stream()
                .allMatch(a -> a.getFinalized());
        if (allFinalized) {
            setFinalized();
        }
    }

    public void setActivityAsUnFinalized(Activity activity) {
        activity.setActivityAsUnFinalized();
        setUnFinalized(); // If any activity is unfinalized, we mark the whole as unfinalized
    }

    private void setUnFinalized() {
        this.isFinalized = false;
    }


}

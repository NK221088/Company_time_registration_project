package dtu.timemanager.domain;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Objects;


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

    public void setProjectStartDate(LocalDate startDate) throws Exception {
        if (endDate==null || startDate.isBefore(endDate)) {
            this.startDate = startDate;
        } else {
            throw new Exception("The start date of the project can't be after the end date of the project.");
        }
    }

    public void setProjectEndDate(LocalDate endDate) throws Exception {
        if (startDate==null || endDate.isAfter(startDate)) {
            this.endDate = endDate;
        } else {
            throw new Exception("The end date of the project can't be before the start date of the project.");
        }
    }

    public LocalDate getStartDate() {
        return startDate;
    }

    public LocalDate getEndDate() {
        return endDate;
    }

    public ArrayList<Activity> getActivities() {return activities;}


    public void addActivity(Activity activity) throws Exception {
        if (!activities.contains(activity)) {
            activities.add(activity);
        } else {
            throw new Exception("An activity with name '" + activity.getActivityName() + "' already exists within '" + this.getProjectName() + "' two activities cannot exist with the same name within the same project.");
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

    public String getTimeInterval() {
        String p1 = getStartDate() != null ? getStartDate().toString() : "";
        String p2 = getEndDate() != null ? getEndDate().toString() : "";
        return p1 + " - " + p2;
    }
}
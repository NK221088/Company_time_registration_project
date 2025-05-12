package dtu.timemanager.domain;

import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

// Alexander Wittrup
public class ProjectReport {
    private Project project;
    private List<Activity> activities;

    public ProjectReport(Project project) {
        this.project = project;
        this.activities = project.getActivities();
    }

    public String getProjectID() {
        return project.getProjectID();
    }
    public String getProjectName() {
        return project.getProjectName();
    }
    public List<Activity> getActivities() {
        return activities;
    }
    public User getProjectLead() {
        return project.getProjectLead();
    }
    public String getProjectInterval() {
        return project.getTimeInterval();
    }

    public Map<Activity, Double> getWorkedHours() {
        return this.activities.stream().collect(Collectors.toMap(Function.identity(), Activity::getWorkedHours));
    }
    public Map<Activity, Double> getExpectedHours() {
        return this.activities.stream().collect(Collectors.toMap(Function.identity(), Activity::getExpectedWorkHours));
    }
    public Map<Activity, List<User>> getAssignedUsers() {
        return this.activities.stream().collect(Collectors.toMap(Function.identity(), Activity::getAssignedUsers));
    }
    public Map<Activity, List<User>> getContributingEmployees() {
        return this.activities.stream().collect(Collectors.toMap(Function.identity(), Activity::getContributingUsers));
    }
    public Map<Activity, String> getActivityIntervals() {
        return this.activities.stream().collect(Collectors.toMap(Function.identity(), Activity::getTimeInterval));
    }
}

package dtu.time_manager.domain;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

public class Activity {
    private String activityName;
    private LocalDate activityStartTime;
    private LocalDate activityEndTime;
    private double expectedHours;
    private double workedHours;
    private List<User> assignedUsers;
    private List<User> workingUsers;

    public Activity(String activityName) {
        this.activityName = activityName;
        this.assignedUsers = new ArrayList<>();
        this.workingUsers = new ArrayList<>();
        this.expectedHours = 0.0;
        this.workedHours = 0.0;
    }

    public String getActivityName() {
        return activityName;
    }

    public LocalDate getActivityStartTime() {
        return activityStartTime;
    }

    public void setActivityStartTime(LocalDate activityStartTime) {
        this.activityStartTime = activityStartTime;
    }

    public LocalDate getActivityEndTime() {
        return activityEndTime;
    }

    public void setActivityEndTime(LocalDate activityEndTime) {
        this.activityEndTime = activityEndTime;
    }

    public double getExpectedHours() {
        return expectedHours;
    }

    public void setExpectedHours(double expectedHours) {
        this.expectedHours = expectedHours;
    }

    public double getWorkedHours() {
        return workedHours;
    }

    public void setWorkedHours(double workedHours) {
        this.workedHours = workedHours;
    }

    public List<User> getAssignedUsers() {
        return new ArrayList<>(assignedUsers);
    }

    public void assignUser(String userInitials) {
        // This method will be implemented by the ActivityService
    }

    public void removeUser(String userInitials) {
        // This method will be implemented by the ActivityService
    }

    public List<User> getWorkingUsers() {
        return new ArrayList<>(workingUsers);
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (obj == null || getClass() != obj.getClass()) return false;
        Activity activity = (Activity) obj;
        return activityName.equals(activity.activityName);
    }

    @Override
    public int hashCode() {
        return activityName.hashCode();
    }
} 
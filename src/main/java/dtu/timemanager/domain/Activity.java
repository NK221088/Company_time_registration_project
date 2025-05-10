package dtu.timemanager.domain;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Map;
import java.util.*;

public class Activity {

    private String activityName;
    private ArrayList<User> assignedUsers = new ArrayList<>();
    private ArrayList<User> contributingUsers = new ArrayList<>();
    private double expectedWorkHours;
    private LocalDate activityStartTime;
    private LocalDate activityEndTime;
    private Boolean isFinalized = false;

    public Activity(String activityName) {
        this.activityName = activityName;
    }

    public void setExpectedWorkHours(double expectedWorkHours) { this.expectedWorkHours = expectedWorkHours; }
    public double getExpectedWorkHours() { return this.expectedWorkHours;}

    public double getWorkedHours() {
        double workedHours = 0.0;
        for (User user : getContributingUsers()) {
            for (TimeRegistration timeReg : user.getActivityRegistrations().getOrDefault(this, Collections.emptyList())) {
                workedHours += timeReg.getRegisteredHours();
            }
        }
        return workedHours;
    }

    public ArrayList<User> getAssignedUsers() { return this.assignedUsers;}
    public String getActivityName() { return this.activityName;}

    public Map<String, Object> viewActivity() {
        Map<String,Object> info = new HashMap();
        info.put("Name", getActivityName());
        info.put("Assigned employees", getAssignedUsers());
        info.put("Contributing employees", getContributingUsers());
        info.put("ExpectedWorkHours", getExpectedWorkHours());
        info.put("WorkedHours", getWorkedHours());
        info.put("Time interval", getTimeInterval());

        return info;
    }

    public void assignUser(User user) {
        if (user.getActivityCount() < 20){ // 1
            if(assignedUsers.contains(user)){ // 2
                throw new RuntimeException(user.getUserInitials() + " is already assigned to the activity " + getActivityName()); // 3
            }
            assignedUsers.add(user); // 4
            user.incrementActivityCount(); // 5
        } else {
            throw new RuntimeException(user.getUserInitials() + " is already assigned to the maximum number of 20 activities"); // 6
        }
    }

    public void unassignUser(User user) {
        if (this.assignedUsers.contains(user)) {
            user.decrementActivityCount();
            this.assignedUsers.remove(user);
        } else {
            throw new RuntimeException("The user can not be unassigned from an activity they are not assigned to");
        }
    }
    public void addContributingUser(User user) {
        if (!contributingUsers.contains(user)) {
            contributingUsers.add(user);
        }
    }
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (obj == null || getClass() != obj.getClass()) return false;
        Activity activity = (Activity) obj;
        return activityName.equals(activity.getActivityName());
    }

    public ArrayList<User> getContributingUsers() { return this.contributingUsers; }

    public String toString() {
        return activityName;
    }

    public void setActivityName(String activityName) {
        this.activityName = activityName;
    }

    public LocalDate getActivityStartTime() {
        return activityStartTime;
    }

    public void setActivityStartTime(LocalDate activityStartTime) throws Exception {

        if (activityEndTime==null || activityStartTime.isBefore(activityEndTime)) {
            this.activityStartTime = activityStartTime;
        } else {
            throw new Exception("The start date of the activity can't be after the start date of the activity.");
        }
    }
    public LocalDate getActivityEndTime() {
        return activityEndTime;
    }

    public String getTimeInterval() {
        String p1 = getActivityStartTime() != null ? getActivityStartTime().toString() : "";
        String p2 = getActivityEndTime() != null ? getActivityEndTime().toString() : "";
        return p1 + " - " + p2;
    }

    public void setActivityEndTime(LocalDate activityEndTime) throws Exception {
        if (activityStartTime==null || activityEndTime.isAfter(activityStartTime)) {
            this.activityEndTime = activityEndTime;
        } else {
            throw new Exception("The end date of the activity can't be before the start date of the activity.");
        }

    }

    public void setActivityAsFinalized() {
        this.isFinalized = true;
    }

    public boolean getFinalized() {
        return isFinalized;
    }

    public void setActivityAsUnFinalized() {
        this.isFinalized = false;
    }
}
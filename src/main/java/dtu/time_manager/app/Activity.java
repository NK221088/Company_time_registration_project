package dtu.time_manager.app;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Map;
import java.util.*;

public class Activity {

    private String ActivityName;
    private ArrayList<User> assignedUsers = new ArrayList<>();
    private ArrayList<User> workingUsers = new ArrayList<>();
    private double expectedWorkHours;
    private LocalDate activityStartTime;
    private LocalDate activityEndTime;
    private Boolean isFinalized = false;

    public Activity(String ActivityName) {
        this.ActivityName = ActivityName;
    }

    public void setExpectedWorkHours(double expectedWorkHours) { this.expectedWorkHours = expectedWorkHours; }
    public double getExpectedWorkHours() { return this.expectedWorkHours;}

    public double getWorkedHours() {
        double workedHours = 0.0;
        for (User user : getWorkingUsers()) {
            for (TimeRegistration timeReg : user.getActivityRegistrations().getOrDefault(this, Collections.emptyList())) {
                workedHours += timeReg.getRegisteredHours();
            }
        }
        return workedHours;
    }

    public ArrayList<User> getAssignedUsers() { return this.assignedUsers;}
    public String getActivityName() { return this.ActivityName;}

    public Map<String, Object> viewActivity() {
        Map<String,Object> info = new HashMap();
        info.put("Name", getActivityName());
        info.put("Assigned employees", getAssignedUsers());
        info.put("Contributing employees", getWorkingUsers());
        info.put("ExpectedWorkHours", getExpectedWorkHours());
        info.put("WorkedHours", getWorkedHours());
        info.put("StartTime", getActivityStartTime());
        info.put("EndTime", getActivityEndTime());

        return info;
    }

    public void assignUser(String userInitials) {
        User user = TimeManager.getUser(userInitials);
        Integer activityCount = user.getActivityCount();
        if (activityCount < 20){
            if(assignedUsers.contains(user)){
                throw new RuntimeException(userInitials + " is already assigned to the activity " + getActivityName());
            }
            assignedUsers.add(user);
            user.incrementActivityCount();
        } else {
            throw new RuntimeException(userInitials + " is already assigned to the maximum number of 20 activities");
        }
    }

    public void unassignUser(String userInitials) {
        User user = TimeManager.getUser(userInitials);

        if (this.assignedUsers.contains(user)) {
            this.assignedUsers.remove(user);
        } else {
            throw new RuntimeException("The user can not be unassigned from an activity they are not assigned to");
        }
    }

    public void addWorkingUser(User user) {
        if (!workingUsers.contains(user)) {
            workingUsers.add(user);
        }
    }

    public ArrayList<User> getWorkingUsers() { return this.workingUsers; }

    public String toString() {
        return ActivityName;
    }

    public void setActivityName(String activityName) {
        this.ActivityName = activityName;
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

    public void setActivityAsFinalized() {
        this.isFinalized = true;
    }

    public boolean getFinalized() {
        return isFinalized;
    }

    public void setActivityAsNotFinalized() {
        this.isFinalized = false;
    }
}


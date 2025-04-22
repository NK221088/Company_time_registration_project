package dtu.time_manager.app;

import java.util.ArrayList;
import java.util.Map;
import java.util.*;

public class Activity {

    private String ActivityName;
    private ArrayList<User> assignedUsers = new ArrayList<>();
    private double expectedWorkHours;

    public Activity(String ActivityName) {
        this.ActivityName = ActivityName;
    }

    public void setExpectedWorkHours(double expectedWorkHours) { this.expectedWorkHours = expectedWorkHours; }
    public double getExpectedWorkHours() { return this.expectedWorkHours;}

    public double getAssignedWorkHours() {
        double assignedWorkHours = 0.0;
        for (User user : getAssignedUsers()) {
            for (TimeRegistration timeReg : user.getActivityRegistrations().getOrDefault(this, Collections.emptyList())) {
                assignedWorkHours += timeReg.getRegisteredHours();
            }
        }
        return assignedWorkHours;
    }

    public ArrayList<User> getAssignedUsers() { return this.assignedUsers;}
    public String getActivityName() { return this.ActivityName;}

    public Map<String, Object> viewActivity() {
        Map<String,Object> info = new HashMap();
        info.put("Name", getActivityName());
        info.put("Assigned Users", getAssignedUsers());
        info.put("ExpectedWorkHours", getExpectedWorkHours());

        return info;
    }

    public void assignUser(String userInitials) {
        User user = TimeManager.getUser(userInitials);
        Integer activityCount = user.getActivityCount();
        if (activityCount < 20){

            assignedUsers.add(user);
            user.incrementActivityCount();
        } else {
            throw new RuntimeException(userInitials + " is already assigned to the maximum number of 20 activities");
        }


        Integer activityCount = user.getActivityCount();
        if (activityCount < 20){

            assignedUsers.add(user);
            user.incrementActivityCount();
        } else {
            throw new RuntimeException(userInitials + " is already assigned to the maximum number of 20 activities");
        }




    }
}


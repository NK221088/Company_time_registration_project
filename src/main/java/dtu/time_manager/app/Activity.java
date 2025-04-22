package dtu.time_manager.app;

import java.util.ArrayList;
import java.util.Map;
import java.util.*;

public class Activity {

    private String ActivityName;
    private ArrayList<User> assignedUsers = new ArrayList<>();
    private Long expectedWorkHours;

    public Activity(String ActivityName) {
        this.ActivityName = ActivityName;
    }

    public void setExpectedHours(Long ExpectedHours) {this.expectedWorkHours = ExpectedHours;}

    public ArrayList<User> getUsers() { return this.assignedUsers;}
    public Long getExpectedWorkHours() { return this.expectedWorkHours;}
    public String getActivityName() { return this.ActivityName;}

    public Map<String, Object> viewActivity() {
        Map<String,Object> info = new HashMap();
        info.put("Name", getActivityName());
        info.put("Assigned Users", getUsers());
        info.put("ExpectedWorkHours", getExpectedWorkHours());

        return info;
    }

    public void assignUser(String userInitials) {
        User user = TimeManager.getUser(userInitials);
        assignedUsers.add(user);



    }
}


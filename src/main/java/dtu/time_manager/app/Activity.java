package dtu.time_manager.app;

import java.util.ArrayList;
import java.util.Map;
import java.util.*;

public class Activity {

    private String ActivityName;
    private ArrayList<User> users = new ArrayList<>();
    private Long expectedWorkHours;

    public Activity(String ActivityName) {
        this.ActivityName = ActivityName;
    }

    public String getActivityName() {
        return this.ActivityName;
    }
    public ArrayList<User> getUsers() { return this.users;}
    public Long getExpectedWorkHours() { return this.expectedWorkHours;}

    public Map<String, Object> viewActivity() {
        Map<String,Object> info = new HashMap();
        info.put("Name", getActivityName());
        info.put("Users", getUsers());
        info.put("ExpectedWorkHours", getExpectedWorkHours());

        return info;
    }

}


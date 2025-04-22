package dtu.time_manager.app;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class User {
    private String user_initials;
    private Map<Activity, List<TimeRegistration>> activity_registrations;
    private Integer activityCount;

    public User(String userInitials) {
        this.user_initials = userInitials;
        TimeManager.addUser(this);
        this.activityCount = 0;
    }

    public Object getUserInitials() {
        return user_initials;
    }

    public Integer getActivityCount() {
        return activityCount;
    }

    public String toString() {
        return user_initials;
    }

    public void addTimeRegistration(TimeRegistration timeRegistration) {
        ArrayList<TimeRegistration> timeReg = new ArrayList<>();
        timeReg.add(timeRegistration);

//        activity_registrations.put(timeRegistration.getRegisteredActivity(), new ArrayList<TimeRegistration>);
    }

    public void incrementActivityCount() {
        activityCount++;
    }
}

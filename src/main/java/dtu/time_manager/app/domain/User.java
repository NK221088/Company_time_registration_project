package dtu.time_manager.app.domain;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class User {
    private String user_initials;
    private Map<Activity, List<TimeRegistration>> activity_registrations = new HashMap<Activity, List<TimeRegistration>>();
    private Integer activityCount = 0;

    public User(String userInitials) {
        this.user_initials = userInitials;
    }

    public String getUserInitials() {
        return this.user_initials;
    }

    public Integer getActivityCount() {
        return activityCount;
    }

    public void addTimeRegistration(TimeRegistration timeRegistration) {
        Activity a = timeRegistration.getRegisteredActivity();
        List<TimeRegistration> activity_value = activity_registrations.get(a);
        if (activity_value != null) {
            activity_value.add(timeRegistration);
        } else {
            ArrayList<TimeRegistration> timeReg = new ArrayList<>();
            timeReg.add(timeRegistration);
            activity_registrations.put(a, timeReg);
        }
        a.addWorkingUser(timeRegistration.getRegisteredUser());
    }

    public Map<Activity, List<TimeRegistration>> getActivityRegistrations() {
        return activity_registrations;
    }

    public String toString() {
        return user_initials;
    }

    public void incrementActivityCount() {
        activityCount++;
    }
}

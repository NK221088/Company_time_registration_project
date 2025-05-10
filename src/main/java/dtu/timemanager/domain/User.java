package dtu.timemanager.domain;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class User {
    private String userInitials;
    private Map<Activity, List<TimeRegistration>> activityRegistrations = new HashMap<Activity, List<TimeRegistration>>();
    private Integer activityCount = 0;

    public User(String userInitials) {
        this.userInitials = userInitials;
    }

    public String getUserInitials() {
        return this.userInitials;
    }

    public Integer getActivityCount() {
        return activityCount;
    }

    public void addTimeRegistration(TimeRegistration timeRegistration) {
        Activity a = timeRegistration.getRegisteredActivity();
        List<TimeRegistration> activity_value = activityRegistrations.get(a);
        if (activity_value != null) {
            activity_value.add(timeRegistration);
        } else {
            ArrayList<TimeRegistration> timeReg = new ArrayList<>();
            timeReg.add(timeRegistration);
            activityRegistrations.put(a, timeReg);
        }
        a.addContributingUser(timeRegistration.getRegisteredUser());
    }

    public Map<Activity, List<TimeRegistration>> getActivityRegistrations() {
        return activityRegistrations;
    }

    public String toString() {
        return userInitials;
    }

    public void incrementActivityCount() {
        activityCount++;
    }

    public void decrementActivityCount() {
    activityCount--;}

    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (obj == null || getClass() != obj.getClass()) return false;
        User user = (User) obj;
        return getUserInitials().equals(user.getUserInitials());
    }
}
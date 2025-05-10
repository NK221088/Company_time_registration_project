package dtu.timemanager.domain;

import java.util.*;

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
        List<TimeRegistration> activityValue = activityRegistrations.get(a);
        if (activityValue != null) {
            activityValue.add(timeRegistration);
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

    @Override
    public boolean equals(Object obj) {
        if (obj instanceof User) {
            return Objects.equals(getUserInitials(), ((User) obj).userInitials);
        } else {
            return false;
        }
    }
}
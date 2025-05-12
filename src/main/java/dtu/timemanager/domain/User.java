package dtu.timemanager.domain;

import java.util.*;
import javax.persistence.*;

@Entity
@Table(name = "users")
public class User {
    @Id
    private String userInitials;

    @Transient // This complex structure needs custom handling
    private Map<Activity, List<TimeRegistration>> activityRegistrations = new HashMap<Activity, List<TimeRegistration>>();

    private Integer activityCount = 0;

    // No-args constructor required by JPA
    protected User() {}

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
        Activity activity = timeRegistration.getRegisteredActivity(); // 1
        List<TimeRegistration> activityValue = activityRegistrations.get(activity); // 2
        if (activityValue != null) { // 3
            activityValue.add(timeRegistration); // 4
        } else {
            ArrayList<TimeRegistration> timeReg = new ArrayList<>(); // 5
            timeReg.add(timeRegistration); // 6
            activityRegistrations.put(activity, timeReg); // 7
        }
        activity.addContributingUser(timeRegistration.getRegisteredUser()); // 8
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
        activityCount--;
    }

    @Override
    public boolean equals(Object obj) {
        if (obj instanceof User) {
            return Objects.equals(getUserInitials(), ((User) obj).userInitials);
        } else {
            return false;
        }
    }
}
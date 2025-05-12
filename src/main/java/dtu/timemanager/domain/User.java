package dtu.timemanager.domain;

import java.util.*;
import jakarta.persistence.*;

// Alexander Wittrup
@Entity
public class User {
    @Id
    public String userInitials;

    public int activityCount = 0;

    @OneToMany(mappedBy = "registeredUser", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    public List<TimeRegistration> timeRegistrations = new ArrayList<>();

    @ManyToMany(mappedBy = "assignedUsers", fetch = FetchType.LAZY)
    public List<Activity> assignedActivities = new ArrayList<>();

    // No-args constructor required by JPA
    public User() {}

    public User(String userInitials) {
        this.userInitials = userInitials;
    }

    public String getUserInitials() {
        return this.userInitials;
    }

    public int getActivityCount() {
        return activityCount;
    }

    // Alexander Wittrup
    public void addTimeRegistration(TimeRegistration timeRegistration) {
        if (!timeRegistrations.contains(timeRegistration)) {
            timeRegistrations.add(timeRegistration);
        }
//        Activity a = timeRegistration.getRegisteredActivity();
//        List<TimeRegistration> activityValue = activityRegistrations.get(a);
//        if (activityValue != null) {
//            activityValue.add(timeRegistration);
//        } else {
//            ArrayList<TimeRegistration> timeReg = new ArrayList<>();
//            timeReg.add(timeRegistration);
//            activityRegistrations.put(a, timeReg);
//        }
//        a.addContributingUser(timeRegistration.getRegisteredUser());
    }

    // Helper method to get activity registrations as a map
    public Map<Activity, List<TimeRegistration>> getActivityRegistrations() {
        Map<Activity, List<TimeRegistration>> activityRegistrations = new HashMap<>();

        for (TimeRegistration registration : timeRegistrations) {
            Activity activity = registration.getRegisteredActivity();
            if (activity != null) {
                if (!activityRegistrations.containsKey(activity)) {
                    activityRegistrations.put(activity, new ArrayList<>());
                }
                activityRegistrations.get(activity).add(registration);
            }
        }

        return activityRegistrations;
    }

    public List<TimeRegistration> getTimeRegistrations() {
        return Collections.unmodifiableList(timeRegistrations);
    }

    public List<Activity> getAssignedActivities() {
        return Collections.unmodifiableList(assignedActivities);
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

    @Override
    public int hashCode() {
        return Objects.hashCode(userInitials);
    }
}
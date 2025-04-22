package dtu.time_manager.app;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class User {
    private String user_initials;
    private Map<Activity, List<TimeRegistration>> activity_registrations = new HashMap<Activity, List<TimeRegistration>>();

    public User(String userInitials) {
        this.user_initials = userInitials;
        TimeManager.addUser(this);
    }

    public Object getUserInitials() {
        return user_initials;
    }

    public void addTimeRegistration(TimeRegistration timeRegistration) {
        List<TimeRegistration> activity_value = activity_registrations.get(timeRegistration.getRegisteredActivity());
        if (activity_value != null) {
            activity_value.add(timeRegistration);
        } else {
            ArrayList<TimeRegistration> timeReg = new ArrayList<>();
            timeReg.add(timeRegistration);
            activity_registrations.put(timeRegistration.getRegisteredActivity(), timeReg);
        }
    }

    public Map<Activity, List<TimeRegistration>> getActivityRegistrations() {
        return activity_registrations;
    }

    public String toString() {
        return user_initials;
    }
}

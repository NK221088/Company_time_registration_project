package dtu.time_manager.app;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class User {
    private String user_initials;
    private Map<Activity, List<TimeRegistration>> activity_registrations;

    public User(String userInitials) {
        this.user_initials = userInitials;
        TimeManager.addUser(this);
    }

    public Object getUserInitials() {
        return user_initials;
    }

    public String toString() {
        return user_initials;
    }

    public void addTimeRegistration(TimeRegistration timeRegistration) {
        ArrayList<TimeRegistration> timeReg = new ArrayList<>();
        timeReg.add(timeRegistration);

//        activity_registrations.put(timeRegistration.getRegisteredActivity(), new ArrayList<TimeRegistration>);
    }
}

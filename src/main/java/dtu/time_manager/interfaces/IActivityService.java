package dtu.time_manager.interfaces;

import dtu.time_manager.domain.Activity;
import dtu.time_manager.domain.User;
import java.util.List;

public interface IActivityService {
    void addIndependentActivity(Activity activity);
    List<Activity> getIndependentActivities();
    void assignUserToActivity(Activity activity, String userInitials);
    void removeUserFromActivity(Activity activity, String userInitials);
    List<User> getAssignedUsers(Activity activity);
    List<User> getWorkingUsers(Activity activity);
    double getWorkedHours(Activity activity);
    double getExpectedHours(Activity activity);
} 
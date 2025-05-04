package dtu.time_manager.services;

import dtu.time_manager.domain.Activity;
import dtu.time_manager.domain.User;
import dtu.time_manager.interfaces.IActivityService;
import dtu.time_manager.interfaces.IUserService;
import java.util.ArrayList;
import java.util.List;

public class ActivityService implements IActivityService {
    private static final List<Activity> independentActivities = new ArrayList<>();
    private final IUserService userService;

    public ActivityService(IUserService userService) {
        this.userService = userService;
    }

    @Override
    public void addIndependentActivity(Activity activity) {
        if (!independentActivities.stream().anyMatch(a -> a.getActivityName().equals(activity.getActivityName()))) {
            independentActivities.add(activity);
        } else {
            throw new RuntimeException(
                "An independent activity with name '" + activity.getActivityName() + "' already exists."
            );
        }
    }

    @Override
    public List<Activity> getIndependentActivities() {
        return new ArrayList<>(independentActivities);
    }

    @Override
    public void assignUserToActivity(Activity activity, String userInitials) {
        User user = userService.getUser(userInitials);
        if (!activity.getAssignedUsers().contains(user)) {
            activity.getAssignedUsers().add(user);
        }
    }

    @Override
    public void removeUserFromActivity(Activity activity, String userInitials) {
        User user = userService.getUser(userInitials);
        activity.getAssignedUsers().remove(user);
        activity.getWorkingUsers().remove(user);
    }

    @Override
    public List<User> getAssignedUsers(Activity activity) {
        return activity.getAssignedUsers();
    }

    @Override
    public List<User> getWorkingUsers(Activity activity) {
        return activity.getWorkingUsers();
    }

    @Override
    public double getWorkedHours(Activity activity) {
        return activity.getWorkedHours();
    }

    @Override
    public double getExpectedHours(Activity activity) {
        return activity.getExpectedHours();
    }
} 
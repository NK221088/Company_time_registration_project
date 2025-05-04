package dtu.time_manager.services;

import dtu.time_manager.domain.TimeRegistration;
import dtu.time_manager.domain.User;
import dtu.time_manager.domain.Activity;
import dtu.time_manager.interfaces.ITimeRegistrationService;
import dtu.time_manager.interfaces.IUserService;
import dtu.time_manager.interfaces.IActivityService;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;
import java.time.LocalTime;

public class TimeRegistrationService implements ITimeRegistrationService {
    private static final List<TimeRegistration> timeRegistrations = new ArrayList<>();
    private final IUserService userService;
    private final IActivityService activityService;

    public TimeRegistrationService(IUserService userService, IActivityService activityService) {
        this.userService = userService;
        this.activityService = activityService;
    }

    @Override
    public void addTimeRegistration(TimeRegistration timeRegistration) {
        validateTimeRegistration(timeRegistration);
        timeRegistrations.add(timeRegistration);
        updateActivityWorkedHours(timeRegistration);
    }

    @Override
    public List<TimeRegistration> getTimeRegistrations() {
        return new ArrayList<>(timeRegistrations);
    }

    @Override
    public List<TimeRegistration> getTimeRegistrationsForUser(String userInitials) {
        User user = userService.getUser(userInitials);
        return timeRegistrations.stream()
                .filter(tr -> tr.getUser().equals(user))
                .collect(Collectors.toList());
    }

    @Override
    public List<TimeRegistration> getTimeRegistrationsForActivity(String activityId) {
        return timeRegistrations.stream()
                .filter(tr -> tr.getActivity().getActivityName().equals(activityId))
                .collect(Collectors.toList());
    }

    private void validateTimeRegistration(TimeRegistration timeRegistration) {
        // Validate user exists and is assigned to the activity
        User user = timeRegistration.getUser();
        Activity activity = timeRegistration.getActivity();
        
        if (!activityService.getAssignedUsers(activity).contains(user)) {
            throw new RuntimeException("User " + user.getUserInitials() + " is not assigned to activity " + activity.getActivityName());
        }

        // Validate time registration doesn't overlap with existing ones
        boolean hasOverlap = timeRegistrations.stream()
                .anyMatch(tr -> tr.getUser().equals(user) &&
                        tr.getDate().equals(timeRegistration.getDate()) &&
                        isTimeOverlap(tr.getStartTime(), tr.getEndTime(),
                                timeRegistration.getStartTime(), timeRegistration.getEndTime()));

        if (hasOverlap) {
            throw new RuntimeException("Time registration overlaps with existing registration for user " + user.getUserInitials());
        }
    }

    private boolean isTimeOverlap(LocalTime start1, LocalTime end1, LocalTime start2, LocalTime end2) {
        return !start1.isAfter(end2) && !start2.isAfter(end1);
    }

    private void updateActivityWorkedHours(TimeRegistration timeRegistration) {
        Activity activity = timeRegistration.getActivity();
        double currentHours = activity.getWorkedHours();
        activity.setWorkedHours(currentHours + timeRegistration.getHours());
    }
} 
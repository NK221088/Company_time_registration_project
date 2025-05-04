package dtu.time_manager.interfaces;

import dtu.time_manager.domain.TimeRegistration;
import java.util.List;

public interface ITimeRegistrationService {
    void addTimeRegistration(TimeRegistration timeRegistration);
    List<TimeRegistration> getTimeRegistrations();
    List<TimeRegistration> getTimeRegistrationsForUser(String userInitials);
    List<TimeRegistration> getTimeRegistrationsForActivity(String activityId);
} 
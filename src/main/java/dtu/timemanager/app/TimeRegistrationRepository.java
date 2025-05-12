package dtu.timemanager.app;

import dtu.timemanager.domain.Activity;
import dtu.timemanager.domain.IntervalTimeRegistration;
import dtu.timemanager.domain.TimeRegistration;
import dtu.timemanager.domain.User;

import java.time.LocalDate;
import java.util.List;

public interface TimeRegistrationRepository {

    TimeRegistration addTimeRegistration(User user, Activity activity, double hours, LocalDate date) throws Exception;
    IntervalTimeRegistration addIntervalTimeRegistration(User user, String leaveOption, LocalDate startDate, LocalDate endDate) throws Exception;
    List<TimeRegistration> getTimeRegistrationsForUser(User user);
    List<TimeRegistration> getTimeRegistrationsForActivity(Activity activity);
    List<IntervalTimeRegistration> getIntervalTimeRegistrationsForUser(User user);
    void updateTimeRegistration(TimeRegistration registration);

    void clearTimeRegistrationDataBase();
}
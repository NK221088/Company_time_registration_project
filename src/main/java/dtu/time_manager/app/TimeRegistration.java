package dtu.time_manager.app;

import java.time.LocalDate;
import java.util.List;
import java.util.Map;

public class TimeRegistration {
    private User registeredUser;
    private Activity registeredActivity;
    private double registeredHours;
    private LocalDate registeredDate;

    public TimeRegistration(User registeredUser, Activity registeredActivity, double registeredHours, LocalDate registeredDate) throws Exception {
        this.registeredUser = registeredUser;
        this.registeredActivity = registeredActivity;
        this.registeredHours = registeredHours;
        this.registeredDate = registeredDate;

        if (registeredDate.isAfter(LocalDate.now())) {
            throw new IllegalArgumentException("Date is after now");
        }
        if (this.registeredActivity.getFinalized()){
            throw new Exception("The activity is set as finalized: Time registrations can't be added.");
        }

        registeredUser.addTimeRegistration(this);
    }
    public User getRegisteredUser() {
        return registeredUser;
    }
    public Activity getRegisteredActivity() {
        return registeredActivity;
    }
    public double getRegisteredHours() {
        return registeredHours;
    }
    public LocalDate getRegisteredDate() {
        return registeredDate;
    }

    public void setRegisteredUser(User registeredUser) {
        Map<Activity, List<TimeRegistration>> registrationList = this.registeredUser.getActivityRegistrations();
        List<TimeRegistration> actRegList = registrationList.get(this.registeredActivity);

        if (this.registeredUser != null && this.registeredUser != registeredUser && actRegList.size() <= 1) {
            this.registeredActivity.getWorkingUsers().remove(this.registeredUser);
            this.registeredUser = registeredUser;

        } else { this.registeredUser = registeredUser;}

    }

    public void setRegisteredActivity(Activity registeredActivity) {
        this.registeredActivity = registeredActivity;
    }
    public void setRegisteredHours(double registeredHours) {
        this.registeredHours = registeredHours;
    }
    public void setRegisteredDate(LocalDate registeredDate) {
        this.registeredDate = registeredDate;
    }

}

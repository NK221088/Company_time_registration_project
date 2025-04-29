package dtu.time_manager.app;

import java.time.LocalDate;

public class TimeRegistration {
    private User registeredUser;
    private Activity registeredActivity;
    private double registeredHours;
    private LocalDate registeredDate;

    public TimeRegistration(User registeredUser, Activity registeredActivity, double registeredHours, LocalDate registeredDate) {
        this.registeredUser = registeredUser;
        this.registeredActivity = registeredActivity;
        this.registeredHours = registeredHours;
        this.registeredDate = registeredDate;

        if (registeredDate.isAfter(LocalDate.now())) {
            throw new IllegalArgumentException("Date is after now");
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
        this.registeredUser = registeredUser;
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

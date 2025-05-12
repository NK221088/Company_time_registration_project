package dtu.timemanager.domain;

import java.time.LocalDate;
import java.util.List;
import java.util.Map;

// Alexander Wittrup
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

        if (!(this instanceof IntervalTimeRegistration)) {
            if (this.registeredActivity.getFinalized()) {
                throw new Exception("The activity is set as finalized: time registrations can't be added.");
            }
            registeredUser.addTimeRegistration(this);
        }
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

    // Alexander Wittrup
    public void setRegisteredUser(User registeredUser) {
        if (this instanceof IntervalTimeRegistration) {
            this.registeredUser = registeredUser;
            return;
        }

        Map<Activity, List<TimeRegistration>> registrationList = this.registeredUser.getActivityRegistrations();
        List<TimeRegistration> actRegList = registrationList.get(this.registeredActivity);

        if (this.registeredUser != registeredUser) { // IF CHANGE TO NEW USER
            if (actRegList.size() < 2) {
                this.registeredActivity.getContributingUsers().remove(this.registeredUser);
            }
            this.registeredActivity.addContributingUser(registeredUser);
        }
        this.registeredUser = registeredUser;
    }

    // Nikolai Kuhl
    public void setRegisteredActivity(Activity registeredActivity) throws Exception {
        if (!registeredActivity.getFinalized()) {
            this.registeredActivity = registeredActivity;
        } else {throw new Exception("The activity is set as finalized: Time registrations can't be added.");}
    }

    public void setRegisteredHours(double registeredHours) {
        this.registeredHours = registeredHours;
    }

    // Nikolai Kuhl
    public void setRegisteredDate(LocalDate registeredDate) throws Exception {
        if (!registeredDate.isAfter(LocalDate.now())) {
            this.registeredDate = registeredDate;
        } else {
            throw new Exception("Registered date cannot be in the future.");
        }
    }
}
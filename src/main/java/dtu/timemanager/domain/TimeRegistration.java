package dtu.timemanager.domain;

import javax.persistence.*;
import java.time.LocalDate;
import java.util.List;
import java.util.Map;

@Entity
@Table(name = "time_registrations")
@Inheritance(strategy = InheritanceType.JOINED)
@DiscriminatorColumn(name = "registration_type")
public class TimeRegistration {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "user_id")
    private User registeredUser;

    @ManyToOne
    @JoinColumn(name = "activity_id")
    private Activity registeredActivity;

    private double registeredHours;

    @Column(name = "registered_date")
    private LocalDate registeredDate;

    // JPA requires a no-arg constructor
    protected TimeRegistration() {}

    public TimeRegistration(User registeredUser, Activity registeredActivity, double registeredHours, LocalDate registeredDate) throws Exception {
        this.registeredUser = registeredUser;
        this.registeredActivity = registeredActivity;
        this.registeredHours = registeredHours;
        this.registeredDate = registeredDate;

        if (!(this instanceof IntervalTimeRegistration)) {
            if (registeredActivity != null && registeredActivity.getFinalized()) {
                throw new Exception("The activity is set as finalized: time registrations can't be added.");
            }
            if (registeredUser != null) {
                registeredUser.addTimeRegistration(this);
            }
        }
    }

    public Long getId() {
        return id;
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
        if (this instanceof IntervalTimeRegistration) {
            this.registeredUser = registeredUser;
            return;
        }

        if (this.registeredUser != null && this.registeredActivity != null) {
            Map<Activity, List<TimeRegistration>> registrationList = this.registeredUser.getActivityRegistrations();
            List<TimeRegistration> actRegList = registrationList.get(this.registeredActivity);

            if (this.registeredUser != registeredUser) { // IF CHANGE TO NEW USER
                if (actRegList != null && actRegList.size() < 2) {
                    this.registeredActivity.getContributingUsers().remove(this.registeredUser);
                }
                this.registeredActivity.addContributingUser(registeredUser);
            }
        }
        this.registeredUser = registeredUser;
    }

    public void setRegisteredActivity(Activity registeredActivity) throws Exception {
        if (registeredActivity == null || !registeredActivity.getFinalized()) {
            this.registeredActivity = registeredActivity;
        } else {
            throw new Exception("The activity is set as finalized: Time registrations can't be added.");
        }
    }

    public void setRegisteredHours(double registeredHours) {
        this.registeredHours = registeredHours;
    }

    public void setRegisteredDate(LocalDate registeredDate) throws Exception {
        if (registeredDate == null || !registeredDate.isAfter(LocalDate.now())) {
            this.registeredDate = registeredDate;
        } else {
            throw new Exception("Registered date cannot be in the future.");
        }
    }
}
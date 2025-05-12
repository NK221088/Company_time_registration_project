package dtu.timemanager.domain;

import jakarta.persistence.*;
import java.time.LocalDate;
import java.util.Objects;

// Alexander Wittrup
@Entity
@Table(name = "time_registrations")
@Inheritance(strategy = InheritanceType.JOINED)
@DiscriminatorColumn(name = "registration_type")
public class TimeRegistration {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    public Long id;

    @ManyToOne
    @JoinColumn(name = "user_initials")
    private User registeredUser;

    @ManyToOne
    @JoinColumn(name = "activity_name")
    private Activity registeredActivity;


    private double registeredHours;

    @Column(name = "registered_date")
    private LocalDate registeredDate;

    // JPA requires a no-arg constructor
    public TimeRegistration() {}

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
            if (registeredActivity != null) {
                registeredActivity.addContributingUser(registeredUser);
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

    // Alexander Wittrup
    public void setRegisteredUser(User registeredUser) {
//        if (this instanceof IntervalTimeRegistration) {
//            this.registeredUser = registeredUser;
//            return;
//        }
//
//        Map<Activity, List<TimeRegistration>> registrationList = this.registeredUser.getActivityRegistrations();
//        List<TimeRegistration> actRegList = registrationList.get(this.registeredActivity);
//
//        if (this.registeredUser != registeredUser) { // IF CHANGE TO NEW USER
//            if (actRegList.size() < 2) {
//                this.registeredActivity.getContributingUsers().remove(this.registeredUser);
//            }
//            this.registeredActivity.addContributingUser(registeredUser);
//        }
        this.registeredUser = registeredUser;
    }

    // Nikolai Kuhl
    public void setRegisteredActivity(Activity registeredActivity) throws Exception {
        if (registeredActivity == null || !registeredActivity.getFinalized()) {
            this.registeredActivity = registeredActivity;
        } else {
            throw new Exception("The activity is set as finalized: Time registrations can't be added.");
        }
    }

    public void setRegisteredHours(double registeredHours) throws Exception {
        this.registeredHours = registeredHours;
    }

    // Nikolai Kuhl
    public void setRegisteredDate(LocalDate registeredDate) throws Exception {
        if (registeredDate == null || !registeredDate.isAfter(LocalDate.now())) {
            this.registeredDate = registeredDate;
        } else {
            throw new Exception("Registered date cannot be in the future.");
        }
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        TimeRegistration that = (TimeRegistration) o;
        return Double.compare(that.registeredHours, registeredHours) == 0 &&
                Objects.equals(registeredDate, that.registeredDate) &&
                Objects.equals(registeredUser, that.registeredUser) &&
                Objects.equals(registeredActivity, that.registeredActivity);
    }

    @Override
    public int hashCode() {
        return Objects.hash(registeredHours, registeredDate, registeredUser, registeredActivity);
    }
}
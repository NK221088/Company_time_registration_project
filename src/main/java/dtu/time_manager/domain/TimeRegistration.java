package dtu.time_manager.domain;

import java.time.LocalDate;
import java.time.LocalTime;

public class TimeRegistration {
    private User user;
    private Activity activity;
    private LocalDate date;
    private LocalTime startTime;
    private LocalTime endTime;
    private double hours;

    public TimeRegistration(User user, Activity activity, LocalDate date, LocalTime startTime, LocalTime endTime) {
        this.user = user;
        this.activity = activity;
        this.date = date;
        this.startTime = startTime;
        this.endTime = endTime;
        this.hours = calculateHours(startTime, endTime);
    }

    private double calculateHours(LocalTime start, LocalTime end) {
        if (start == null || end == null) {
            return 0.0;
        }
        return (end.toSecondOfDay() - start.toSecondOfDay()) / 3600.0;
    }

    public User getUser() {
        return user;
    }

    public Activity getActivity() {
        return activity;
    }

    public LocalDate getDate() {
        return date;
    }

    public LocalTime getStartTime() {
        return startTime;
    }

    public LocalTime getEndTime() {
        return endTime;
    }

    public double getHours() {
        return hours;
    }

    public void updateTimes(LocalTime newStartTime, LocalTime newEndTime) {
        this.startTime = newStartTime;
        this.endTime = newEndTime;
        this.hours = calculateHours(newStartTime, newEndTime);
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (obj == null || getClass() != obj.getClass()) return false;
        TimeRegistration other = (TimeRegistration) obj;
        return user.equals(other.user) &&
               activity.equals(other.activity) &&
               date.equals(other.date) &&
               startTime.equals(other.startTime) &&
               endTime.equals(other.endTime);
    }

    @Override
    public int hashCode() {
        int result = user.hashCode();
        result = 31 * result + activity.hashCode();
        result = 31 * result + date.hashCode();
        result = 31 * result + startTime.hashCode();
        result = 31 * result + endTime.hashCode();
        return result;
    }
} 
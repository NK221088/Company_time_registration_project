package dtu.time_manager.app;

import java.time.LocalDate;

public class TimeRegistration {
    private String registeredName;
    private int registeredHours;
    private LocalDate registeredDate;

    public TimeRegistration(String registeredName, int registeredHours, LocalDate registeredDate) {
        this.registeredName = registeredName;

        this.registeredHours = registeredHours;

        this.registeredDate = registeredDate;
        if (registeredDate.isAfter(LocalDate.now())) {
            throw new IllegalArgumentException("Date is after now");
        }
    }

    public String getRegisteredName() {
        return registeredName;
    }

    public int getRegisteredHours() {
        return registeredHours;
    }

    public LocalDate getRegisteredDate() {
        return registeredDate;
    }
}

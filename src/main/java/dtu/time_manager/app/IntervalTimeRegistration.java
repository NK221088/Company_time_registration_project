package dtu.time_manager.app;

import java.time.LocalDate;

public class IntervalTimeRegistration extends TimeRegistration {
    private LocalDate startDate;
    private LocalDate endDate;

    public IntervalTimeRegistration(User user, Activity activity, LocalDate startDate, LocalDate endDate) throws Exception {
        super(user, activity, 0.0, LocalDate.now());

        if (endDate.isBefore(startDate)) { throw new IllegalArgumentException("End date must not be before start date"); }

        this.startDate = startDate;
        this.endDate   = endDate;
    }

    public void setStartDate(LocalDate startDate) {
        this.startDate = startDate;
    }
    public LocalDate getStartDate() {
        return startDate;
    }

    public void setEndDate(LocalDate endDate) {
        this.endDate = endDate;
    }
    public LocalDate getEndDate() {
        return endDate;
    }

    @Override
    public LocalDate getRegisteredDate() {
        return endDate;
    }
}
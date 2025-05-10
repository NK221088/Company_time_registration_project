package dtu.timemanager.domain;

import java.time.LocalDate;

public class IntervalTimeRegistration extends TimeRegistration {
    private LocalDate startDate;
    private LocalDate endDate;
    private String leaveOption;

    public IntervalTimeRegistration(User user, String leaveOption, LocalDate startDate, LocalDate endDate) throws Exception {
        super(user, null, 0.0, null);

        if (endDate.isBefore(startDate)) { throw new IllegalArgumentException("End date must not be before start date"); }

        this.leaveOption = leaveOption;
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

    public String getLeaveOption() {
        return leaveOption;
    }

    public String getTimeInterval() {
        String p1 = getStartDate() != null ? getStartDate().toString() : "";
        String p2 = getEndDate() != null ? getEndDate().toString() : "";
        return p1 + " - " + p2;
    }
}
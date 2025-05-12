package dtu.timemanager.domain;

import jakarta.persistence.*;
import java.time.LocalDate;

@Entity
@Table(name = "interval_time_registrations")
@DiscriminatorValue("INTERVAL")
// Alexander Wittrup
public class IntervalTimeRegistration extends TimeRegistration {
    @Id
    private int id;

    @Column(name = "start_date")
    private LocalDate startDate;

    @Column(name = "end_date")
    private LocalDate endDate;

    @Column(name = "leave_option")
    private String leaveOption;

    public IntervalTimeRegistration() {
        super();
    }

    public IntervalTimeRegistration(User user, String leaveOption, LocalDate startDate, LocalDate endDate) throws Exception {
        super(user, null, 0.0, null);

        if (endDate.isBefore(startDate)) {
            throw new IllegalArgumentException("End date must not be before start date");
        }

        this.leaveOption = leaveOption;
        this.startDate = startDate;
        this.endDate = endDate;
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

    public String getLeaveOption() {
        return leaveOption;
    }

    public void setLeaveOption(String leaveOption) {
        this.leaveOption = leaveOption;
    }

    public String getTimeInterval() {
        String p1 = getStartDate().toString();
        String p2 = getEndDate().toString();
        return p1 + " - " + p2;
    }
}
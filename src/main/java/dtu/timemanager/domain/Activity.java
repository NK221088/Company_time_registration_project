package dtu.timemanager.domain;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Map;
import java.util.*;

// Nikolai Kuhl?
public class Activity {
    private String activityName;
    private ArrayList<User> assignedUsers = new ArrayList<>();
    private ArrayList<User> contributingUsers = new ArrayList<>();
    private double expectedWorkHours;
    private LocalDate activityStartTime;
    private LocalDate activityEndTime;
    private Boolean isFinalized = false;

    public Activity(String activityName) {
        this.activityName = activityName;
    }

    public void setExpectedWorkHours(double expectedWorkHours) { this.expectedWorkHours = expectedWorkHours; }

    public double getExpectedWorkHours() { return this.expectedWorkHours;}

    // Alexander Wittrup
    public double getWorkedHours() {
        double workedHours = 0.0;
        for (User user : getContributingUsers()) {
            for (TimeRegistration timeReg : user.getActivityRegistrations().getOrDefault(this, Collections.emptyList())) {
                workedHours += timeReg.getRegisteredHours();
            }
        }
        return workedHours;
    }

    public ArrayList<User> getAssignedUsers() { return this.assignedUsers;}

    public String getActivityName() { return this.activityName;}

    // For TA, this is the code which was responsible for the Time Interval view in the GUI,
    // and the last two lines were missing. (StartTime and EndTime) making them null. Is fixed now.
    // Isak Petrin
    public Map<String, Object> viewActivity() {
        Map<String,Object> info = new HashMap();
        info.put("Name", getActivityName());
        info.put("Assigned employees", getAssignedUsers());
        info.put("Contributing employees", getContributingUsers());
        info.put("ExpectedWorkHours", getExpectedWorkHours());
        info.put("WorkedHours", getWorkedHours());
        info.put("Time interval", getTimeInterval());
        info.put("StartTime", getActivityStartTime());
        info.put("EndTime", getActivityEndTime());
        return info;
    }

    // Nikolai Kuhl
    public void assignUser(User user) {
        assert user != null && getAssignedUsers() != null && getActivityName() != null && user.getActivityCount() >= 0 && user.getActivityCount() <= 20 : "precondition";
        int activityCountAtPre = user.getActivityCount();
        if (user.getActivityCount() < 20){ // 1
            if(assignedUsers.contains(user)){ // 2
                assert (assignedUsers.contains(user) && user.getActivityCount() == activityCountAtPre);
                throw new RuntimeException("'" + user.getUserInitials() + "' is already assigned to the activity '" + getActivityName() + "'"); // 3
            }
            assignedUsers.add(user); // 4
            user.incrementActivityCount(); // 5
            assert (assignedUsers.contains(user) &&
                    user.getActivityCount() == activityCountAtPre +1);
        } else {
            assert (!assignedUsers.contains(user) && user.getActivityCount() == activityCountAtPre && user.getActivityCount() == 20);
            throw new RuntimeException("'" + user.getUserInitials() + "' is already assigned to the maximum number of 20 activities"); // 6
        }
    }

    // Isak Petrin
    public void unassignUser(User user) {
        if (this.assignedUsers.contains(user)) {
            user.decrementActivityCount();
            this.assignedUsers.remove(user);
        } else {
            throw new RuntimeException("The user can not be unassigned from an activity they are not assigned to");
        }
    }

    public void addContributingUser(User user) {
        if (!contributingUsers.contains(user)) {
            contributingUsers.add(user);
        }
    }

    // Nikolai Kuhl
    @Override
    public boolean equals(Object obj) {
        if (obj instanceof Activity) {
            return Objects.equals(getActivityName(), ((Activity) obj).getActivityName());
        } else {
            return false;
        }
    }

    public ArrayList<User> getContributingUsers() { return this.contributingUsers; }

    public String toString() {
        return activityName;
    }

    public void setActivityName(String activityName) {
        this.activityName = activityName;
    }

    public LocalDate getActivityStartTime() {
        return activityStartTime;
    }

    // Nikolai Kuhl
    public void setActivityStartTime(LocalDate activityStartTime) throws Exception {
        if (activityEndTime==null || activityStartTime.isBefore(activityEndTime)) {
            this.activityStartTime = activityStartTime;
        } else {
            throw new Exception("The start date of the activity can't be after the start date of the activity.");
        }
    }

    public LocalDate getActivityEndTime() {
        return activityEndTime;
    }

    // Alexander Wittrup
    public String getTimeInterval() {
        String p1 = getActivityStartTime() != null ? getActivityStartTime().toString() : "";
        String p2 = getActivityEndTime() != null ? getActivityEndTime().toString() : "";
        return p1 + " - " + p2;
    }

    // Nikolai Kuhl
    public void setActivityEndTime(LocalDate activityEndTime) throws Exception {
        if (activityStartTime==null || activityEndTime.isAfter(activityStartTime)) {
            this.activityEndTime = activityEndTime;
        } else {
            throw new Exception("The end date of the activity can't be before the start date of the activity.");
        }

    }

    public void setActivityAsFinalized() {
        this.isFinalized = true;
    }

    public boolean getFinalized() {
        return isFinalized;
    }

    public void setActivityAsUnFinalized() {
        this.isFinalized = false;
    }
}
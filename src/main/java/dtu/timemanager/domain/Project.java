package dtu.timemanager.domain;

import javax.persistence.*;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Objects;

@Entity
@Table(name = "projects")
public class Project {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id; // Database ID for JPA

    private String projectName;
    private String projectID;

    @Column(name = "start_date")
    private LocalDate startDate;

    @Column(name = "end_date")
    private LocalDate endDate;

    @ManyToOne
    @JoinColumn(name = "project_lead_id")
    private User projectLead;

    @OneToMany(mappedBy = "project", cascade = CascadeType.ALL, orphanRemoval = true)
    private ArrayList<Activity> activities;

    private boolean isFinalized;

    // No-arg constructor required by JPA
    protected Project() {
        this.activities = new ArrayList<>();
    }

    public Project(String projectName) {
        this(); // Call no-arg constructor to initialize activities
        this.projectName = projectName;
    }

    public void setProjectLead(User projectLead) {
        this.projectLead = projectLead;
    }

    public User getProjectLead() {
        return this.projectLead;
    }

    public void setProjectName(String projectName) {
        this.projectName = projectName;
    }

    public String getProjectName() {
        return projectName;
    }

    public String getProjectID() { return projectID; }

    public void setProjectStartDate(LocalDate startDate) throws Exception {
        if (endDate==null || startDate.isBefore(endDate)) {
            this.startDate = startDate;
        } else {
            throw new Exception("The start date of the project can't be after the end date of the project.");
        }
    }

    public void setProjectEndDate(LocalDate endDate) throws Exception {
        if (startDate==null || endDate.isAfter(startDate)) {
            this.endDate = endDate;
        } else {
            throw new Exception("The end date of the project can't be before the start date of the project.");
        }
    }

    public LocalDate getStartDate() {
        return startDate;
    }

    public LocalDate getEndDate() {
        return endDate;
    }

    public ArrayList<Activity> getActivities() {return activities;}


    public void addActivity(Activity activity) throws Exception {
        if (!activities.contains(activity)) {
            activities.add(activity);
        } else {
            throw new Exception("An activity with name '" + activity.getActivityName() + "' already exists within '" + this.getProjectName() + "' two activities cannot exist with the same name within the same project.");
        }
    }

    @Override
    public boolean equals(Object obj) {
        if (obj instanceof Project) {
            return Objects.equals(getProjectName(), ((Project) obj).getProjectName());
        } else {
            return false;
        }
    }

    public Activity getActivityFromName(String activityName) {
        return getActivities().stream().filter(activity -> activity.getActivityName().equals(activityName)).findFirst().orElse(null);
    }

    public String toString() {
            return projectName;
        }

    public boolean getFinalized() {
        return this.isFinalized;
    }

    public void setFinalized() {
        this.isFinalized = true;
    }

    public void setActivityAsFinalized(Activity activity) {
        activity.setActivityAsFinalized();
        boolean allFinalized = activities.stream()
                .allMatch(a -> a.getFinalized());
        if (allFinalized) {
            setFinalized();
        }
    }

    public void setActivityAsUnFinalized(Activity activity) {
        activity.setActivityAsUnFinalized();
        setUnFinalized(); // If any activity is unfinalized, we mark the whole as unfinalized
    }

    private void setUnFinalized() {
        this.isFinalized = false;
    }

    public String getTimeInterval() {
        String p1 = getStartDate() != null ? getStartDate().toString() : "";
        String p2 = getEndDate() != null ? getEndDate().toString() : "";
        return p1 + " - " + p2;
    }

    public void renameActivity(Activity activity, String newName) throws Exception {
        for (Activity a : activities) {
            if (a.getActivityName().equals(newName)) {
                throw new Exception("An activity with name " + newName + " already exists within "+ this.getProjectName() + ". Two activities cannot exist with the same name within the same project.");
            }
        }
        activity.setActivityName(newName);
    }


    public void setProjectID(String id) {
        this.projectID = id;
    }

    public Object getId() {
        return id;
    }
}
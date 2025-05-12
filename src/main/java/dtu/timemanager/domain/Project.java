package dtu.timemanager.domain;

import jakarta.persistence.*;
import java.time.LocalDate;
import java.util.*;

// Nikolai Kuhl
@Entity
@Table(name = "Project")
public class Project {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)

    public Long id;

    public String projectName;
    public String projectID;

    @Column(name = "start_date")
    public LocalDate startDate;

    @Column(name = "end_date")
    public LocalDate endDate;

    @ManyToOne
    @JoinColumn(name = "project_lead_initials")
    public User projectLead;

    @OneToMany(mappedBy = "project", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.LAZY)
    public List<Activity> activities = new ArrayList<>();

    public boolean isFinalized;

    // No-arg constructor required by JPA
    public Project() {}

    public Project(String projectName) {
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

    public String getProjectID() {
        return projectID;
    }

    // Nikolai Kuhl
    public void setProjectStartDate(LocalDate startDate) throws Exception {
        if (endDate == null || startDate.isBefore(endDate)) {
            this.startDate = startDate;
        } else {
            throw new Exception("The start date of the project can't be after the end date of the project.");
        }
    }

    // Nikolai Kuhl
    public void setProjectEndDate(LocalDate endDate) throws Exception {
        if (startDate == null || endDate.isAfter(startDate)) {
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

    public List<Activity> getActivities() {
        return Collections.unmodifiableList(activities);
    }

    // Isak Petrin
    public void addActivity(Activity activity) throws Exception {
        int activityCountpre = getActivities().size();

        assert activity != null && activity.getActivityName() != null: "precondition";
        if (!activities.contains(activity)) {
            assert (!activities.contains(activity));
            activities.add(activity);
            assert (activities.contains(activity) && activityCountpre != getActivities().size());
            activity.setProject(this); // BEMÆRK
        } else {
            assert (activityCountpre == getActivities().size()) ;
            throw new Exception("An activity with name '" + activity.getActivityName() + "' already exists within '" + this.getProjectName() + "' two activities cannot exist with the same name within the same project.");
        }
    }

    @Override
    public boolean equals(Object obj) {
        if (obj instanceof Project project) {
            if (project.getProjectName() == this.getProjectName()) {
                return true;
            }
            return Objects.equals(getId(), ((Project) obj).getId());
        } else {
            return false;
        }
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(id);
    }

    // Alexander Wittrup
    public Activity getActivityFromName(String activityName) {
        return getActivities().stream()
                .filter(activity -> activity.getActivityName().equals(activityName))
                .findFirst()
                .orElse(null);
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

    // Nikolai Kuhl
    public void setActivityAsFinalized(Activity activity) {
        activity.setActivityAsFinalized();
        boolean allFinalized = activities.stream()
                .allMatch(a -> a.getFinalized());
        if (allFinalized) {
            setFinalized();
        }
    }

    // Nikolai Kuhl
    public void setActivityAsUnFinalized(Activity activity) {
        activity.setActivityAsUnFinalized();
        setUnFinalized(); // If any activity is unfinalized, we mark the whole as unfinalized
    }

    private void setUnFinalized() {
        this.isFinalized = false;
    }

    // Alexander Wittrup
    public String getTimeInterval() {
        String p1 = getStartDate() != null ? getStartDate().toString() : "";
        String p2 = getEndDate() != null ? getEndDate().toString() : "";
        return p1 + " - " + p2;
    }

    // Nikolai Kuhl
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

    public Long getId() {
        return id;
    }
}
package dtu.timemanager.domain;

import java.time.LocalDate;
import java.util.ArrayList;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

public class TimeManager {
    private User current_user;
    private List<User> users = new ArrayList<>();
    private List<Project> projects = new ArrayList<>();
    private List<Activity> independentActivities = new ArrayList<>();
    private Map<String, Project> projectMap = new HashMap<>();
    private List<TimeRegistration> time_registrations = new ArrayList<>();
    private int projectCount = 0;

    public TimeManager() {}

    public void assignUser(Activity activity, User user) {
        activity.assignUser(user);
    }

    public void unassignUser(Activity activity, User user) {
        activity.unassignUser(user);
    }

    public void setCurrentUser(User user) {
        current_user = user;
    }

    public void addUser(User user) {
        if (!users.contains(user)) {
            users.add(user);
        }
    }

    public User getUserFromInitials(String user_initials) {
        try {
            return users.stream().filter(user -> user.getUserInitials().equals(user_initials)).findFirst().get();
        } catch (Exception e) {
            throw new RuntimeException("The user " + user_initials + " don't exist in the system.");
        }
    }

    public List<User> getUsers() { return users; }

    public User getCurrentUser() { return current_user; }

    private String formatID(int count) { return "25" + String.format("%03d", count); }

    public Project createProject(String projectName) {
        String id = formatID(++this.projectCount);
        return new Project(projectName, id);
    }

    public Project createExampleProject(String projectName, Integer numberOfActivities) throws Exception {
        Project project = createProject(projectName);
        project.setProjectStartDate(LocalDate.parse("2025-01-01"));
        project.setProjectEndDate(LocalDate.parse("2025-01-08"));
        try {
            for (int i = 1; i <= numberOfActivities; i++) {
                project.addActivity(new Activity("Activity "+String.valueOf(i)));
            }
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
        return project;
    }

    public void addProject(Project project) {
        if (!projectExists(project)) {
            projectMap.put(project.getProjectID(), project);
            projects.add(project);
        } else {
            decProjectCount();
            throw new RuntimeException("A project with name '" + project.getProjectName() + "' already exists in the system and two projects can’t have the same name.");
        }
    }

    private void decProjectCount() {
        projectCount--;
    }

    public List<Project> getProjects() {
        return projects;
    }

    public int incProjectCount() {
        return ++projectCount;
    }

    public int getProjectCount() {
        return projectCount;
    }

    public boolean projectExists(Project project) {
        return projects.contains(project);
    }
    public boolean projectExists(String projectName) {
        return projects.contains(projectName);
    }

    public Map viewProject(Project project) {
        Map<String, Object> projectVariables = new HashMap<>();

        LocalDate startDate = project.getStartDate();
        LocalDate endDate = project.getEndDate();
        String projectInterval;

        if (startDate != null) {
            projectInterval = startDate.toString() + " - " + endDate.toString(); // Formatting the project interval so it looks correct
        } else {
            projectInterval = "";
        }

        projectVariables.put("Project name", project.getProjectName());
        projectVariables.put("Project ID", project.getProjectID());
        if (project.getProjectLead() != null) {
            projectVariables.put("Project Lead", project.getProjectLead());
        } else {
            projectVariables.put("Project Lead", "");
        }
        projectVariables.put("Project interval", projectInterval);
        projectVariables.put("Project activities", project.getActivities());

        return projectVariables;
    }

    public Map getProjectReport(Project project) {
        String projectID = project.getProjectID(); // Retrieve the project for which to generate the project report
        Map<String, Object> reportVariables = new HashMap<>();
        reportVariables.put("Project Name", project.getProjectName()); // Insert the name in the report
        reportVariables.put("Project ID", projectID); // Insert the project ID in the report
        LocalDate startDate = project.getStartDate();
        LocalDate endDate = project.getEndDate();
        String projectInterval;
        User projectLead = project.getProjectLead();

        if (startDate != null) {
            projectInterval = startDate.toString() + " - " + endDate.toString(); // Formatting the project interval so it looks correct
        } else {
            projectInterval = "";
        }
        reportVariables.put("Project interval", projectInterval);
        reportVariables.put("Project ID", projectID);
        if (projectLead != null) {
            reportVariables.put("Project Lead", projectLead);
        } else {
            reportVariables.put("Project Lead", "");
        }
        List<Activity> activities = project.getActivities();
        Map<Activity, Double> workedHours = project.getActivities().stream()
        .collect(Collectors.toMap(
                Function.identity(),        // key mapping function
                Activity::getWorkedHours   // value mapping function
        ));
        reportVariables.put("Worked hours", workedHours);

        Map<Activity, Double> expectedHours = project.getActivities().stream()
                .collect(Collectors.toMap(
                        Function.identity(),        // key mapping function
                        Activity::getWorkedHours   // value mapping function
                ));
        reportVariables.put("Expected hours", expectedHours);

        reportVariables.put("Project Activities", activities); // List all the activities in the project
        Map<Activity, List> assignedEmployees = project.getActivities().stream()
                .collect(Collectors.toMap(
                        Function.identity(),        // key mapping function
                        Activity::getAssignedUsers   // value mapping function
                ));
        reportVariables.put("Assigned employees", assignedEmployees);
        Map<Activity, List> contributingEmployees = project.getActivities().stream()
                .collect(Collectors.toMap(
                        Function.identity(),        // key mapping function
                        Activity::getWorkingUsers   // value mapping function
                ));
        reportVariables.put("Contributing employees", assignedEmployees);
        Map<Activity, String> activityIntervals = project.getActivities().stream()
                .collect(Collectors.toMap(
                        Function.identity(),
                        activity -> {
                            String start = activity.getActivityStartTime() != null
                                    ? activity.getActivityStartTime().toString()
                                    : "";
                            String end = activity.getActivityEndTime() != null
                                    ? activity.getActivityEndTime().toString()
                                    : "";
                            return start + "-" + end;
                        }
                ));


        reportVariables.put("Activity intervals", activityIntervals);


        return reportVariables;
    }

    public void addTimeRegistration(TimeRegistration timeRegistration) {
        time_registrations.add(timeRegistration);
    }

    public List<TimeRegistration> getTimeRegistrations() {
        return time_registrations;
    }

    public void addIndependentActivity(Activity activity) throws Exception {
        if (!independentActivities.contains(activity)) {
            independentActivities.add(activity);
        } else {
            throw new RuntimeException(
                "An independent activity with name '" + activity.getActivityName() + "' already exists."
            );
        }
    }

    public List<Activity> getIndependentActivities() { return independentActivities; }

    public void appInitialize() throws Exception {
        User isak = new User("isak");
        User bria = new User("bria");
        User huba = new User("huba");

        addUser(isak); addUser(bria); addUser(huba);

        Project project1 = createExampleProject("Project 1", 1);
        Project project2 = createExampleProject("Project 2", 2);
        addProject(project1); addProject(project2);

        for (Activity activity : project1.getActivities()) { assignUser(activity, bria); }
        for (Activity activity : project2.getActivities()) { assignUser(activity, isak); }
    }
}

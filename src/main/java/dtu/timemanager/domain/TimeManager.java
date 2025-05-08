package dtu.timemanager.domain;
import dtu.timemanager.services.projectService;

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
    private List<TimeRegistration> time_registrations = new ArrayList<>();
    private int projectCount = 0;
    private projectService IProjectService = new projectService();

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
        IProjectService.addProject(project);
    }

    private void decProjectCount() {
        projectCount--;
    }

    public List<Project> getProjects() {
        return IProjectService.getProjects();
    }

    public int getProjectCount() {
        return IProjectService.getProjectCount();
    }

    public boolean projectExists(Project project) {
        return IProjectService.projectExists(project);
    }
    public boolean projectExists(String projectName) {
        return IProjectService.projectExists(projectName);
    }

    public ProjectReport getProjectReport(Project project) {
        return IProjectService.getProjectReport(project);
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

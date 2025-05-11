package dtu.timemanager.domain;
import dtu.timemanager.services.IProjectService;

import java.util.ArrayList;

import java.util.List;

public class TimeManager {
    private User current_user;
    private List<User> users = new ArrayList<>();
    private List<IntervalTimeRegistration> intervalTimeRegistrations = new ArrayList<>();
    private List<TimeRegistration> timeRegistrations = new ArrayList<>();
    private int projectCount = 0;
    private IProjectService projectService = new IProjectService();

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

    public void addUser(User user) throws Exception {
        String initials = user.getUserInitials();

        if (!initials.matches("[a-zA-Z]{4}")) {
            throw new Exception("The user initials must be 4 letters.");
        }

        if (!users.contains(user)) {
            users.add(user);
        } else {
            throw new Exception("A user with initials '" + initials + "' is already registered in the system, please change the initials and try again.");
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

    public Project addExampleProject(String projectName, Integer numberOfActivities) throws Exception {
        Project project = addProject(projectName);
        try {
            for (int i = 1; i <= numberOfActivities; i++) {
                project.addActivity(new Activity("Activity "+String.valueOf(i)));
            }
        } catch (Exception ignored) {}
        return project;
    }

    public Project addProject(String projectName) {
        return projectService.addProject(projectName);
    }

    public List<Project> getProjects() {
        return projectService.getProjects();
    }

    public int getProjectCount() {
        return projectService.getProjectCount();
    }

    public boolean projectExists(Project project) {
        return projectService.getProjects().contains(project);
    }

    public ProjectReport getProjectReport(Project project) {
        return projectService.getProjectReport(project);
    }

    public void addTimeRegistration(TimeRegistration timeRegistration) {
        timeRegistrations.add(timeRegistration);
    }

    public List<TimeRegistration> getTimeRegistrations() {
        return timeRegistrations;
    }

    public void addIntervalTimeRegistration(IntervalTimeRegistration intervalTimeRegistration) {

        intervalTimeRegistrations.add(intervalTimeRegistration);
    }

    public List<IntervalTimeRegistration> getIntervalTimeRegistrations() {
        return intervalTimeRegistrations;
    }

    public void renameProject(Project project, String newName) {
        projectService.renameProject(project, newName);
    }
}

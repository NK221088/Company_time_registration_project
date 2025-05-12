package dtu.timemanager.domain;

import dtu.timemanager.persistence.SqliteRepository;

import java.util.ArrayList;
import java.util.List;

public class TimeManager {
    private SqliteRepository repository;
    private User current_user;
    private List<User> users = new ArrayList<>();
    private List<IntervalTimeRegistration> intervalTimeRegistrations = new ArrayList<>();
    private List<TimeRegistration> timeRegistrations = new ArrayList<>();
    private int projectCount = 0;

    public TimeManager(SqliteRepository repository) {
        this.repository = repository;
    }

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

    private String formatID(int count) { return "25" + String.format("%03d", count); }

    public Project addProject(String projectName) {
        Project project = new Project(projectName);
        if (!projectExists(project)) {
            String id = formatID(++this.projectCount);
            project.setProjectID(id);
            repository.addProject(projectName);
            return project;
        } else {
            throw new IllegalArgumentException("A project with name '" + project.getProjectName() + "' already exists in the system and two projects can’t have the same name.");
        }
    }

    public List<Project> getProjects() {
        return new ArrayList<>(repository.getProjects());
    }

    public int getProjectCount() {return projectCount;}

    public boolean projectExists(Project project) {
        return repository.projectExists(project);
    }

    public ProjectReport getProjectReport(Project project) {
        return new ProjectReport(project);
    }

    public void addTimeRegistration(TimeRegistration timeRegistration) throws Exception {
        repository.addTimeRegistration(timeRegistration.getRegisteredUser(), timeRegistration.getRegisteredActivity(), timeRegistration.getRegisteredHours(), timeRegistration.getRegisteredDate());
    }

    public List<TimeRegistration> getTimeRegistrations() {
        return timeRegistrations;
    }

    public void addIntervalTimeRegistration(IntervalTimeRegistration intervalTimeRegistration) throws Exception {
        repository.addIntervalTimeRegistration(intervalTimeRegistration.getRegisteredUser(), intervalTimeRegistration.getLeaveOption(), intervalTimeRegistration.getStartDate(), intervalTimeRegistration.getEndDate());
    }

    public List<IntervalTimeRegistration> getIntervalTimeRegistrations() {
        return intervalTimeRegistrations;
    }

    public void renameProject(Project project, String newName) throws IllegalArgumentException {
        for (Project p : repository.getProjects()) {
            if (p.getProjectName().equals(newName)) {
                throw new RuntimeException("A project with name " + newName + " already exists and two projects cannot exist with the same name.");
            }
        }
        project.setProjectName(newName);
    }

    public void assignProjectLead(Project project, User user) {
        project.setProjectLead(user);
    }
}

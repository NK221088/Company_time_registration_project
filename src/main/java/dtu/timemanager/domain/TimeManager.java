package dtu.timemanager.domain;

import dtu.timemanager.app.ActivityRepository;
import dtu.timemanager.app.ProjectRepository;
import dtu.timemanager.app.TimeRegistrationRepository;
import dtu.timemanager.app.UserRepository;
import dtu.timemanager.persistence.SqliteRepository;

import java.util.ArrayList;
import java.util.List;

public class TimeManager {
    private User current_user;
    private List<IntervalTimeRegistration> intervalTimeRegistrations = new ArrayList<>();
    private List<TimeRegistration> timeRegistrations = new ArrayList<>();
    private int projectCount = 0;
    private ActivityRepository activityRepository;
    private ProjectRepository projectRepository;
    private TimeRegistrationRepository timeRegistrationRepository;
    private UserRepository userRepository;

    public TimeManager(ActivityRepository activityRepository, ProjectRepository projectRepository, TimeRegistrationRepository timeRegistrationRepository, UserRepository userRepository) {
        this.activityRepository = activityRepository;
        this.projectRepository = projectRepository;
        this.timeRegistrationRepository = timeRegistrationRepository;
        this.userRepository = userRepository;
    }
    public TimeManager() {
        SqliteRepository sqliteRepository = new SqliteRepository(false);
        this.activityRepository = sqliteRepository;
        this.projectRepository = sqliteRepository;
        this.timeRegistrationRepository = sqliteRepository;
        this.userRepository = sqliteRepository;
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
        userRepository.addUser(user);
//        String initials = user.getUserInitials();
//
//        if (!initials.matches("[a-zA-Z]{4}")) {
//            throw new Exception("The user initials must be 4 letters.");
//        }
//
//        if (!users.contains(user)) {
//            users.add(user);
//        } else {
//            throw new Exception("A user with initials '" + initials + "' is already registered in the system, please change the initials and try again.");
//        }
    }

    public User getUserFromInitials(String user_initials) {
        return userRepository.getUserByUsername(user_initials);
//        try {
//            return users.stream().filter(user -> user.getUserInitials().equals(user_initials)).findFirst().get();
//        } catch (Exception e) {
//            throw new RuntimeException("The user " + user_initials + " don't exist in the system.");
//        }
    }

    public List<User> getUsers() { return userRepository.getUsers(); }

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
            projectRepository.addProject(projectName);
            return project;
        } else {
            throw new IllegalArgumentException("A project with name '" + project.getProjectName() + "' already exists in the system and two projects can’t have the same name.");
        }
    }

    public List<Project> getProjects() {
        return new ArrayList<>(projectRepository.getProjects());
    }

    public int getProjectCount() {return projectCount;}

    public boolean projectExists(Project project) {
        return projectRepository.projectExists(project);
    }

    public ProjectReport getProjectReport(Project project) {
        return new ProjectReport(project);
    }

    public void addTimeRegistration(TimeRegistration timeRegistration) throws Exception {
        timeRegistrationRepository.addTimeRegistration(timeRegistration.getRegisteredUser(), timeRegistration.getRegisteredActivity(), timeRegistration.getRegisteredHours(), timeRegistration.getRegisteredDate());
    }

    public List<TimeRegistration> getTimeRegistrations() {
        return timeRegistrations;
    }

    public void addIntervalTimeRegistration(IntervalTimeRegistration intervalTimeRegistration) throws Exception {
        timeRegistrationRepository.addIntervalTimeRegistration(intervalTimeRegistration.getRegisteredUser(), intervalTimeRegistration.getLeaveOption(), intervalTimeRegistration.getStartDate(), intervalTimeRegistration.getEndDate());
    }

    public List<IntervalTimeRegistration> getIntervalTimeRegistrations() {
        return intervalTimeRegistrations;
    }

    public void renameProject(Project project, String newName) throws IllegalArgumentException {
        for (Project p : projectRepository.getProjects()) {
            if (p.getProjectName().equals(newName)) {
                throw new RuntimeException("A project with name " + newName + " already exists and two projects cannot exist with the same name.");
            }
        }
        project.setProjectName(newName);
    }

    public void assignProjectLead(Project project, User user) {
        project.setProjectLead(user);
    }

    public void clearDatabase() {
        activityRepository.clearActivityDataBase();
        projectRepository.clearProjectDataBase();
        timeRegistrationRepository.clearTimeRegistrationDataBase();
        userRepository.clearUserDatabase();

    }
}

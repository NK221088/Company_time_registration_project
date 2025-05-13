package dtu.timemanager.domain;

import dtu.timemanager.app.ActivityRepository;
import dtu.timemanager.app.ProjectRepository;
import dtu.timemanager.app.TimeRegistrationRepository;
import dtu.timemanager.app.UserRepository;
import dtu.timemanager.persistence.SqliteRepository;

import java.util.ArrayList;
import java.util.List;

public class TimeManager {
    private User currentUser;
    private SqliteRepository repository;
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
        currentUser = user;
    }

    // Nikolai Kuhl
    public void addUser(User user) throws Exception {
        String initials = user.getUserInitials();

        if (!initials.matches("[a-zA-Z]{4}")) {
            throw new Exception("The user initials must be 4 letters.");
        }

        if (!userRepository.getUsers().contains(user)) {
            userRepository.addUser(user);
        } else {
            throw new Exception("A user with initials '" + initials + "' is already registered in the system, please change the initials and try again.");
        }
    }

    // Alexander Wittrup
    public User getUserFromInitials(String user_initials) {
        User user = userRepository.getUserByUsername(user_initials);
        if (user != null) {
            return user;
        }
        try {
            return userRepository.getUsers().stream()
                    .filter(u -> u.getUserInitials().equals(user_initials))
                    .findFirst()
                    .orElseThrow(() -> new RuntimeException("The user " + user_initials + " doesn't exist in the system."));
        } catch (Exception e) {
            throw new RuntimeException("The user " + user_initials + " doesn't exist in the system.", e);
        }
    }

    public List<User> getUsers() { return userRepository.getUsers(); }

    public User getCurrentUser() { return currentUser; }

    // Alexander Wittrup
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

    // Alexander Wittrup
    public Project addProject(String projectName) {
        assert projectName != null && getProjectCount() == projectRepository.getProjects().size();
        List<Project> projectsPre = new ArrayList<>(getProjects());
        int projectCountPre = getProjectCount();

        Project project = new Project(projectName);
        if (!projectExists(project)) {
            String id = formatID(++this.projectCount);
            project.setProjectID(id);
            projectRepository.addProject(projectName);

            assert !projectsPre.contains(project) && getProjects().contains(project) && getProjectCount() == projectCountPre + 1 && project.getProjectID().equals(formatID(getProjectCount()));
            return project;
        } else {
            assert projectsPre.contains(project)
                    && getProjects().equals(projectsPre)
                    && getProjectCount() == projectCountPre;
            throw new IllegalArgumentException("A project with name '" + project.getProjectName() + "' already exists in the system and two projects can’t have the same name.");
        }
    }

    public List<Project> getProjects() {
        return new ArrayList<>(projectRepository.getProjects());
    }

    public int getProjectCount() {
        return projectCount;
    }

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
        return timeRegistrationRepository.getAllTimeRegistrations();
    }

    public void addIntervalTimeRegistration(IntervalTimeRegistration intervalTimeRegistration) throws Exception {
        timeRegistrationRepository.addIntervalTimeRegistration(intervalTimeRegistration.getRegisteredUser(), intervalTimeRegistration.getLeaveOption(), intervalTimeRegistration.getStartDate(), intervalTimeRegistration.getEndDate());
    }


    // Nikolai Kuhl
    public void renameProject(Project project, String newName) throws IllegalArgumentException {
        for (Project p : projectRepository.getProjects()) {
            if (p.getProjectName().equals(newName)) {
                throw new RuntimeException("A project with name " + newName + " already exists and two projects cannot exist with the same name.");
            }
        }
        project.setProjectName(newName);
    }

    public void clearDatabase() {
        activityRepository.clearActivityDataBase();
        projectRepository.clearProjectDataBase();
        timeRegistrationRepository.clearTimeRegistrationDataBase();
        userRepository.clearUserDatabase();
    }
}

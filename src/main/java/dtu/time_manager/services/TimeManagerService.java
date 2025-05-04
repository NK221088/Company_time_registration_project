package dtu.time_manager.services;

import dtu.time_manager.domain.*;
import dtu.time_manager.interfaces.*;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;
import java.util.Map;

public class TimeManagerService {
    private final IUserService userService;
    private final IProjectService projectService;
    private final IActivityService activityService;
    private final ITimeRegistrationService timeRegistrationService;

    public TimeManagerService() {
        // Initialize services with proper dependencies
        this.userService = new UserService();
        this.activityService = new ActivityService(userService);
        this.projectService = new ProjectService();
        this.timeRegistrationService = new TimeRegistrationService(userService, activityService);
    }

    // User Management
    public void login(String userInitials) {
        userService.login(userInitials);
    }

    public void logout() {
        userService.logout();
    }

    public User getCurrentUser() {
        return userService.getCurrentUser();
    }

    public void addUser(User user) {
        userService.addUser(user);
    }

    // Project Management
    public void createProject(String projectName) {
        Project project = new Project(projectName);
        projectService.addProject(project);
    }

    public void assignProjectLead(String projectName, String userInitials) {
        Project project = projectService.getProjectFromName(projectName);
        User user = userService.getUser(userInitials);
        projectService.assignProjectLead(project, user);
    }

    public Map<String, Object> viewProject(String projectID) {
        return projectService.viewProject(projectID);
    }

    public Map<String, Object> getProjectReport(String projectID) {
        return projectService.getProjectReport(projectID);
    }

    // Activity Management
    public void createActivity(String projectName, String activityName) {
        Project project = projectService.getProjectFromName(projectName);
        Activity activity = new Activity(activityName);
        project.addActivity(activity);
    }

    public void assignUserToActivity(String projectName, String activityName, String userInitials) {
        Project project = projectService.getProjectFromName(projectName);
        Activity activity = project.getActivities().stream()
                .filter(a -> a.getActivityName().equals(activityName))
                .findFirst()
                .orElseThrow(() -> new RuntimeException("Activity not found"));
        activityService.assignUserToActivity(activity, userInitials);
    }

    // Time Registration
    public void registerTime(String projectName, String activityName, LocalDate date, 
                           LocalTime startTime, LocalTime endTime) {
        User currentUser = getCurrentUser();
        if (currentUser == null) {
            throw new RuntimeException("No user is currently logged in");
        }

        Project project = projectService.getProjectFromName(projectName);
        Activity activity = project.getActivities().stream()
                .filter(a -> a.getActivityName().equals(activityName))
                .findFirst()
                .orElseThrow(() -> new RuntimeException("Activity not found"));

        TimeRegistration timeRegistration = new TimeRegistration(
            currentUser, activity, date, startTime, endTime
        );
        timeRegistrationService.addTimeRegistration(timeRegistration);
    }

    public List<TimeRegistration> getTimeRegistrationsForCurrentUser() {
        User currentUser = getCurrentUser();
        if (currentUser == null) {
            throw new RuntimeException("No user is currently logged in");
        }
        return timeRegistrationService.getTimeRegistrationsForUser(currentUser.getUserInitials());
    }

    public List<TimeRegistration> getTimeRegistrationsForActivity(String projectName, String activityName) {
        Project project = projectService.getProjectFromName(projectName);
        Activity activity = project.getActivities().stream()
                .filter(a -> a.getActivityName().equals(activityName))
                .findFirst()
                .orElseThrow(() -> new RuntimeException("Activity not found"));
        return timeRegistrationService.getTimeRegistrationsForActivity(activity.getActivityName());
    }

    // Utility methods
    public List<Project> getAllProjects() {
        return projectService.getProjects();
    }

    public List<Activity> getActivitiesForProject(String projectName) {
        Project project = projectService.getProjectFromName(projectName);
        return project.getActivities();
    }

    public List<User> getAssignedUsersForActivity(String projectName, String activityName) {
        Project project = projectService.getProjectFromName(projectName);
        Activity activity = project.getActivities().stream()
                .filter(a -> a.getActivityName().equals(activityName))
                .findFirst()
                .orElseThrow(() -> new RuntimeException("Activity not found"));
        return activityService.getAssignedUsers(activity);
    }
} 
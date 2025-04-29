package dtu.time_manager.app;

import java.time.LocalDate;
import java.util.ArrayList;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

public class TimeManager {
    public static User current_user;
    private static List<User> users = new ArrayList<>();
    private static List<Project> projects = new ArrayList<>();
    private static Map<String, Project> projectMap = new HashMap<>();
    private static List<TimeRegistration> time_registrations = new ArrayList<>();
    private static int projectCount = 0;

    static {
        addUser(new User("huba"));
        addUser(new User("isak"));
        addUser(new User("bria"));
        addProject(Project.exampleProject("Project 1", 1));
        addProject(Project.exampleProject("Project 2", 20));
        Project project = getProjectFromName("Project 2");
        List<Activity> activities = project.getActivities();
        for (Activity activity : activities) {
            activity.assignUser("isak");
        }
        Project project1 = getProjectFromName("Project 1");
        List<Activity> activities1 = project1.getActivities();
        for (Activity activity : activities1) {
            activity.assignUser("bria");
        }
        }

    public static void login(String userInitials) {
        try {
            current_user = getUser(userInitials);
        } catch (Exception e) {
            throw e;
        }
    }

    public static void addUser(User user) {
        if (!users.contains(user)) {
            users.add(user);
        }
    }
    public static User getUser(String user_initials) {
        try {
            return users.stream().filter(user -> user.getUserInitials().equals(user_initials)).findFirst().get();
        } catch (Exception _) {
            throw new RuntimeException("The user " + user_initials + " don't exist in the system.");
        }
    }
    public static List<User> getUsers() {
        return users;
    }
    public static User getCurrentUser() {
        return current_user;
    }

    public static void addProject(Project project) {
        if (!projectExists(project.getProjectName())) {
            projectMap.put(project.getProjectID(), project);
            projects.add(project);
        } else {
            decProjectCount();
            throw new RuntimeException("A project with name '" + project.getProjectName() + "' already exists in the system and two projects can’t have the same name.");
        }
    }

    private static void decProjectCount() {
        projectCount--;
//        perhaps project count should only be incremented IF the project is valid to begin with, which would make this function superfluous
    }

    public static List<Project> getProjects() {
        return projects;
    }

    public static Project getProjectFromID(String projectID) {
        return projectMap.get(projectID);
    }

    public static Project getProjectFromName(String projectName) {
        return projects.stream()
                .filter(project -> project.getProjectName().equals(projectName))
                .findFirst() // Returns an Optional<Project>
                .orElse(null); // If no project is found, return null
    }

    public static int incProjectCount() {
        return ++projectCount;
    }

    public static int getProjectCount() {
        return projectCount;
    }

    public static boolean projectExists(String projectName) {
        return projects.stream().map(Project::getProjectName).anyMatch(name -> name.equals(projectName));
    }

    public static boolean projectDuplicateExists(String projectName) {
        long counted = projects.stream()
                .map(Project::getProjectName)
                .filter(name -> name.equals(projectName))
                .count();
        return counted > 1;
    }

    public static Map viewProject(String projectID) {
        Map<String, Object> projectVariables = new HashMap<>();

        Project project = projectMap.get(projectID);
        String projectName = project.getProjectName();
        List<Activity> activities = project.getActivities();
        User projectLead = project.getProjectLead();


        LocalDate startDate = project.getStartDate();
        LocalDate endDate = project.getEndDate();
        String projectInterval;

        if (startDate != null) {
            projectInterval = startDate.toString() + " - " + endDate.toString(); // Formatting the project interval so it looks correct
        } else {
            projectInterval = "";
        }

        projectVariables.put("Project name", projectName);
        projectVariables.put("Project ID", projectID);
        if (projectLead != null) {
            projectVariables.put("Project Lead", projectLead);
        } else {
            projectVariables.put("Project Lead", "");
        }
        projectVariables.put("Project interval", projectInterval);
        projectVariables.put("Project activities", activities);

        return projectVariables;
    }

    public static Map getProjectReport(String projectID) {
        Project project = getProjectFromID(projectID); // Retrieve the project for which to generate the project report
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
        Map<Activity, Double> map = project.getActivities().stream()
        .collect(Collectors.toMap(
                Function.identity(),        // key mapping function
                Activity::getWorkedHours   // value mapping function
        ));

        reportVariables.put("Project Activities", activities); // List all the activities in the project


        return reportVariables;
    }

    public static void logout() {
        current_user = null;
    }

    public static void addTimeRegistration(TimeRegistration timeRegistration) {
        time_registrations.add(timeRegistration);
//        this function needs to
    }
}

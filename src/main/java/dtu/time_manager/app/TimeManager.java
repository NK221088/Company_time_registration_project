package dtu.time_manager.app;

import java.util.ArrayList;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class TimeManager {
    public static String logged_in = "";
    private static List<User> users = new ArrayList<>();
    private static List<Project> projects = new ArrayList<>();
    private static Map<String, Project> projectMap = new HashMap<>();
    private static int projectCount = -1; // The counter starts at -1, to make sure the first project is assigned an id ending with 000

    public TimeManager() {
        User huba_user = new User("huba");
        add(huba_user);
        addProject(new Project());
    }

    public static void login(String userInitials) {
        if (users.stream().map(User::getUserInitials).anyMatch(initials -> initials.equals(userInitials))) {
            logged_in = userInitials;

        }
        else {
            logged_in = "";
            throw new RuntimeException("The user " + userInitials + " don't exist in the system.");
        }
    }

    public static void add(User user) {
        users.add(user);
    }

    public static void createProject(String projectName) {
       if (!projectExists(projectName)){
        projectCount++; // Update the projectCount variable to keep track of the number of projects - mostly for use in the creation of a projectID

        String projectID = createProjectID(projectCount);
        Project project = new Project(projectName, projectID);

        projects.add(project);
        projectMap.put(project.getProjectID(), project); // Add the projects to the hashmap for easy identification
        } else {
           throw new RuntimeException("A project with name '"+ projectName +"' already exists in the system and two projects can’t have the same name.");
       }
    }

    public static boolean projectExists(String projectName) {
        return projects.stream().map(Project::getProjectName).anyMatch(name -> name.equals(projectName));
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

    public static int getProjectCount() {return projectCount;}

    public static String createProjectID(int projectCount) {
        String projectID = "24" + String.format("%03d", projectCount); // We pad with leading zeros to make sure all ids have the same length
        return projectID;
    }

    public static boolean projectDuplicateExists(String projectName) {
        long projectCount = projects.stream()
                .map(Project::getProjectName)
                .filter(name -> name.equals(projectName))
                .count();
        return projectCount > 1;
    }

    public static Map viewProject(String projectID) {
        Map<String, Object> projectVariables = new HashMap<>();
        
        Project project = projectMap.get(projectID);
        String projectName = project.getProjectName();

        String startDate = project.getStartDate();
        String endDate = project.getEndDate();
        String projectInterval = startDate + " - " + endDate; // Formatting the project interval so it looks correct
        List<Activity> activities = project.getActivities();


        projectVariables.put("Project name", projectName);
        projectVariables.put("Project ID", projectID);
        projectVariables.put("Project interval", projectInterval);
        projectVariables.put("Project activities", activities);

        return projectVariables;
    }

    public static void addProject(Project project) {
        String projectID = project.getProjectID();
        projectMap.put(projectID, project);
        projects.add(project);
    }


    public static void getProjectReport(String projectID) {
        Project project = getProjectFromID(projectID); // Retrieve the project for which to generate the project report
    }

    public static List<Project> getProjects() {
        return projects;
    }

    public void logout() {
        // MAKE FUNCTIONs
        return;
    }
}

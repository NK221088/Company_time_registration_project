package dtu.time_manager.app;

import java.util.ArrayList;

import java.util.List;

public class TimeManager {
    public static String logged_in = "";
    private static List<User> users = new ArrayList<>();
    private static List<Project> projects = new ArrayList<>();


    public static void login(String userInitials) {
        if (users.stream().map(User::getUserInitials).anyMatch(initials -> initials.equals(userInitials))) {
            logged_in = userInitials;

        }
        else {
            logged_in = "";
        }
    }
    public static void add(User user) {
        users.add(user);
    }

    public static void addProject(String projectName) {
       if (!projectExists(projectName)){
        projects.add(new Project(projectName));
        } else {
           throw new RuntimeException("A project with name '"+ projectName +"' already exists in the system and two projects can’t have the same name.");
       }
    }

    public static boolean projectExists(String projectName) {
        return projects.stream().map(Project::getProjectName).anyMatch(name -> name.equals(projectName));
    }

    public static boolean projectDuplicateExists(String projectName) {
        long projectCount = projects.stream()
                .map(Project::getProjectName)
                .filter(name -> name.equals(projectName))
                .count();
        return projectCount > 1;
    }
}

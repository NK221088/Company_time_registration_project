package dtu.time_manager.example;

import dtu.time_manager.domain.*;
import dtu.time_manager.services.TimeManagerService;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;
import java.util.Map;

public class TimeManagerExample {
    public static void main(String[] args) {
        // Initialize the service
        TimeManagerService timeManager = new TimeManagerService();

        try {
            // 1. User Management
            System.out.println("=== User Management ===");
            timeManager.login("isak");
            System.out.println("Logged in as: " + timeManager.getCurrentUser().getUserInitials());

            // 2. Project Management
            System.out.println("\n=== Project Management ===");
            timeManager.createProject("Website Redesign");
            timeManager.assignProjectLead("Website Redesign", "isak");
            
            // View project details
            List<Project> projects = timeManager.getAllProjects();
            String projectId = projects.get(0).getProjectID();
            Map<String, Object> projectDetails = timeManager.viewProject(projectId);
            System.out.println("Project Details: " + projectDetails);

            // 3. Activity Management
            System.out.println("\n=== Activity Management ===");
            timeManager.createActivity("Website Redesign", "Frontend Development");
            timeManager.createActivity("Website Redesign", "Backend Development");
            
            // Assign users to activities
            timeManager.assignUserToActivity("Website Redesign", "Frontend Development", "isak");
            timeManager.assignUserToActivity("Website Redesign", "Backend Development", "huba");

            // View activities for project
            List<Activity> activities = timeManager.getActivitiesForProject("Website Redesign");
            System.out.println("Activities in project: " + activities.size());

            // 4. Time Registration
            System.out.println("\n=== Time Registration ===");
            // Register time for frontend development
            timeManager.registerTime(
                "Website Redesign",
                "Frontend Development",
                LocalDate.now(),
                LocalTime.of(9, 0),  // 9:00 AM
                LocalTime.of(12, 0)  // 12:00 PM
            );

            // Register time for backend development
            timeManager.registerTime(
                "Website Redesign",
                "Backend Development",
                LocalDate.now(),
                LocalTime.of(13, 0),  // 1:00 PM
                LocalTime.of(17, 0)   // 5:00 PM
            );

            // View time registrations for current user
            List<TimeRegistration> userRegistrations = timeManager.getTimeRegistrationsForCurrentUser();
            System.out.println("Time registrations for current user: " + userRegistrations.size());
            for (TimeRegistration reg : userRegistrations) {
                System.out.println("Activity: " + reg.getActivity().getActivityName() +
                                 ", Hours: " + reg.getHours());
            }

            // 5. Project Report
            System.out.println("\n=== Project Report ===");
            Map<String, Object> projectReport = timeManager.getProjectReport(projectId);
            System.out.println("Project Report: " + projectReport);

            // 6. Logout
            timeManager.logout();
            System.out.println("\nLogged out successfully");

        } catch (Exception e) {
            System.err.println("Error: " + e.getMessage());
            e.printStackTrace();
        }
    }
} 
package acceptance_tests;

import dtu.time_manager.app.Activity;
import dtu.time_manager.app.Project;
import dtu.time_manager.app.TimeManager;

public class ProjectHelper {
    private Project project;

    Project exampleProject(String projectID) {
        int projectCount = TimeManager.getProjectCount();
        Project project = new Project(projectID);
        project.setProjectName("Project 1");
        project.setProjectStartDate("01/01/2025");
        project.setProjectEndDate("01/08/2025");
        try {
            project.addActivity(new Activity("Activity 1"));
        } catch (Exception e) {

        }
        return project;
    }
}
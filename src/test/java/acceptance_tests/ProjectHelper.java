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
        project.setProjectStartDate("01/01/2024");
        project.setProjectEndDate("01/08/2024");
        project.addActivity(new Activity("Activity 1"));
        return project;
    }
}
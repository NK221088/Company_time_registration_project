package dtu.timemanager.services;
import dtu.timemanager.domain.User;
import dtu.timemanager.interfaces.projectInterface;
import dtu.timemanager.domain.Project;

import java.time.LocalDate;
import java.util.*;

public class projectService implements projectInterface {
    private static final List<Project> projects = new ArrayList<>();
    private static int projectCount = 0;

    @Override
    public void addProject(Project project) {
        if (!projectExists(project.getProjectName())) {
            project.setProjectID("P" + incProjectCount());
            projects.add(project);
        } else {
            decProjectCount();
            throw new RuntimeException("A project with name '" + project.getProjectName() + "' already exists in the system and two projects can't have the same name.");
        }
    }

    @Override
    public List<Project> getProjects() {
        return new ArrayList<>(projects);
    }

    @Override
    public Project getProjectFromName(String projectName) {
        return projects.stream()
                .filter(project -> project.getProjectName().equals(projectName))
                .findFirst()
                .orElse(null);
    }

    @Override
    public boolean projectExists(String projectName) {
        return projects.stream()
                .map(Project::getProjectName)
                .anyMatch(name -> name.equals(projectName));
    }

    @Override
    public boolean projectDuplicateExists(String projectName) {
        return projects.stream()
                .map(Project::getProjectName)
                .filter(name -> name.equals(projectName))
                .count() > 1;
    }

    @Override
    public Map<String, Object> getProjectReport(String projectID) {

    }

    @Override
    public void assignProjectLead(Project project, User user) {
        project.setProjectLead(user);
    }

    private static int incProjectCount() {
        return ++projectCount;
    }

    private static void decProjectCount() {
        projectCount--;
    }
}
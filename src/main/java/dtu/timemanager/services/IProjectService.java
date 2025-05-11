package dtu.timemanager.services;
import dtu.timemanager.domain.ProjectReport;
import dtu.timemanager.domain.User;
import dtu.timemanager.interfaces.ProjectInterface;
import dtu.timemanager.domain.Project;

import java.util.*;

public class IProjectService implements ProjectInterface {
    private final List<Project> projects = new ArrayList<>();
    private int projectCount = 0;

    @Override
    public void renameProject(Project project, String newName) throws IllegalArgumentException {
        for (Project p : projects) {
            if (p.getProjectName().equals(newName)) {
                throw new RuntimeException("A project with name " + newName + " already exists and two projects cannot exist with the same name.");
            }
        }
        project.setProjectName(newName);
    }

    private String formatID(int count) { return "25" + String.format("%03d", count); }

    @Override
    public Project addProject(String projectName) {
        Project project = new Project(projectName);
        if (!projectExists(project)) {
            String id = formatID(++this.projectCount);
            project.setProjectID(id);
            projects.add(project);
            return project;
        } else {
            throw new IllegalArgumentException("A project with name '" + project.getProjectName() + "' already exists in the system and two projects can’t have the same name.");
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
    public boolean projectExists(Project project) {
        return projects.contains(project);
    }

    @Override
    public boolean projectExists(String projectName) {
        return projects.contains(projectName);
    }

    @Override
    public ProjectReport getProjectReport(Project project) {
        return new ProjectReport(project);
    }

    @Override
    public void assignProjectLead(Project project, User user) {
        project.setProjectLead(user);
    }

    @Override
    public void incProjectCount() {
        ++projectCount;
    }

//    @Override
//    public void decProjectCount() {
//        projectCount--;
//    }

    @Override
    public int getProjectCount() {return projectCount;}
}
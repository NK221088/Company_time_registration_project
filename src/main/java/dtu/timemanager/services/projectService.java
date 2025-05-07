package dtu.timemanager.services;
import dtu.timemanager.domain.User;
import dtu.timemanager.interfaces.projectInterface;
import dtu.timemanager.domain.Project;

import java.time.LocalDate;
import java.util.*;

public class projectService implements projectInterface {
    private static final List<Project> projects = new ArrayList<>();
    private static final Map<String, Project> projectMap = new HashMap<>();
    private static int projectCount = 0;

    @Override
    public void addProject(Project project) {
        if (!projectExists(project.getProjectName())) {
            project.setProjectID("P" + incProjectCount());
            projectMap.put(project.getProjectID(), project);
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
    public Project getProjectFromID(String projectID) {
        return projectMap.get(projectID);
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
    public Map<String, Object> viewProject(String projectID) {
        Map<String, Object> projectVariables = new HashMap<>();
        Project project = projectMap.get(projectID);

        if (project == null) {
            throw new RuntimeException("Project not found");
        }

        projectVariables.put("Project name", project.getProjectName());
        projectVariables.put("Project ID", projectID);
        projectVariables.put("Project Lead", project.getProjectLead() != null ? project.getProjectLead() : "");

        LocalDate startDate = project.getStartDate();
        LocalDate endDate = project.getEndDate();
        String projectInterval = (startDate != null) ? startDate.toString() + " - " + endDate.toString() : "";
        projectVariables.put("Project interval", projectInterval);
        projectVariables.put("Project activities", project.getActivities());

        return projectVariables;
    }

    @Override
    public Map<String, Object> getProjectReport(String projectID) {
        Project project = getProjectFromID(projectID);
        if (project == null) {
            throw new RuntimeException("Project not found");
        }

        Map<String, Object> reportVariables = new HashMap<>();
        reportVariables.put("Project Name", project.getProjectName());
        reportVariables.put("Project ID", projectID);

        LocalDate startDate = project.getStartDate();
        LocalDate endDate = project.getEndDate();
        String projectInterval = (startDate != null) ? startDate.toString() + " - " + endDate.toString() : "";
        reportVariables.put("Project interval", projectInterval);

        User projectLead = project.getProjectLead();
        reportVariables.put("Project Lead", projectLead != null ? projectLead : "");

        reportVariables.put("Project Activities", project.getActivities());

        return reportVariables;
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
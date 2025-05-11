package dtu.timemanager.interfaces;
import dtu.timemanager.domain.Project;
import dtu.timemanager.domain.User;
import dtu.timemanager.domain.ProjectReport;

import java.util.List;


public interface ProjectInterface {
    Project addProject(String projectName);
    List<Project> getProjects();
    Project getProjectFromName(String projectName);
    boolean projectExists(Project project);
    boolean projectExists(String projectName);
    ProjectReport getProjectReport(Project project);
    void assignProjectLead(Project project, User user);
    void incProjectCount();
    int getProjectCount();
    void renameProject(Project project, String newName);
}
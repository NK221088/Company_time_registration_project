package dtu.timemanager.interfaces;
import dtu.timemanager.domain.Project;
import dtu.timemanager.domain.User;
import dtu.timemanager.domain.ProjectReport;

import java.util.List;


public interface ProjectInterface {
    Project addProject(String projectName);
    List<Project> getProjects();
    boolean projectExists(Project project);
    ProjectReport getProjectReport(Project project);
    int getProjectCount();
    void renameProject(Project project, String newName);
}
package dtu.timemanager.interfaces;
import dtu.timemanager.domain.Project;
import dtu.timemanager.domain.User;
import dtu.timemanager.domain.ProjectReport;

import java.util.List;
import java.util.Map;


public interface projectInterface {
    void addProject(Project project);
    List<Project> getProjects();
    Project getProjectFromName(String projectName);
    boolean projectExists(Project project);
    boolean projectExists(String projectName);
    ProjectReport getProjectReport(Project project);
    void assignProjectLead(Project project, User user);
    int incProjectCount();
    void decProjectCount();
    int getProjectCount();
}
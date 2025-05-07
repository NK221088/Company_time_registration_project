package dtu.timemanager.interfaces;
import dtu.timemanager.domain.Project;
import dtu.timemanager.domain.User;

import java.util.List;
import java.util.Map;


public interface projectInterface {
    void addProject(Project project);
    List<Project> getProjects();
    Project getProjectFromID(String projectID);
    Project getProjectFromName(String projectName);
    boolean projectExists(String projectName);
    boolean projectDuplicateExists(String projectName);
    Map<String, Object> viewProject(String projectID);
    Map<String, Object> getProjectReport(String projectID);
    void assignProjectLead(Project project, User user);
}
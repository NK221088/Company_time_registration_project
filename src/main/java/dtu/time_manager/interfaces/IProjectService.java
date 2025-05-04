package dtu.time_manager.interfaces;

import dtu.time_manager.domain.Project;
import dtu.time_manager.domain.User;
import java.util.List;
import java.util.Map;

public interface IProjectService {
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
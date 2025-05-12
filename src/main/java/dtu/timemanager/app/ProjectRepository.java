package dtu.timemanager.app;
import dtu.timemanager.domain.Project;
import dtu.timemanager.domain.ProjectReport;

import java.util.List;

public interface ProjectRepository {
    Project addProject(String projectName);
    List<Project> getProjects();
    boolean projectExists(Project project);
    void renameProject(Project project, String newName);
    void assignProjectLead(Project project, String userName);

    void clearProjectDataBase();
}
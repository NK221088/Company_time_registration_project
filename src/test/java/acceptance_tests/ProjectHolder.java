package acceptance_tests;

import dtu.timemanager.domain.Project;

public class ProjectHolder {
    private Project project;
    private String oldDate;

    public Project getProject() {
        return project;
    }

    public void setProject(Project project) {
        this.project = project;
    }

    public String getOldDate() {
        return oldDate;
    }

    public void setOldDate(String oldDate) {
        this.oldDate = oldDate;
    }
}

package whitebox_tests;

import dtu.timemanager.domain.*;
import java.util.List;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;


class AddProjectTest {
    private TimeManager timeManager;

    @BeforeEach
    void setUp() {
        this.timeManager = new TimeManager();
    }

    @Test
    void testAddProjectSuccess() {
        Project project = timeManager.addProject("Project 1");
        List<Project> projects = timeManager.getProjects();

        assertNotNull(project);
        assertEquals("Project 1", project.getProjectName());
        assertEquals(1, projects.size());
        assertSame(project, projects.getFirst());
    }

    @Test
    void testAddProjectException() {
        timeManager.addProject("Project 1");

        IllegalArgumentException e = assertThrows(
                IllegalArgumentException.class, () -> timeManager.addProject("Project 1")
        );
        String expectedMsg = "A project with name 'Project 1' already exists in the system and two projects can’t have the same name.";
        assertEquals(expectedMsg, e.getMessage());
    }
}
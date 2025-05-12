package whitebox_tests;

import dtu.timemanager.domain.*;
import java.util.List;

import dtu.timemanager.persistence.SqliteRepository;
import io.cucumber.java.en_old.Ac;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

// Isak Petrin
class AddActivityTest {
    private TimeManager timeManager;

    @BeforeEach
    void setUp() {
        this.timeManager = new TimeManager(new SqliteRepository(false));
    }

    @Test
    void testAddActivitySuccess() throws Exception {
        Project project = timeManager.addProject("Project A");
        Activity activity = new Activity("Design");
        project.addActivity(activity);

        assertNotNull(project);
        assertNotNull(activity);
        assertEquals(1, project.getActivities().size());
        assertSame(activity, project.getActivities().get(0));
    }

    @Test
    void testAddProjectException() throws Exception {
        Project project = timeManager.addProject("Project A");
        Activity activity = new Activity("Design");
        project.addActivity(activity);

        Exception e = assertThrows(Exception.class, () -> project.addActivity(activity));
        String expectedMsg = "An activity with name 'Design' already exists within 'Project A' two activities cannot exist with the same name within the same project.";

        assertEquals(1, project.getActivities().size());
        assertEquals(expectedMsg, e.getMessage());
    }
}
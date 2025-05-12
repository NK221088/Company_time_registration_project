package whitebox_tests;

import dtu.timemanager.domain.Activity;
import dtu.timemanager.domain.Project;
import dtu.timemanager.domain.TimeManager;
import dtu.timemanager.domain.User;
import dtu.timemanager.persistence.SqliteRepository;
import io.cucumber.java.bs.A;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

// Nikolai Kuhl
public class AssignUserTest {
    private TimeManager timeManager;

    @BeforeEach
    void setUp() {
        SqliteRepository sqLiteRepository = new SqliteRepository(false);
        this.timeManager = new TimeManager(sqLiteRepository, sqLiteRepository, sqLiteRepository, sqLiteRepository);
    }

    @Test
    void testAssignUserExceptionAlready() throws Exception {
        Project project = timeManager.addProject("Project 1");
        Activity activity = new Activity("Activity 1");
        project.addActivity(activity);
        User user = new User("huba");
        timeManager.addUser(user);
        activity.assignUser(user);
        ArrayList<User> users = new ArrayList<>(List.of(user));

        assertEquals("huba", user.getUserInitials() );
        assertEquals("Activity 1", activity.getActivityName());
        assertEquals(1, user.getActivityCount() );
        assertEquals(users , activity.getAssignedUsers());

        RuntimeException e = assertThrows(RuntimeException.class, () -> activity.assignUser(user));
        String expectedMsg = "'huba' is already assigned to the activity 'Activity 1'";

        assertEquals(expectedMsg, e.getMessage());
    }

    @Test
    void testAssignUserSucces() throws Exception {
        Project project = timeManager.addProject("Project 1");
        Activity activity = new Activity("Activity 1");
        project.addActivity(activity);
        User user = new User("huba");
        timeManager.addUser(user);
        ArrayList<User> users = new ArrayList<>();

        assertEquals("huba", user.getUserInitials() );
        assertEquals("Activity 1", activity.getActivityName());
        assertEquals(0, user.getActivityCount() );
        assertEquals(users , activity.getAssignedUsers());

        activity.assignUser(user);
        users.add(user);

        assertEquals(users , activity.getAssignedUsers());
        assertEquals(1, user.getActivityCount() );
    }

    @Test
    void testAssignUserExceptionTooMany() throws Exception {
        Project project = timeManager.addProject("Project 1");
        User user = new User("huba");
        timeManager.addUser(user);

        for (int i = 1; i <= 20; i++) {
            Activity activity = new Activity("Activity "+String.valueOf(i));
            project.addActivity(activity);
            activity.assignUser(user);
        }

        ArrayList<User> users = new ArrayList<>(List.of(user));
        Activity activity = new Activity("Activity X");
        project.addActivity(activity);

        assertEquals("huba", user.getUserInitials() );
        assertEquals("Activity X", activity.getActivityName());
        assertEquals(20, user.getActivityCount() );
//        assertEquals(users , activity.getAssignedUsers());

        RuntimeException e = assertThrows(RuntimeException.class, () -> activity.assignUser(user));
        String expectedMsg = "'huba' is already assigned to the maximum number of 20 activities";

        assertEquals(expectedMsg, e.getMessage());
    }
}

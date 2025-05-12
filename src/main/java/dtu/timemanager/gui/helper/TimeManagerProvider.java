package dtu.timemanager.gui.helper;

import dtu.timemanager.domain.Activity;
import dtu.timemanager.domain.Project;
import dtu.timemanager.domain.TimeManager;
import dtu.timemanager.domain.User;
import dtu.timemanager.persistence.SqliteRepository;

public class TimeManagerProvider {

    private static TimeManager instance;

    public static void appInitialize(TimeManager timeManager) throws Exception {
        User isak = new User("isak");
        User bria = new User("bria");
        User huba = new User("huba");

        timeManager.addUser(isak); timeManager.addUser(bria); timeManager.addUser(huba);

        Project project1 = timeManager.addExampleProject("Project 1", 1);
        Project project2 = timeManager.addExampleProject("Project 2", 2);

        for (Activity activity : project1.getActivities()) { timeManager.assignUser(activity, bria); }
        for (Activity activity : project2.getActivities()) { timeManager.assignUser(activity, isak); }
    }

    public static TimeManager getInstance() throws Exception {
        if (instance == null) {
            instance = new TimeManager(new SqliteRepository(true));
            appInitialize(instance);
        }
        return instance;
    }

    public static void setInstance(TimeManager customInstance) {
        instance = customInstance;
    }

    public static void reset() {
        instance = null;
    }
}

package dtu.example.ui;

import dtu.time_manager.app.TimeManager;

public class TimeManagerProvider {

    private static TimeManager instance;

    public static TimeManager getInstance() {
        if (instance == null) {
            instance = new TimeManager();
            instance.appInitialize();
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

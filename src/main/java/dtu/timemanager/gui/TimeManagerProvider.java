package dtu.timemanager.gui;

import dtu.timemanager.domain.TimeManager;

public class TimeManagerProvider {

    private static TimeManager instance;

    public static TimeManager getInstance() throws Exception {
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

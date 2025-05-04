package acceptance_tests;

import dtu.time_manager.services.TimeManagerService;

public class TestBase {
    protected static final TimeManagerService timeManager = new TimeManagerService();

    protected static TimeManagerService getTimeManager() {
        return timeManager;
    }
} 
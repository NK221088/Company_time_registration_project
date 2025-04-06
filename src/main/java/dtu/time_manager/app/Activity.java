package dtu.time_manager.app;

public class Activity {

    private String ActivityName;

    // Constructor with project name
    public Activity(String ActivityName) {
        this.ActivityName = ActivityName;
    }

    public String getActivityName() {
        return ActivityName;
    }
}

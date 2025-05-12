package dtu.timemanager.app;

import dtu.timemanager.domain.Activity;
import dtu.timemanager.domain.Project;

import java.util.List;

public interface ActivityRepository {
    Activity addActivity(Project project, String activityName) throws Exception;
    List<Activity> getActivitiesForProject(Project project);
    Activity getActivityByName(Project project, String activityName);
    void setActivityFinalized(Activity activity, boolean finalized);
    void renameActivity(Activity activity, String newName) throws Exception;

    void clearActivityDataBase();
}
package dtu.time_manager.app;



public class User {
    private String user_initials;

    public User(String userInitials) {
        this.user_initials = userInitials;

        TimeManager.add(this);
    }

    public Object getUserInitials() {
        return user_initials;
    }
}

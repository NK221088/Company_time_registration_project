package dtu.time_manager.app;

import java.util.ArrayList;

import java.util.List;

public class TimeManager {
    public static String logged_in = "";
    private static List<User> users = new ArrayList<>();

    public static void login(String userInitials) {
        if (users.stream().map(User::getUserInitials).anyMatch(initials -> initials.equals(userInitials))) {
            logged_in = userInitials;

        }
        else {
            logged_in = "";
        }
    }
    public static void add(User user) {
        users.add(user);
    }
}

package dtu.time_manager.app;

import java.util.ArrayList;

import java.util.List;

public class TimeManager {
    private static List<User> users = new ArrayList<>();

    public static boolean login(String userInitials) {
        return users.stream()
                .map(User::getUserInitials)
                .anyMatch(initials -> initials.equals(userInitials));
    }

    public static void add(User user) {
        users.add(user);
    }
}

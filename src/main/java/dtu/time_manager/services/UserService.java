package dtu.time_manager.services;

import dtu.time_manager.domain.User;
import dtu.time_manager.interfaces.IUserService;
import java.util.ArrayList;
import java.util.List;

public class UserService implements IUserService {
    private static User currentUser;
    private static final List<User> users = new ArrayList<>();

    static {
        // Initialize with example users
        addUser(new User("huba"));
        addUser(new User("isak"));
        addUser(new User("bria"));
    }

    @Override
    public void login(String userInitials) {
        currentUser = getUser(userInitials);
    }

    @Override
    public void logout() {
        currentUser = null;
    }

    @Override
    public void addUser(User user) {
        if (!users.contains(user)) {
            users.add(user);
        }
    }

    @Override
    public User getUser(String userInitials) {
        return users.stream()
                .filter(user -> user.getUserInitials().equals(userInitials))
                .findFirst()
                .orElseThrow(() -> new RuntimeException("The user " + userInitials + " doesn't exist in the system."));
    }

    @Override
    public List<User> getUsers() {
        return new ArrayList<>(users);
    }

    @Override
    public User getCurrentUser() {
        return currentUser;
    }
}

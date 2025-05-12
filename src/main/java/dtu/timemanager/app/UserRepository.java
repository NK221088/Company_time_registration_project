package dtu.timemanager.app;

import dtu.timemanager.domain.User;

import java.util.List;

public interface UserRepository {
    void addUser(User user);
    List<User> getUsers();
    boolean userExists(String username);
    User getUserByUsername(String username);
}
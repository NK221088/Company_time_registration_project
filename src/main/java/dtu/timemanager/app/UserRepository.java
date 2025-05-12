package dtu.timemanager.app;

import dtu.timemanager.domain.User;

import java.util.List;

public interface UserRepository {
    void addUser(User user);
    List<User> getUsers();
    User getUserByUsername(String username);

    void clearUserDatabase();
}
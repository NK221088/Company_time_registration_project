package dtu.time_manager.interfaces;

import dtu.time_manager.domain.User;
import java.util.List;

public interface IUserService {
    void login(String userInitials);
    void logout();
    void addUser(User user);
    User getUser(String userInitials);
    List<User> getUsers();
    User getCurrentUser();
} 
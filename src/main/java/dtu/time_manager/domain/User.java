package dtu.time_manager.domain;

public class User {
    private String userInitials;

    public User(String userInitials) {
        this.userInitials = userInitials;
    }

    public String getUserInitials() {
        return userInitials;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (obj == null || getClass() != obj.getClass()) return false;
        User user = (User) obj;
        return userInitials.equals(user.userInitials);
    }

    @Override
    public int hashCode() {
        return userInitials.hashCode();
    }

    @Override
    public String toString() {
        return userInitials;
    }
} 
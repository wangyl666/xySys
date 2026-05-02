package com.supplychain.util;

import com.supplychain.entity.User;

public class SecurityUtils {

    private static final ThreadLocal<User> currentUser = new ThreadLocal<>();

    public static void setCurrentUser(User user) {
        currentUser.set(user);
    }

    public static User getCurrentUser() {
        return currentUser.get();
    }

    public static Long getCurrentUserId() {
        User user = currentUser.get();
        return user != null ? user.getId() : null;
    }

    public static String getCurrentUsername() {
        User user = currentUser.get();
        return user != null ? user.getUsername() : null;
    }

    public static Long getCurrentRoleId() {
        User user = currentUser.get();
        return user != null ? user.getRoleId() : null;
    }

    public static void clear() {
        currentUser.remove();
    }

    public static boolean isAdmin() {
        Long roleId = getCurrentRoleId();
        return roleId != null && roleId == 1;
    }
}

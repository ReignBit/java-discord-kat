package com.reign.kat.lib.utils;

/**
 * Role/user types defined in the database. Set via command/dashboard.
 * A Role of ADMINISTRATOR has access to the things MODERATOR and EVERYONE has, and so on.
 */
public enum PermissionGroupType {
    EVERYONE("everyone"),
    MODERATOR("moderator"),
    ADMINISTRATOR("administrator"),
    OWNER("owner");

    public final String flag;

    PermissionGroupType(String flag)
    {
        this.flag = flag;
    }
}

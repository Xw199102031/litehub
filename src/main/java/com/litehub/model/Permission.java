package com.litehub.model;

public final class Permission {

    private Permission() {
    }

    public static final String PROJECT_READ = "PROJECT_READ";
    public static final String PROJECT_WRITE = "PROJECT_WRITE";
    public static final String PROJECT_DELETE = "PROJECT_DELETE";
    public static final String MEMBER_MANAGE = "MEMBER_MANAGE";
    public static final String TASK_CREATE = "TASK_CREATE";
    public static final String TASK_UPDATE = "TASK_UPDATE";
    public static final String TASK_DELETE = "TASK_DELETE";
    public static final String TASK_ASSIGN = "TASK_ASSIGN";
}
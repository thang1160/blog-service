package com.mindx.blog_service;

public enum Path {
    LOGIN("/login"),
    LOGOUT("/logout"),
    PROFILE("/profile"),
    ;

    private final String stringPath;

    Path(final String path) {
        this.stringPath = path;
    }

    @Override
    public String toString() {
        return stringPath;
    }
}

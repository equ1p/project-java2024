package org.example.javafxdemo;

public class UserData {
    private final int id;
    private final String title;
    private final String login;
    private final String password;
    private final String lastModified;

    public UserData(int id, String title, String login, String password, String lastModified) {
        this.id = id;
        this.title = title;
        this.login = login;
        this.password = password;
        this.lastModified = lastModified;
    }

    public int getId() {
        return id;
    }

    public String getTitle() {
        return title;
    }

    public String getLogin() {
        return login;
    }

    public String getPassword() {
        return password;
    }

    public String getLastModified() {
        return lastModified;
    }

    public void setPassword(String newPassword) {
    }
}

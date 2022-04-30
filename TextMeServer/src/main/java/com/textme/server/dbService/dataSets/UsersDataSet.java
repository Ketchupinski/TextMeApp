package com.textme.server.dbService.dataSets;

public class UsersDataSet {
    private String userName;
    private String userPass;
    private long userID;

    public UsersDataSet(long userID, String userName, String userPass) {
        this.userName = userName;
        this.userPass = userPass;
        this.userID = userID;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public String getUserPass() {
        return userPass;
    }

    public void setUserPass(String userPass) {
        this.userPass = userPass;
    }

    public long getUserID() {
        return userID;
    }

    public void setUserID(long userID) {
        this.userID = userID;
    }

    @Override
    public String toString() {
        return "UsersDataSet{" +
                "userName='" + userName + '\'' +
                ", userPass='" + userPass + '\'' +
                ", userID=" + userID +
                '}';
    }
}

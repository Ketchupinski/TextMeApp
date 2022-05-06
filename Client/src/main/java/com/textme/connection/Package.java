package com.textme.connection;

import java.io.Serializable;
import java.sql.Timestamp;

public class Package implements Serializable {
    private PackageType type;
    private String messageText;
    private String fromUser;
    private String toUser;
    private Timestamp messageDate;

    public Package(PackageType type, String messageText) {
        this.type = type;
        this.messageText = messageText;
    }

    public Package(PackageType type, String messageText, String fromUser, String toUser, Timestamp messageDate) {
        this.type = type;
        this.messageText = messageText;
        this.fromUser = fromUser;
        this.toUser = toUser;
        this.messageDate = messageDate;
    }

    public Package(PackageType type, String fromUser, String toUser) {
        this.type = type;
        this.fromUser = fromUser;
        this.toUser = toUser;
    }


    public PackageType getType() {
        return type;
    }

    public void setType(PackageType type) {
        this.type = type;
    }

    public String getMessageText() {
        return messageText;
    }

    public void setMessageText(String messageText) {
        this.messageText = messageText;
    }

    public String getFromUser() {
        return fromUser;
    }

    public void setFromUser(String fromUser) {
        this.fromUser = fromUser;
    }

    public String getToUser() {
        return toUser;
    }

    public void setToUser(String toUser) {
        this.toUser = toUser;
    }

    public Timestamp getMessageDate() {
        return messageDate;
    }

    public void setMessageDate(Timestamp messageDate) {
        this.messageDate = messageDate;
    }
}

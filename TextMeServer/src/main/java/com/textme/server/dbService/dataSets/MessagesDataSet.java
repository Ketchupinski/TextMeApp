package com.textme.server.dbService.dataSets;

import java.sql.Timestamp;

public class MessagesDataSet {
    private long messageID;
    private long fromUserID;
    private long toUserID;
    private java.lang.String messageText;

    private Timestamp messageTime;

    public MessagesDataSet(long messageID, String messageText, long fromUserID, long toUserID, Timestamp messageTime) {
        this.messageID = messageID;
        this.fromUserID = fromUserID;
        this.toUserID = toUserID;
        this.messageText = messageText;
        this.messageTime = messageTime;
    }

    public long getFromUserID() {
        return fromUserID;
    }

    public long getToUserID() {
        return toUserID;
    }

    public String getMessageText() {
        return messageText;
    }

    public Timestamp getMessageTime() {
        return messageTime;
    }

    public void setFromUserID(long fromUserID) {
        this.fromUserID = fromUserID;
    }

    public void setToUserID(long toUserID) {
        this.toUserID = toUserID;
    }

    public void setMessageText(String messageText) {
        this.messageText = messageText;
    }

    public void setMessageTime(Timestamp messageTime) {
        this.messageTime = messageTime;
    }


    public void setMessageID(long messageID) {
        this.messageID = messageID;
    }

    public long getMessageID() {
        return messageID;
    }
}

package com.example.orderfood.Model;

public class Message {
    private String senderID;
    private String receiverID;
    private String message;
    private long time;
    private boolean isSeen;

    public Message() {
    }

    public Message(String senderID, String receiverID, String message, long time, boolean isSeen) {
        this.senderID = senderID;
        this.receiverID = receiverID;
        this.message = message;
        this.time = time;
        this.isSeen = isSeen;
    }

    public boolean isSeen() {
        return isSeen;
    }

    public void setSeen(boolean seen) {
        isSeen = seen;
    }

    public String getReceiverID() {
        return receiverID;
    }

    public void setReceiverID(String receiverID) {
        this.receiverID = receiverID;
    }

    public String getSenderID() {
        return senderID;
    }

    public void setSenderID(String senderID) {
        this.senderID = senderID;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public long getTime() {
        return time;
    }

    public void setTime(long time) {
        this.time = time;
    }
}


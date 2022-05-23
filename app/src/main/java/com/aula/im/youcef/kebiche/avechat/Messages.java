package com.aula.im.youcef.kebiche.avechat;

public class Messages {
    String message;
    String uid;
    String username;
    long timeStamp;

    // constructors

    public Messages() {

    }

    public Messages(String message, String uid, String username, long timeStamp) {
        this.message = message;
        this.uid = uid;
        this.timeStamp = timeStamp;
        this.username = username;
    }

    // getters and setters


    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public String getUid() {
        return uid;
    }

    public String getUsername(){return username;}

    public void setUid(String uid) {
        this.uid = uid;
    }

    public long getTimeStamp() {
        return timeStamp;
    }

    public void setTimeStamp(long timeStamp) {
        this.timeStamp = timeStamp;
    }

    public void setUsername(String username){ this.username = username; }
}

package com.smartdsepro.android;

/**
 * Created by mashnoor on 3/10/16.
 */
public class ChatMsg {
    String userName;
    String msg;
    String sentTime;

    public ChatMsg(String userName, String msg, String sentTime) {
        this.userName = userName;
        this.msg = msg;
        this.sentTime = sentTime;
    }

    public String getUserName() {
        return userName;
    }

    public String getMsg() {
        return msg;
    }

    public String getSentTime() {
        return sentTime;
    }
}

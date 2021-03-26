package com.ewu.moonx.Pojo.DB.Models;

import java.io.Serializable;
import java.util.Date;

public class PublicMessages implements Serializable {
    String id, userId, userName, text;
    Date date;
    String replayMsgId;

    public PublicMessages(String id, String userId, String userName, String text, Date date, String replayMsgId) {
        this.id = id;
        this.userId = userId;
        this.userName = userName;
        this.text = text;
        this.date = date;
        this.replayMsgId = replayMsgId;
    }

    public PublicMessages() {
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }

    public Date getDate() {
        return date;
    }

    public void setDate(Date date) {
        this.date = date;
    }

    public String getReplayMsgId() {
        return replayMsgId;
    }

    public void setReplayMsgId(String replayMsgId) {
        this.replayMsgId = replayMsgId;
    }
}

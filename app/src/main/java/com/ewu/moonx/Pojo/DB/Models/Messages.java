package com.ewu.moonx.Pojo.DB.Models;

import java.io.Serializable;
import java.util.Date;

public class Messages implements Serializable {
    String id,text;
    Date date;
    String replayMsgId;

    public Messages() {
    }

    public Messages(String id, String text, Date date, String replayMsgId) {
        this.id = id;
        this.text = text;
        this.date = date;
        this.replayMsgId = replayMsgId;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
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

package com.ewu.moonx.Pojo.DB.Models;

import java.util.Date;

public class Messages {

    String id,senderUid,receiverUid,content,type;
    Date date;
    String replayMsgId;

    public Messages() {
    }

    public Messages(String id, String senderUid, String receiverUid, String content, String type, Date date, String replayMsgId) {
        this.id = id;
        this.senderUid = senderUid;
        this.receiverUid = receiverUid;
        this.content = content;
        this.type = type;
        this.date = date;
        this.replayMsgId = replayMsgId;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getReceiverUid() {
        return receiverUid;
    }

    public void setReceiverUid(String receiverUid) {
        this.receiverUid = receiverUid;
    }

    public String getSenderUid() {
        return senderUid;
    }

    public void setSenderUid(String senderUid) {
        this.senderUid = senderUid;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
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

package com.ewu.moonx.Pojo.DB.Models;

import java.util.Date;

public class UserMessages extends Messages {

    String senderUid, receiverUid, type;

    public static final String TEXT_TYPE = "TEXT_TYPE";
    public static final String IMAGE_TYPE = "IMAGE_TYPE";
    public static final String RECORD_TYPE = "RECORD_TYPE";
    public static final String FILE_TYPE = "FILE_TYPE";

    public UserMessages() {
    }

    public UserMessages(String id, String senderUid, String receiverUid, String text, String type, Date date, String replayMsgId) {
        this.id = id;
        this.senderUid = senderUid;
        this.receiverUid = receiverUid;
        this.text = text;
        this.type = type;
        this.date = date;
        this.replayMsgId = replayMsgId;
    }

    public String getSenderUid() {
        return senderUid;
    }

    public void setSenderUid(String senderUid) {
        this.senderUid = senderUid;
    }

    public String getReceiverUid() {
        return receiverUid;
    }

    public void setReceiverUid(String receiverUid) {
        this.receiverUid = receiverUid;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }
}

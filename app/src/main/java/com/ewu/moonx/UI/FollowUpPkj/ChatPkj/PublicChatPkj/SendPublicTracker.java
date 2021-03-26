package com.ewu.moonx.UI.FollowUpPkj.ChatPkj.PublicChatPkj;

import com.ewu.moonx.Pojo.DB.Models.PublicMessages;

public class SendPublicTracker {
    PublicMessages message;
    int status;

    public static final int NOT_SENT = 0;
    public static final int ON_PROGRESS = 1;

    public SendPublicTracker(PublicMessages message) {
        this.message = message;
        status = NOT_SENT;
    }

    public PublicMessages getMessage() {
        return message;
    }

    public void setStatus(int status) {
        this.status = status;
    }

    public int getStatus() {
        return status;
    }
}

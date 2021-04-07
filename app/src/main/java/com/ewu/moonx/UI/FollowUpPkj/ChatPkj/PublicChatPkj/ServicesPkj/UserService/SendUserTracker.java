package com.ewu.moonx.UI.FollowUpPkj.ChatPkj.PublicChatPkj.ServicesPkj.UserService;

import com.ewu.moonx.Pojo.DB.Models.PublicMessages;
import com.ewu.moonx.Pojo.DB.Models.UserMessages;

public class SendUserTracker {
    UserMessages message;
    int status;

    public static final int NOT_SENT = 0;
    public static final int ON_PROGRESS = 1;

    public SendUserTracker(UserMessages message) {
        this.message = message;
        status = NOT_SENT;
    }

    public UserMessages getMessage() {
        return message;
    }

    public void setStatus(int status) {
        this.status = status;
    }

    public int getStatus() {
        return status;
    }
}

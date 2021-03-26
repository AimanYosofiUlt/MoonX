package com.ewu.moonx.Pojo.DB.Models;

public class OldAccount {
    String uid, reason;

    public OldAccount() {
    }

    public OldAccount(String uid, String reason) {
        this.uid = uid;
        this.reason = reason;
    }

    public String getUid() {
        return uid;
    }

    public void setUid(String uid) {
        this.uid = uid;
    }

    public String getReason() {
        return reason;
    }

    public void setReason(String reason) {
        this.reason = reason;
    }

}

package com.ewu.moonx.Pojo.DB.Models;

public class UserConfig {
    String uid, type;

    public UserConfig() {
    }

    public UserConfig(String uid, String type) {
        this.uid = uid;
        this.type = type;
    }

    public String getUid() {
        return uid;
    }

    public void setUid(String uid) {
        this.uid = uid;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }
}

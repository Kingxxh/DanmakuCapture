package com.bilibili.danmu.pojo;

public class Certification {
    private final Integer uid = 0;
    private Integer roomid;
    private final Integer protover = 3;
    private final String platform = "web";
    private final Integer type = 2;
    private final String key = "";

    public Certification(Integer roomid) {
        this.roomid = roomid;
    }

    public Integer getUid() {
        return uid;
    }

    public Integer getRoomid() {
        return roomid;
    }

    public void setRoomid(Integer roomid) {
        this.roomid = roomid;
    }

    public Integer getProtover() {
        return protover;
    }

    public String getPlatform() {
        return platform;
    }

    public Integer getType() {
        return type;
    }

    public String getKey() {
        return key;
    }
}

package com.zheman.lock.model;

import java.util.Date;

public class Authorize {
    private Integer id;

    private Integer roomid;

    private Date datetime;

    private Integer residentid;

    private Integer authorize;

    private Integer authresidentid;

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public Integer getRoomid() {
        return roomid;
    }

    public void setRoomid(Integer roomid) {
        this.roomid = roomid;
    }

    public Date getDatetime() {
        return datetime;
    }

    public void setDatetime(Date datetime) {
        this.datetime = datetime;
    }

    public Integer getResidentid() {
        return residentid;
    }

    public void setResidentid(Integer residentid) {
        this.residentid = residentid;
    }

    public Integer getAuthorize() {
        return authorize;
    }

    public void setAuthorize(Integer authorize) {
        this.authorize = authorize;
    }

    public Integer getAuthresidentid() {
        return authresidentid;
    }

    public void setAuthresidentid(Integer authresidentid) {
        this.authresidentid = authresidentid;
    }
}
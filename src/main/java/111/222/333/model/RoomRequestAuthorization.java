package com.zheman.lock.model;

import java.util.Date;

public class RoomRequestAuthorization {
    private Long id;

    private Long residentid;

    private Long roomid;

    private Date datetime;

    private Integer state;

    private Date changestatedatetime;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getResidentid() {
        return residentid;
    }

    public void setResidentid(Long residentid) {
        this.residentid = residentid;
    }

    public Long getRoomid() {
        return roomid;
    }

    public void setRoomid(Long roomid) {
        this.roomid = roomid;
    }

    public Date getDatetime() {
        return datetime;
    }

    public void setDatetime(Date datetime) {
        this.datetime = datetime;
    }

    public Integer getState() {
        return state;
    }

    public void setState(Integer state) {
        this.state = state;
    }

    public Date getChangestatedatetime() {
        return changestatedatetime;
    }

    public void setChangestatedatetime(Date changestatedatetime) {
        this.changestatedatetime = changestatedatetime;
    }
}
package com.zheman.lock.model;

import java.util.Date;

public class Feedback {
    private Long id;

    private String message;

    private Long residentid;

    private Date datetime;

    private Integer state;

    private Date answerdatetime;

    private String answermessage;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message == null ? null : message.trim();
    }

    public Long getResidentid() {
        return residentid;
    }

    public void setResidentid(Long residentid) {
        this.residentid = residentid;
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

    public Date getAnswerdatetime() {
        return answerdatetime;
    }

    public void setAnswerdatetime(Date answerdatetime) {
        this.answerdatetime = answerdatetime;
    }

    public String getAnswermessage() {
        return answermessage;
    }

    public void setAnswermessage(String answermessage) {
        this.answermessage = answermessage == null ? null : answermessage.trim();
    }
}
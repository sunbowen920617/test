package com.zheman.lock.model;

import java.util.Date;

public class UnlockHistory {
	private Long id;

	private Long roomid;

	private Integer type;

	private Long residentid;

	private Integer state;

	private Date datetime;

	private String errorreason;

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public Long getRoomid() {
		return roomid;
	}

	public void setRoomid(Long roomid) {
		this.roomid = roomid;
	}

	public Integer getType() {
		return type;
	}

	public void setType(Integer type) {
		this.type = type;
	}

	public Long getResidentid() {
		return residentid;
	}

	public void setResidentid(Long residentid) {
		this.residentid = residentid;
	}

	public Integer getState() {
		return state;
	}

	public void setState(Integer state) {
		this.state = state;
	}

	public Date getDatetime() {
		return datetime;
	}

	public void setDatetime(Date datetime) {
		this.datetime = datetime;
	}

	public String getErrorreason() {
		return errorreason;
	}

	public void setErrorreason(String errorreason) {
		this.errorreason = errorreason;
	}

}
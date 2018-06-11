package com.zheman.lock.model;

import java.util.Date;

public class ResidentRoom {

	private Long residentid;

	private Long roomid;

	private Integer leader;

	private String mobilephone;

	private Integer sharedauth;

	private Long inviterid;

	private Date datetime;

	private Integer type;

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

	public Integer getLeader() {
		return leader;
	}

	public void setLeader(Integer leader) {
		this.leader = leader;
	}

	public String getMobilephone() {
		return mobilephone;
	}

	public void setMobilephone(String mobilephone) {
		this.mobilephone = mobilephone == null ? null : mobilephone.trim();
	}

	public Integer getSharedauth() {
		return sharedauth;
	}

	public void setSharedauth(Integer sharedauth) {
		this.sharedauth = sharedauth;
	}

	public Long getInviterid() {
		return inviterid;
	}

	public void setInviterid(Long inviterid) {
		this.inviterid = inviterid;
	}

	public Date getDatetime() {
		return datetime;
	}

	public void setDatetime(Date datetime) {
		this.datetime = datetime;
	}

	public Integer getType() {
		return type;
	}

	public void setType(Integer type) {
		this.type = type;
	}

}
package com.zheman.lock.model;

import java.util.Date;

public class ResidentRoomBackup {
	private Long residentid;

	private Long roomid;

	private Integer leader;

	private String mobilephone;

	private Integer shareauth;

	private Long inviterid;

	private Date datetime;
	private Date backupdatetime;

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

	public Integer getShareauth() {
		return shareauth;
	}

	public void setShareauth(Integer shareauth) {
		this.shareauth = shareauth;
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

	public Date getBackupdatetime() {
		return backupdatetime;
	}

	public void setBackupdatetime(Date backupdatetime) {
		this.backupdatetime = backupdatetime;
	}

}
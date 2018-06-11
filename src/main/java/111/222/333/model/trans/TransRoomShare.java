package com.zheman.lock.model.trans;

public class TransRoomShare {

	private long residentId;
	private long roomId;
	private String mobilePhone;
	private String dateTime;
	private int shareAuth;

	public String getMobilePhone() {
		return mobilePhone;
	}

	public void setMobilePhone(String mobilePhone) {
		this.mobilePhone = mobilePhone;
	}

	public String getDateTime() {
		return dateTime;
	}

	public void setDateTime(String dateTime) {
		this.dateTime = dateTime;
	}

	public int getShareAuth() {
		return shareAuth;
	}

	public void setShareAuth(int shareAuth) {
		this.shareAuth = shareAuth;
	}

	public long getResidentId() {
		return residentId;
	}

	public void setResidentId(long residentId) {
		this.residentId = residentId;
	}

	public long getRoomId() {
		return roomId;
	}

	public void setRoomId(long roomId) {
		this.roomId = roomId;
	}

}

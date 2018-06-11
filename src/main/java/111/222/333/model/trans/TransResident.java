package com.zheman.lock.model.trans;

public class TransResident {

	long id;
	String nickName;
	String profileFaceUrl;
	long defaultRoomId;
	long faceDatetime;

	public long getId() {
		return id;
	}

	public void setId(long id) {
		this.id = id;
	}

	public String getNickName() {
		return nickName;
	}

	public void setNickName(String nickName) {
		this.nickName = nickName;
	}

	public String getProfileFaceUrl() {
		return profileFaceUrl;
	}

	public void setProfileFaceUrl(String profileFaceUrl) {
		this.profileFaceUrl = profileFaceUrl;
	}

	public long getDefaultRoomId() {
		return defaultRoomId;
	}

	public void setDefaultRoomId(long defaultRoomId) {
		this.defaultRoomId = defaultRoomId;
	}

	public long getFaceDatetime() {
		return faceDatetime;
	}

	public void setFaceDatetime(long faceDatetime) {
		this.faceDatetime = faceDatetime;
	}

	
	
}

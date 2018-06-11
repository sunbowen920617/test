package com.zheman.lock.model.trans;

public class TransCallHistory {

	long id;
	String faceImageUrl;
	long datetime;
	String name;
	long faceDatetime;
	long state;
	long residentId;

	public long getId() {
		return id;
	}

	public void setId(long id) {
		this.id = id;
	}

	public String getFaceImageUrl() {
		return faceImageUrl;
	}

	public void setFaceImageUrl(String faceImageUrl) {
		this.faceImageUrl = faceImageUrl;
	}

	public long getDatetime() {
		return datetime;
	}

	public void setDatetime(long datetime) {
		this.datetime = datetime;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}


	public long getFaceDatetime() {
		return faceDatetime;
	}

	public void setFaceDatetime(long faceDatetime) {
		this.faceDatetime = faceDatetime;
	}

	public long getState() {
		return state;
	}

	public void setState(long state) {
		this.state = state;
	}

	public long getResidentId() {
		return residentId;
	}

	public void setResidentId(long residentId) {
		this.residentId = residentId;
	}

}

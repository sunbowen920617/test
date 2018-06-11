package com.zheman.lock.model.trans;

public class TransNotice {

	long id;
	int type;
	int shareResult;
	boolean readed;
	String message;
	String title;
	String datetime;
	String faceUrl;
	String url;
	long faceDatetime;
	long residentId;

	public long getId() {
		return id;
	}

	public void setId(long id) {
		this.id = id;
	}

	public int getType() {
		return type;
	}

	public void setType(int type) {
		this.type = type;
	}

	public boolean isReaded() {
		return readed;
	}

	public void setReaded(boolean readed) {
		this.readed = readed;
	}

	public String getMessage() {
		return message;
	}

	public void setMessage(String message) {
		this.message = message;
	}

	public String getTitle() {
		return title;
	}

	public void setTitle(String title) {
		this.title = title;
	}

	public String getDatetime() {
		return datetime;
	}

	public void setDatetime(String datetime) {
		this.datetime = datetime;
	}

	public int getShareResult() {
		return shareResult;
	}

	public void setShareResult(int shareResult) {
		this.shareResult = shareResult;
	}

	public String getUrl() {
		return url;
	}

	public void setUrl(String url) {
		this.url = url;
	}

	public String getFaceUrl() {
		return faceUrl;
	}

	public void setFaceUrl(String faceUrl) {
		this.faceUrl = faceUrl;
	}

	public long getFaceDatetime() {
		return faceDatetime;
	}

	public void setFaceDatetime(long faceDatetime) {
		this.faceDatetime = faceDatetime;
	}

	public long getResidentId() {
		return residentId;
	}

	public void setResidentId(long residentId) {
		this.residentId = residentId;
	}




}

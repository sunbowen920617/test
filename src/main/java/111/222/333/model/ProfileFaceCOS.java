package com.zheman.lock.model;

import java.util.Date;

public class ProfileFaceCOS {
	private Long id;

	private Long residentid;

	private String etag;

	private String downloadurl;

	private Date datetime;

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

	public String getEtag() {
		return etag;
	}

	public void setEtag(String etag) {
		this.etag = etag == null ? null : etag.trim();
	}

	public String getDownloadurl() {
		return downloadurl;
	}

	public void setDownloadurl(String downloadurl) {
		this.downloadurl = downloadurl == null ? null : downloadurl.trim();
	}

	public Date getDatetime() {
		return datetime;
	}

	public void setDatetime(Date datetime) {
		this.datetime = datetime;
	}

}
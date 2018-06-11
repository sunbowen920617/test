package com.zheman.lock.model;

import java.util.Date;

public class FaceCOS {
	private Long id;

	private Long residentid;

	private String etag;

	private String keyname;

	private Date datetime;

	private String youtuid;

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

	public String getKeyname() {
		return keyname;
	}

	public void setKeyname(String keyname) {
		this.keyname = keyname == null ? null : keyname.trim();
	}

	public Date getDatetime() {
		return datetime;
	}

	public void setDatetime(Date datetime) {
		this.datetime = datetime;
	}

	public String getYoutuid() {
		return youtuid;
	}

	public void setYoutuid(String youtuid) {
		this.youtuid = youtuid;
	}

}
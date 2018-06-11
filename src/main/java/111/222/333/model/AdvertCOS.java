package com.zheman.lock.model;

import java.util.Date;

public class AdvertCOS {
	private Long id;

	private Long advertid;

	private String etag;

	private String keyname;

	private Date datetime;

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public Long getAdvertid() {
		return advertid;
	}

	public void setAdvertid(Long advertid) {
		this.advertid = advertid;
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

}
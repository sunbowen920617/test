package com.zheman.lock.model;

import java.util.Date;

public class AdvertHistory {
	private Long id;

	private Long advertid;

	private Date datetime;

	private Long residentid;

	private Long timestemp;

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

	public Date getDatetime() {
		return datetime;
	}

	public void setDatetime(Date datetime) {
		this.datetime = datetime;
	}

	public Long getResidentid() {
		return residentid;
	}

	public void setResidentid(Long residentid) {
		this.residentid = residentid;
	}

	public Long getTimestemp() {
		return timestemp;
	}

	public void setTimestemp(Long timestemp) {
		this.timestemp = timestemp;
	}

}
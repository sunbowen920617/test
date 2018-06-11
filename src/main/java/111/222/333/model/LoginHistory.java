package com.zheman.lock.model;

import java.util.Date;

public class LoginHistory {
	private Long id;

	private Long residentid;

	private String device;

	private String ipaddress;

	private Date datetime;

	private String deviceid;

	private String timestemp;

	private Integer logintype;

	private String smscode;

	private String token;

	private String tencentsign;

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

	public String getDevice() {
		return device;
	}

	public void setDevice(String device) {
		this.device = device == null ? null : device.trim();
	}

	public String getIpaddress() {
		return ipaddress;
	}

	public void setIpaddress(String ipaddress) {
		this.ipaddress = ipaddress == null ? null : ipaddress.trim();
	}

	public Date getDatetime() {
		return datetime;
	}

	public void setDatetime(Date datetime) {
		this.datetime = datetime;
	}

	public String getDeviceid() {
		return deviceid;
	}

	public void setDeviceid(String deviceid) {
		this.deviceid = deviceid == null ? null : deviceid.trim();
	}

	public String getTimestemp() {
		return timestemp;
	}

	public void setTimestemp(String timestemp) {
		this.timestemp = timestemp == null ? null : timestemp.trim();
	}

	public Integer getLogintype() {
		return logintype;
	}

	public void setLogintype(Integer logintype) {
		this.logintype = logintype;
	}

	public String getSmscode() {
		return smscode;
	}

	public void setSmscode(String smscode) {
		this.smscode = smscode == null ? null : smscode.trim();
	}

	public String getToken() {
		return token;
	}

	public void setToken(String token) {
		this.token = token;
	}

	public String getTencentsign() {
		return tencentsign;
	}

	public void setTencentsign(String tencentsign) {
		this.tencentsign = tencentsign;
	}

}
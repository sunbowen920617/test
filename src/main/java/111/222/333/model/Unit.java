package com.zheman.lock.model;

public class Unit {
	private Long id;

	private String unitcode;

	private Long communityid;

	private Long deviceid;

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public String getUnitcode() {
		return unitcode;
	}

	public void setUnitcode(String unitcode) {
		this.unitcode = unitcode == null ? null : unitcode.trim();
	}

	public Long getCommunityid() {
		return communityid;
	}

	public void setCommunityid(Long communityid) {
		this.communityid = communityid;
	}

	public Long getDeviceid() {
		return deviceid;
	}

	public void setDeviceid(Long deviceid) {
		this.deviceid = deviceid;
	}
}
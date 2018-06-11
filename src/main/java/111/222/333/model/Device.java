package com.zheman.lock.model;

import java.util.Date;

public class Device {
    private Long id;

    private String devicecode;

    private Date datetime;

    private String gpsaddress;

    private Integer activate;
    
    private String mobilephone;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getDevicecode() {
        return devicecode;
    }

    public void setDevicecode(String devicecode) {
        this.devicecode = devicecode == null ? null : devicecode.trim();
    }

    public Date getDatetime() {
        return datetime;
    }

    public void setDatetime(Date datetime) {
        this.datetime = datetime;
    }

    public String getGpsaddress() {
        return gpsaddress;
    }

    public void setGpsaddress(String gpsaddress) {
        this.gpsaddress = gpsaddress == null ? null : gpsaddress.trim();
    }

    public Integer getActivate() {
        return activate;
    }

    public void setActivate(Integer activate) {
        this.activate = activate;
    }

	public String getMobilephone() {
		return mobilephone;
	}

	public void setMobilephone(String mobilephone) {
		this.mobilephone = mobilephone;
	}
    
    
}
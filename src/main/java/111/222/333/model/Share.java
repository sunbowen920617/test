package com.zheman.lock.model;

import java.util.Date;

public class Share {
	private Long id;

	private Long shareresidentid;

	private Long acceptresidentid;

	private Date datetime;

	private String uuid;

	private String qrcodepath;

	private Integer accepted;

	private Integer sharetype;

	private Long roomid;

	private String keyname;

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public Long getShareresidentid() {
		return shareresidentid;
	}

	public void setShareresidentid(Long shareresidentid) {
		this.shareresidentid = shareresidentid;
	}

	public Long getAcceptresidentid() {
		return acceptresidentid;
	}

	public void setAcceptresidentid(Long acceptresidentid) {
		this.acceptresidentid = acceptresidentid;
	}

	public Date getDatetime() {
		return datetime;
	}

	public void setDatetime(Date datetime) {
		this.datetime = datetime;
	}

	public String getUuid() {
		return uuid;
	}

	public void setUuid(String uuid) {
		this.uuid = uuid == null ? null : uuid.trim();
	}

	public String getQrcodepath() {
		return qrcodepath;
	}

	public void setQrcodepath(String qrcodepath) {
		this.qrcodepath = qrcodepath == null ? null : qrcodepath.trim();
	}

	public Integer getAccepted() {
		return accepted;
	}

	public void setAccepted(Integer accepted) {
		this.accepted = accepted;
	}

	public Integer getSharetype() {
		return sharetype;
	}

	public void setSharetype(Integer sharetype) {
		this.sharetype = sharetype;
	}

	public Long getRoomid() {
		return roomid;
	}

	public void setRoomid(Long roomid) {
		this.roomid = roomid;
	}

	public String getKeyname() {
		return keyname;
	}

	public void setKeyname(String keyname) {
		this.keyname = keyname;
	}

}
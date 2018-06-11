package com.zheman.lock.model;

public class CallHistory {
	private Long id;

	private Long roomid;

	private Integer state;

	private Integer answerdevice;

	private Long calldatetime;

	private String failreason;

	private String deviceid;

	private Long answerdatetime;

	private Long enddatetime;

	private Long residentid;

	private Integer answertype;

	private Long unlockdatetime;
	
	private Long unlockhistoryid;

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public Long getRoomid() {
		return roomid;
	}

	public void setRoomid(Long roomid) {
		this.roomid = roomid;
	}

	public Integer getState() {
		return state;
	}

	public void setState(Integer state) {
		this.state = state;
	}

	public Integer getAnswerdevice() {
		return answerdevice;
	}

	public void setAnswerdevice(Integer answerdevice) {
		this.answerdevice = answerdevice;
	}

	public Long getCalldatetime() {
		return calldatetime;
	}

	public void setCalldatetime(Long calldatetime) {
		this.calldatetime = calldatetime;
	}

	public String getFailreason() {
		return failreason;
	}

	public void setFailreason(String failreason) {
		this.failreason = failreason == null ? null : failreason.trim();
	}

	public String getDeviceid() {
		return deviceid;
	}

	public void setDeviceid(String deviceid) {
		this.deviceid = deviceid == null ? null : deviceid.trim();
	}

	public Long getAnswerdatetime() {
		return answerdatetime;
	}

	public void setAnswerdatetime(Long answerdatetime) {
		this.answerdatetime = answerdatetime;
	}

	public Long getEnddatetime() {
		return enddatetime;
	}

	public void setEnddatetime(Long enddatetime) {
		this.enddatetime = enddatetime;
	}

	public Long getResidentid() {
		return residentid;
	}

	public void setResidentid(Long residentid) {
		this.residentid = residentid;
	}

	public Integer getAnswertype() {
		return answertype;
	}

	public void setAnswertype(Integer answertype) {
		this.answertype = answertype;
	}

	public Long getUnlockdatetime() {
		return unlockdatetime;
	}

	public void setUnlockdatetime(Long unlockdatetime) {
		this.unlockdatetime = unlockdatetime;
	}

	public Long getUnlockhistoryid() {
		return unlockhistoryid;
	}

	public void setUnlockhistoryid(Long unlockhistoryid) {
		this.unlockhistoryid = unlockhistoryid;
	}

	
	
}
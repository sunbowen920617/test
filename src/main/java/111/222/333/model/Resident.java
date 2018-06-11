package com.zheman.lock.model;

import java.util.Date;
import java.util.List;

public class Resident {
	private Long id;

	private String mobilephonenumber;

	private String name;

	private Integer idcardtype;

	private String idcardnumber;

	private String telephonenumber;

	private Date datetime;

	private Integer appregister;

	private String nickname;

	private Integer leader;

	private Integer authorize;

	private String password;

	private Long profilefaceid;

	private List<Room> rooms;

	private long defaultroomid;

	private Integer registerstep;
	
	private Integer type;

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public String getMobilephonenumber() {
		return mobilephonenumber;
	}

	public void setMobilephonenumber(String mobilephonenumber) {
		this.mobilephonenumber = mobilephonenumber == null ? null : mobilephonenumber.trim();
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name == null ? null : name.trim();
	}

	public Integer getIdcardtype() {
		return idcardtype;
	}

	public void setIdcardtype(Integer idcardtype) {
		this.idcardtype = idcardtype;
	}

	public String getIdcardnumber() {
		return idcardnumber;
	}

	public void setIdcardnumber(String idcardnumber) {
		this.idcardnumber = idcardnumber == null ? null : idcardnumber.trim();
	}

	public String getTelephonenumber() {
		return telephonenumber;
	}

	public void setTelephonenumber(String telephonenumber) {
		this.telephonenumber = telephonenumber == null ? null : telephonenumber.trim();
	}

	public Date getDatetime() {
		return datetime;
	}

	public void setDatetime(Date datetime) {
		this.datetime = datetime;
	}

	public Integer getAppregister() {
		return appregister;
	}

	public void setAppregister(Integer appregister) {
		this.appregister = appregister;
	}

	public String getNickname() {
		return nickname;
	}

	public void setNickname(String nickname) {
		this.nickname = nickname == null ? null : nickname.trim();
	}

	public Integer getLeader() {
		return leader;
	}

	public void setLeader(Integer leader) {
		this.leader = leader;
	}

	public Integer getAuthorize() {
		return authorize;
	}

	public void setAuthorize(Integer authorize) {
		this.authorize = authorize;
	}

	public List<Room> getRooms() {
		return rooms;
	}

	public void setRooms(List<Room> rooms) {
		this.rooms = rooms;
	}

	public String getPassword() {
		return password;
	}

	public void setPassword(String password) {
		this.password = password;
	}

	public Long getProfilefaceid() {
		return profilefaceid;
	}

	public void setProfilefaceid(Long profilefaceid) {
		this.profilefaceid = profilefaceid;
	}

	public long getDefaultroomid() {
		return defaultroomid;
	}

	public void setDefaultroomid(long defaultroomid) {
		this.defaultroomid = defaultroomid;
	}

	public Integer getRegisterstep() {
		return registerstep;
	}

	public void setRegisterstep(Integer registerstep) {
		this.registerstep = registerstep;
	}

	public Integer getType() {
		return type;
	}

	public void setType(Integer type) {
		this.type = type;
	}

	
	
}
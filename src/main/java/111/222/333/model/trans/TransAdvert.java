package com.zheman.lock.model.trans;

import java.util.List;

public class TransAdvert {

	long id;
	List<String> images;
	String name;
	String descri;
	String url;
	long imageDatetime;

	public List<String> getImages() {
		return images;
	}

	public void setImages(List<String> images) {
		this.images = images;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getDescri() {
		return descri;
	}

	public void setDescri(String descri) {
		this.descri = descri;
	}

	public long getId() {
		return id;
	}

	public void setId(long id) {
		this.id = id;
	}

	public String getUrl() {
		return url;
	}

	public void setUrl(String url) {
		this.url = url;
	}

	public long getImageDatetime() {
		return imageDatetime;
	}

	public void setImageDatetime(long imageDatetime) {
		this.imageDatetime = imageDatetime;
	}

}

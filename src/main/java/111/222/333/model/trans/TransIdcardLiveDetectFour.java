package com.zheman.lock.model.trans;

public class TransIdcardLiveDetectFour {

	int code;
	String message;
	Data data;

	public int getCode() {
		return code;
	}

	public void setCode(int code) {
		this.code = code;
	}

	public String getMessage() {
		return message;
	}

	public void setMessage(String message) {
		this.message = message;
	}

	public Data getData() {
		return data;
	}

	public void setData(Data data) {
		this.data = data;
	}

	public class Data {
		int live_status;
		String live_msg;
		int compare_status;
		String compare_msg;
		int sim;
		String video_photo;

		public int getLive_status() {
			return live_status;
		}

		public void setLive_status(int live_status) {
			this.live_status = live_status;
		}

		public String getLive_msg() {
			return live_msg;
		}

		public void setLive_msg(String live_msg) {
			this.live_msg = live_msg;
		}

		public int getCompare_status() {
			return compare_status;
		}

		public void setCompare_status(int compare_status) {
			this.compare_status = compare_status;
		}

		public String getCompare_msg() {
			return compare_msg;
		}

		public void setCompare_msg(String compare_msg) {
			this.compare_msg = compare_msg;
		}

		public int getSim() {
			return sim;
		}

		public void setSim(int sim) {
			this.sim = sim;
		}

		public String getVideo_photo() {
			return video_photo;
		}

		public void setVideo_photo(String video_photo) {
			this.video_photo = video_photo;
		}

	}

}

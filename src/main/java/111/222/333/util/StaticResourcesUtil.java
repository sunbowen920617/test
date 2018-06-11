package com.zheman.lock.util;

import java.io.File;
import java.util.Date;

import org.apache.commons.lang3.time.DateFormatUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Component
public class StaticResourcesUtil {

	@Value("${zheman.log.path}")
	static String logsPath;
	@Value("${zheman.image.path}")
	static String imagesPath;
	@Value("${zheman.vedio.path}")
	static String vediosPath;

	/**
	 * 获取本地当天日志文件夹
	 * 
	 * @return
	 */
	public static File getLogsPath() {
		String dirPath = logsPath + File.separator + getToday();
		File dir = new File(dirPath);
		if (!dir.exists()) {
			dir.mkdirs();
		}
		return dir;
	}

	/**
	 * 获取本地当天图片文件夹
	 * 
	 * @return
	 */
	public static File getImagesPath() {
		String dirPath = imagesPath + File.separator + getToday();
		File dir = new File(dirPath);
		if (!dir.exists()) {
			dir.mkdirs();
		}
		return dir;
	}

	/**
	 * 获取本地当天视频文件夹
	 * 
	 * @return
	 */
	public static File getVediosPath() {
		String dirPath = vediosPath + File.separator + getToday();
		File dir = new File(dirPath);
		if (!dir.exists()) {
			dir.mkdirs();
		}
		return dir;
	}

	private static String getToday() {
		return DateFormatUtils.format(new Date(), "YYYY-MM-dd");
	}

}

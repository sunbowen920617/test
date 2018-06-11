package com.zheman.lock.util;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.Base64;
import java.util.Base64.Decoder;
import java.util.Random;

import org.apache.commons.codec.digest.DigestUtils;
import org.apache.commons.lang3.RandomStringUtils;
import org.apache.commons.lang3.StringUtils;
import org.bouncycastle.util.encoders.Base64Encoder;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Component
public class GenerateUtil {

	private static String nickNamePrefix;

	@Value("${resident.nickname.prefix}")
	public void setNickNamePrefix(String nickNamePrefix) {
		this.nickNamePrefix = nickNamePrefix;
	}

	/**
	 * 生成六位验证码
	 * 
	 * @return
	 */
	public static String genSmsCode() {
		Random random = new Random();
		String smsCode = String.valueOf(random.nextInt(1000000));
		return smsCode;
	}

	/**
	 * 生成随机昵称
	 * 
	 * @return
	 */
	public static String genNickName() {
		return nickNamePrefix + RandomStringUtils.randomAlphabetic(6);
	}

	/**
	 * 生成token
	 * 
	 * @param mobilePhone
	 * @param name
	 * @return
	 */
	public static String genToken(String mobilePhone, String deviceId, String timestemp) {
		return DigestUtils.md5Hex(mobilePhone + deviceId + timestemp);
	}

	/**
	 * 生成face图片名字(住户编号，当前时间戳)
	 * 
	 * @param id
	 *            住户数据库编号
	 * @param fileContentType
	 *            文件后缀
	 * @return
	 */
	public static String genFaceIDName(long id, String fileContentType) {
		StringBuffer buff = new StringBuffer();
		buff.append(id).append("_").append(System.currentTimeMillis()).append(File.separator).append(fileContentType);
		return buff.toString();
	}

	/**
	 * 生成二维码图片名字
	 * 
	 * @param timestemp
	 * @param residentId
	 * @return
	 */
	public static String genShareQrcodeName(long timestemp, long residentId) {
		StringBuffer stringBuffer = new StringBuffer();
		stringBuffer.append("share_").append(timestemp).append("_").append(residentId);
		return stringBuffer.toString();
	}

	/**
	 * 生成个人资料头像名字 ，时间戳+住戶ID 例：1517467043553-1
	 * 
	 * @param unitId
	 * @param roomId
	 * @return
	 */
	public static String getProfileFaceName(long residentId) {
		StringBuffer profileFaceName = new StringBuffer();
		profileFaceName.append(System.currentTimeMillis()).append("-").append(residentId);
		return profileFaceName.toString();
	}

	/**
	 * 生成人脸识别key ，时间戳+住戶ID 例：1517467043553-1
	 * 
	 * @param unitId
	 * @param roomId
	 * @return
	 */
	public static String getFaceName(long residentId) {
		StringBuffer faceName = new StringBuffer();
		faceName.append(System.currentTimeMillis()).append("-").append(residentId);
		return faceName.toString();
	}

	/**
	 * 生成二维码key,时间戳+住戶ID 例：1517467043553-1
	 * 
	 * @param residentId
	 * @return
	 */
	public static String getQrcodeName(long residentId) {
		StringBuffer qrcodeName = new StringBuffer();
		qrcodeName.append(System.currentTimeMillis()).append("-").append(residentId);
		return qrcodeName.toString();
	}

	/**
	 * 生成第一张人脸识别名称
	 * 
	 * @return
	 */
	public static String getRegisterImage(long residentId) {
		StringBuffer idcardLiveDetectFour = new StringBuffer();
		idcardLiveDetectFour.append("idcardlivedetectfour").append("-").append(System.currentTimeMillis()).append("-")
				.append(residentId);
		return idcardLiveDetectFour.toString();
	}

	public static String getRegisterVedio(long residentId) {
		StringBuffer idcardLiveDetectFour = new StringBuffer();
		idcardLiveDetectFour.append("idcardlivedetectfourvedio").append("-").append(System.currentTimeMillis())
				.append("-").append(residentId);
		return idcardLiveDetectFour.toString();
	}

	/**
	 * base64字符串转换成图片
	 * 
	 * @param imgStr
	 *            base64字符串
	 * @param imgFilePath
	 *            图片存放路径
	 * @return
	 * 
	 * @author ZHANGJL
	 * @dateTime 2018-02-23 14:42:17
	 */
	public static File Base64ToImage(String imgStr, String imgFilePath) { // 对字节数组字符串进行Base64解码并生成图片
		if (StringUtils.isEmpty(imgStr)) // 图像数据为空
			return null;
		Decoder decoder = Base64.getDecoder();
		try {
			// Base64解码
			byte[] b = decoder.decode(imgStr);
			for (int i = 0; i < b.length; ++i) {
				if (b[i] < 0) {// 调整异常数据
					b[i] += 256;
				}
			}
			File file = new File(imgFilePath);
			OutputStream out = new FileOutputStream(file);
			out.write(b);
			out.flush();
			out.close();
			return file;
		} catch (Exception e) {
			return null;
		}

	}
}

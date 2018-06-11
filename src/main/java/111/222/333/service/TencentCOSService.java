package com.zheman.lock.service;

import java.io.File;
import java.io.FileInputStream;
import java.net.URL;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import com.qcloud.cos.COSClient;
import com.qcloud.cos.ClientConfig;
import com.qcloud.cos.auth.BasicCOSCredentials;
import com.qcloud.cos.auth.COSCredentials;
import com.qcloud.cos.http.HttpMethodName;
import com.qcloud.cos.model.GeneratePresignedUrlRequest;
import com.qcloud.cos.model.ObjectMetadata;
import com.qcloud.cos.model.PutObjectResult;
import com.qcloud.cos.region.Region;
import com.zheman.lock.dao.FaceCOSMapper;
import com.zheman.lock.dao.ProfileFaceCOSMapper;
import com.zheman.lock.model.FaceCOS;
import com.zheman.lock.model.ProfileFaceCOS;
import com.zheman.lock.util.GenerateUtil;

@Service
public class TencentCOSService {

	Logger logger = LoggerFactory.getLogger(TencentCOSService.class);

	@Value("${tencent.cos.secretId}")
	String secretId;
	@Value("${tencent.cos.secretKey}")
	String secretKey;
	@Value("${tencent.cos.appId}")
	String appId;
	@Value("${tencent.cos.profileFaceBucketName}")
	String profileFaceBucketName;
	@Value("${tencent.cos.faceBucketName}")
	String faceBucketName;
	@Value("${tencent.cos.facetempBucketName}")
	String facetempBucketName;
	@Value("${tencent.cos.qrcodeBucketName}")
	String qrcodeBuckerName;
	@Value("${tencent.cos.advertBucketName}")
	String advertBucketName;
	@Value("${tencent.cos.idcardLiveDetectFourName}")
	String idcardLiveDetectFourName;

	@Autowired
	ProfileFaceCOSMapper profileFaceCOSMapper;

	@Autowired
	FaceCOSMapper faceCOSMapper;

	/**
	 * 初始化
	 * 
	 * @return
	 */
	private COSClient getCOSClient() {
		// 1 初始化用户身份信息(secretId, secretKey)
		COSCredentials cred = new BasicCOSCredentials(secretId, secretKey);
		// 2 设置bucket的区域, COS地域的简称请参照
		// https://cloud.tencent.com/document/product/436/6224
		ClientConfig clientConfig = new ClientConfig(new Region("ap-shanghai"));
		// 3 生成cos客户端
		COSClient cosclient = new COSClient(cred, clientConfig);
		return cosclient;
	}

	/**
	 * 上传个人资料头像
	 * 
	 * @param bucketName
	 * @return
	 */
	public String updateProfileFace(long residentId, MultipartFile file, String key) throws Exception {
		COSClient cosClient = getCOSClient();
		// 从输入流上传(需提前告知输入流的长度, 否则可能导致 oom)
		ObjectMetadata objectMetadata = new ObjectMetadata();
		// 设置流长度
		objectMetadata.setContentLength(file.getSize());
		// 设置 Content type, 默认是 application/octet-stream
		objectMetadata.setContentType(file.getContentType());
		PutObjectResult putObjectResult = cosClient.putObject(profileFaceBucketName, key, file.getInputStream(),
				objectMetadata);
		String etag = putObjectResult.getETag();
		return etag;
	}

	/**
	 * 获取住户头像的可下载链接
	 * 
	 * @param key
	 * @return
	 */
	public String getProfileFacePresignedUrl(long residentId) {
		ProfileFaceCOS profileFaceCOS = profileFaceCOSMapper.selectByResidentId(residentId);
		if (null == profileFaceCOS)
			return null;
		// 生成一个下载链接
		// bucket的命名规则为{name}-{appid} ，此处填写的存储桶名称必须为此格式
		GeneratePresignedUrlRequest req = new GeneratePresignedUrlRequest(profileFaceBucketName,
				profileFaceCOS.getDownloadurl(), HttpMethodName.GET);
		// 设置签名过期时间(可选), 若未进行设置, 则默认使用ClientConfig中的签名过期时间(5分钟)
		// 这里设置签名在半个小时后过期
		Date expirationDate = new Date(System.currentTimeMillis() + 2 * 60 * 1000);
		req.setExpiration(expirationDate);
		// 获取cosclient
		COSClient cosClient = getCOSClient();
		URL downloadUrl = cosClient.generatePresignedUrl(req);
		return downloadUrl.toString();
	}

	/**
	 * 获取住户头像的可下载链接
	 * 
	 * @param key
	 * @return
	 */
	public String getProfileFacePresignedUrl(String keyName) {
		// 生成一个下载链接
		// bucket的命名规则为{name}-{appid} ，此处填写的存储桶名称必须为此格式
		GeneratePresignedUrlRequest req = new GeneratePresignedUrlRequest(profileFaceBucketName, keyName,
				HttpMethodName.GET);
		// 设置签名过期时间(可选), 若未进行设置, 则默认使用ClientConfig中的签名过期时间(5分钟)
		// 这里设置签名在半个小时后过期
		Date expirationDate = new Date(System.currentTimeMillis() + 2 * 60 * 1000);
		req.setExpiration(expirationDate);
		// 获取cosclient
		COSClient cosClient = getCOSClient();
		URL downloadUrl = cosClient.generatePresignedUrl(req);
		return downloadUrl.toString();
	}

	/**
	 * 上传人脸识别信息
	 * 
	 * @param residentId
	 *            住户ID
	 * @param file
	 *            文件
	 * @param key
	 *            文件名称
	 * @return
	 */
	public Map<String, String> uploadFace(long residentId, MultipartFile file) throws Exception {
		Map<String, String> result = new HashMap<>();
		// 生成keyname
		String prefix = GenerateUtil.getFaceName(residentId);
		String fileName = file.getOriginalFilename();
		int index = StringUtils.lastIndexOf(fileName, ".");
		String suffix = StringUtils.substring(fileName, index + 1);
		String key = prefix + "." + suffix;
		//
		Map<String, String> map = new HashMap<>();
		COSClient cosClient = getCOSClient();
		// 从输入流上传(需提前告知输入流的长度, 否则可能导致 oom)
		ObjectMetadata objectMetadata = new ObjectMetadata();
		// 设置流长度
		objectMetadata.setContentLength(file.getSize());
		// 设置 Content type, 默认是 application/octet-stream
		objectMetadata.setContentType(file.getContentType());
		PutObjectResult putObjectResult = cosClient.putObject(faceBucketName, key, file.getInputStream(),
				objectMetadata);
		String etag = putObjectResult.getETag();
		result.put("etag", etag);
		result.put("key", key);
		return result;
	}

	/**
	 * 上传人脸识别信息
	 * 
	 * @param residentId
	 *            住户ID
	 * @param file
	 *            文件
	 * @param key
	 *            文件名称
	 * @return
	 */
	public String uploadFaceByFile(File file, String key) throws Exception {
		COSClient cosClient = getCOSClient();
		// 从输入流上传(需提前告知输入流的长度, 否则可能导致 oom)
		ObjectMetadata objectMetadata = new ObjectMetadata();
		// 设置流长度
		objectMetadata.setContentLength(file.length());
		// 设置 Content type, 默认是 application/octet-stream
		objectMetadata.setContentType("image/jpeg");
		FileInputStream fileInputStream = new FileInputStream(file);
		PutObjectResult putObjectResult = cosClient.putObject(faceBucketName, key, fileInputStream, objectMetadata);
		fileInputStream.close();
		String etag = putObjectResult.getETag();
		return key;
	}

	/**
	 * 获取人脸信息的可下载链接
	 * 
	 * @param key
	 * @return
	 */
	public List<String> getFacePresignedUrl(long residentId) {
		List<String> list = new ArrayList<>();
		List<FaceCOS> faceCOSs = faceCOSMapper.selectByResidentId(residentId);
		// 生成一个下载链接
		// bucket的命名规则为{name}-{appid} ，此处填写的存储桶名称必须为此格式
		for (FaceCOS faceCOS : faceCOSs) {
			GeneratePresignedUrlRequest req = new GeneratePresignedUrlRequest(faceBucketName, faceCOS.getKeyname(),
					HttpMethodName.GET);
			// 设置签名过期时间(可选), 若未进行设置, 则默认使用ClientConfig中的签名过期时间(5分钟)
			// 这里设置签名在半个小时后过期
			Date expirationDate = new Date(System.currentTimeMillis() + 2 * 60 * 1000);
			req.setExpiration(expirationDate);
			// 获取cosclient
			COSClient cosClient = getCOSClient();
			URL downloadUrl = cosClient.generatePresignedUrl(req);
			list.add(downloadUrl.toString());
		}
		return list;
	}

	/**
	 * 获取人脸信息的可下载链接
	 * 
	 * @param key
	 * @return
	 */
	public String getFacePresignedUrl(String keyName) {
		// 生成一个下载链接
		// bucket的命名规则为{name}-{appid} ，此处填写的存储桶名称必须为此格式
		GeneratePresignedUrlRequest req = new GeneratePresignedUrlRequest(faceBucketName, keyName, HttpMethodName.GET);
		// 设置签名过期时间(可选), 若未进行设置, 则默认使用ClientConfig中的签名过期时间(5分钟)
		// 这里设置签名在半个小时后过期
		Date expirationDate = new Date(System.currentTimeMillis() + 2 * 60 * 1000);
		req.setExpiration(expirationDate);
		// 获取cosclient
		COSClient cosClient = getCOSClient();
		URL downloadUrl = cosClient.generatePresignedUrl(req);
		return downloadUrl.toString();
	}

	/**
	 * 上传二维码图片到cos
	 * 
	 * @param residentId
	 * @param file
	 * @return
	 */
	public Map<String, String> uploadQrcode(long residentId, File file) throws Exception {
		Map<String, String> result = new HashMap<>();
		// 生成keyname
		String prefix = GenerateUtil.getProfileFaceName(residentId);
		String fileName = file.getName();
		int index = StringUtils.lastIndexOf(fileName, ".");
		String suffix = StringUtils.substring(fileName, index + 1);
		String key = prefix + "." + suffix;
		//
		Map<String, String> map = new HashMap<>();
		COSClient cosClient = getCOSClient();
		// 从输入流上传(需提前告知输入流的长度, 否则可能导致 oom)
		ObjectMetadata objectMetadata = new ObjectMetadata();
		// 设置流长度
		objectMetadata.setContentLength(file.length());
		// 设置 Content type, 默认是 application/octet-stream
		objectMetadata.setContentType("image/png");
		FileInputStream fileInputStream = new FileInputStream(file);
		PutObjectResult putObjectResult = cosClient.putObject(qrcodeBuckerName, key, fileInputStream, objectMetadata);
		String etag = putObjectResult.getETag();
		result.put("etag", etag);
		result.put("key", key);
		fileInputStream.close();
		return result;
	}

	/**
	 * 获取二维码的可下载链接
	 * 
	 * @param key
	 * @return
	 */
	public String getQrcodePresignedUrl(String keyName) {
		// 生成一个下载链接
		// bucket的命名规则为{name}-{appid} ，此处填写的存储桶名称必须为此格式
		GeneratePresignedUrlRequest req = new GeneratePresignedUrlRequest(qrcodeBuckerName, keyName,
				HttpMethodName.GET);
		// 设置签名过期时间(可选), 若未进行设置, 则默认使用ClientConfig中的签名过期时间(5分钟)
		// 这里设置签名在半个小时后过期
		Date expirationDate = new Date(System.currentTimeMillis() + 2 * 60 * 1000);
		req.setExpiration(expirationDate);
		// 获取cosclient
		COSClient cosClient = getCOSClient();
		URL downloadUrl = cosClient.generatePresignedUrl(req);
		return downloadUrl.toString();
	}

	/**
	 * 获取广告的可下载链接
	 * 
	 * @param keyName
	 * @return
	 */
	public String getAdvertPresignedUrl(String keyName) {
		// 生成一个下载链接
		// bucket的命名规则为{name}-{appid} ，此处填写的存储桶名称必须为此格式
		GeneratePresignedUrlRequest req = new GeneratePresignedUrlRequest(advertBucketName, keyName,
				HttpMethodName.GET);
		// 设置签名过期时间(可选), 若未进行设置, 则默认使用ClientConfig中的签名过期时间(5分钟)
		// 这里设置签名在半个小时后过期
		Date expirationDate = new Date(System.currentTimeMillis() + 2 * 60 * 1000);
		req.setExpiration(expirationDate);
		// 获取cosclient
		COSClient cosClient = getCOSClient();
		URL downloadUrl = cosClient.generatePresignedUrl(req);
		return downloadUrl.toString();
	}

	/**
	 * 上传活体唇语验证视频
	 * 
	 * @param key
	 * @param file
	 * @return
	 */
	public String uploadIdcardLiveDetectFourVedio(String key, File file) throws Exception {
		COSClient cosClient = getCOSClient();
		// 从输入流上传(需提前告知输入流的长度, 否则可能导致 oom)
		ObjectMetadata objectMetadata = new ObjectMetadata();
		// 设置流长度
		objectMetadata.setContentLength(file.length());
		// 设置 Content type, 默认是 application/octet-stream
		objectMetadata.setContentType("video/mpeg4");
		FileInputStream fileInputStream=new FileInputStream(file);
		PutObjectResult putObjectResult = cosClient.putObject(idcardLiveDetectFourName, key,fileInputStream,
				objectMetadata);
		fileInputStream.close();
		String etag = putObjectResult.getETag();
		return etag;
	}

	public String uploadIdcardLiveDatectFourImage(String key, File image) throws Exception {
		COSClient cosClient = getCOSClient();
		// 从输入流上传(需提前告知输入流的长度, 否则可能导致 oom)
		ObjectMetadata objectMetadata = new ObjectMetadata();
		// 设置流长度
		objectMetadata.setContentLength(image.length());
		// 设置 Content type, 默认是 application/octet-stream
		objectMetadata.setContentType("image/jpeg");
		FileInputStream fileInputStream = new FileInputStream(image);
		PutObjectResult putObjectResult = cosClient.putObject(faceBucketName, key, fileInputStream, objectMetadata);
		fileInputStream.close();
		String etag = putObjectResult.getETag();
		return etag;
	}

	/**
	 * 删除人脸图片
	 * 
	 * @param residentId
	 * @param faceIds
	 * @return
	 */
	public void deleteFace(String keyName) {
		COSClient cosClient = getCOSClient();
		cosClient.deleteObject(faceBucketName, keyName);
	}

	/**
	 * 删除二维码失败
	 * 
	 * @param keyName
	 */
	public void deleteQrcode(String keyName) {
		COSClient cosClient = getCOSClient();
		cosClient.deleteObject(qrcodeBuckerName, keyName);
	}

	public void deleteProfileFace(String keyName) {
		COSClient cosClient = getCOSClient();
		cosClient.deleteObject(faceBucketName, keyName);
	}
}

package com.zheman.lock.service;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.apache.http.client.utils.DateUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.zheman.lock.common.CommonResult;
import com.zheman.lock.dao.AdvertCOSMapper;
import com.zheman.lock.dao.AdvertHistoryMapper;
import com.zheman.lock.dao.AdvertMapper;
import com.zheman.lock.model.Advert;
import com.zheman.lock.model.AdvertCOS;
import com.zheman.lock.model.AdvertHistory;
import com.zheman.lock.model.trans.TransAdvert;

@Service
public class AdvertService {

	@Autowired
	AdvertMapper advertMapper;

	@Autowired
	TencentCOSService tencentCOSService;

	@Autowired
	AdvertHistoryMapper advertHistoryMapper;

	@Autowired
	AdvertCOSMapper advertCOSMapper;

	@Autowired
	RedisService redisService;

	/**
	 * 获取首页广告
	 * 
	 * @param timestemp
	 * @return
	 */
	public CommonResult getHomePageAdvert() {
		CommonResult commonResult = new CommonResult();
		String today = DateUtils.formatDate(new Date(), "yyyy-MM-dd HH:mm:ss");
		Advert advert = advertMapper.selectHomePageAdvert(today);
		TransAdvert transAdvert = convertToTransAdvert(advert);
		commonResult.setCode(CommonResult.SUCCESS);
		commonResult.setMessage("查询成功");
		if (null == transAdvert)
			commonResult.setObj("");
		else
			commonResult.setObj(transAdvert);
		return commonResult;
	}

	public List<TransAdvert> convertToTransAdverts(List<Advert> adverts) {
		List<TransAdvert> transAdverts = new ArrayList<>();
		for (Advert advert : adverts) {
			transAdverts.add(convertToTransAdvert(advert));
		}
		return transAdverts;
	}

	public TransAdvert convertToTransAdvert(Advert advert) {
		if (null == advert)
			return null;
		List<String> images = new ArrayList<>();
		TransAdvert transAdvert = new TransAdvert();
		List<AdvertCOS> advertCOSs = advertCOSMapper.selectByAdvertId(advert.getId());
		for (AdvertCOS advertCOS : advertCOSs) {
			images.add(tencentCOSService.getAdvertPresignedUrl(advertCOS.getKeyname()));
			// transAdvert.setImageDatetime(advertCOS.getDatetime().getTime());
		}
		transAdvert.setDescri(advert.getDescri());
		transAdvert.setName(advert.getName());
		transAdvert.setImages(images);
		transAdvert.setUrl(advert.getUrl());
		transAdvert.setId(advert.getId());
		return transAdvert;
	}

	/**
	 * 获取房屋列表广告
	 * 
	 * @param timestemp
	 * @return
	 */
	public CommonResult getRoomListAdvert() {
		CommonResult commonResult = new CommonResult();
		Date date = new Date();
		String today = DateUtils.formatDate(date, "yyyy-MM-dd HH:mm:ss");
		List<Advert> adverts = advertMapper.selectRoomListAdvert(today);
		List<TransAdvert> transAdverts = convertToTransAdverts(adverts);
		commonResult.setCode(CommonResult.SUCCESS);
		commonResult.setMessage("查询成功");
		if (null == transAdverts)
			commonResult.setObj("");
		else
			commonResult.setObj(transAdverts);
		return commonResult;
	}

	/**
	 * 广告点击事件
	 * 
	 * @param residentId
	 * @param advertId
	 * @throws Exception
	 */
	public void advertClicked(String token, long advertId, long timestemp) throws Exception {
		AdvertHistory advertHistory = new AdvertHistory();
		advertHistory.setAdvertid(advertId);
		long residentId = redisService.getResidentIdByLoginToken(token);
		advertHistory.setResidentid(residentId);
		advertHistory.setDatetime(new Date());
		advertHistory.setTimestemp(timestemp);
		advertHistoryMapper.insert(advertHistory);
	}
}

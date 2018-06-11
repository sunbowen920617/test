package com.zheman.lock.service;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.zheman.lock.common.CommonResult;
import com.zheman.lock.dao.FeedbackMapper;
import com.zheman.lock.model.Feedback;

@Service
public class FeedbackService {

	@Autowired
	FeedbackMapper feedbackMapper;

	@Autowired
	RedisService redisService;

	/**
	 * 添加住户反馈 超过三条未答复的反馈 不可发送
	 * 
	 * @param token
	 * @param title
	 * @param message
	 * @throws Exception
	 */
	public CommonResult addFeedback(String token, String message) throws Exception {
		CommonResult commonResult = new CommonResult();
		long residentId = redisService.getResidentIdByLoginToken(token);

		SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd");
		String daily = simpleDateFormat.format(new Date());
		String start = daily + " 00:00:00";
		String end = daily + " 23:59:59";
		List<Feedback> feedbacks = feedbackMapper.selectDailyByResidentId(residentId, start, end);

		if (feedbacks.size() >= 3) {
			commonResult.setCode(CommonResult.FAILURE);
			commonResult.setMessage("每天可提交三次反馈");
			commonResult.setObj("OK");
			return commonResult;
		}

		Feedback feedback = new Feedback();
		feedback.setResidentid(residentId);
		feedback.setDatetime(new Date());
		feedback.setMessage(message);
		feedback.setState(1);
		feedbackMapper.insert(feedback);
		commonResult.setCode(CommonResult.SUCCESS);
		commonResult.setMessage("住户反馈已添加");
		return commonResult;
	}

	/**
	 * 根据住户ID查询反馈信息
	 * 
	 * @param residentId
	 * @return
	 * @throws Exception
	 */
	public CommonResult selectFeedbacksByResidentId(String token) throws Exception {
		CommonResult commonResult = new CommonResult();
		commonResult.setCode(CommonResult.SUCCESS);
		long residentId = redisService.getResidentIdByLoginToken(token);
		List<Feedback> feedbacks = feedbackMapper.selectFeedbacksByResidentId(residentId);
		commonResult.setObj(feedbacks);
		return commonResult;
	}

	/**
	 * 根据反馈ID查询
	 * 
	 * @param feedbackId
	 * @return
	 */
	public CommonResult selectFeedback(long feedbackId) {
		CommonResult commonResult = new CommonResult();
		Feedback feedback = feedbackMapper.selectByPrimaryKey(feedbackId);
		commonResult.setCode(CommonResult.SUCCESS);
		commonResult.setMessage("查詢成功");
		commonResult.setObj(feedback);
		return commonResult;
	}

}

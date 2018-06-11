package com.zheman.lock.filter;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.zheman.lock.common.CommonResult;
import com.zheman.lock.util.JSONUtil;

public class KeywordsFilter implements Filter {

	private static final Logger log = LoggerFactory.getLogger(KeywordsFilter.class);

	private List<String> banWords = new ArrayList<String>(); // 禁用词

	@Override
	public void destroy() {
		// TODO Auto-generated method stub

	}

	@Override
	public void doFilter(ServletRequest arg0, ServletResponse arg1, FilterChain arg2)
			throws IOException, ServletException {
		// TODO Auto-generated method stub
		HttpServletRequest request = (HttpServletRequest) arg0;
		HttpServletResponse response = (HttpServletResponse) arg1;
		// 检查提交数据是否包含禁用词
		/*
		 * 此刻是不知道客户机要提交过来的数据的，也即不知道客户机会以什么名称提交数据过来，
		 * 所以应得到客户机提交的所有数据，然后挨个检查，怎么得到客户机提交的所有数据呢？ 可得到客户机提交的所有数据的名称，然后挨个取出来检查即可。
		 */
		Enumeration<String> e = request.getParameterNames();
		while (e.hasMoreElements()) {
			String name = (String) e.nextElement();
			if (StringUtils.equals(name, "token") || StringUtils.equals(name, "pushToken")||StringUtils.equals(name, "timestemp"))
				continue;
			String data = request.getParameter(name); // 乱码：82342949842934*76%，所以这里还要解决乱码
			// String regexData = data.replaceAll(" +", "");
			for (String regex : banWords) {
				Pattern pattern = Pattern.compile(regex); // 编译regex这个正则表达式，得到代表此正则表达式的对象
				Matcher m = pattern.matcher(data); // 看data数据里面有没有和该正则表达式相匹配的内容
				if (m.find()) { // 匹配器的find方法若返回true，则客户机提交的数据里面有和正则表达式相匹配的内容
					CommonResult commonResult = new CommonResult();
					commonResult.setCode(CommonResult.FAILURE);
					commonResult.setMessage("操作失败,请稍候重试");
					response.setCharacterEncoding("utf-8");
					response.getWriter().print(JSONUtil.parseToString(commonResult));
					return;
				}
			}
		}
		arg2.doFilter(request, response);
	}

	@Override
	public void init(FilterConfig arg0) throws ServletException {
		// TODO Auto-generated method stub
		try {
			String path = this.getClass().getClassLoader().getResource("keywords.txt").getPath();
			String path2 = this.getClass().getClassLoader().getResource("keywords2.txt").getPath();
			File file = new File(path);
			File file2 = new File(path2);
			BufferedReader br = new BufferedReader(new InputStreamReader(new FileInputStream(file), "utf-8"));
			BufferedReader br2 = new BufferedReader(new InputStreamReader(new FileInputStream(file2), "utf-8"));
			String line = null;
			while ((line = br.readLine()) != null) {
				if (StringUtils.isNotBlank(line))
					banWords.add(line);
			}
			while ((line = br2.readLine()) != null) {
				if (StringUtils.isNotBlank(line))
					banWords.add(line);
			}
		} catch (Exception e) {
			// TODO: handle exception
			e.printStackTrace();
			log.error("KeywordsFilter init fail , ", e);
		}

	}

}

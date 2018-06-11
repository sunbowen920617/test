package com.zheman.lock.controller;

import java.net.InetAddress;
import java.net.UnknownHostException;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

@Controller
public class MainController {

	@RequestMapping("/protocol")
	public String protocol() {
		return "protocol";
	}

	@RequestMapping("/privacy")
	public String privacy() {
		return "privacy";
	}

	@RequestMapping("/help")
	public String help() {
		return "help";
	}

	@RequestMapping("/upload")
	public String upload() {
		return "upload";
	}
	
	@RequestMapping("/")
	@ResponseBody
	public String index() {
		String hostAddress = "";
		String hostAddress1 = "";
		try {
			InetAddress address = InetAddress.getLocalHost();// 获取的是本地的IP地址 //PC-20140317PXKX/192.168.0.121
			hostAddress = address.getHostAddress();// 192.168.0.121
			InetAddress address1 = InetAddress.getByName("www.wodexiangce.cn");// 获取的是该网站的ip地址，比如我们所有的请求都通过nginx的，所以这里获取到的其实是nginx服务器的IP地
			hostAddress1 = address1.getHostAddress();// 124.237.121.122
			InetAddress[] addresses = InetAddress.getAllByName("www.baidu.com");// 根据主机名返回其可能的所有InetAddress对象
			for (InetAddress addr : addresses) {
				System.out.println(addr);// www.baidu.com/14.215.177.38
				// www.baidu.com/14.215.177.37
			}
		} catch (UnknownHostException e) {
			e.printStackTrace();
		}
		return hostAddress1 + "-" + hostAddress;
	}

}
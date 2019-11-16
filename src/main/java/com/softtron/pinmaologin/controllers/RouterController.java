package com.softtron.pinmaologin.controllers;

import java.util.Map;
import java.util.UUID;

import org.apache.shiro.SecurityUtils;
import org.apache.shiro.authc.UsernamePasswordToken;
import org.apache.shiro.subject.Subject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.softtron.pinmaologin.domains.TUser;
import com.softtron.pinmaologin.services.UserService;
import com.softtron.pinmaologin.utils.ExceptionUtil;
import com.softtron.pinmaologin.utils.TokenUtil;

@RestController
public class RouterController extends CommonController {
	@Autowired
	TokenUtil tokenUtil;
	@Autowired
	UserService userService;

	@RequestMapping(path = "login")
	public Map login(String username,String password) throws Exception {
		Subject subject = SecurityUtils.getSubject();
		UsernamePasswordToken token = new UsernamePasswordToken(username, password);
		 // 执行认证登陆
        subject.login(token);
        String randomKey = UUID.randomUUID().toString(); 
        TUser user = userService.getUser(username);
        tokenUtil.saveUser(randomKey, user);
        Map map = resultMap();
        map.put("token", randomKey);
        return map;
	}

	@RequestMapping(path = "index")
	public Map index() {
		System.out.println("index");
		return resultMap();
	}
	@RequestMapping(path = "show")
	public Map show() {
		System.out.println("show");
		return resultMap();
	}
	@RequestMapping(path = "notRole")
	public Map notRole() {
		throw ExceptionUtil.NOTPREMISSIONEXCEPTION;
	}
	@RequestMapping(path = "notlogin")
	public Map notLogin() {
		throw ExceptionUtil.NOLOGIN;
	}

	
	
}

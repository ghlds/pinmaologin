package com.softtron.pinmaologin.configs;

import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Set;

import javax.servlet.Filter;

import org.apache.shiro.SecurityUtils;
import org.apache.shiro.authc.AuthenticationException;
import org.apache.shiro.authc.AuthenticationInfo;
import org.apache.shiro.authc.AuthenticationToken;
import org.apache.shiro.authc.SimpleAuthenticationInfo;
import org.apache.shiro.authc.UsernamePasswordToken;
import org.apache.shiro.authz.AuthorizationInfo;
import org.apache.shiro.authz.SimpleAuthorizationInfo;
import org.apache.shiro.mgt.DefaultSessionStorageEvaluator;
import org.apache.shiro.mgt.DefaultSubjectDAO;
import org.apache.shiro.realm.AuthorizingRealm;
import org.apache.shiro.session.mgt.DefaultSessionManager;
import org.apache.shiro.spring.web.ShiroFilterFactoryBean;
import org.apache.shiro.subject.PrincipalCollection;
import org.apache.shiro.web.mgt.DefaultWebSecurityManager;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import com.softtron.pinmaologin.daos.UserDao;
import com.softtron.pinmaologin.domains.TUser;
import com.softtron.pinmaologin.utils.ExceptionUtil;

@Configuration
public class ShiroConfig {
	@Autowired
	UserDao userDao;

	@Bean
	public ShiroFilterFactoryBean shiroFilter() {
		ShiroFilterFactoryBean shiroFilterFactoryBean = new ShiroFilterFactoryBean();
		shiroFilterFactoryBean.setSecurityManager(securityManager());
		// 设置登录路径
		shiroFilterFactoryBean.setLoginUrl("/notlogin");
		// 无权限
		shiroFilterFactoryBean.setUnauthorizedUrl("/notRole");
		Map<String, String> filterChainDefinitionMap = new LinkedHashMap<>();
		// 游客，开发权限
		// filterChainDefinitionMap.put("/index/**", "anon");
		// 用户，需要角色权限 “user”
		filterChainDefinitionMap.put("/show", "roles[user]");
		// 开放登陆接口
		filterChainDefinitionMap.put("/login", "anon");
		// 其余接口一律拦截
		// 主要这行代码必须放在所有权限设置的最后，不然会导致所有 url 都被拦截
		// filterChainDefinitionMap.put("/**", "authc");
		filterChainDefinitionMap.put("/**", "authc");
		shiroFilterFactoryBean.setFilterChainDefinitionMap(filterChainDefinitionMap);
		return shiroFilterFactoryBean;
	}

	@Bean
	public org.apache.shiro.mgt.SecurityManager securityManager() {
		DefaultWebSecurityManager securityManager = new DefaultWebSecurityManager();
		securityManager.setRealm(customRealm());
		return securityManager;
	}

	@Bean
	public AuthorizingRealm customRealm() {
		return new AuthorizingRealm() {

			@Override
			protected AuthorizationInfo doGetAuthorizationInfo(PrincipalCollection principals) {
				String username = (String) SecurityUtils.getSubject().getPrincipal();
				SimpleAuthorizationInfo info = new SimpleAuthorizationInfo();
				// 获得该用户角色
				Set<String> roles = userDao.getRole(username);
				roles = new HashSet();
				roles.add("admin");
				// 设置该用户拥有的角色
				info.setRoles(roles);
				return info;
			}

			/**
			 * 验证
			 */
			@Override
			protected AuthenticationInfo doGetAuthenticationInfo(AuthenticationToken authenticationToken)
					throws AuthenticationException {
				UsernamePasswordToken token = (UsernamePasswordToken) authenticationToken;
				TUser user = userDao.findUserByUsername(token.getUsername());
				if(user==null) {
					throw ExceptionUtil.NOTUSEREXCEPTION;
				}
				String password = user.getPassword();
				if (null == password) {
					throw ExceptionUtil.NOTUSEREXCEPTION;
				} else if (!password.equals(new String((char[]) token.getCredentials()))) {
					throw ExceptionUtil.LOGINERROREXCEPTION;
				}
				
				// 最终会封装到一个AuthenticationInfo类中
				return new SimpleAuthenticationInfo(token.getPrincipal(), password, getName());
				

			}


		};
	}
	

}

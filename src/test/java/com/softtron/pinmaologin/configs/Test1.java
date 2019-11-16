package com.softtron.pinmaologin.configs;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import com.softtron.pinmaologin.filters.LoginFilter;
@RunWith(SpringRunner.class)
@SpringBootTest(classes = { Init.class })
class Test1 {
	@Autowired
	LoginFilter loginFilter;
	@Test
	void test() {
		System.out.println(loginFilter);
	}

}

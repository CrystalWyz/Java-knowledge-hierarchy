package cn.wyz;

import cn.wyz.bean.WyzZzz;
import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;

public class Entrance {
	public static void main(String[] args) {
		ApplicationContext applicationContext = new ClassPathXmlApplicationContext("application.xml");
		WyzZzz wyzZzz = applicationContext.getBean(WyzZzz.class);
		System.out.println(wyzZzz);
	}
}

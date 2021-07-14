package cn.wyz;

import cn.wyz.bean.WyzZzz;
import org.springframework.context.support.ClassPathXmlApplicationContext;

public class Debug {
	public static void main(String[] args) {
		ClassPathXmlApplicationContext classPathXmlApplicationContext = new ClassPathXmlApplicationContext("application.xml");
		WyzZzz bean = classPathXmlApplicationContext.getBean(WyzZzz.class);
		System.out.println(bean);
	}
}

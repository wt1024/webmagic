package us.codecraft.webmagic.pipeline.util;

import java.sql.Connection;
import java.sql.DriverManager;


public class DBDataSourceWtblog {
	private static String URL;
	private static String DRIVER;
	private static String USERNAME;
	private static String PASSWORD;
	
	private volatile static DBDataSourceWtblog instance;
	private static Object lock = new Object();
	
	private DBDataSourceWtblog () {
		URL = "jdbc:mysql://139.129.48.66:3306/wtblog?useUnicode=true&characterEncoding=utf-8";
		DRIVER = "com.mysql.jdbc.Driver";
		USERNAME = "root";
		PASSWORD = "wt123caibudaookm";
	}
	
	public static DBDataSourceWtblog getInstance() {
		if (instance == null) {
			synchronized (lock) {
				instance = new DBDataSourceWtblog();
			}
		}
		return instance;
	}
	
	public Connection getConnection() throws Exception {
		Class.forName(DRIVER);
		return DriverManager.getConnection(URL, USERNAME, PASSWORD);
	}
}
package us.codecraft.webmagic.pipeline.util;

import java.sql.Connection;
import java.sql.DriverManager;


public class DBDataSourceWlheihei {
	private static String URL;
	private static String DRIVER;
	private static String USERNAME;
	private static String PASSWORD;
	
	private volatile static DBDataSourceWlheihei instance;
	private static Object lock = new Object();
	
	private DBDataSourceWlheihei () {
		URL = "jdbc:mysql://121.42.33.175:3306/fs12307?useUnicode=true&characterEncoding=utf-8";
		DRIVER = "com.mysql.jdbc.Driver";
		USERNAME = "root";
		PASSWORD = "wt12120211314";
	}
	
	public static DBDataSourceWlheihei getInstance() {
		if (instance == null) {
			synchronized (lock) {
				instance = new DBDataSourceWlheihei();
			}
		}
		return instance;
	}
	
	public Connection getConnection() throws Exception {
		Class.forName(DRIVER);
		return DriverManager.getConnection(URL, USERNAME, PASSWORD);
	}
}
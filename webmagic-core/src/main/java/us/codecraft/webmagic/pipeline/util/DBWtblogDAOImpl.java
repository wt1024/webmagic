package us.codecraft.webmagic.pipeline.util;


import java.io.Serializable;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.math.BigDecimal;
import java.sql.Blob;
import java.sql.Clob;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;



public class DBWtblogDAOImpl {
	
	/**
	 * 通用查询方法，返回Map集合
	 * @param sql
	 * @param params
	 * @return
	 * @throws Exception
	 */
	public List<Map<String, Object>> findMapList(String sql, Object[] params) throws Exception {
		ResultSet rs = null;
		Connection conn = null;
		PreparedStatement ps = null;
		List<Map<String, Object>> list = new ArrayList<Map<String, Object>>();
		try {
			conn = DBDataSourceWtblog.getInstance().getConnection();
			ps = conn.prepareStatement(sql);
			if (params != null && params.length > 0) {
				for (int i = 0; i < params.length; i++) {
					ps.setObject(i + 1, params[i]);
				}
			}
			rs = ps.executeQuery();
			
			ResultSetMetaData rsmd = rs.getMetaData();
			Map<String, Object> map = null;
			while (rs.next()) {
				map = new HashMap<String, Object>();
				for (int i = 0; i < rsmd.getColumnCount(); i++) {
					map.put(rsmd.getColumnName(i + 1), rs.getObject(i + 1));
				}
				list.add(map);
			}
		} finally {
			if (rs != null) {
				rs.close();
			}
			if (ps != null) {
				ps.close();
			}
			if (conn != null) {
				conn.close();
			}
		}
		return list;
	}
	
	/**
	 * 通用查询数据方法，返回clazz类型集合
	 * @param sql
	 * @param params
	 * @param clazz
	 * @return
	 * @throws Exception
	 */
	public <T> List<T> findListByClass(String sql, Object[] params, Class<T> clazz) throws Exception{
		ResultSet rs = null;
		Connection conn = null;
		PreparedStatement ps = null;
		List<T> list = new ArrayList<T>();
		try {
			conn = DBDataSourceWtblog.getInstance().getConnection();
			ps = conn.prepareStatement(sql);
			if (params != null && params.length > 0) {
				for (int i = 0; i < params.length; i++) {
					ps.setObject(i + 1, params[i]);
				}
			}
			rs = ps.executeQuery();
			list = parseResultSet(clazz, rs);
		} finally {
			if (rs != null) {
				rs.close();
			}
			if (ps != null) {
				ps.close();
			}
			if (conn != null) {
				conn.close();
			}
		}
		return list;
	}
	
	/**
	 * 通用更新方法
	 * @param sql 更新语句
	 * @param params 参数
	 * @return
	 * @throws Exception
	 */
	public int update(String sql, Object[] params) throws Exception {
		Connection conn = null;
		PreparedStatement ps = null;
		try {
			conn = DBDataSourceWtblog.getInstance().getConnection();
			conn.setAutoCommit(false);
			ps = conn.prepareStatement(sql);
			if (params != null && params.length > 0) {
				for (int i = 0; i < params.length; i++) {
					ps.setObject(i + 1, params[i]);
				}
			}
			
			int count = ps.executeUpdate();
			conn.commit();
			return count;
		} catch (Exception e) {
			if (conn != null) {
				conn.rollback();
			}
			throw e;
		} finally {
			if (ps != null) {
				ps.close();
			}
			if (conn != null) {
				conn.close();
			}
		}
	}
	
	/**
	 * 解析sql查询结果，并创建clazz类型集合
	 * @param clazz
	 * @param rs
	 * @return
	 * @throws Exception
	 */
	private <T> List<T> parseResultSet(Class<T> clazz, ResultSet rs) throws Exception {
		Class.forName(clazz.getName());
		Field[] fields = clazz.getDeclaredFields();
		T instance = null;
		@SuppressWarnings("rawtypes")
		Class type = null;
		String name = null;
		Method method = null;
		String methodName = null;
		List<T> list = new ArrayList<T>();
		while (rs.next()) {
			instance = clazz.newInstance();
			for (Field field : fields) {
				name = field.getName();
				if (!"serialVersionUID".equals(name)) {
					methodName = "set" + name.substring(0, 1).toUpperCase() + name.substring(1);
					type = field.getType();
					method = clazz.getMethod(methodName, type);
					if (type == Integer.class) {
						method.invoke(instance, rs.getInt(name));
					} else if (type == String.class) {
						method.invoke(instance, rs.getString(name));
					} else if (type == Long.class) {
						method.invoke(instance, rs.getLong(name));
					} else if (type == Double.class) {
						method.invoke(instance, rs.getDouble(name));
					} else if (type == Float.class) {
						method.invoke(instance, rs.getFloat(name));
					} else if (type == Boolean.class) {
						method.invoke(instance, rs.getBoolean(name));
					} else if (type == BigDecimal.class) {
						method.invoke(instance, rs.getBigDecimal(name));
					} else if (type == Blob.class) {
						method.invoke(instance, rs.getBlob(name));
					} else if (type == Clob.class) {
						method.invoke(instance, rs.getClob(name));
					} else if (type == Byte.class) {
						method.invoke(instance, rs.getByte(name));
					} else if (type == Date.class) {
						method.invoke(instance, new Date(rs.getDate(name).getTime()));
					} else if (type == Short.class) {
						method.invoke(instance, rs.getShort(name));
					}
				}
			}
			list.add(instance);
		}
		return list;
	}
	
	
	public void persist(Serializable obj) throws Exception {
		Connection conn = null;
		PreparedStatement ps = null;
		try {
			conn = DBDataSourceWtblog.getInstance().getConnection();
			conn.setAutoCommit(false);
			
			Map<String, Object> map = buildSqlAndParams(obj);
			String sql = (String) map.get("sql");
			ps = conn.prepareStatement(sql);
			
			@SuppressWarnings("unchecked")
			List<Object> params = (List<Object>) map.get("params");
			if (params != null && params.size() > 0) {
				for (int i = 0; i < params.size(); i++) {
					ps.setObject(i + 1, params.get(i));
				}
			}
			
			ps.execute();
			conn.commit();
		} catch (Exception e) {
			if (conn != null) {
				conn.rollback();
			}
			throw e;
		} finally {
			if (ps != null) {
				ps.close();
			}
			if (conn != null) {
				conn.close();
			}
		}
	}
	
	private Map<String, Object> buildSqlAndParams (Serializable obj) throws Exception {
		Class<?> clazz = obj.getClass();
		Class.forName(clazz.getName());
		
		StringBuffer columns = new StringBuffer("(");
		StringBuffer values = new StringBuffer("(");
		List<Object> params = new ArrayList<Object>();
		
		String name = null;
		Method method = null;
		Field[] fields = clazz.getDeclaredFields();
		for (Field field : fields) {
			name = field.getName();
			if (!"serialVersionUID".equals(name) && !"table".equals(name)) {
				method = obj.getClass().getDeclaredMethod("get" + name.substring(0, 1).toUpperCase() + name.substring(1), null);
				params.add(method.invoke(obj, null));
				columns.append(name).append(",");
				values.append("?,");
			}
		}
		
		if (columns.length() > 1) {
			columns.setLength(columns.length() - 1);
			values.setLength(values.length() - 1);
		}
		columns.append(")");
		values.append(")");
		
		Field tableField = clazz.getDeclaredField("table");
		tableField.setAccessible(true);
		Object temp = tableField.get(obj);
		if (temp == null) {
			throw new Exception(clazz.getName() + " 缺少table成员变量 ");
		}
		
		String table = String.valueOf(temp);
		StringBuffer sql = new StringBuffer("insert into ").append(table);
		sql.append(columns).append(" values ").append(values);
		System.out.println(sql.toString());
		Map<String, Object> map = new HashMap<String, Object>();
		map.put("sql", sql.toString());
		map.put("params", params);
		return map;
	}
	
	public static void main(String[] args) throws Exception {
		DBWtblogDAOImpl dao = new DBWtblogDAOImpl();
		String sql = "select * from car_pv_uv_count";
		List<Map<String, Object>> lst = dao.findMapList(sql, null);
		System.out.println(lst.size());
		
	}
}

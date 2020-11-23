package com.yhy.eventjudgement;

import java.io.IOException;
import java.io.InputStream;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Properties;

import com.db.JdbcUtil;

/**
* @author Haiyou Yu
* @version 1.0
*/
public class DBUtil {
   
	private static String url = null;
	private static String username = null;
	private static String password = null;
	private static String driver = null;


	public static Connection getConnection() {
		Connection conn = null;
		try {
			Properties properties = new Properties();
			InputStream in = JdbcUtil.class.getClassLoader().getResourceAsStream("./jdbc.properties");

			properties.load(in);
			properties.setProperty("driver", "com.mysql.cj.jdbc.Driver");
			driver = properties.getProperty("driver");

			properties.setProperty("url", "jdbc:mysql://localhost:3306/ks?serverTimezone=UTC&useSSL=false");
			url = properties.getProperty("url");

			username = properties.getProperty("username");
			password = properties.getProperty("password");

			Class.forName(driver);

			conn = DriverManager.getConnection(url, username, password);
			
		} catch (SQLException e) {
			e.printStackTrace();
		} catch (ClassNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
		return conn;
	}
   
   
	public static void close(Connection conn, Statement st, ResultSet rs) {
		try {
			if (conn != null) {
				conn.close();
			}

			if (st != null) {
				st.close();
			}

			if (rs != null) {
				rs.close();
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}

	
   //-----------------------------------------------------------------
   //this is the test code
   public static void main(String[] args) throws SQLException {
	   Connection c= getConnection();
	   System.out.println("connection:"+c);
       close(c,null,null);
   }
   //end test code
   //------------------------------------------------------------------

}
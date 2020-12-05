package com.yhy.eventjudgement;

import java.io.IOException;
import java.io.InputStream;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.Properties;


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
			InputStream in = DBUtil.class.getClassLoader().getResourceAsStream("./jdbc.properties");

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

	public static ArrayList<String> query(String sql) {
		ArrayList<String> resultSet =new  ArrayList<>();
		ResultSet rs = null;
		String path = "";
		Connection conn = null;
		try {
			conn = DBUtil.getConnection();
			rs = conn.createStatement().executeQuery(sql);
			
			while (rs.next()) {	
				path = rs.getString("wenjianming");  
				//file name maybe repeat in the database, so we need judgment first. 
				if(!resultSet.contains(path))  
					resultSet.add(path);
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}finally {
			if(conn!=null) {
				close(conn,null,null);
			}
		}
		return resultSet;
	}
	
   //-----------------------------------------------------------------
   //this is the test code
   public static void main(String[] args) throws SQLException {
	   //Connection c= getConnection();
	   //System.out.println("connection:"+c);
	   String sql = "select wenjianming from mine_quack_results where id>409";
	   ArrayList<String> path = query(sql);
       //close(c,null,null);
       for(String s:path) {
    	   System.out.println("path:"+s);
       }
   }
   //end test code
   //------------------------------------------------------------------

}
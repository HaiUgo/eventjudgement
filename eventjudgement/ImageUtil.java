package com.yhy.eventjudgement;


import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;

/**
* @author Haiyou Yu
* @version 1.0
* @since 2020.11.23
*/
public class ImageUtil {

   /**
    * read binary stream from data table, then output the image
    * @param in ,InputStream
    * @param targetPath , the generated image file path
    */
   private static void readBin2Image(InputStream in, String targetPath) {
       File file = new File(targetPath);
       String path = targetPath.substring(0, targetPath.lastIndexOf("/"));
       if (!file.exists()) {
           new File(path).mkdir();
       }
       FileOutputStream fos = null;
       try {
           fos = new FileOutputStream(file);
           int len = 0;
           byte[] buf = new byte[1024];
           while ((len = in.read(buf)) != -1) {
               fos.write(buf, 0, len);
           }
           fos.flush();
       } catch (Exception e) {
           e.printStackTrace();
       } finally {
           if (null != fos) {
               try {
                   fos.close();
               } catch (IOException e) {
                   e.printStackTrace();
               }
           }
       }
   }
   
   
   /**
    * save the image binary stream to the data table 
    * @param path ,the image source path, such as :"D:/1.jpg"
    * @param para, the parameters satisfy the PreparedStatement  
    */
   public static void readImage2DB(String path,String[] para) {
       Connection conn = null;
       PreparedStatement ps = null;
       FileInputStream in = null;
       try {
           in = new FileInputStream(new File(path));
           conn = DBUtil.getConnection();
           String sql = "insert into mine_quack_wavejudgement (day,id,panfu,event_possibility,"
           		+ "noise_possibility,judgement,image)values(?,?,?,?,?,?,?)";
           ps = conn.prepareStatement(sql);
           ps.setString(1, para[0]);
           ps.setString(2, para[1]);
           ps.setString(3, para[2]);
           ps.setString(4, para[3]);
           ps.setString(5, para[4]);
           ps.setString(6, para[5]);
           ps.setBinaryStream(7, in, in.available());
           int count = ps.executeUpdate();
           if (count > 0) {
               System.out.println("insert successfull！");
           } else {
               System.out.println("insert failed！");
           }
       } catch (Exception e) {
           e.printStackTrace();
       } finally {
           DBUtil.close(conn,ps,null);
       }
   }
   
   /**
    * read image binary stream from data table, then convert this stream to image file.
    * @param targetPath , the generated image file path, such as :"D:/new.jpg"
    * @param para, the primary key(day,id)
    */
   public static void readDB2Image(String targetPath,String[] para) {
       Connection conn = null;
       PreparedStatement ps = null;
       ResultSet rs = null;
       try {
           conn = DBUtil.getConnection();
           String sql = "select * from photo where day =? and id=?";
           ps = conn.prepareStatement(sql);
           ps.setString(1, para[0]);
           ps.setString(2, para[1]);
           rs = ps.executeQuery();
           while (rs.next()) {
        	   //image is the  columnLabel in mine_quack_wavejudgement
               InputStream in = rs.getBinaryStream("image");
               ImageUtil.readBin2Image(in, targetPath);
           }
       } catch (Exception e) {
           e.printStackTrace();
       } finally {
           DBUtil.close(conn,ps,rs);
       }
   }
   
   
   //---------------------------------------------------------------------
   //this is the test code
   public static void main(String[] args) {
	   //2020-10-23110956_2_utyw event11.11%_noise88.89%
	   String path = "D:/1.jpg";
	   String[] para = {"2020-10-23110956","2","utyw","11.11%","88.89%","1"};
	   readImage2DB(path,para);
   }
   //end test code
   //----------------------------------------------------------------------
}
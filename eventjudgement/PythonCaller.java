package com.yhy.eventjudgement;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;

/**
 * This class is designed to call the python program
 * @author Haiyou Yu
 * 
 */
public class PythonCaller {
	
	
	public static void pythonCaller(String []parameters){
		Process proc;

		try {
			//python execution parameters
			String[] commands = new String[3];
			commands[0] = parameters[0];     //要执行的Python文件
			commands[1] = parameters[1];     //预测模型
			commands[2] = parameters[2];     //要预测的jpg格式的图片
			proc = Runtime.getRuntime().exec("python "+commands[0]+commands[1]+commands[2]);

			//read subprocess's standard output results 
			BufferedReader in = new BufferedReader(new InputStreamReader(proc.getInputStream()));
			String line = null;
			while ((line = in.readLine()) != null) {
				System.out.println(line);
			}
			in.close();

			//read subprocess's error output results
			//          BufferedReader errorIn = new BufferedReader(new InputStreamReader(proc.getErrorStream()));
			//          String error = null;
			//          while ((error = errorIn.readLine()) != null) {
			//              System.out.println(error);
			//          }
			//          errorIn.close();

			proc.waitFor();
			System.out.println("done!");

		} catch (IOException e) {
			e.printStackTrace();
		} catch (InterruptedException e) {
			e.printStackTrace();
		} 
	}
	
	//--------------------------------------------------------------
	//this is the test code
    public static void main(String[] args) {
        String currentExecutePath = " "+System.getProperty("user.dir")+File.separator;
        //currentExecutePath = currentExecutePath.replaceAll("\\\\", "//");//替换\
        System.out.println("path = "+currentExecutePath);
        
    	String[] commands = new String[3];
    	//note: do not forget to leave a blank space before the command parameter
      	commands[0] = currentExecutePath+"Predict_EQ.py";     //the python file to execute
      	commands[1] = currentExecutePath+"EQfinder.h5";       //prediction model
      	commands[2] = currentExecutePath+"a.jpg";             //prediction .jpg image 
      	for(String temp:commands) {
      		System.out.println("command:"+temp);
      	}
      	pythonCaller(commands);
    }
    //end test code
    //-----------------------------------------------------------------------

}
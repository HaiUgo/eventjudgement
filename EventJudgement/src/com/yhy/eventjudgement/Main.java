package com.yhy.eventjudgement;

import java.util.ArrayList;
import java.util.Vector;

public class Main {

	public static void run() {
		if(EventQueue.isFirstStart) {
			EventQueue.startManager();
			EventQueue.isFirstStart = false;
		}
		
		String sql = "select wenjianming from mine_quack_results where id>409";
		ArrayList<String> filePath = DBUtil.query(sql);

		for (int i = 0; i < filePath.size(); i++) {
			System.out.println("------------loop" + i + "----------------------");
			System.out.println("start to put data into event data queue------------");
			String path = filePath.get(i); // such as:D:/data/ConstructionData/5moti/zstryu 2020-11-23 07-15-34`05.csv
			String[] strList = path.split("/");
			
			System.out.println("path is:"+path);
			Vector[] v = ReadCSVFile.readCSV(path);             //获取csv文件每个台站的数据
		    System.out.println("v size:"+v.length);
		    
		    String fileName = strList[strList.length - 1]; // zstryu 2020-11-23 07-15-34`05.csv
			String[] nameItems = fileName.split(" ");
			String panfu = nameItems[0]; // zstryu
			String time = nameItems[1] + nameItems[2].split("`")[0]; // 2020-11-2307-15-34
			// char[] panfuName = panfu.toCharArray();
			for (int j = 0; j < panfu.length(); j++) {
				String finalPath = "D:/data/" + time + "_" + j + "_" + panfu + ".jpeg";
				System.out.println("finalPath:" + finalPath);
				Vector<String> vec = v[j];
				System.out.println("vector"+j+" size:"+vec.size());
				EventQueue.produce(vec,finalPath);
			}
			System.out.println("finished putting data into event data queue------------");
		}
		
		if(PythonCaller.isFirstStart) {
			PythonCaller.startManager();
			PythonCaller.isFirstStart = false;
		}
	}

	public static void main(String[] args) {
		run();
	}
}

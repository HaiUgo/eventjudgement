package com.yhy.eventjudgement;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.util.Vector;


public class ReadCSVFile {
	
	private static Vector[] datas = null;

	public ReadCSVFile() {
	}
	
	public Vector[] getVector() {
		return datas;
	}
	
	public ReadCSVFile(String filePath) {
		//解析文件路径  比如路径：D:/data/ConstructionData/5moti/syztwxu 2020-06-18 04-19-20`47.csv
        String[] items = filePath.split("/");	
        String panfu = items[items.length-1].split(" ")[0]; //即syztwxu
        int panfuNumber = panfu.length();
		File file = new File(filePath);
		BufferedReader bufferedReader = null;
		String line = "";
		datas = new Vector[panfuNumber]; //数据添加的是String类型的数据
		for(int i=0;i<panfuNumber;i++) {         //将每一行所有台站数据分割成几个台站的数据
			datas[i] = new Vector<String>();
		}
		
		try {
			bufferedReader = new BufferedReader(new InputStreamReader(new FileInputStream(file), "utf-8"));
			int count = 0;
			try {
				//后台读到的一行数据: 0 0 0 -833 -87 811 2020-10-2311:09:59
				//实际的CSV文件中的数据：2020-02-1413:59:32,0,0,0,-80,27,-25,12100,1,0,0,0,-1,13,-6,13050,6,......
				while((line=bufferedReader.readLine())!=null){
					String item[] = line.split(",");//CSV格式文件分割符,
					if(item.length>1) {
						if(count>89999) {  //大小不能超出索引
							break;
						}
						int index = 0;
						for(int i=0;i<item.length;i++) {
							if(i%8 == 0 && i>0) {    //因为每个台站有8列数据
								String s ="";
								for(int j=i-7;j<=i-2;j++) { //比如：0,0,0,-80,27,-25
									s += item[j]+" ";
								}
								datas[index].add(s);
								index ++ ;
							}
						}
						count++;
					}
				}
			} catch (IOException e) {
				e.printStackTrace();
			}
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		}finally {
			if(bufferedReader!=null) {
				try {
					bufferedReader.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}
	}
	
	public static void main(String[] args)  {
		ReadCSVFile r = new ReadCSVFile("D:\\data\\ConstructionData\\5moti\\syztwxu 2020-06-18 04-19-20`47.csv");
	    Vector[] v = r.getVector();
	    Vector<String> vec = v[0];
	    int i = 0;
	    for(String s:vec) {
	    	System.out.println("s:"+i+" "+s);
	    	i++;
	    }
	}
}

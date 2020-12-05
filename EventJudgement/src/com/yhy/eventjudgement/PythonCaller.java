package com.yhy.eventjudgement;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * This class is designed to call the python program
 * 
 * @author Haiyou Yu
 * 
 */
public class PythonCaller {

	/**
	 * the max capacity of this queue
	 */
	private static final int MAXVALUE = 21;
	
	/**
	 * a flag indicates that the startManager() function only need start once,
	 * you can find this variable used in EarthQuake class.
	 */
	public static boolean isFirstStart = true;
	
	/**
	 * which is used for storing the absolute path that is over-written by CNN predict file. 
	 */
    public static final ArrayList<String> ABSOLUTEPATHLIST = new ArrayList<String>();
	
    /**
   	 * the global blocking queue is used for storing .png or jpeg image path
   	 */
   	public static final BlockingQueue<String> PATHLIST = new ArrayBlockingQueue<>(MAXVALUE);
    
	/**
	 * call python program
	 * 
	 * @param parameters
	 *            ,python execution parameters 
	 *            0 the python file to execute 
	 *            1 prediction model 
	 *            2 prediction .png image
	 */
	private static void pythonCaller(String[] parameters) {
		Process proc;
		//String absolutePath = "";
		
		try {
			proc = Runtime.getRuntime().exec("python " + parameters[0] + parameters[1] + parameters[2]);

			// read subprocess's standard output results
			BufferedReader in = new BufferedReader(new InputStreamReader(proc.getInputStream()));
			String line = null;
			while ((line = in.readLine()) != null) {
				ABSOLUTEPATHLIST.add(line);
			}
			in.close();

			 //read subprocess's error output results
//			 BufferedReader errorIn = new BufferedReader(new InputStreamReader(proc.getErrorStream()));
//			 String error = null;
//			 while ((error = errorIn.readLine()) != null) {
//			 System.out.println(error);
//			 }
//			 errorIn.close();

			proc.waitFor();
			
			for(String str:ABSOLUTEPATHLIST) {
				ABSOLUTEPATHLIST.remove(str);
				//+++++++++++D:/data/2020-11-23071534_0_zstryu_event1.48%_noise98.52%.jpeg++++++++++++
				System.out.println("+++++++++++"+str+"++++++++++++");
				int startIndex = str.lastIndexOf('/');
				int endIndex = str.lastIndexOf('.');
				String[] lists = str.substring(startIndex+1, endIndex).split("_");
				//lists = [2020-11-23071534,0,zstryu,event1.48%,noise98.52%]
				if(lists.length==5) {
					String a = lists[3];    //event1.48%
			        String b = lists[4];    //noise98.52%
			        
			        int sIndex1 = a.lastIndexOf('t');
			        int sIndex2 = b.lastIndexOf('e');
					int eIndex1 = a.lastIndexOf("%");
					int eIndex2 = b.lastIndexOf("%");
					//System.out.println(sIndex1+" "+sIndex2+" "+eIndex1+" "+eIndex2);
					String tempA = a.substring(sIndex1+1, eIndex1); //1.48
			        String tempB = b.substring(sIndex2+1, eIndex2); //98.52
			        //System.out.println("tempA:"+tempA+"  tempB:"+tempB);
			        BigDecimal dataA = new BigDecimal(tempA);
			        BigDecimal dataB = new BigDecimal(tempB);
			        
			        int result = dataA.compareTo(dataB);
					String judgeResult = new String();
			        if(result>=0) {
			        	judgeResult = "0";   //if event possibility>= noise possibility, judgeResult = 0 indicates event
			        }else {
			        	judgeResult = "1";   //if event possibility< noise possibility, judgeResult = 1 indicates noise
			        }
			        //finalLists = [2020-11-23071534,0,zstryu,event1.48%,noise98.52%, 0 or 1]
		            String[] finalLists= {lists[0],lists[1],lists[2],lists[3],lists[4],judgeResult};
		            ImageUtil.readImage2DB(str, finalLists);
		            System.out.println("----+++++----read image to database finished----+++++----");
				}
				
			}
		} catch (IOException e) {
			e.printStackTrace();
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
	}

	private static void consume() {
		try {
			if(PATHLIST.size()>0) {
                System.out.println("-------------------------------------------------------");
				System.out.println("cnn consumer:start consume data");

				String path = PATHLIST.take();
				String currentExecutePath = " " + System.getProperty("user.dir") + File.separator+"resource"+File.separator;
				String[] commands = new String[3];
				commands[0] = currentExecutePath + "Predict_EQ.py"; // the python file to execute
				commands[1] = currentExecutePath + "EQfinder.h5"+" "; // prediction model
				commands[2] = path; // prediction .jpeg image
				for(String str:commands) {
					System.out.println(str);
				}
				pythonCaller(commands);
				
				System.out.println("cnn consumer:consume data over");
				System.out.println("cnn file path queue:the queue size is " + PATHLIST.size()+"after consumer consumes data");
                System.out.println("-------------------------------------------------------");
//                if(PATHLIST.size()==0 ) {
//                	MainThread.exitCNN = true;
//                }
			}
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
	}

	private static class Consumer implements Runnable {
		public void run() {
			try {
				Thread.sleep(10);
				while (true) {
					consume();
					Thread.sleep(10);
				}
			} catch (InterruptedException e) {
			}
		}
	}

	public static void startManager() {
		if (isFirstStart) {
			ExecutorService service = Executors.newCachedThreadPool();
			Consumer consumer = new Consumer();
			service.submit(consumer);
			try {
				Thread.sleep(10);
			} catch (InterruptedException e) {

			}
			// service.shutdownNow();
		}

	}

	// --------------------------------------------------------------
	// this is the test code
	public static void main(String[] args) {
		String currentExecutePath = " " + System.getProperty("user.dir") + File.separator+"resource"+File.separator;
		// currentExecutePath = currentExecutePath.replaceAll("\\\\", "//");//替换\
		System.out.println("path = " + currentExecutePath);

		String[] commands = new String[3];
		// note: do not forget to leave a blank space before the command parameter
		commands[0] = currentExecutePath + "Predict_EQ.py"; // the python file to execute
		commands[1] = currentExecutePath + "EQfinder.h5"; // prediction model
		commands[2] = currentExecutePath + "a.jpeg"; // prediction .jpg image
		for (String temp : commands) {
			System.out.println("command:" + temp);
		}
		pythonCaller(commands);
	}
	// end test code
	// -----------------------------------------------------------------------

}
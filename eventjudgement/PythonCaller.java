package com.yhy.eventjudgement;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
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
	 * a flag indicates that the startManager() function only need start once,
	 * you can find this variable used in EarthQuake class.
	 */
	public static boolean isFirstStart = true;
	

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
		try {
			proc = Runtime.getRuntime().exec("python " + parameters[0] + parameters[1] + parameters[2]);

			// read subprocess's standard output results
			BufferedReader in = new BufferedReader(new InputStreamReader(proc.getInputStream()));
			String line = null;
			while ((line = in.readLine()) != null) {
				System.out.println(line);
			}
			in.close();

			// read subprocess's error output results
			// BufferedReader errorIn = new BufferedReader(new
			// InputStreamReader(proc.getErrorStream()));
			// String error = null;
			// while ((error = errorIn.readLine()) != null) {
			// System.out.println(error);
			// }
			// errorIn.close();

			proc.waitFor();
			System.out.println("***************cnn predict done!******************");
		} catch (IOException e) {
			e.printStackTrace();
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
	}

	private static void consume() {
		try {
			if(EventQueue.PATHQUEUE.size()>0) {
                System.out.println("-------------------------------------------------------");
				System.out.println("cnn consumer:start consume data");

				String path = EventQueue.PATHQUEUE.take();
				String currentExecutePath = " " + System.getProperty("user.dir") + File.separator;
				String[] commands = new String[3];
				commands[0] = currentExecutePath + "Predict_EQ.py"; // the python file to execute
				commands[1] = currentExecutePath + "EQfinder.h5"; // prediction model
				commands[2] = path; // prediction .png image
				System.out.println("*****************"+path+"***************");
				pythonCaller(commands);
				
				System.out.println("cnn consumer:consume data over");
				System.out.println("cnn file path queue:the queue size is " + EventQueue.PATHQUEUE.size()+"after consumer consumes data");
                System.out.println("-------------------------------------------------------");
			}
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
	}

	private static class Consumer implements Runnable {
		public void run() {
			try {
				Thread.sleep(20000);
				while (true) {
					consume();
					Thread.sleep(6000);
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
		String currentExecutePath = " " + System.getProperty("user.dir") + File.separator;
		// currentExecutePath = currentExecutePath.replaceAll("\\\\", "//");//替换\
		System.out.println("path = " + currentExecutePath);

		String[] commands = new String[3];
		// note: do not forget to leave a blank space before the command parameter
		commands[0] = currentExecutePath + "Predict_EQ.py"; // the python file to execute
		commands[1] = currentExecutePath + "EQfinder.h5"; // prediction model
		commands[2] = currentExecutePath + "a.jpg"; // prediction .jpg image
		for (String temp : commands) {
			System.out.println("command:" + temp);
		}
		pythonCaller(commands);
	}
	// end test code
	// -----------------------------------------------------------------------

}
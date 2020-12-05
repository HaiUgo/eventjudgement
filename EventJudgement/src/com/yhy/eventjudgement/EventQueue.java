/**
 * This class is used for saving the whole event  data, 
 * we need save the event data when countNumber>=3 in EarthQuake,
 * so we should use a queue to record all events in the data stream.
 * 
 */
package com.yhy.eventjudgement;


import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Vector;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
 
/**
 * @author Haiyou Yu
 */


public class EventQueue {
	
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
	 * the global blocking queue is used for storing 18s data of each station 
	 */
    private static final BlockingQueue<Vector<String>> MANAGER = new ArrayBlockingQueue<>(MAXVALUE);
    
    /**
	 * the global blocking queue is used for storing .png or jpeg image path
	 */
	private static final BlockingQueue<String> PATHQUEUE = new ArrayBlockingQueue<>(MAXVALUE);
    
    private EventQueue() {
		
	}
    
    /**
     * 
     * @param datas,the 18s data ,this function is used in EarthQuacke class.
     * @param path,the generated image path 
     */
    public static void produce(Vector<String> datas,String path) {
    	try {
    		if(getQueueSize()<=MAXVALUE) {
    			MANAGER.put(datas);
    			PATHQUEUE.put(path);
                System.out.println("event data queue:the queue size is " + getQueueSize()+" after producer produces data");
    		}else {
    			System.out.println("--------event data queue is full-------------");
    		}
			
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
    }
    
    /**
     * 
     * @return the used capacity of manager queue
     */
    private static int getQueueSize() {
        return MANAGER.size();
    }
    
    /**
     * convert datas to pictures
     */
    private static void consume(){
    	try {
            System.out.println("-------------------------------------------------------");
            System.out.println("event data consumer:start to consume data");
    		
    		Vector<String> data = MANAGER.take();
    		String path = PATHQUEUE.take();
    		ArrayList<Double> vd = new ArrayList<>();
    		//each s is a string, such as: 0 0 0 -833 -87 811 2020-10-2311:09:59
    		for(String s:data) {
    			//System.out.println("-------------------------------------");
    			//System.out.println(s);
    			
    			//only need 4,5,6 columns's data, such as -833,-87,811
    			String[] v = s.split(" ");
    			//calculate the mean value of -833,-87,811 ,and the result accuracy is two bits.
    		    double mean =(Double.parseDouble(v[3])+Double.parseDouble(v[4])
    		    			+Double.parseDouble(v[5]))/3;
    		    mean = new BigDecimal(mean).setScale(2, BigDecimal.ROUND_HALF_UP).doubleValue();
    			
    		    vd.add(mean);
    		    
    			//System.out.println("mean:"+mean);
    			//System.out.println("-------------------------------------");
    		}
    		
    		WaveChart.saveAsFile(WaveChart.init(vd, false), path, 600, 600, 1);
    		
    		if(path!=null)
    			PythonCaller.PATHLIST.put(path);
    		
    		
    		System.out.println("event data consumer:consume data over");
            System.out.println("event data queue:the queue size is " + getQueueSize()+"after consumer consumes data");
            System.out.println("-------------------------------------------------------");
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
    }
    
    private static class Consumer implements Runnable {
        public void run() {
            try {
                while (true) {
                    consume();
                    Thread.sleep(10);
                }
            } catch (InterruptedException e) {}
        }
    }
    
    public static void startManager() {
    	if(isFirstStart) {
    		ExecutorService service = Executors.newCachedThreadPool();
            Consumer consumer = new Consumer();
            service.submit(consumer);
            try {
                Thread.sleep(10);
            } catch (InterruptedException e) {
            	
            }
            //service.shutdownNow();
    	}
        
    }
 
    //-------------------------------------------------------------
    //this is the test code
    public static void main(String[] args) {
        EventQueue.startManager();
    }
    //end test code 
    //--------------------------------------------------------------
}

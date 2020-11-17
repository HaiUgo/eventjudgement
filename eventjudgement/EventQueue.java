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
	 * the global blocking queue
	 */
    private static final BlockingQueue<Vector<String>> manager = new ArrayBlockingQueue<>(MAXVALUE);
    
    
    private EventQueue() {
		
	}
    
    /**
     * 
     * @param datas ,the 18s data ,this function is used in EarthQuacke class.
     */
    public static void produce(Vector<String> datas) {
    	try {
    		if(getQueueSize()<=MAXVALUE) {
    			manager.put(datas);
                System.out.println("the queue size is：" + getQueueSize()+" after producer produces data");
    		}else {
    			System.out.println("--------the queue is full-------------");
    		}
			
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
    }
    
    /**
     * 
     * @return the used capacity of this queue
     */
    private static int getQueueSize() {
        return manager.size();
    }
    
    /**
     * 
     */
    private static void consume(){
    	try {
    		Vector<String> str = manager.take();
    		ArrayList<Double> vd = new ArrayList<>();
    		//each s is a string, such as: 0 0 0 -833 -87 811 2020-10-2311:09:59
    		for(String s:str) {
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
    		
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
    }
    
    private static class Consumer implements Runnable {
        public void run() {
            try {
                while (true) {
                    System.out.println("消费者开始消费数据----------------");
                    consume();
                    System.out.println("消费者消费数据完毕----------------");
                    System.out.println("消费者消费完毕后队列大小：" + getQueueSize());
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

package api;

import java.util.Iterator;
import java.util.Map;

import com.fxcm.fix.FXCMTimingIntervalFactory;
import com.fxcm.fix.IFXCMTimingInterval;

public class Utils {
	
	public static boolean safeEquals(String str1, String str2) {
        if (str1 == null || str2 == null) {
            return false;
        } else {
            return str1.equals(str2);
        }
    }
	
	
	/*	 for example, round 1.23342345 => toFixed(1.23342345, 4) => 1.2334	 */
	public static double toFixed(double number, int digitsAfterDecimal){
		double roundedNumber = Math.round(number*Math.pow(10, digitsAfterDecimal)) / Math.pow(10, digitsAfterDecimal);
		return roundedNumber;
	}
	
	public static String getAllThreadsStat(){
		int totalThreads = 0;
		int runnablesCounter=0;
		int waitingCounter = 0;
		int TimedWaitingCounter = 0;
		String threadsStatistics = "";
		Map<Thread, StackTraceElement[]> threads = Thread.getAllStackTraces();
		Iterator<Thread> iter = threads.keySet().iterator();
		while(iter.hasNext()){
			Thread thread = (Thread) iter.next();
			threadsStatistics += "Thread Name: " + thread.getName() + ",\t State:  " + thread.getState()+"\n";
			if(thread.getState().toString().equals("WAITING")) waitingCounter++;
			else if(thread.getState().toString().equals("RUNNABLE")) runnablesCounter++;
			else if(thread.getState().toString().equals("TIMED_WAITING")) TimedWaitingCounter++;
			totalThreads++;
		}
		threadsStatistics += "Active Threads:\t" + Thread.currentThread() + ", Total: " + totalThreads + ", Runnable: " + 
							 runnablesCounter + ", Waiting: " + waitingCounter + ", TimedWaiting: " + TimedWaitingCounter;
		return threadsStatistics;
	}
	

}

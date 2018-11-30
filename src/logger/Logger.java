package logger;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

import com.fxcm.fix.UTCTimestamp;

import api.LimitOrder;
import api.MarketOrder;

public class Logger {
	
	public enum LogType {
        MARKET_ORDER  ("Market Order"),
        LIMIT_ORDER   ("Limit Order"),
        STOP_ORDER   ("Stop Order"),
		ORDER	("Order"),
		ERROR	("Error"),
		EXECUTION	("Execution"),
		GENERAL	("General"),
		CONNECTION	("Connection");
		

        private final String e_name;

        LogType(String name)
            { this.e_name = name; }

        public String getName() { return e_name; }
   }
	

	private static Logger logger = null;
	private List<Log> logList;
	
	private Logger(){
		logList = new ArrayList<Log>();
	}
	
	public static Logger getInstance(){
		if(logger == null){
			logger = new Logger();
		}
		return logger;
	}
	
	public List<Log> getAllLogs(){
		return logList;
	}
	
	public void log(LogType logType,Object rawContent){
		String logContent = "";
		switch(logType) {
        case ORDER :
    		if(rawContent instanceof MarketOrder){
    			logContent = "Market order pending => " + ((MarketOrder) rawContent).getDirection() + " " + 
    					((MarketOrder) rawContent).getQuantity() +	" " + ((MarketOrder) rawContent).getAsset();
        	}
        	else if(rawContent instanceof LimitOrder){
        		
        	}
           break;
        case EXECUTION :
    		if(rawContent instanceof MarketOrder){
        		logContent = "Market order executed => " + ((MarketOrder) rawContent).getDirection() + " " + 
        				((MarketOrder) rawContent).getQuantity() + " " + ((MarketOrder) rawContent).getAsset() + " at " + 
        				((MarketOrder) rawContent).getPrice();
        	}
        	else if(rawContent instanceof LimitOrder){
        		
        	}
           break;
        case ERROR :
           if(rawContent instanceof String){
        	   logContent = (String) rawContent;
           }
           break;
		case GENERAL :
			if(rawContent instanceof String){
				logContent = (String) rawContent;
			}
			break;
		case CONNECTION :
			if(rawContent instanceof String){
				logContent = (String) rawContent;
			}
			break;
		}
		
		UTCTimestamp logTime = new UTCTimestamp();
		Log log = new Log(logTime, logType, logContent);
		logList.add(log);
		
		System.out.println(logTime + " " + logType + ": " + logContent);
	}

}

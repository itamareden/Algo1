package strategy;

import java.util.Date;
import java.util.List;

import api.Candlestick;
import api.MarketData;
import api.MarketOrder;
import api.OrderProperties;
import api.Trade;
import api.OrderProperties.Direction;
import logger.Logger;
import logger.Logger.LogType;

public abstract class Strategy extends Thread {
	
	public Trade trade;
	public Logger logger;
	StrategyProperties strategyProp;
	public boolean isKeepRunning = false;
	
	public Strategy(StrategyProperties strategyProp){
		this.trade = Trade.getInstance();
		this.logger = Logger.getInstance();
		Date date = new Date();
		this.strategyProp = strategyProp;
		this.setName(strategyProp.getStrategyName() + "   =>  " + date.toString());;
	}
	
	public void run(){
		try {
			while(!isReadyToStart()){
				Thread.sleep(1000);
			}
			runStrategy();
		} 
		catch (InterruptedException e) {
			e.printStackTrace();
		}
	}
	
	public boolean isReadyToStart() {
		if(MarketData.collateralReport != null && MarketData.assetsDataReady){
			isKeepRunning = true;
			return true;
		}
		return false;
	}

	public void stopStrategy() {
		logger.log(LogType.GENERAL, "Stopped Strategy :)");
		isKeepRunning  = false;
	}
	
	public void runStrategy(){
		
	}
	

}

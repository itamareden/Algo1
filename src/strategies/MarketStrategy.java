package strategies;

import java.util.Date;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import api.Candlestick;
import api.MarketData;
import api.MarketOrder;
import api.OrderProperties;
import api.Utils;
import logger.Logger;
import logger.Logger.LogType;
import strategy.Strategy;
import strategy.StrategyProperties;

public class MarketStrategy extends Strategy {
	
//	private static MarketStrategy marketStrategy = null;
	private boolean isActivePosition = false;
	private double tradePrice;
	private final double targetDistance = 0.003;	// fix it
	
	private boolean showLog = false;	// DELETE ME!
	private int counter ; // ME TOO
	
//	List<Candlestick> assetData;	MAKE IT SAFE. CHECK IF MarketData IS NOT NULL!!!!!!
	private StrategyProperties strategyProp;

	public MarketStrategy(StrategyProperties strategyProp) {
		super(strategyProp);
	}
	
	public void runStrategy(){
		while(isKeepRunning){
			try {
				if(!isActivePosition){
					MarketOrder marketOrder = new MarketOrder("EUR/USD", OrderProperties.Direction.LONG, 1000);
					boolean isConfirmed = trade.sendMarketOrder(marketOrder);
					if(isConfirmed){
						tradePrice = marketOrder.getPrice();
						isActivePosition = true;
						this.showLog = true;	// DELETE ME!
					}
				}
				else if(isTargetBreached()){
					MarketOrder marketOrder = new MarketOrder("EUR/USD", OrderProperties.Direction.SHORT, 1000);
					boolean isConfirmed = trade.sendMarketOrder(marketOrder);
					if(isConfirmed){
						isActivePosition = false;
					}
				}
				System.out.println(counter++ * 15 + " seconds have passed. rate: " + MarketData.assetData.get(0).getClose());
				Thread.sleep(15000);
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
	}
	
	private boolean isTargetBreached(){
		double lastPrice = MarketData.assetData != null ? MarketData.assetData.get(0).getClose() : 0;
		double lowerBoundry = (1 - this.targetDistance) * this.tradePrice;
		double upperBoundry = (1 + this.targetDistance) * this.tradePrice;
		if(showLog){
			showLog = false;
			logger.log(LogType.GENERAL, "stop: "+ Utils.toFixed(lowerBoundry, 4) + "  , profit: "+ Utils.toFixed(upperBoundry, 4));
		}
		if(lastPrice != 0 && (lastPrice > upperBoundry || lastPrice < lowerBoundry)) return true;
		return false;
	}

}

package strategies;

import java.util.Date;

import api.MarketData;
import api.MarketOrder;
import api.OrderProperties;
import api.Utils;
import logger.Logger;
import logger.Logger.LogType;
import strategy.IStrategy;
import strategy.StrategyProperties;

public class TestStrategy implements IStrategy {

	private boolean isKeepRunning = true;
	private StrategyProperties strategyProp;
	private double targetDistance = 0.003;
	private boolean isActivePosition = false;
	private double tradePrice;
	
	private boolean showLog = false;	// DELETE ME!
	private int counter ; // ME TOO
	
	public TestStrategy(StrategyProperties strategyProp) {
		Date date = new Date();
		this.strategyProp = strategyProp;
		this.strategyProp.setStrategyName(strategyProp.getStrategyName() + "   =>  " + date.toString());
		Logger logger = Logger.getInstance();
		logger.log(LogType.GENERAL, "inside strategy constructor");
	}

	@Override
	public void run() {
		System.out.println("we made it!");
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


	@Override
	public boolean isReadyToStart() {
		if(MarketData.collateralReport != null && MarketData.assetsDataReady){
			isKeepRunning = true;
			return true;
		}
		return false;
	}

	@Override
	public void stopStrategy() {
		isKeepRunning  = false;
	}

	private boolean isTargetBreached(){
		double lastPrice = MarketData.assetData.get(0).getClose();
		double lowerBoundry = (1 - this.targetDistance) * this.tradePrice;
		double upperBoundry = (1 + this.targetDistance) * this.tradePrice;
		if(showLog){
			showLog = false;
			logger.log(LogType.GENERAL, "stop: "+ Utils.toFixed(lowerBoundry, 4) + "  , profit: "+ Utils.toFixed(upperBoundry, 4));
		}
		if(lastPrice > upperBoundry || lastPrice < lowerBoundry) return true;
		return false;
	}

}

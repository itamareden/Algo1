package strategy;

import api.Trade;
import logger.Logger;

public interface IStrategy extends Runnable {
	Trade trade = Trade.getInstance();
	Logger logger = Logger.getInstance();
	public void stopStrategy();
	public boolean isReadyToStart();
}

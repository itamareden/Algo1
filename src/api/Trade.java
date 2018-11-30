package api;


import java.util.ArrayList;
import java.util.List;

import com.fxcm.external.api.util.MessageGenerator;
import com.fxcm.fix.FXCMOrdStatusFactory;
import com.fxcm.fix.IFXCMOrdStatus;
import com.fxcm.fix.ISide;
import com.fxcm.fix.Instrument;
import com.fxcm.fix.SideFactory;
import com.fxcm.fix.TimeInForceFactory;
import com.fxcm.fix.posttrade.CollateralReport;
import com.fxcm.fix.trade.ExecutionReport;
import com.fxcm.fix.trade.OrderSingle;

import logger.Logger;
import logger.Logger.LogType;

public class Trade {
	
	private static Trade trade;
	private Logger logger;
	private List<Order> ordersList;
	static IFXCMOrdStatus [] pendingOrderStatuses = {
			FXCMOrdStatusFactory.WAITING,
			FXCMOrdStatusFactory.INPROCESS,
			FXCMOrdStatusFactory.EXECUTING
	};
	
	
	private Trade(){
		this.logger = Logger.getInstance();
		this.ordersList = new ArrayList<>();
	}
	
	public static Trade getInstance(){
		if(trade == null){
			trade = new Trade();
		}
		return trade;
	}
	
	
	public boolean sendMarketOrder(MarketOrder marketOrder) throws InterruptedException{
		fireMarketOrder(marketOrder);
		boolean isOrderConfirmed = checkIfMarketOrderConfirmed(marketOrder);
		return isOrderConfirmed;
	}
	
	private String fireMarketOrder(MarketOrder marketOrder){
		String messageId = "";
		String account = MarketData.collateralReport.getAccount();
		double quantity = marketOrder.getQuantity();
		ISide direction = marketOrder.getDirection() == OrderProperties.Direction.LONG ? SideFactory.BUY : SideFactory.SELL;
		String asset = marketOrder.getAsset();
		OrderSingle orderSingle = MessageGenerator.generateMarketOrder(account, quantity,direction, asset, marketOrder.getId());
		orderSingle.setTimeInForce(TimeInForceFactory.FILL_OR_KILL);
	    try {
	    	messageId = Status.gateway.sendMessage(orderSingle);
	    	marketOrder.setFXCMOrderId(messageId);
	    	marketOrder.setStatus(FXCMOrdStatusFactory.INPROCESS); 		
	    	ordersList.add(marketOrder);
	    	logger.log(LogType.ORDER, marketOrder);
	    } 
	    catch (Exception e) {
	        e.printStackTrace();
	    }
	    return messageId;
	}
	
	private boolean checkIfMarketOrderConfirmed(MarketOrder marketOrder) throws InterruptedException{
		int isExecutionNullCounter = 20;
		int isOrderNotUpdatedCounter = 10;
		int sleepTime = 500;
		while(MarketData.executionReport == null && isExecutionNullCounter > 0){
			Thread.sleep(sleepTime);
			isExecutionNullCounter--;
			if(isExecutionNullCounter == 0 && MarketData.executionReport == null){
				logger.log(LogType.ERROR, "Market order execution not confirmed. Didn't receive execution report within " 
						+ sleepTime * isExecutionNullCounter + " seconds.");
				return false;
			}
		}
		String orderId = marketOrder.getId();
		while(!MarketData.executionReport.getSecondaryClOrdID().equals(orderId) && isOrderNotUpdatedCounter > 0){
			Thread.sleep(sleepTime);
			isOrderNotUpdatedCounter--;
			if(isOrderNotUpdatedCounter == 0 && !MarketData.executionReport.getSecondaryClOrdID().equals(orderId)){
				logger.log(LogType.ERROR, "Market order not confirmed. Order didn't update properly within " 
						+ sleepTime * isOrderNotUpdatedCounter + " seconds.");
				return false;
			}
		}
		String executionObjOrderId = MarketData.executionReport.getClOrdID();
		if(Utils.safeEquals(marketOrder.getFXCMOrderId(), executionObjOrderId)){
			while(isStatusPending(MarketData.executionReport.getFXCMOrdStatus())){
				Thread.sleep(1000);
			}
			if(MarketData.executionReport.getFXCMOrdStatus() == FXCMOrdStatusFactory.EXECUTED){
				marketOrder.setStatus(FXCMOrdStatusFactory.EXECUTED);
				marketOrder.setPrice(MarketData.executionReport.getPrice());
				logger.log(LogType.EXECUTION, marketOrder);
				return true;
			}
			else{
				logger.log(LogType.ERROR, "Market order wasn't executed");
				return false;
			}
		}
		else{
			return false;
		}
	}
	
	public List<Order> getAllOrders() {
		return ordersList;
	}
	
	public List<Order> getAllOrdersForAsset(String asset) throws Exception{
		if(!Assets.isContainsAsset(asset)) throw new Exception("cannot retrieve orders for: " + asset + ". asset is not valid.");
		List<Order> orders = new ArrayList<Order>();
		for(Order order : ordersList){
			if(order.getAsset().equals(asset))
				orders.add(order);
		}
		return orders;
	}
	
	public List<Order> getAllExecutions() {
		List<Order> executions = new ArrayList<Order>();
		for(Order order : ordersList){
			if(order.getStatus() == FXCMOrdStatusFactory.EXECUTED)
				executions.add(order);
		}
		return executions;
	}
	
	public List<Order> getAllExecutionsForAsset(String asset) {
		List<Order> executions = new ArrayList<Order>();
		for(Order order : ordersList){
			if(order.getStatus() == FXCMOrdStatusFactory.EXECUTED && order.getAsset().equals(asset))
				executions.add(order);
		}
		return executions;
	}
	
	
	public static boolean isStatusPending(IFXCMOrdStatus orderStatus){
		for( IFXCMOrdStatus statusInArr : pendingOrderStatuses){
			if(statusInArr == orderStatus) return true;
		}
		return false;
	}
	

}

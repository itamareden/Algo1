package api;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Set;

import com.fxcm.fix.posttrade.PositionReport;

import logger.Logger;

public class Account {
	
	public static Account account = null;
	private Logger logger;
	
	private Account(){
		this.logger = Logger.getInstance();
	}
	
	public static Account getInstance(){
		if(account == null){
			account = new Account();
		}
		return account;
	}
	
	public List<Position> getOpenPositions() {
		  List<Position> openPositionsList = new ArrayList<Position>();
		  try {
			  System.out.println(Status.gateway != null && Status.gateway.isConnected() && MarketData.collateralReport !=null);
			  if(Status.gateway != null && Status.gateway.isConnected() && MarketData.collateralReport !=null){
				  int counter = 0;
				  MarketData.positionReportCounter = 0;
				  long accountId = Long.parseLong(MarketData.collateralReport.getAccount());
				  String requestId = Status.gateway.requestOpenPositions(accountId);
				  int iterationsCounter = 0;
				  while(iterationsCounter < 20 && (MarketData.positionReport == null || !Utils.safeEquals(MarketData.positionReport.getRequestID(),requestId))){
					  iterationsCounter++;
					  counter++;
					  Thread.sleep(250);
					  if(MarketData.positionReport != null && Utils.safeEquals(MarketData.positionReport.getRequestID(),requestId)){
						  int totalOpenPositions = MarketData.positionReport.getTotalNumPosReports();
						  iterationsCounter = 0;
						  while(iterationsCounter < 20 && MarketData.positionReportCounter < totalOpenPositions){
							  Thread.sleep(250);
							  counter++;
						  }
					  }
				  }
				  System.out.println(counter);
				  List<PositionReport> openPositions = new ArrayList<>();
				  for (PositionReport position : MarketData.positionsList) {
					  if(Utils.safeEquals(position.getRequestID(), requestId)){
						  openPositions.add(position);
					  }
				  }
				  if(openPositions.size() > 0 && openPositions.size() == openPositions.get(0).getTotalNumPosReports()){
					  int positionsListSize = MarketData.positionsList.size(); 
					  for (int i = 0; i < positionsListSize ; i++) {	// clean positionsList
						PositionReport pr = MarketData.positionsList.get(i);
						if(Utils.safeEquals(pr.getRequestID(), requestId)){
							MarketData.positionsList.remove(i);
							i--;
							positionsListSize--;
						}
					}
				  }
				  HashMap<String, Position> openPositionsSummary = new HashMap<String, Position>();
				  for (PositionReport transaction : openPositions) {
					  String assetSymbol = transaction.getInstrument().getSymbol();
					  Double quantity = transaction.getPositionQty().getQty();
					  Double tradeQuantity = transaction.getPositionQty().getSide().getDesc().equals("Buy") ? quantity : -quantity;
					  Double tradePrice = transaction.getSettlPrice(); 
					  if(openPositionsSummary.containsKey(assetSymbol)){
						  Position positionInMap = openPositionsSummary.get(assetSymbol);
						  Double oldPositionQuantity = positionInMap.getQuantity();
						  positionInMap.setQuantity(oldPositionQuantity + tradeQuantity);
						  Double oldAvgPrice = positionInMap.getAveragePrice();
						  Double updatedAvgPrice = (oldAvgPrice * oldPositionQuantity + tradeQuantity * tradePrice) / (oldPositionQuantity + tradeQuantity);
						  positionInMap.setAveragePrice(updatedAvgPrice);
					  }
					  else{
						  Position position = new Position(assetSymbol, tradeQuantity, tradePrice);
						  openPositionsSummary.put(assetSymbol, position);
					  }
				  }
				  Set<String> keys = openPositionsSummary.keySet();
				  for (String key : keys) {
					  Position position = openPositionsSummary.get(key);
					  position.setAveragePrice(Utils.toFixed(position.getAveragePrice(), 4));
					  openPositionsList.add(position);
				  }
				  return openPositionsList;
			  }
		  } 
		  catch (Exception e) {
			  e.printStackTrace();
		  }
		  return openPositionsList;
	  }

}

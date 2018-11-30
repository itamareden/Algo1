package servlets;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;

import api.MarketData;
import api.MarketOrder;
import api.OrderProperties;
import api.OrderProperties.Direction;
import api.Status;
import api.Trade;
import api.Utils;
import logger.Logger;
import logger.Logger.LogType;
import strategies.MarketStrategy;
import strategies.TestStrategy;
import strategy.IStrategy;
import strategy.Strategy;
import strategy.StrategyManager;
import strategy.StrategyProperties;


@Path("/strategy")
public class StrategySrvlt {
  @GET
  @Path("start/{asset: [A-Z]{3}\\/[A-Z]{3}}/{interval}/{dataSize}") 
  public String startStrategy(@PathParam("asset") String asset, 
		  @PathParam("interval") String interval, @PathParam("dataSize") String dataSize) {
	  
	  if(Status.gateway != null && Status.gateway.isConnected()){
		  try{
			  StrategyProperties strategyP = new StrategyProperties("Market_Strategy", asset, dataSize, interval);
			  MarketData marketData = MarketData.getInstance();
			  marketData.setStrategyProp(strategyP);
			  StrategyManager strategyManager = StrategyManager.getInstance();
			  if(strategyManager.runStrategy("Market_Strategy", strategyP)){
				  Logger logger = Logger.getInstance();			// delete me. initialize in constructor.
				  logger.log(LogType.GENERAL, "Started the strategy ");	// delete me too :)
				  return "success";
			  }
			  else{
				  return "There is an active strategy already";
			  }
		  } 
		  catch (Exception e) {
			  e.printStackTrace();
			  return e.getMessage();
		  }
	  }
	  return "No Connection";
  }
  
  
  @GET
  @Path("stop") 
  public String stopStrategy(){
	  try{
		  StrategyManager strategyManager = StrategyManager.getInstance();
		  Strategy strategy = strategyManager.getCurrentStrategy();
		  if(strategy != null){
			  strategyManager.stopStrategy();
//			  strategy.stopStrategy();			check why it's not working
		  }
		  return "stopped";
	  }
	  catch(Exception e){
		  return e.getMessage();
	  }
  }
  
  
  @GET
  @Path("activeStrategy") 
  public String getStrategy(){
	  try{
		  StrategyManager strategyManager = StrategyManager.getInstance();
		  Strategy strategy = strategyManager.getCurrentStrategy();
		  if(strategy == null) return "There is no active strategy";
		  else return strategy.toString();
	  }
	  catch(Exception e){
		  return e.getMessage();
	  }
  }
  
  @GET
  @Path("marketData") 
  public String getMarketData(){		// SHOULD MOVE TO A NEW SERVLET FOR MARKET DATA OR DELETED
	  try{
		  System.out.println(MarketData.assetData.size());
		  if(MarketData.assetData.size() == 0) return "There is no asset data to return ";
		  else return MarketData.assetData.toString();
	  }
	  catch(Exception e){
		  return e.getMessage();
	  }
  }
  

}



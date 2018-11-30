package servlets;

import java.util.List;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;

import api.MarketData;
import api.Status;
import api.Utils;
import logger.Log;
import logger.Logger;
import strategies.MarketStrategy;

import javax.ws.rs.PathParam;

@Path("/logs")
public class Logs {

  @GET
  @Produces(MediaType.APPLICATION_JSON)
  public List<Log> getAllLogs() {
	  Logger logger = Logger.getInstance();
	  List<Log> logsList = logger.getAllLogs();
	  return logsList;
  }
  
  @GET
  @Path("jjj") 
  public String jjj()	{	// SHOULD MOVE TO A NEW SERVLET FOR EXECUTIONS
	  boolean isSuccess = false;
	  try{
//		  Trade trade = Trade.getInstance();
//		  Direction direction = directionParam.equals("LONG") ? OrderProperties.Direction.LONG : OrderProperties.Direction.SHORT;
//		  int amount = Integer.parseInt(amountParam);
//		  MarketOrder order = new MarketOrder(asset, direction, amount);
//		  isSuccess = trade.sendMarketOrder(order);
		  return isSuccess ? "Success" : "Failed";
	  }
	  catch(Exception e){
		  return e.getMessage();
	  }
  }
  
  
  
  
}
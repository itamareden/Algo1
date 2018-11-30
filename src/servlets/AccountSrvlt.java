package servlets;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fxcm.fix.PositionQty;
import com.fxcm.fix.posttrade.CollateralReport;
import com.fxcm.fix.posttrade.PositionReport;

import api.Account;
import api.MarketData;
import api.MarketOrder;
import api.OrderProperties;
import api.OrderProperties.Direction;
import api.Position;
import api.Status;
import api.Trade;
import api.Utils;
import logger.Logger;
import strategies.MarketStrategy;
import strategy.Strategy;

import javax.ws.rs.PathParam;

@Path("/account2")
public class AccountSrvlt {

  // This method is called if TEXT_PLAIN is request
  @GET
  @Path("equity") 
  public double getEquity() {
	  double netEquity = 0;
	  try {
		  if(Status.gateway != null && MarketData.collateralReport != null){
			  String requestId = Status.gateway.requestAccountByName(MarketData.collateralReport.getAccount());
			  int counter = 10;
			  while(!Utils.safeEquals(requestId, MarketData.collateralReport.getRequestID()) && counter > 0){
				  counter--;
				  Thread.sleep(1000);
			  }
			  netEquity = MarketData.collateralReport.getEndCash();
			  return netEquity;
		  }
	  } 
	  catch (Exception e) {
		  e.printStackTrace();
	  }
	  return netEquity;
  }
  
  
  @GET
  @Path("openPositions") 
  @Produces(MediaType.APPLICATION_JSON)
  public List<Position> getOpenPositions() {System.out.println("yep");
	  Account account = Account.getInstance();
	  List<Position> openPositions = account.getOpenPositions();
	  return openPositions;
  }
  
  
  
  
  @GET
  @Path("sendMarketOrder/{asset: [A-Z]{3}\\/[A-Z]{3}}/{direction}/{amount}") 
  public String trade(@PathParam("asset") String asset, @PathParam("direction") String directionParam, 
		  @PathParam("amount") String amountParam){
	  
  
	  boolean isSuccess = false;
	  try{
		  Trade trade = Trade.getInstance();
		  Direction direction = directionParam.equals("LONG") ? OrderProperties.Direction.LONG : OrderProperties.Direction.SHORT;
		  int amount = Integer.parseInt(amountParam);
		  MarketOrder order = new MarketOrder(asset, direction, amount);
		  isSuccess = trade.sendMarketOrder(order);
		  return isSuccess ? "Success" : "Failed";
	  }
	  catch(Exception e){
		  return e.getMessage();
	  }
  }
	  
  
  
  

}
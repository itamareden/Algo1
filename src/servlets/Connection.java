package servlets;

import java.util.Iterator;
import java.util.Map;

import javax.swing.plaf.synth.SynthSeparatorUI;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;

import api.MarketData;
import api.Status;
import api.Utils;
import logger.Logger;
import logger.Logger.LogType;
import strategies.MarketStrategy;
import strategy.Strategy;
import strategy.StrategyProperties;

import javax.ws.rs.PathParam;

@Path("/connection")
public class Connection {

  // This method is called if TEXT_PLAIN is request
  @GET
  @Path("start/{pass}/{asset: [A-Z]{3}\\/[A-Z]{3}}/{interval}/{dataSize}") 
  public String login(@PathParam("pass") String password, @PathParam("asset") String asset, 
		  @PathParam("interval") String interval, @PathParam("dataSize") String dataSize) {
	  if(password.equals("LOGIN")){
		  try {
			  Status status = Status.getInstance();
			  MarketData marketData = MarketData.getInstance(); 
			  status.login(marketData, status);
			  return "Logged In";
			} 
			catch (Exception e) {
				e.printStackTrace();
				return e.getMessage();
			}
	  }
	  else{
		  return "Password is incorrect";  
	  }
  }
  
  
  @GET
  @Path("end/{pass}") 
  public String logout(@PathParam("pass") String password){
	  if(password.equals("LOGOUT")){
		  try {
			  Status status = Status.getInstance();
			  MarketData marketData = MarketData.getInstance();
			  status.logout(marketData, status);
			  return "logged out";
			} 
			catch (Exception e) {
				e.printStackTrace();
				return e.getMessage();
			}
	  }
	  else{
		  return "Password is incorrect";  
	  }
  }
  
  
  @GET
  @Path("threads") 
  public String getThreads(){
	  return Utils.getAllThreadsStat();
  }

  // This method is called if XML is request
//  @GET
//  @Produces(MediaType.TEXT_XML)
//  public String sayXMLHello() {
//    return "<?xml version=\"1.0\"?>" + "<hello> Hello Jersey" + "</hello>";
//  }
//
//  // This method is called if HTML is request
//  @GET
//  @Produces(MediaType.TEXT_HTML)
//  public String sayHtmlHello() {
//    return "<html> " + "<title>" + "Hello Jersey" + "</title>"
//        + "<body><h1>" + "Hello Jersey" + "</body></h1>" + "</html> ";
//  }
  

}
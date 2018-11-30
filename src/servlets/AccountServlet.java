package servlets;

import java.util.List;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;

import api.Account;
import api.MarketData;
import api.Position;
import api.Status;
import api.Utils;

import javax.ws.rs.PathParam;

@Path("/account")
public class AccountServlet {

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
  
  

}
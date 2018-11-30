package api;


import com.fxcm.external.api.transport.FXCMLoginProperties;
import com.fxcm.external.api.transport.GatewayFactory;
import com.fxcm.external.api.transport.IGateway;
import com.fxcm.external.api.transport.listeners.IGenericMessageListener;
import com.fxcm.external.api.transport.listeners.IStatusMessageListener;
import com.fxcm.messaging.ISessionStatus;

import logger.Logger;
import logger.Logger.LogType;


public class Status implements IStatusMessageListener{
	
	private static Status status = null;  
	private Logger logger = null;
	private final String server = "http://www.fxcorporate.com/Hosts.jsp";
	private final String username = "d101537828";
	private final String password = "1234";
	private final String terminal = "Demo";
	private FXCMLoginProperties login = null;
	public static IGateway gateway;
	private String currentRequest;
	  
	public String accounts;
	
	private Status() {
		this.logger = Logger.getInstance();
		this.login = new FXCMLoginProperties(username, password, terminal, server);
	}
	
	public static Status getInstance(){
		if(status == null){
			status = new Status();
		}
		return status;
	}
	 
	  
//	public void init(String username, String password, String terminal) {
//		// check if the login object was already initialized
//		if(this.login != null) return;
//		// create a local LoginProperty
//	    this.login = new FXCMLoginProperties(username, password, terminal, server);
//	}


//	public void init(String[] args){
//		// call the proper init method
//		this.init(args[0], args[1], args[2]);
//	}
	  
	  

	  public void login(IGenericMessageListener genericMessageListener, IStatusMessageListener statusMessageListener) throws Exception {
		  
	      // if the gateway has not been defined
	      if(gateway == null){
	    	  // assign it to a new gateway created by the factory
		      gateway = GatewayFactory.createGateway();
	      }
	      // register the generic message listener with the gateway
	      gateway.registerGenericMessageListener(genericMessageListener);
	      // register the status message listener with the gateway
	      gateway.registerStatusMessageListener(statusMessageListener);
	      // if the gateway has not been connected
	      if(!gateway.isConnected()){
	        // attempt to login with the local login properties
	        gateway.login(this.login);
	      }
	      else{
	        // attempt to re-login to the api
	        gateway.relogin();
	      }
	      // must call this after login as part of handshake process otherwise no messages will be received
	      currentRequest = Status.gateway.requestTradingSessionStatus();
	      accounts = Status.gateway.requestAccounts();
	  }
	  

	  /**
	   * Attempt to logout, removing the supplied listeners prior to disconnection
	   * 
	   * @param genericMessageListener - the listener object for trading events
	   * @param statusMessageListener - the listener object for status events
	   */
	  public void logout(IGenericMessageListener genericMessageListener, IStatusMessageListener statusMessageListener){
		  
		   // attempt to logout of the api
		   gateway.logout();
		   // remove the generic message listener, stop listening to updates
		   gateway.removeGenericMessageListener(genericMessageListener);
		   // remove the status message listener, stop listening to status changes
		   gateway.removeStatusMessageListener(statusMessageListener);
	  }
	  
	  @Override 
	  public void messageArrived(ISessionStatus status){
		  	// check to the status code
		    if(status.getStatusCode() == ISessionStatus.STATUSCODE_ERROR ||
		       status.getStatusCode() == ISessionStatus.STATUSCODE_DISCONNECTING ||
		       status.getStatusCode() == ISessionStatus.STATUSCODE_CONNECTING ||
		       status.getStatusCode() == ISessionStatus.STATUSCODE_CONNECTED ||
		       status.getStatusCode() == ISessionStatus.STATUSCODE_CRITICAL_ERROR ||
		       status.getStatusCode() == ISessionStatus.STATUSCODE_EXPIRED ||
		       status.getStatusCode() == ISessionStatus.STATUSCODE_LOGGINGIN ||
		       status.getStatusCode() == ISessionStatus.STATUSCODE_LOGGEDIN ||
		       status.getStatusCode() == ISessionStatus.STATUSCODE_PROCESSING ||
		       status.getStatusCode() == ISessionStatus.STATUSCODE_DISCONNECTED)
		    {
		      // display status message
		    	logger.log(LogType.CONNECTION, status.getStatusMessage());
		    	System.out.println(status.getStatusMessage());
		    }
	  }
	    

}

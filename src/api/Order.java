package api;

import java.util.Calendar;

import com.fxcm.fix.FXCMOrdStatusFactory;
import com.fxcm.fix.IFXCMOrdStatus;
import com.fxcm.fix.Instrument;
import api.OrderProperties.Direction;

public abstract class Order {
	
	private String asset;
	private String id;
	private Direction direction;
	private int quantity;
	private double price;
	private IFXCMOrdStatus status;
	private float takeProfit;
	private float stopLoss;
	private String FXCMOrderId;
	private static int ordersCounter = 0;
	
	public Order(String asset, Direction direction, int quantity) throws Exception {
		if(Assets.isContainsAsset(asset)){
			this.asset = asset;
		}
		else{
			throw new Exception("Error. Asset symbol is not a valid symbol.");
		}
		this.direction = direction;
		this.quantity = quantity;
		this.id = setOrderId();
	}
	
	public Order(String asset, Direction direction, int quantity, float takeProfit, float stopLoss) throws Exception {
		if(Assets.isContainsAsset(asset)){
			this.asset = asset;
		}
		else{
			throw new Exception("Error. Asset symbol is not a valid symbol.");
		}
		this.direction = direction;
		this.quantity = quantity;
		this.takeProfit = takeProfit;
		this.stopLoss = stopLoss;
		this.id = setOrderId();
	}
	
	
	public int getQuantity() {
		return quantity;
	}

	public void setQuantity(int quantity) {
		this.quantity = quantity;
	}

	public IFXCMOrdStatus getStatus() {
		return status;
	}

	public void setStatus(IFXCMOrdStatus status) {
		this.status = status;
	}

	public float getTakeProfit() {
		return takeProfit;
	}

	public void setTakeProfit(float takeProfit) {
		this.takeProfit = takeProfit;
	}

	public float getStopLoss() {
		return stopLoss;
	}

	public String getFXCMOrderId() {
		return FXCMOrderId;
	}

	public void setFXCMOrderId(String fXCMOrderId) {
		FXCMOrderId = fXCMOrderId;
	}

	public void setStopLoss(float stopLoss) {
		this.stopLoss = stopLoss;
	}

	public String getAsset() {
		return asset;
	}

	public String getId() {
		return id;
	}

	public Direction getDirection() {
		return direction;
	}
	
	public double getPrice() {
		return price;
	}

	public void setPrice(double price) {
		this.price = price;
	}

	private String setOrderId(){
		String counter = Integer.toString(++ordersCounter);
		int desiredCounterDigits = 4;
		for(int i = counter.length(); i < desiredCounterDigits; i++) {
			counter = "0" + counter;
		}
		String year = Integer.toString((Calendar.getInstance().get(Calendar.YEAR)));
		String weekOfYear = Integer.toString(Calendar.getInstance().get(Calendar.WEEK_OF_YEAR));
		if(weekOfYear.length() == 1) weekOfYear = "0" + weekOfYear;
		String id = year + weekOfYear + counter;
		
		return id;
	}

	@Override
	public String toString() {
		return "Order [asset=" + asset + ", id=" + id + ", direction=" + direction + ", quantity=" + quantity
				+ ", price=" + price + ", status=" + status + ", takeProfit=" + takeProfit + ", stopLoss=" + stopLoss + ", FXCMOrderId="
				+ FXCMOrderId + "]";
	}

	

}

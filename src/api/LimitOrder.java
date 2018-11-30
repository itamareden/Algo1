package api;

import com.fxcm.fix.Instrument;
import api.OrderProperties.Direction;

public class LimitOrder extends Order {
	
	private double priceLimit;

	public LimitOrder(String asset, Direction direction, int quantity, float price) throws Exception {
		super(asset, direction, quantity);
		this.priceLimit = price;
	}
	
	public LimitOrder(String asset, Direction direction, int quantity, float stopLoss, float takeProfit, float price) throws Exception{
		super(asset, direction, quantity);
		this.priceLimit = price;
		if(direction.equals(OrderProperties.Direction.LONG) && (price < stopLoss || price > takeProfit)){
			throw new Exception("Error generating long limit order. Price must be higher than stop loss and lower than take profit.");
		}
		else if(direction.equals(OrderProperties.Direction.SHORT) && (price > stopLoss || price < takeProfit)){
			throw new Exception("Error generating short limit order. Price must be lower than stop loss and higher than take profit.");
		}
	}

	public double getLimitPrice() {
		return priceLimit;
	}

	public void setLimitPrice(double price) {
		this.priceLimit = price;
	}
	
	
}

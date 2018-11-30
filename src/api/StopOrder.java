package api;

import com.fxcm.fix.Instrument;

import api.OrderProperties.Direction;

public class StopOrder extends Order {
	
	private float stopPrice;

	public StopOrder(String asset, Direction direction, int quantity, float price) throws Exception {
		super(asset, direction, quantity);
		this.stopPrice = price;
	}
	
	public StopOrder(String asset, Direction direction, int quantity, float stopLoss, float takeProfit, float price) throws Exception{
		super(asset, direction, quantity);
		this.stopPrice = price;
		if(direction.equals(OrderProperties.Direction.LONG) && (price < stopLoss || price > takeProfit)){
			throw new Exception("Error generating long stop order. Price must be higher than stop loss and lower than take profit.");
		}
		else if(direction.equals(OrderProperties.Direction.SHORT) && (price > stopLoss || price < takeProfit)){
			throw new Exception("Error generating short stop order. Price must be lower than stop loss and higher than take profit.");
		}
	}

	public float getStopPrice() {
		return stopPrice;
	}

	public void setStopPrice(float stopPrice) {
		this.stopPrice = stopPrice;
	}

}

package api;

import api.OrderProperties.Direction;

public class MarketOrder extends Order {
	

	public MarketOrder(String asset, Direction direction, int quantity) throws Exception {
		super(asset, direction, quantity);
	}
	
	public MarketOrder(String asset, Direction direction, int quantity, float stopLoss, float takeProfit) throws Exception {
		super(asset, direction, quantity);
	}

}

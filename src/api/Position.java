package api;

public class Position {
	
	private String assetName;
	private double quantity;
	private double averagePrice;
	
	public Position(String assetName, double quantity, double averagePrice){
		this.assetName = assetName;
		this.quantity = quantity;
		this.averagePrice = averagePrice;
	}

	public String getAssetName() {
		return assetName;
	}

	public double getQuantity() {
		return quantity;
	}

	public void setQuantity(double quantity) {
		this.quantity = quantity;
	}
	
	public double getAveragePrice() {
		return averagePrice;
	}

	public void setAveragePrice(double averagePrice) {
		this.averagePrice = averagePrice;
	}

	@Override
	public String toString() {
		return "Position [assetName=" + assetName + ", quantity=" + quantity + ", averagePrice=" + averagePrice + "]";
	}


}

package api;


import java.util.Comparator;
import java.util.Date;

import com.fxcm.fix.UTCTimestamp;



public class Candlestick implements Comparator<Candlestick>{
	
	
	private UTCTimestamp openTime;	// maybe should be Date...
	private double open;
	private double low;
	private double high;
	private double close;

	
	public Candlestick(UTCTimestamp openTime, double open, double low, double high, double close) {
		
		this.openTime = openTime;
		this.open = open;
		this.low = low;
		this.high = high;
		this.close = close;
	}
	
	
	public Candlestick() {
		
	}
	
	

	public UTCTimestamp getOpenTime() {
		return openTime;
	}

	public void setOpenTime(UTCTimestamp openTime) {
		this.openTime = openTime;
	}

	public double getOpen() {
		return open;
	}

	public void setOpen(double open) {
		this.open = open;
	}

	public double getLow() {
		return low;
	}

	public void setLow(double low) {
		this.low = low;
	}

	public double getHigh() {
		return high;
	}

	public void setHigh(double high) {
		this.high = high;
	}

	public double getClose() {
		return close;
	}

	public void setClose(double close) {
		this.close = close;
	}
	

	@Override
	public String toString() {
		return "CandleStick [openTime=" + openTime + ", open=" + open + ", low=" + low + ", high=" + high + ", close=" + close
				+ "]";
	}


	@Override
	public int compare(Candlestick candle1, Candlestick candle2) {
		if(candle1.getOpenTime().getTime() < candle2.getOpenTime().getTime()) return 1;
		if(candle1.getOpenTime().getTime() > candle2.getOpenTime().getTime()) return -1;
		return 0;
	}

	
	

}


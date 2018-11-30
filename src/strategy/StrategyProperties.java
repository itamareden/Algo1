package strategy;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import com.fxcm.fix.FXCMTimingIntervalFactory;
import com.fxcm.fix.IFXCMTimingInterval;
import com.fxcm.fix.UTCTimestamp;

import api.Assets;

public class StrategyProperties {
	
	private String strategyName;
	private final List<String> assets;
	private int dataSize;
	private IFXCMTimingInterval timeInterval;
	private long timeIntervalInMilliseconds;
	
	public StrategyProperties(String strategyName, String assets, String dataSize, String timeInterval) throws Exception {
		this.strategyName = strategyName;
		this.assets = this.convertAssetsStringIntoAssetsArray(assets);
		this.dataSize = this.getValidDataSize(dataSize);
		UTCTimestamp timestamp = new UTCTimestamp();
		this.timeInterval = this.getValidTimeInterval(timeInterval);
		this.timeIntervalInMilliseconds = this.timeInterval.getDuration(timestamp);
	}
	
	
	public String getStrategyName() {
		return strategyName;
	}

	public List<String> getAssets() {
		return assets;
	}

	public int getDataSize() {
		return dataSize;
	}

	public IFXCMTimingInterval getTimeInterval() {
		return timeInterval;
	}
	
	public long getTimeIntervalInMilliseconds() {
		return timeIntervalInMilliseconds;
	}
	
	public void setStrategyName(String strategyName) {
		this.strategyName = strategyName;
	}


	private IFXCMTimingInterval getValidTimeInterval(String aInterval) throws Exception{
		String interval = formatTimeInterval(aInterval);
		Iterator intervalIterator = FXCMTimingIntervalFactory.getIntervals().iterator();
		while(intervalIterator.hasNext()){
			IFXCMTimingInterval currentInterval = (IFXCMTimingInterval) intervalIterator.next();
			if(currentInterval.toString().contains(interval)){
				return currentInterval;
			}
		}
		throw new Exception("Error. " + interval + " is not a valid time interval. valid format: 1 Min.");
	}
	
	private String formatTimeInterval(String aInterval){
		Matcher matcher = Pattern.compile("(\\d+)(?=[A-Za-z]+$)").matcher(aInterval);
		String timeInterval = matcher.replaceAll("$1 ");
		return timeInterval;
		
	}
	
	private int getValidDataSize(String dataSizeStr) throws Exception{
		int dataSize = Integer.parseInt(dataSizeStr);
		if(dataSize<0){
			throw new Exception("Error. data size cannot be negative.");
		}
		return dataSize;
	}
	
	private List<String> convertAssetsStringIntoAssetsArray(String assetsStrParam) throws Exception {
		/*
		 * regex to capture symbols only if they match an FXCM symbol pattern
		 * and are surrounded with a white space or beginning or end of a line
		 */
		List<String> assetsList = new ArrayList<>();
		Matcher matcher = Pattern.compile("(?<=(^|\\s+))[A-Z]{3}/[A-Z]{3}(?=(\\s+|$))").matcher(assetsStrParam);
		while (matcher.find()) {
			String assetSymbol = matcher.group();
			if (Assets.isContainsAsset(assetSymbol)) {
				assetsList.add(assetSymbol);
			}
		}
		if (assetsList.size() == 0) {
			throw new Exception("Assets symbols are not valid.");
		}
		return assetsList;
	}
	
	

}

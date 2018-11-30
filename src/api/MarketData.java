package api;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Set;
import java.util.TreeSet;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import com.fxcm.external.api.transport.listeners.IGenericMessageListener;
import com.fxcm.fix.IFixDefs;
import com.fxcm.fix.Instrument;
import com.fxcm.fix.SubscriptionRequestTypeFactory;
import com.fxcm.fix.UTCDate;
import com.fxcm.fix.UTCTimeOnly;
import com.fxcm.fix.UTCTimestamp;
import com.fxcm.fix.posttrade.CollateralReport;
import com.fxcm.fix.posttrade.PositionReport;
import com.fxcm.fix.pretrade.MarketDataRequest;
import com.fxcm.fix.pretrade.MarketDataRequestReject;
import com.fxcm.fix.pretrade.MarketDataSnapshot;
import com.fxcm.fix.pretrade.TradingSessionStatus;
import com.fxcm.fix.trade.ExecutionReport;
import com.fxcm.messaging.ITransportable;

import strategy.StrategyProperties;

public class MarketData implements IGenericMessageListener {
	
	private static MarketData marketData;
	private String historicDataRequest;
	public static final List<Candlestick> assetData = new ArrayList<>();	
	private UTCDate startDate; 
	private UTCTimeOnly startTime;
	private UTCTimestamp interimEndTime;
	private final HashMap<UTCTimestamp, MarketDataSnapshot> historicalData = new HashMap<>();
	public static boolean assetsDataReady = false;
	public static CollateralReport collateralReport;
	public static PositionReport positionReport;
	public static int positionReportCounter;
	public static List<PositionReport> positionsList = new ArrayList<>();
	public static ExecutionReport executionReport;
	private StrategyProperties strategyProp;
	private boolean readyToMineHistoricData;
	
	private MarketData(){}
	
	public static MarketData getInstance(){
		if(marketData == null){
			marketData = new MarketData();
		}
		return marketData;
	}

//	public void init(String assetsStr, String timeIntervalStr, String dataSizeStr) throws Exception {
//		if(initialized) {
//			return ;
//		}
//		this.convertAssetsStringIntoAssetsArray(assetsStr);
//		UTCTimestamp timestamp = new UTCTimestamp();
//		this.timeInterval = this.getValidTimeInterval(timeIntervalStr);
//		this.timeIntervalInMilliseconds = timeInterval.getDuration(timestamp);
//		this.dataSize = this.getValidDataSize(dataSizeStr);
//		Calendar calendar = Calendar.getInstance();
//		calendar.roll(Calendar.YEAR, -1); // 1 year back, just in case I need that much data...
//		this.startDate = new UTCDate(calendar.getTime());
//		this.startTime = new UTCTimeOnly(calendar.getTime());
//		this.initialized = true;
//	}

	@Override
	public void messageArrived(ITransportable message) {

		// decide which child function to send an cast instance of the message
		try {
			if (message instanceof MarketDataSnapshot)
				messageArrived((MarketDataSnapshot) message);
			else if (message instanceof MarketDataRequestReject)
				messageArrived((MarketDataRequestReject) message);
			else if (message instanceof TradingSessionStatus)
				messageArrived((TradingSessionStatus) message);
			else if (message instanceof CollateralReport)
				messageArrived((CollateralReport) message);
			else if (message instanceof PositionReport)
				messageArrived((PositionReport) message);
			else if (message instanceof ExecutionReport)
				messageArrived((ExecutionReport) message);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public void messageArrived(TradingSessionStatus tss) {
		/* First message of this listener goes here */
//		sendHistoricalDataRequest(null);
	}

	public void messageArrived(CollateralReport cr) {
		collateralReport = cr;
	}

	public void messageArrived(PositionReport pr) {
		if(pr.getRequestID() != null){
			/*this PR is a response to a request for all of the open positions. a PR with a requestId == null
			is in response to a new trade which was just executed */
			positionReportCounter++;
		}
		positionReport = pr;
		positionsList.add(positionReport);
	}

	public void messageArrived(ExecutionReport er) {
		System.out.println("execution");
		System.out.println(er);
		executionReport = er;

	}

	public void messageArrived(MarketDataRequestReject mdrr) {
		// display note consisting of the reason the request was rejected
		System.out.println("Data rejected: " + mdrr.getMDReqRejReason());
	}

	public void messageArrived(MarketDataSnapshot mds) {
		try {
			if(strategyProp != null){	// strategy on
				if (assetsDataReady) {
//					System.out.println("finished mining...");
					if (strategyProp.getAssets().contains(mds.getInstrument().getSymbol()) && isMessageTimeValid(mds)) {
						if (isCandleIntervalElapsed(mds.getOpenTimestamp())) {
							updateCandlesList(mds);
							System.out.println("updated data...");
						} 
						else {
							updateActiveCandle(mds.getBidClose());
						}
					}
				} 
				else{
					if(mds.getRequestID() != null && mds.getRequestID().equals(historicDataRequest)) {
						processAssetHistoricalData(mds);
					}
					else if(!readyToMineHistoricData){
						System.out.println("started mining");
						prepareHistoricDataRequest();
						sendHistoricalDataRequest(null);
					}
				}
			}
			
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	private void prepareHistoricDataRequest() {
		Calendar calendar = Calendar.getInstance();
		calendar.roll(Calendar.YEAR, -1); // 1 year back, just in case I need that much data...
		this.startDate = new UTCDate(calendar.getTime());
		this.startTime = new UTCTimeOnly(calendar.getTime());
		readyToMineHistoricData = true;
	}

	public String sendRequest(ITransportable request) {
		try {
			// send the request message to the api.
			historicDataRequest = Status.gateway.sendMessage(request);
			// return the request id for authentication when messages would arrive from the API.
			return historicDataRequest;
		} catch (Exception e) {
			e.printStackTrace();
		}
		// if an error occurred, return no result
		return null;
	}
	
	public void setStrategyProp(StrategyProperties strategyProperties){
		if(this.strategyProp == null){
			this.strategyProp = strategyProperties;
		}
	}

//	private void convertAssetsStringIntoAssetsArray(String assetsStrParam) throws Exception {
//		/*
//		 * regex to capture symbols only if they match an FXCM symbol pattern
//		 * and are surrounded with a white space or beginning or end of a line
//		 */
//		Matcher matcher = Pattern.compile("(?<=(^|\\s+))[A-Z]{3}/[A-Z]{3}(?=(\\s+|$))").matcher(assetsStrParam);
//		while (matcher.find()) {
//			String assetSymbol = matcher.group();
//			if (Assets.isContainsAsset(assetSymbol)) {
//				this.assetsList.add(assetSymbol);
//			}
//		}
//		if (this.assetsList.size() == 0) {
//			throw new Exception("Assets symbols are not valid.");
//		}
//	}
	

	private boolean isCandleIntervalElapsed(UTCTimestamp lastQuoteTime) {
		long activeCandleOpenTime = assetData.get(0).getOpenTime().getTime();
		if (lastQuoteTime.getTime() - activeCandleOpenTime >= strategyProp.getTimeIntervalInMilliseconds()) {
			return true;
		}
		return false;
	}
	
	private boolean isMessageTimeValid(MarketDataSnapshot mds){
		// check if the message has accurate time. for some reason, the first messages that arrive after we obtained
		// the historical data contain old candles from a few hours back
		return mds.getOpenTimestamp().getTime() > assetData.get(0).getOpenTime().getTime();
	}

	private void updateCandlesList(MarketDataSnapshot mds) {
		Candlestick candlestick = createCandlestick(mds);
		assetData.remove(assetData.size() - 1);
		assetData.add(0, candlestick);
	}

	private void updateActiveCandle(double lastPrice) {
		Candlestick candlestick = assetData.get(0);
		candlestick.setClose(lastPrice);
		if (lastPrice > candlestick.getHigh()) {
			candlestick.setHigh(lastPrice);
		} 
		else if (lastPrice < candlestick.getLow()) {
			candlestick.setLow(lastPrice);
		}
	}

	private Candlestick createCandlestick(MarketDataSnapshot mds) {
		UTCTimestamp candleOpenTime = setCandleOpenTime(mds);
		double openPrice = mds.getBidOpen();
		Candlestick candlestick = new Candlestick(candleOpenTime, openPrice, openPrice, openPrice, openPrice);
		return candlestick;
	}
	
	private UTCTimestamp setCandleOpenTime(MarketDataSnapshot mds){
		UTCTimestamp candleOpenTime = mds.getOpenTimestamp();
		int extraSeconds = 0;
		String openTimeStr = mds.getTime().toString();	// we use the TimeOnly because for the REGEX we only need time and not date
		Matcher matcher = Pattern.compile("(?<=:)[0-9]{2}$").matcher(openTimeStr);
		while (matcher.find()) {
			extraSeconds = Integer.parseInt(matcher.group());
		}
		if(extraSeconds > 0){
			candleOpenTime.addDuration(extraSeconds * 1000 * -1);
		}
		return candleOpenTime;
	}
	

//	private Calendar getHistoricalDataStartingTime(int dataSize, long timeIntervalInMilliseconds) {
//		double dataNeededInMilliseconds = dataSize * timeIntervalInMilliseconds;
//		double MillisecondsInDay = 24 * 60 * 60 * 1000;
//		int tradingDaysToRollback = (int) Math.ceil(dataNeededInMilliseconds / MillisecondsInDay);
//		// we add 3 just in case we request the data on Sunday or Saturday, and
//		// to roundup because we multiply by 1.4
//		int daysToRollback = tradingDaysToRollback * 7 / 5 + 3;
//		Calendar calendar = Calendar.getInstance();
//		calendar.roll(Calendar.DAY_OF_YEAR, -daysToRollback);
//		return calendar;
//	}

	private void processAssetHistoricalData(MarketDataSnapshot mds) {
		historicalData.put(mds.getOpenTimestamp(), mds);
		if (this.interimEndTime == null) {
			// first message of this packet. the time stamp of this candle will
			// be used as the end date in the next packet.
			this.interimEndTime = mds.getOpenTimestamp();
		}
		if (mds.getFXCMContinuousFlag() == IFixDefs.FXCMCONTINUOUS_END) {
			// end of packet of 300 candles. send a new request for another 300 candles.
			sendHistoricalDataRequest(this.interimEndTime);
			this.interimEndTime = null;
		}
//		if (historicalData.size() == this.dataSize) {
		if (historicalData.size() == strategyProp.getDataSize()) {
			convertHistoricalRatesToCandleSticks();
			MarketData.assetsDataReady = true;	// from here on we will process live data
		}

	}

	private void sendHistoricalDataRequest(UTCTimestamp interimEndTime) {
		MarketDataRequest mdr = new MarketDataRequest();
		// set the subscription type to ask for only a snapshot of the history
		mdr.setSubscriptionRequestType(SubscriptionRequestTypeFactory.SNAPSHOT);
		// request the response to be formated FXCM style
		mdr.setResponseFormat(IFixDefs.MSGTYPE_FXCMRESPONSE);
		// set the intervale of the data candles
		mdr.setFXCMTimingInterval(strategyProp.getTimeInterval());
		// set the type set for the data candles
		mdr.setMDEntryTypeSet(MarketDataRequest.MDENTRYTYPESET_ALL);
		mdr.setFXCMStartDate(this.startDate);
		mdr.setFXCMStartTime(this.startTime);
//		mdr.addRelatedSymbol(new Instrument(this.assetsList.get(0)));
		mdr.addRelatedSymbol(new Instrument(strategyProp.getAssets().get(0)));
		if (interimEndTime != null) {
			mdr.setFXCMEndDate(new UTCDate(interimEndTime));
			mdr.setFXCMEndTime(new UTCTimeOnly(interimEndTime));
		}
		// send request for historical data
		historicDataRequest = sendRequest(mdr);
	}

	private void convertHistoricalRatesToCandleSticks() {
		// get the keys for the historicalRates table into a sorted list
		Set<UTCTimestamp> dateList = new TreeSet<>(historicalData.keySet());
		Set<UTCTimestamp> sortedDateList = new TreeSet<UTCTimestamp>(new Comparator<UTCTimestamp>() {
			@Override
			public int compare(UTCTimestamp o1, UTCTimestamp o2) {
				return o2.compareTo(o1);
			}
		});
		sortedDateList.addAll(dateList);
		for (UTCTimestamp time : sortedDateList) {
			// create a single instance of the snapshot
			MarketDataSnapshot candleData;
			candleData = historicalData.get(time);
			Candlestick candlestick = new Candlestick();
			candlestick.setOpenTime(time);
			candlestick.setOpen(candleData.getBidOpen());
			candlestick.setLow(candleData.getBidLow());
			candlestick.setHigh(candleData.getAskHigh()); // for short positions,the relevant price is the ask price
			candlestick.setClose(candleData.getBidClose());
			assetData.add(candlestick);
		}
	}

}

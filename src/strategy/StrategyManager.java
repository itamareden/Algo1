package strategy;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.util.HashMap;

import strategies.MarketStrategy;
import strategies.TestStrategy;

public class StrategyManager {
	
	public static StrategyManager SManager = null;
	private Strategy currentStrategy = null;	
	
	private StrategyManager() {}
	
	public static StrategyManager getInstance(){
		if(SManager == null){
			SManager = new StrategyManager();
		}
		return SManager;
	}

	private HashMap<String, Class<? extends Strategy>> strategiesMap = new HashMap<String, Class<? extends Strategy>>(){
		{
			put("Market_Strategy", MarketStrategy.class);
		}
	};
	
	public Strategy getCurrentStrategy(){
		return currentStrategy;
	}
	
	
	public boolean runStrategy(String strategyName, StrategyProperties strategyProp) throws Exception{		
		try {
			if(currentStrategy == null){
				System.out.println("inisde");
				Class<? extends Strategy> strategyClass = strategiesMap.get(strategyName);
				Constructor ctor = strategyClass.getDeclaredConstructor(StrategyProperties.class);
				currentStrategy =  (Strategy) ctor.newInstance(strategyProp);
				currentStrategy.start();
				return true;
			}
			return false;
		} catch (Exception e) {
			e.printStackTrace();
			return false;
		}
	}
	
	public void stopStrategy(){
		Strategy tempPointer = currentStrategy;
		currentStrategy = null;
		tempPointer.stopStrategy();
	}

}

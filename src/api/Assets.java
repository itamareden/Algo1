package api;

import java.util.ArrayList;
import java.util.List;

public class Assets {
	
	public static String [] assets = {
			"EUR/USD",
			"GBP/USD",
			"USD/JPY",
			"AUD/USD",
			"NZD/USD",
			"USD/CAD",
			"USD/CHF",
			"USD/ZAR",
			"XAU/USD",
			"XAG/USD",
	};

	public static boolean isContainsAsset(String assetSymbol){
		for( String assetStr : assets){
			if(assetStr.equals(assetSymbol)) return true;
		}
		return false;
	}

}

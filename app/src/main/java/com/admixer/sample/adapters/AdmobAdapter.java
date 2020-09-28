package com.admixer.sample.adapters;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.view.View;
import android.view.ViewGroup.LayoutParams;
import android.widget.RelativeLayout;

import com.admixer.ads.AdInfo;
import com.admixer.ads.AdMixer;
import com.admixer.common.Constants;
import com.admixer.common.Logger;
import com.admixer.common.Logger.LogLevel;
import com.admixer.mediation.BaseAdAdapter;
import com.google.android.gms.ads.AdListener;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdSize;
import com.google.android.gms.ads.AdView;
import com.google.android.gms.ads.InterstitialAd;
import com.google.android.gms.ads.MobileAds;
import com.google.android.gms.ads.RequestConfiguration;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.Arrays;

/**
 * Admob Adapter
 */
public class AdmobAdapter extends BaseAdAdapter {

	String adunitId;

	AdView adView;
	AdRequest adRequest;
	InterstitialAd interstitial;
	boolean hasAd = false;

	//adapterAdInfo
	String[] adSizeList = {"SMART_BANNER", "BANNER", "LARGE_BANNER", "MEDIUM_RECTANGLE", "FULL_BANNER", "LEADERBOARD"};
	String adSize = adSizeList[0];

	public String getAdapterName() {
		return AdMixer.ADAPTER_ADMOB;
	}

	@Override
	public void initAdapter(Context context, JSONObject adConfig, AdInfo adInfo) {
		super.initAdapter(context, adConfig, adInfo);
		
		try {
			adunitId = keyInfo.getString("adunit_id");
		} catch (JSONException e) {
			e.printStackTrace();
		}

		try {
			JSONObject adapterAdInfo = adInfo.getAdapterAdInfo(getAdapterName());
			if(adapterAdInfo != null) {

				if(adapterAdInfo.has("adSize")) {
					String size = adapterAdInfo.getString("adSize");
					if(Arrays.asList(adSizeList).contains(size))
						adSize = size;
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}

		RequestConfiguration requestConfiguration = new RequestConfiguration.Builder().setTagForChildDirectedTreatment(AdMixer.getTagForChildDirectedTreatment()).build();
		MobileAds.setRequestConfiguration(requestConfiguration);
	}
		
	@Override
	public void closeAdapter() {
		super.closeAdapter();
		Logger.writeLog(LogLevel.Debug, "Admob close Adapter");
		if(adView != null) {
			adView.setAdListener(null);
			adView.destroy();
			adView = null;
			
			Logger.writeLog(LogLevel.Debug, "Admob close banner");
		}
		
		if(interstitial != null) {
			interstitial.setAdListener(null);
			interstitial = null;
			
			Logger.writeLog(LogLevel.Debug, "Admob close interstitial");
		}
	}
	// banner : 320 * 50, smart_banner : screen_width * 32|50|90 
	@SuppressLint("MissingPermission")
	public boolean loadAd(Activity baseActivity, RelativeLayout parentAdView) {
		if(adunitId == null) {
			Logger.writeLog(LogLevel.Warn, "Admob adunit id is empty.");
			return false;
		}

		adformat = Constants.ADFORMAT_BANNER;
		isInterstitial = 0;

		RelativeLayout.LayoutParams params 	= new RelativeLayout.LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT);
		params.addRule(RelativeLayout.CENTER_HORIZONTAL);

		adView = new AdView(baseActivity);
		adView.setAdUnitId(adunitId);

		AdSize adSize;
		switch(this.adSize) {
			case "SMART_BANNER" :
				adSize	= AdSize.SMART_BANNER;
				params = new RelativeLayout.LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT);
				break;
			case "BANNER" :
				adSize 	= AdSize.BANNER;
				break;
			case "LARGE_BANNER" :
				adSize 	= AdSize.LARGE_BANNER;
				break;
			case "MEDIUM_RECTANGLE" :
				adSize	= AdSize.MEDIUM_RECTANGLE;
				break;
			case "FULL_BANNER" :
				adSize	= AdSize.FULL_BANNER;
				break;
			case "LEADERBOARD" :
				adSize	= AdSize.LEADERBOARD;
				break;
			default :
				adSize	= AdSize.BANNER;
				break;
		}

		adView.setAdSize(adSize);
		adView.setLayoutParams(params);
	    adView.setAdListener(new AdListener(){

			@Override
			public void onAdClosed() {
				super.onAdClosed();
				Logger.writeLog(LogLevel.Debug, "Admob banner onAdClosed");
			}

			@Override
			public void onAdFailedToLoad(int errorCode) {
				super.onAdFailedToLoad(errorCode);
				fireOnAdReceiveAdFailed(AdMixer.AX_ERR_ADAPTER, "Admob banner onAdFailedToLoad(" + errorCode + ")");
			}

			@Override
			public void onAdLeftApplication() {
				super.onAdLeftApplication();
			}

			@Override
			public void onAdLoaded() {
				super.onAdLoaded();
				handleAdLoaded();
				Logger.writeLog(LogLevel.Debug, "Admob banner onAdLoaded");
			}

			@Override
			public void onAdOpened() {
				super.onAdOpened();
			}});

	    parentAdView.addView(adView);

		adRequest = new AdRequest.Builder().setRequestAgent("admixer").addTestDevice(AdRequest.DEVICE_ID_EMULATOR).build();

		adView.loadAd(adRequest);
	    
		Logger.writeLog(LogLevel.Debug, "Admob load banner");
	    
	    
	    return true;
	}
	
	@SuppressLint("MissingPermission")
	public boolean loadInterstitialAd(Activity baseActivity, RelativeLayout parentAdView) {
		if(adunitId == null) {
			Logger.writeLog(LogLevel.Warn, "Admob adunit id is empty.");
			return false;
		}

		adformat = Constants.ADFORMAT_BANNER;
		isInterstitial = 1;

		interstitial = new InterstitialAd(baseActivity);
		interstitial.setAdUnitId(adunitId);

		adRequest = new AdRequest.Builder().setRequestAgent("admixer").addTestDevice(AdRequest.DEVICE_ID_EMULATOR).build();

	    interstitial.setAdListener(new AdListener(){

			@Override
			public void onAdClosed() {
				super.onAdClosed();
				fireOnInterstitialAdClosed();
				Logger.writeLog(LogLevel.Debug, "Admob interstitial Closed");
			}

			@Override
			public void onAdFailedToLoad(int errorCode) {
				super.onAdFailedToLoad(errorCode);
				fireOnAdReceiveAdFailed(AdMixer.AX_ERR_ADAPTER, "Admob interstitial onAdFailedToLoad(" + errorCode + ")");
			}

			@Override
			public void onAdLeftApplication() {
				super.onAdLeftApplication();
			}

			@Override
			public void onAdLoaded() {
				super.onAdLoaded();
				handleAdLoaded();
				Logger.writeLog(LogLevel.Debug, "Admob interstitial onAdLoaded");
			}
			
			@Override
			public void onAdOpened() {
				super.onAdOpened();
			}}
	    );

	    interstitial.loadAd(adRequest);		
		
		return true;
	}

	public View getView() {
		return adView;
	}
	
	void handleAdLoaded() {
		if(interstitial != null) {
			if(!loadOnly && interstitial.isLoaded()) {
				interstitial.show();
				Logger.writeLog(LogLevel.Debug, "Admob interstitial show");
			} else
				hasAd = true;
		}
		
		Logger.writeLog(LogLevel.Debug, "Admob fireOnAdReceived");
		fireOnAdReceived();
		if(!loadOnly && interstitial != null)
			fireOnInterstitialAdShown();
	}

	@Override
	public boolean canLoadOnly() {
		return true;
	}

	@Override
	public boolean show() {
		if(!hasAd || interstitial == null)
			return false;
		
		if(interstitial.isLoaded()) {
			hasAd = false;
			interstitial.show();
			Logger.writeLog(LogLevel.Debug, "Admob interstitial show");
			fireOnInterstitialAdShown();
			return true;
		}else {
			return false;
		}
		
	}

}

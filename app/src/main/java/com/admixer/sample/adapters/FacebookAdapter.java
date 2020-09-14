package com.admixer.sample.adapters;

import android.app.Activity;
import android.content.Context;
import android.view.View;
import android.widget.RelativeLayout;

import com.admixer.ads.AdInfo;
import com.admixer.ads.AdMixer;
import com.admixer.common.Constants;
import com.admixer.common.Logger;
import com.admixer.common.Logger.LogLevel;
import com.admixer.mediation.BaseAdAdapter;
import com.facebook.ads.Ad;
import com.facebook.ads.AdError;
import com.facebook.ads.AdListener;
import com.facebook.ads.AdSize;
import com.facebook.ads.AdView;
import com.facebook.ads.InterstitialAd;
import com.facebook.ads.InterstitialAdListener;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.Arrays;

/**
 * Facebook Adapter 
 */
public class FacebookAdapter extends BaseAdAdapter implements AdListener, InterstitialAdListener {

	String placementId;

	AdView adView;
	InterstitialAd interstitialAd;
	boolean hasAd = false;

	String[] adSizeList = {"BANNER_HEIGHT_50", "BANNER_HEIGHT_90", "RECTANGLE_HEIGHT_250"};
	String adSize = adSizeList[0];
	
	@Override
	public String getAdapterName() {
		return AdMixer.ADAPTER_FACEBOOK;
	}

	@Override
	public void initAdapter(Context context, JSONObject adConfig, AdInfo adInfo) {
		super.initAdapter(context, adConfig, adInfo);

		try {
			placementId = keyInfo.getString("placement_id");
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
	}

	/**
	 * 배너광고 호출
	 */
	@Override
	public boolean loadAd(Activity baseActivity, RelativeLayout parentAdView) {
		if(placementId == null) {
			Logger.writeLog(LogLevel.Warn, "Facebook placement id is empty.");
			return false;
		}
		
		isInterstitial = 0;
		adformat = Constants.ADFORMAT_BANNER;

		AdSize adSize;
		switch(this.adSize) {
			case "BANNER_HEIGHT_50":
				adSize	= AdSize.BANNER_HEIGHT_50;
				break;
				
			case "BANNER_HEIGHT_90":
				adSize	= AdSize.BANNER_HEIGHT_90;
				break;
				
			case "RECTANGLE_HEIGHT_250":
				adSize	= AdSize.RECTANGLE_HEIGHT_250;
				break;
			
			default :
				adSize	= AdSize.BANNER_HEIGHT_50;
				break;
		}	
		
		adView = new AdView(baseActivity, placementId, adSize);
		parentAdView.addView(adView);		
		adView.loadAd(adView.buildLoadAdConfig()
				.withAdListener(this)
				.build());

		Logger.writeLog(LogLevel.Debug, "Facebook load banner ad");
		return true;
	}

	/**
	 * 전면광고 호출
	 */
	@Override
	public boolean loadInterstitialAd(Activity baseActivity, RelativeLayout parentAdView) {
		if(placementId == null) {
			Logger.writeLog(LogLevel.Warn, "Facebook placement id is empty.");
			return false;
		}
		
		isInterstitial = 1;
		adformat = Constants.ADFORMAT_BANNER;

		interstitialAd = new InterstitialAd(baseActivity, placementId);
	    interstitialAd.loadAd(interstitialAd.buildLoadAdConfig()
				.withAdListener(this)
				.build());

	    Logger.writeLog(LogLevel.Debug, "Facebook load interstitial ad");
		return true;
	}

	@Override
	public View getView() {
		if(isInterstitial == 1)
			return null;
		return adView;
	}
	
	/**
	 * 전면광고 loadOnly 지원여부
	 */
	@Override
	public boolean canLoadOnly() {
		return true;
	}

	@Override
	public boolean isSupportInterstitialClose() {
		return true;
	}

	@Override
	public void onAdClicked(Ad arg0) {
		Logger.writeLog(LogLevel.Debug, "Facebook Click");
	}
	
	/**
	 * 배너 및 전면광고 로드 성공
	 */
	@Override
	public void onAdLoaded(Ad arg0) {
		Logger.writeLog(LogLevel.Debug, "Facebook OnAdLoaded");
		
		if(isInterstitial == 1) {
			if(!loadOnly && interstitialAd != null && interstitialAd.isAdLoaded()) {
				interstitialAd.show();
				interstitialAd = null;
				Logger.writeLog(LogLevel.Debug, "Facebook interstitial showAd");
			} else
				hasAd = true;
		}

		fireOnAdReceived();
		Logger.writeLog(LogLevel.Debug, "Facebook fireOnAdReceived");
	}

	@Override
	public void onLoggingImpression(Ad arg0) {
		Logger.writeLog(LogLevel.Debug, "Facebook onLoggingImpression");
	}

	/**
	 * 배너 및 전면광고 로드 실패
	 */
	@Override
	public void onError(Ad arg0, AdError arg1) {
		Logger.writeLog(LogLevel.Debug, "Facebook OnAdFailed : " + arg1.getErrorMessage() + "(" + arg1.getErrorCode() + ")");
		
		fireOnAdReceiveAdFailed(AdMixer.AX_ERR_ADAPTER, arg1.getErrorMessage());
	}
	
	/**
	 * 전면광고 닫기 이벤트
	 */
	@Override
	public void onInterstitialDismissed(Ad arg0) {
		Logger.writeLog(LogLevel.Debug, "Facebook onInterstitialDismissed");
		fireOnInterstitialAdClosed();
	}
	
	/**
	 * 전면광고 화면에 노출
	 */
	@Override
	public void onInterstitialDisplayed(Ad arg0) {
		fireOnInterstitialAdShown();
		Logger.writeLog(LogLevel.Debug, "Facebook onInterstitialDisplayed");
	}
	
	/**
	 * 전면광고 Load&Show 기능 사용 시 show이벤트
	 */
	@Override
	public boolean show() {
		if(!hasAd || interstitialAd == null)
			return false;

		hasAd = false;
		interstitialAd.show();
		interstitialAd = null;
		Logger.writeLog(LogLevel.Debug, "Facebook interstitial showAd");
		return true;
	}
	
	@Override
	public void closeAdapter() {
		super.closeAdapter();
		Logger.writeLog(LogLevel.Debug, "Facebook close Adapter");
		if(isInterstitial == 0 && adView != null) {
			adView.destroy();
			adView = null;
			
			Logger.writeLog(LogLevel.Debug, "Facebook close banner");
		}
		
		if(isInterstitial == 1 && interstitialAd != null) {
			interstitialAd.destroy();
			interstitialAd = null;
			
			Logger.writeLog(LogLevel.Debug, "Facebook close interstitial");
		}
	}

}

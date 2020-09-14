package com.admixer.sample.adapters;

import android.app.Activity;
import android.content.Context;
import android.view.View;
import android.widget.RelativeLayout;
import android.widget.RelativeLayout.LayoutParams;

import com.admixer.ads.AdInfo;
import com.admixer.ads.AdMixer;
import com.admixer.common.Constants;
import com.admixer.common.Logger;
import com.admixer.common.Logger.LogLevel;
import com.admixer.common.command.Command;
import com.admixer.common.command.Command.OnCommandCompletedListener;
import com.admixer.common.command.DelayedCommand;
import com.admixer.mediation.BaseAdAdapter;
import com.fsn.cauly.CaulyAdInfo;
import com.fsn.cauly.CaulyAdInfoBuilder;
import com.fsn.cauly.CaulyAdView;
import com.fsn.cauly.CaulyAdViewListener;
import com.fsn.cauly.CaulyInterstitialAd;
import com.fsn.cauly.CaulyInterstitialAdListener;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.Arrays;

/**
 * Cauly Adapter 
 */
public class CaulyAdapter extends BaseAdAdapter implements OnCommandCompletedListener, CaulyAdViewListener, CaulyInterstitialAdListener {

	String appCode;

	CaulyAdInfo caulyAdInfo;
	CaulyAdView adView;
	CaulyInterstitialAd interstitial;

	DelayedCommand command;
	boolean hasAd = false;
	boolean coppa = false;

	String[] bannerHeightList = {"Proportional", "Fixed_50"};
	String bannerHeight = bannerHeightList[1];
	
	public String getAdapterName() {
		return AdMixer.ADAPTER_CAULY;
	}

	@Override
	public void initAdapter(Context context, JSONObject adConfig, AdInfo adInfo) {
		super.initAdapter(context, adConfig, adInfo);

		try {
			appCode = keyInfo.getString("app_code");
		} catch (JSONException e) {
			e.printStackTrace();
		}

		try {
			JSONObject adapterAdInfo = adInfo.getAdapterAdInfo(getAdapterName());
			if(adapterAdInfo != null) {
				if(adapterAdInfo.has("bannerHeight")) { // bannerHeight 정보설정
					String height = adapterAdInfo.getString("bannerHeight");
					if(Arrays.asList(bannerHeightList).contains(height))
						bannerHeight = height;
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}

		if(AdMixer.getTagForChildDirectedTreatment() == 1) {
			coppa = true;
		}
	}
	
	@Override
	public void closeAdapter() {
		super.closeAdapter();
		Logger.writeLog(LogLevel.Debug, "Cauly close Adapter");
		if(command != null) {
			command.cancel();
			command = null;
		}

		if(adView != null) {
			adView.setVisibility(View.GONE);
			adView.setAdViewListener(null);
			adView.pause();
			adView = null;
			Logger.writeLog(LogLevel.Debug, "Cauly close banner");
		}
		
		if(interstitial != null) {
			interstitial.setInterstialAdListener(null);
			interstitial.cancel();
			interstitial = null;
			Logger.writeLog(LogLevel.Debug, "Cauly close interstitial");
		}
	}

	@Override
	public boolean isSupportInterstitialClose() {
		return true;
	}
	
	public boolean loadAd(Activity baseActivity, RelativeLayout parentAdView) {
		if(appCode == null) {
			Logger.writeLog(LogLevel.Warn, "Cauly app code is empty.");
			return false;
		}

		isInterstitial = 0;
		adformat = Constants.ADFORMAT_BANNER;

		caulyAdInfo = new CaulyAdInfoBuilder(appCode)
				.tagForChildDirectedTreatment(coppa)
				.effect("None")
				.threadPriority(5)
				.dynamicReloadInterval(false)
				.reloadInterval(120)
				.enableDefaultBannerAd(false)
				.bannerHeight(bannerHeight)
				.build();

		// CaulyAdInfo를 이용, CaulyAdView 생성.
		adView = new CaulyAdView(baseActivity);
		adView.setAdInfo(caulyAdInfo);
		adView.setAdViewListener(this);
		
		LayoutParams params = new LayoutParams(
				LayoutParams.WRAP_CONTENT,
				LayoutParams.WRAP_CONTENT);
        adView.setVisibility(View.GONE);
        adView.setLayoutParams(params);
        
        parentAdView.addView(adView);
        
		Logger.writeLog(LogLevel.Debug, "Cauly load banner");
        
        
	    return true;
	}
	
	public boolean loadInterstitialAd(Activity baseActivity, RelativeLayout parentAdView) {
		if(appCode == null) {
			Logger.writeLog(LogLevel.Warn, "Cauly app code is empty.");
			return false;
		}

		isInterstitial = 1;
		adformat = Constants.ADFORMAT_BANNER;

		caulyAdInfo = new CaulyAdInfoBuilder(appCode)
				.tagForChildDirectedTreatment(coppa)
				.effect("None")
				.threadPriority(5)
				.build();
		
		interstitial = new CaulyInterstitialAd();
		interstitial.setAdInfo(caulyAdInfo);
		interstitial.setInterstialAdListener(this);

		interstitial.requestInterstitialAd(baseActivity);
		
		Logger.writeLog(LogLevel.Debug, "Cauly load interstitial");
		
		return true;
	}

	public View getView() {
		if(isInterstitial == 1)
			return null;
		return adView;
	}
	
	@Override
	public void onReceiveAd(CaulyAdView paramCaulyAdView, boolean paramBoolean) {
		if(adView == null)
			return;
		adView.setVisibility(View.VISIBLE);
		boolean isChargeable = paramBoolean;
		if(!isChargeable) {
			fireOnAdReceiveAdFailed(AdMixer.AX_ERR_ADAPTER, "Cauly banner No Fill");
			return;
		}
		command = new DelayedCommand(10);
		command.setOnCommandResult(this);
		command.execute();
	}

	@Override
	public void onFailedToReceiveAd(CaulyAdView paramCaulyAdView, int paramInt, String paramString) {
		fireOnAdReceiveAdFailed(AdMixer.AX_ERR_ADAPTER, "Cauly onFailedToReceiveAd ("+paramInt+") : "+paramString);
	}

	@Override
	public void onShowLandingScreen(CaulyAdView paramCaulyAdView) {
	}

	@Override
	public void onCloseLandingScreen(CaulyAdView paramCaulyAdView) {
	}

	@Override
	public void onReceiveInterstitialAd(CaulyInterstitialAd paramCaulyInterstitialAd, boolean paramBoolean) {
		if(interstitial == null) {
			paramCaulyInterstitialAd.cancel();
			return;
		}
		boolean isChargeable = paramBoolean;
		if(!isChargeable) {
			interstitial = null;
			fireOnAdReceiveAdFailed(AdMixer.AX_ERR_ADAPTER, "Cauly interstitial No Fill");
			return;
		}
		
		if(!loadOnly) {
			Logger.writeLog(LogLevel.Debug, "Cauly interstitial showAd");

			interstitial.show();
			fireOnInterstitialAdShown();
		} else
			hasAd = true;
		
		command = new DelayedCommand(10);
		command.setOnCommandResult(this);
		command.execute();
	}

	@Override
	public void onFailedToReceiveInterstitialAd(CaulyInterstitialAd paramCaulyInterstitialAd, int paramInt, String paramString) {
		interstitial = null;
		fireOnAdReceiveAdFailed(AdMixer.AX_ERR_ADAPTER, "Cauly onFailedToReceiveInterstitialAd("+paramInt+") : " + paramString);
	}

	@Override
	public void onClosedInterstitialAd(CaulyInterstitialAd paramCaulyInterstitialAd) {
		fireOnInterstitialAdClosed();
		
		if(interstitial != null)
			interstitial = null;
		
		Logger.writeLog(LogLevel.Debug, "Cauly onClosedInterstitialAd");
	}
	
	@Override
	public void onCommandCompleted(Command command) {
		fireOnAdReceived();
	}

	@Override
	public void onLeaveInterstitialAd(CaulyInterstitialAd arg0) {
		Logger.writeLog(LogLevel.Debug, "Cauly onLeaveInterstitialAd");
	}

	@Override
	public boolean canLoadOnly() {
		return true;
	}
	
	@Override
	public boolean show() {
		if(!hasAd || interstitial == null)
			return false;

		hasAd = false;
		interstitial.show();
		Logger.writeLog(LogLevel.Debug, "Cauly interstitial show");

		Logger.writeLog(LogLevel.Debug, "Cauly fireOnInterstitialAdShown");
		fireOnInterstitialAdShown();
		
		return true;
	}
}

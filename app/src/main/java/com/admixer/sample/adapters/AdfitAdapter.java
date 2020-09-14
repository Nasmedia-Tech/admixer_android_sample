package com.admixer.sample.adapters;

import android.app.Activity;
import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;

import com.admixer.ads.AdInfo;
import com.admixer.ads.AdMixer;
import com.admixer.common.Constants;
import com.admixer.common.Logger;
import com.admixer.common.Logger.LogLevel;
import com.admixer.mediation.BaseAdAdapter;
import com.kakao.adfit.ads.AdListener;
import com.kakao.adfit.ads.ba.BannerAdView;

import org.json.JSONException;
import org.json.JSONObject;

/**
 * Adfit Adapter
 */
public class AdfitAdapter extends BaseAdAdapter implements AdListener {

	String clientId;
	BannerAdView adView;
	
	public String getAdapterName() {
		return AdMixer.ADAPTER_ADFIT;
	}

	@Override
	public void initAdapter(Context context, JSONObject adConfig, AdInfo adInfo) {
		super.initAdapter(context, adConfig, adInfo);

		try {
			clientId = keyInfo.getString("client_id");
		} catch (JSONException e) {
			e.printStackTrace();
		}
	}
	
	@Override
	public void closeAdapter() {
		super.closeAdapter();
		if(adView != null) {
			
			Logger.writeLog(LogLevel.Debug, "Adfit close banner");
			adView.setAdListener(null);
			adView.destroy();
			adView = null;
		}
	}
	
	public boolean loadAd(Activity baseActivity, RelativeLayout parentAdView) {
		if(clientId == null) {
			Logger.writeLog(LogLevel.Warn, "Adfit client id is empty.");
			return false;
		}

		isInterstitial = 0;
		adformat = Constants.ADFORMAT_BANNER;

		try {
			adView = new BannerAdView(baseActivity);

			adView.setClientId(clientId);
			adView.setAdListener(this);

			RelativeLayout.LayoutParams params = new RelativeLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);

			params.addRule(RelativeLayout.CENTER_HORIZONTAL);
			params.addRule(RelativeLayout.CENTER_IN_PARENT);
			adView.setLayoutParams(params);

			parentAdView.addView(adView, 0);
			adView.loadAd();

			Logger.writeLog(LogLevel.Debug, "Adfit load banner ad");
		} catch(Exception e) {
			e.printStackTrace();
			return false;
		}
		
	    return true;
	}
	
	public boolean loadInterstitialAd(Activity baseActivity, RelativeLayout parentAdView) {
		return false;
	}

	@Override
	public void onAdLoaded() {
		Logger.writeLog(LogLevel.Debug, "Adfit OnAdLoaded");
		fireOnAdReceived();
	}

	@Override
	public void onAdFailed(int code) {
		Logger.writeLog(LogLevel.Debug, "Adfit OnAdFailed : " + code);
		fireOnAdReceiveAdFailed(code, "onAdFailed("+code+")");
	}

	@Override
	public void onAdClicked() {
		Logger.writeLog(LogLevel.Debug, "Adfit OnAdClicked");
	}

	public View getView() {
		return adView;
	}

	@Override
	public boolean canInterstitialAd() { return false; }

	@Override
	public boolean canLoadOnly() {
		return false;
	}

	@Override
	public boolean show() {
		return false;
	}
	
}

package com.admixer.sample.adapters;


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
import com.admixer.common.command.Command;
import com.admixer.common.command.Command.OnCommandCompletedListener;
import com.admixer.common.command.DelayedCommand;
import com.admixer.mediation.BaseAdAdapter;
import com.skplanet.tad.AdInterstitial;
import com.skplanet.tad.AdInterstitialListener;
import com.skplanet.tad.AdListener;
import com.skplanet.tad.AdRequest.ErrorCode;
import com.skplanet.tad.AdSlot;
import com.skplanet.tad.AdView;

import org.json.JSONException;
import org.json.JSONObject;

/**
 * DawinClick Adapter
 */
public class DawinClickAdapter extends BaseAdAdapter implements AdListener, OnCommandCompletedListener {

	String clientId;

	static final int CID_EXCEPTION 		= 1; 
	static final int CID_LOAD_RESULT	= 2; 
	
	AdView adView;
	AdInterstitial adInterstitial;
	boolean hasAd = false;
	DelayedCommand command;
	
	@Override
	public String getAdapterName() {
		return AdMixer.ADAPTER_DAWIN_CLICK;
	}

	@Override
	public void initAdapter(Context context, JSONObject adConfig, AdInfo adInfo) {
		super.initAdapter(context, adConfig, adInfo);

		try {
			clientId = keyInfo.getString("client_id");
		}catch (JSONException e) {
			e.printStackTrace();
		}
	}

	@Override
	public boolean loadAd(Activity baseActivity, RelativeLayout parentAdView) {
		if(clientId == null) {
			Logger.writeLog(LogLevel.Warn, "DawinClick client id is empty");
			return false;
		}

		isInterstitial = 0;
		adformat = Constants.ADFORMAT_BANNER;

		this.adView = new AdView(baseActivity);
		this.adView.setClientId(clientId);

		// 광고 요청할 Slot Num 설정 (필수)
		this.adView.setSlotNo(AdSlot.BANNER);
		
		// 광고 새로고침 주기 설정, (15 ~ 60) (선택)
		this.adView.setRefreshInterval(0);
		
		//TestMode 설정
		this.adView.setTestMode(false);
		
		// 노출될 배너의 Animation 효과 지정 (선택)
		this.adView.setAnimationType(AdView.AnimationType.NONE);

		// 광고 수신 상태를 확인 할 수 있는 Listener (선택)
		this.adView.setListener(this);
		
		RelativeLayout.LayoutParams params = new RelativeLayout.LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT);
		parentAdView.addView(adView, params);

		// 광고 요청 (필수) 단 XML상에서 광고를 요청한 경우는 호출하지 않음.
		try {
			adView.loadAd(null);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return true;
	}

	@Override
	public boolean loadInterstitialAd(Activity baseActivity, RelativeLayout parentAdView) {
		if(clientId == null) {
			Logger.writeLog(LogLevel.Warn, "DawinClick client id is empty");
			return false;
		}

		isInterstitial = 1;
		adformat = Constants.ADFORMAT_BANNER;

		adInterstitial = new AdInterstitial(baseActivity);
		
		//발급받은 Client Id 설정 (필수), 발급은 www.t-ad.co.kr에서 가능합니다.  
		adInterstitial.setClientId(clientId);

		// 광고 요청할 Slot Num 설정 (필수)
		adInterstitial.setSlotNo(AdSlot.INTERSTITIAL);

		// 광고 수신 상태를 확인 할 수 있는 Listener (선택)
		adInterstitial.setListener(new AdInterstitialListener(){

			@Override
			public void onAdFailed(ErrorCode arg0) {
				fireLoadResult(arg0);
			}

			@Override
			public void onAdLoaded() {
				fireLoadResult(null);
			}

			@Override
			public void onAdWillLoad() {
			}

			@Override
			public void onAdDismissScreen() {
				if(adInterstitial == null)
					return;
				fireOnInterstitialAdClosed();
				Logger.writeLog(LogLevel.Debug, "DawinClick fireOnInterstitialAdClosed");
				adInterstitial = null;
			}

			@Override
			public void onAdLeaveApplication() {
			}

			@Override
			public void onAdPresentScreen() {
			}});
		
		// 테스트 모드 설정 
		adInterstitial.setTestMode(false);
		try {
			adInterstitial.loadAd(null);
		} catch (Exception e) {
			executeExceptionCommand(e);
		}
		return true;
	}
	
	void handleBannerLoadResult(Command command) {
		if(adView == null)
			return;
		
		ErrorCode errorCode = (ErrorCode)command.getData();
		if(errorCode != null)
			fireOnAdReceiveAdFailed(errorCode.ordinal(), errorCode.toString());
		else
			fireOnAdReceived();
	}
	
	void handleInterstitalLoadResult(Command command) {
		if(adInterstitial == null)
			return;
		
		ErrorCode arg0 = (ErrorCode)command.getData();
		if(arg0 != null) {
			Logger.writeLog(LogLevel.Debug, "DawinClick onAdFailed : " + arg0.toString());
			DawinClickAdapter.this.fireOnAdReceiveAdFailed(arg0.ordinal(), arg0.toString());
			adInterstitial = null;
		} else {
			Logger.writeLog(LogLevel.Debug, "DawinClick onAdLoaded");
			if(!loadOnly) {
				try {
					adInterstitial.showAd();
				} catch (Exception e) {
					executeExceptionCommand(e);
					return;
				}
				Logger.writeLog(LogLevel.Debug, "DawinClick showAd");
			} else {
				hasAd = true;
				Logger.writeLog(LogLevel.Debug, "DawinClick wait showAd");
			}
			
			DawinClickAdapter.this.fireOnAdReceived();
			Logger.writeLog(LogLevel.Debug, "DawinClick fireOnAdReceived");
			
			if(!loadOnly && adInterstitial != null) {
				Logger.writeLog(LogLevel.Debug, "DawinClick fireOnInterstitialAdShown");
				DawinClickAdapter.this.fireOnInterstitialAdShown();
			}			
		}
	}
	
	void executeExceptionCommand(Exception e) {
		command = new DelayedCommand(1);
		command.setTag(CID_EXCEPTION);
		command.setData(e);
		command.setOnCommandResult(this);
		command.execute();
	}
	
	@Override
	public void closeAdapter() {
		super.closeAdapter();
		
		if(adView != null) {
			adView.destroyAd();
			adView = null;
		}
		
		if(adInterstitial != null) {
			Logger.writeLog(LogLevel.Debug, "DawinClick close interstitial");
			adInterstitial.destroyAd();
			adInterstitial = null;
		}
		
		if(command != null) {
			command.cancel();
			command = null;
		}

	}	

	@Override
	public View getView() {
		return adView;
	}
	
	// AdListener Implementation
	
	@Override
	public void onAdClicked() {
	}

	@Override
	public void onAdExpandClosed() {
	}

	@Override
	public void onAdExpanded() {
	}
	
	void fireLoadResult(ErrorCode errorCode) {
		if(command != null)
			command.cancel();
		
		command = new DelayedCommand(1);
		command.setTag(CID_LOAD_RESULT);
		command.setData(errorCode);
		command.setOnCommandResult(DawinClickAdapter.this);
		command.execute();
	}

	@Override
	public void onAdFailed(ErrorCode arg0) {
		fireLoadResult(arg0);
	}

	@Override
	public void onAdLoaded() {
		fireLoadResult(null);
	}

	@Override
	public void onAdResizeClosed() {
	}

	@Override
	public void onAdResized() {
	}

	@Override
	public void onAdWillLoad() {
	}

	public void onAdWillReceive() {
	}
	
	@Override
	public void onAdDismissScreen() {
	}

	@Override
	public void onAdLeaveApplication() {
	}

	@Override
	public void onAdPresentScreen() {
	}

	@Override
	public boolean canLoadOnly() {
		return true;
	}

	@Override
	public boolean isSupportInterstitialClose() {
		return true;
	}
	
	@Override
	public boolean show() {
		if(!hasAd || adInterstitial == null)
			return false;

		hasAd = false;
		try {
			adInterstitial.showAd();
		} catch (Exception e) {
			executeExceptionCommand(e);
			return false;
		}
		Logger.writeLog(LogLevel.Debug, "DawinClick manual showAd");
		fireOnInterstitialAdShown();
		Logger.writeLog(LogLevel.Debug, "DawinClick manual fireOnInterstitialAdShown");
		return true;
	}

	@Override
	public void onCommandCompleted(Command command) {
		int tag = command.getTag();
		switch(tag) {
		case CID_EXCEPTION:
			{
				Exception e = (Exception)command.getData();
				if(e != null)
					fireOnAdReceiveAdFailed(-1, e.toString());
				else
					fireOnAdReceiveAdFailed(-1, "DawinClick Exception");
			}
			break;
			
		case CID_LOAD_RESULT:
			if(isInterstitial == 0)
				handleBannerLoadResult(command);
			else
				handleInterstitalLoadResult(command);
			break;
		}
		
	}
		
}

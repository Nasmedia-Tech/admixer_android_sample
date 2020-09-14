package com.admixer.sample.adapters;

import android.app.Activity;
import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
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
import com.mapps.android.share.AdInfoKey;
import com.mapps.android.view.AdInterstitialView;
import com.mapps.android.view.AdView;
import com.mz.common.listener.AdListener;

import org.json.JSONObject;

/**
 * Man Adapter
 */
public class ManAdapter extends BaseAdAdapter implements AdListener, OnCommandCompletedListener {

	String publisherCode;
	String mediaCode;
	String sectionCode;

	AdView adView;
	AdInterstitialView interstitial;
	RelativeLayout parentAdView;

	static final int CID_INTERSTITIAL_LOAD_COMPLETE = 1;
	static final int CID_BANNER_LOAD_FAILED = 2;

	DelayedCommand command;

	@Override
	public String getAdapterName() {
		return AdMixer.ADAPTER_MAN;
	}

	public View getView() {
		return adView;
	}

	@Override
	public void initAdapter(Context context, JSONObject adConfig, AdInfo adInfo) {
		super.initAdapter(context, adConfig, adInfo);

		try {
			publisherCode = keyInfo.getString("a_publisher");
			mediaCode = keyInfo.getString("a_media");
			sectionCode = keyInfo.getString("a_section");
		} catch(Exception e) {
			Logger.writeLog(LogLevel.Warn, "Man key info is missing");
		}
	}

	@Override
	public void closeAdapter() {
		super.closeAdapter();
		Logger.writeLog(LogLevel.Debug, "Man close Adapter");

		if(adView != null) {
			adView.StopService();
			adView.setAdListener(null);
			adView = null;
			Logger.writeLog(LogLevel.Debug, "Man close banner");
		}

		if(interstitial != null) {
			interstitial.setAdListener(null);
			interstitial = null;
			Logger.writeLog(LogLevel.Debug, "Man close interstitial");
		}

		if(command != null) {
			command.cancel();
			command = null;
		}
	}

	@Override
	public boolean loadAd(Activity baseActivity, RelativeLayout parentAdView) {
		if (publisherCode == null || mediaCode == null || sectionCode == null) {
			if(publisherCode == null)
				Logger.writeLog(LogLevel.Warn, "Man publisher code is empty.");
			if(mediaCode == null)
				Logger.writeLog(LogLevel.Warn, "Man media code is empty.");
			if(sectionCode == null)
				Logger.writeLog(LogLevel.Warn, "Man section code is empty.");
			return false;
		}

		isInterstitial = 0;
		adformat = Constants.ADFORMAT_BANNER;

		if (adView == null) {
			try {
				this.parentAdView = parentAdView;
				adView	= new AdView(baseActivity, 0, 0, 1);
				adView.setAdViewCode(publisherCode, mediaCode, sectionCode);
				adView.isAnimateImageBanner(true);
				adView.setAdListener(this);
				adView.setVisibility(View.GONE);
				adView.setLoaction(false);

				LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(
						ViewGroup.LayoutParams.MATCH_PARENT,
						ViewGroup.LayoutParams.WRAP_CONTENT, 0.0F);
				adView.setLayoutParams(params);

				parentAdView.addView(adView, 0);
				adView.StartService();

			} catch (Exception e) {
				adView = null;
				return false;
			}
			return true;
		}
		return false;
	}

	@Override
	public boolean loadInterstitialAd(Activity baseActivity, RelativeLayout parentAdView) {
		if (publisherCode == null || mediaCode == null || sectionCode == null) {
			if(publisherCode == null)
				Logger.writeLog(LogLevel.Warn, "Man publisher code is empty.");
			if(mediaCode == null)
				Logger.writeLog(LogLevel.Warn, "Man media code is empty.");
			if(sectionCode == null)
				Logger.writeLog(LogLevel.Warn, "Man section code is empty.");
			return false;
		}

		isInterstitial = 1;
		adformat = Constants.ADFORMAT_BANNER;

		if(interstitial == null) {
			interstitial = new AdInterstitialView(baseActivity);
			interstitial.setAdListener(this);
			interstitial.setAdViewCode(publisherCode, mediaCode, sectionCode);
			interstitial.setLoaction(false);
			baseActivity.runOnUiThread(new Runnable() {
				@Override
				public void run() {
					interstitial.ShowInterstitialView();
				}
			});

			return true;
		}

		return false;
	}

	@Override
	public boolean canLoadOnly() {
		return false;
	}

	// banner and interstitial listener (load success and fail)

	public void onFailedToReceive(View v, int errorCode) {
		String strAdType = "banner";
		if (interstitial == v)
			strAdType = "interstitial";

		if (errorCode < 0) {
			if (!isResultFired) {
				loadResult = false;
				if (strAdType == "banner") {
					try {
						((Activity) adView.getContext()).runOnUiThread(new Runnable() {
							@Override
							public void run() {
								adView.setVisibility(View.GONE);
								parentAdView.removeView(adView);
							}
						});
					}catch (Exception e) {
					}

					if(command != null)
						command.cancel();

					command = new DelayedCommand(1);
					command.setTag(CID_BANNER_LOAD_FAILED);
					command.setData(String.valueOf(errorCode));
					command.setOnCommandResult(this);
					command.execute();
				}else {
					String errorMsg = getErrorMsg(errorCode);
					fireOnAdReceiveAdFailed(AdMixer.AX_ERR_ADAPTER, "Man onFailedToReceive (" + strAdType + "): " + errorCode + ", " + errorMsg);
				}
			}
		} else {
			if (!isResultFired) {
				loadResult = true;
				Logger.writeLog(LogLevel.Debug, "Man Load Success (" + strAdType + ")");
				if (strAdType == "banner") {
					fireOnAdReceived();
				}

				if (strAdType == "interstitial") {
					if(command != null)
						command.cancel();

					command = new DelayedCommand(1);
					command.setTag(CID_INTERSTITIAL_LOAD_COMPLETE);
					command.setOnCommandResult(this);
					command.execute();
				}
			}
		}
	}

	private String getErrorMsg(int errorCode) {
		String errorMsg = "";
		switch (errorCode) {
			case AdInfoKey.NETWORK_ERROR:
				errorMsg = "NETWORK_ERROR";
				break;
			case AdInfoKey.AD_SERVER_ERROR:
				errorMsg = "SERVER_ERROR";
				break;
			case AdInfoKey.AD_API_TYPE_ERROR:
				errorMsg = "API_TYPE_ERROR";
				break;
			case AdInfoKey.AD_APP_ID_ERROR:
				errorMsg = "APP_ID_ERROR";
				break;
			case AdInfoKey.AD_WINDOW_ID_ERROR:
				errorMsg = "WINDOW_ID_ERROR";
				break;
			case AdInfoKey.AD_ID_BAD:
				errorMsg = "AD_ID_BAD";
				break;
			case AdInfoKey.AD_ID_NO_AD:
				errorMsg = "AD_ID_NO_AD";
				break;
			case AdInfoKey.AD_CREATIVE_ERROR:
				errorMsg = "CREATIVE_ERROR";
				break;
			case AdInfoKey.AD_ETC_ERROR:
				errorMsg = "AD_ETC_ERROR";
				break;
			default:
				break;
		}
		return errorMsg;
	}

	@Override
	public void onChargeableBannerType(View arg0, boolean arg1) {
		// TODO Auto-generated method stub
	}

	@Override
	public void onInterClose(View v) {
		Logger.writeLog(LogLevel.Debug, "Man onInterClose");
		fireOnInterstitialAdClosed();
	}

	@Override
	public void onAdClick(View v) {
		Logger.writeLog(LogLevel.Debug, "Man onAdClick");
	}

	@Override
	public void onCommandCompleted(Command command) {
		int tag = command.getTag();
		switch(tag) {
			case CID_INTERSTITIAL_LOAD_COMPLETE:
				fireOnAdReceived();
				fireOnInterstitialAdShown();
				break;
			case CID_BANNER_LOAD_FAILED:
				int errorCode = Integer.parseInt(command.getData().toString());
				String errorMsg = getErrorMsg(errorCode);
				fireOnAdReceiveAdFailed(AdMixer.AX_ERR_ADAPTER, "Man onFailedToReceive (banner): " + errorCode + ", " + errorMsg);
		}

	}

}
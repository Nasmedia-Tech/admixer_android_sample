package com.admixer.sample.adapters;

import android.annotation.SuppressLint;
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
import com.mopub.mobileads.MoPubErrorCode;
import com.mopub.mobileads.MoPubView;
import com.mopub.mobileads.MoPubView.MoPubAdSize;
import com.mopub.mobileads.MoPubView.BannerAdListener;
import com.mopub.mobileads.MoPubInterstitial;
import com.mopub.mobileads.MoPubInterstitial.InterstitialAdListener;

import org.jetbrains.annotations.NotNull;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.Arrays;

import static com.admixer.ads.AdMixer.AX_ERR_ADAPTER;

/**
 * Mopub Adapter
 */
public class MopubAdapter extends BaseAdAdapter implements BannerAdListener, InterstitialAdListener {
    String adunitId;

    MoPubView adView;
    MoPubInterstitial interstitial;
    boolean hasAd = false;

    //adapterAdInfo
    String[] adSizeList = {"MATCH_VIEW", "HEIGHT_50", "HEIGHT_90", "HEIGHT_250", "HEIGHT_280"};
    String adSize = adSizeList[0];

    public String getAdapterName() {
        return AdMixer.ADAPTER_MOPUB;
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
            if (adapterAdInfo != null) {

                if (adapterAdInfo.has("adSize")) {
                    String size = adapterAdInfo.getString("adSize");
                    if (Arrays.asList(adSizeList).contains(size))
                        adSize = size;
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    @Override
    public void closeAdapter() {
        super.closeAdapter();
        Logger.writeLog(LogLevel.Debug, "Mopub close Adapter");

        if (adView != null) {
            adView.destroy();
            adView = null;
        }

        if (interstitial != null) {
            interstitial.destroy();
            interstitial = null;
        }
    }

    @SuppressLint("MissingPermission")
    public boolean loadAd(Activity baseActivity, RelativeLayout parentAdView) {
        if (adunitId == null) {
            Logger.writeLog(LogLevel.Warn, "Mopub adunit id is empty.");
            return false;
        }

        adformat = Constants.ADFORMAT_BANNER;
        isInterstitial = 0;

        RelativeLayout.LayoutParams params = new RelativeLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        params.addRule(RelativeLayout.CENTER_HORIZONTAL);

        adView = new MoPubView(baseActivity);
        adView.setAdUnitId(adunitId);

        MoPubAdSize adSize;
        switch (this.adSize) {
            case "MATCH_VIEW":
                adSize = MoPubAdSize.MATCH_VIEW;
                params = new RelativeLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
                break;
            case "HEIGHT_50":
                adSize = MoPubAdSize.HEIGHT_50;
                break;
            case "HEIGHT_90":
                adSize = MoPubAdSize.HEIGHT_90;
                break;
            case "HEIGHT_250":
                adSize = MoPubAdSize.HEIGHT_250;
                break;
            case "HEIGHT_280":
                adSize = MoPubAdSize.HEIGHT_280;
                break;
            default:
                adSize = MoPubAdSize.MATCH_VIEW;
                params = new RelativeLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
                break;
        }
        adView.setAdSize(adSize);
        adView.setLayoutParams(params);
        adView.setBannerAdListener(this);
        parentAdView.addView(adView);
        adView.loadAd();

        Logger.writeLog(LogLevel.Debug, "Mopub load banner");

        return true;
    }

    @SuppressLint("MissingPermission")
    public boolean loadInterstitialAd(Activity baseActivity, RelativeLayout parentAdView) {
        if (adunitId == null) {
            Logger.writeLog(LogLevel.Warn, "Mopub adunit id is empty.");
            return false;
        }

        interstitial = new MoPubInterstitial(baseActivity, adunitId);
        interstitial.setInterstitialAdListener(this);
        interstitial.load();

        return true;
    }

    public View getView() {
        return adView;
    }

    @Override
    public void onBannerLoaded(@NotNull MoPubView banner) {
        Logger.writeLog(LogLevel.Debug, "Mopub onBannerLoaded");
        fireOnAdReceived();
    }

    @Override
    public void onBannerFailed(MoPubView banner, MoPubErrorCode errorCode) {
        fireOnAdReceiveAdFailed(AX_ERR_ADAPTER, "onBannerFailed(" + errorCode + ")");
    }

    @Override
    public void onBannerClicked(MoPubView banner) {
        Logger.writeLog(LogLevel.Debug, "Mopub onBannerClicked");
    }

    @Override
    public void onBannerExpanded(MoPubView banner) {
        Logger.writeLog(LogLevel.Debug, "Mopub onBannerExpanded");
    }

    @Override
    public void onBannerCollapsed(MoPubView banner) {
        Logger.writeLog(LogLevel.Debug, "Mopub onBannerCollapsed");
    }

    @Override
    public void onInterstitialLoaded(MoPubInterstitial interstitial) {
        Logger.writeLog(LogLevel.Debug, "Mopub onInterstitialLoaded");
        fireOnAdReceived();

        hasAd = true;
        if (!loadOnly) {
            show();
        }
    }

    @Override
    public void onInterstitialFailed(MoPubInterstitial interstitial, MoPubErrorCode errorCode) {
        fireOnAdReceiveAdFailed(AX_ERR_ADAPTER, "onInterstitialFailed(" + errorCode + ")");
    }

    @Override
    public void onInterstitialShown(MoPubInterstitial interstitial) {
        Logger.writeLog(LogLevel.Debug, "Mopub onInterstitialShown");
    }

    @Override
    public void onInterstitialClicked(MoPubInterstitial interstitial) {
        Logger.writeLog(LogLevel.Debug, "Mopub onInterstitialClicked");
    }

    @Override
    public void onInterstitialDismissed(MoPubInterstitial interstitial) {
        Logger.writeLog(LogLevel.Debug, "Mopub onInterstitialDismissed");
        fireOnInterstitialAdClosed();
    }

    @Override
    public boolean canLoadOnly() {
        return true;
    }

    @Override
    public boolean show() {
        if (!hasAd || interstitial == null)
            return false;

        hasAd = false;
        if (interstitial.isReady()) {
            interstitial.show();
            Logger.writeLog(LogLevel.Debug, "Mopub interstitial show");
            fireOnInterstitialAdShown();
            return true;
        } else {
            return false;
        }
    }
}
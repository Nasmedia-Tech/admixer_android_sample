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
import com.smaato.sdk.banner.widget.BannerError;
import com.smaato.sdk.banner.widget.BannerView;
import com.smaato.sdk.banner.ad.BannerAdSize;
import com.smaato.sdk.core.SmaatoSdk;
import com.smaato.sdk.interstitial.EventListener;
import com.smaato.sdk.interstitial.Interstitial;
import com.smaato.sdk.interstitial.InterstitialAd;
import com.smaato.sdk.interstitial.InterstitialError;
import com.smaato.sdk.interstitial.InterstitialRequestError;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.Arrays;

import androidx.annotation.NonNull;

import static com.admixer.ads.AdMixer.AX_ERR_ADAPTER;

/**
 * Smaato Adapter
 */
public class SmaatoAdapter extends BaseAdAdapter implements BannerView.EventListener, EventListener {
    String adspaceId;

    BannerView adView;
    boolean hasAd = false;

    Activity baseActivity;

    //adapterAdInfo
    String[] adSizeList = {"XX_LARGE_320x50", "MEDIUM_RECTANGLE_300x250", "LEADERBOARD_728x90", "SKYSCRAPER_120x600"};
    String adSize = adSizeList[0];

    public String getAdapterName() {
        return AdMixer.ADAPTER_SMAATO;
    }

    @Override
    public void initAdapter(Context context, JSONObject adConfig, AdInfo adInfo) {
        super.initAdapter(context, adConfig, adInfo);

        try {
            adspaceId = keyInfo.getString("adspace_id");
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

        boolean coppa = false;
        if(AdMixer.getTagForChildDirectedTreatment() == 1)
            coppa = true;
        SmaatoSdk.setCoppa(coppa);
    }

    @Override
    public void closeAdapter() {
        super.closeAdapter();
        Logger.writeLog(LogLevel.Debug, "Smaato close Adapter");

        if(adView != null) {
            adView.destroy();
            adView = null;
        }

    }

    @SuppressLint("MissingPermission")
    public boolean loadAd(Activity baseActivity, RelativeLayout parentAdView) {
        if(adspaceId == null) {
            Logger.writeLog(LogLevel.Warn, "Smaato adspace id is empty.");
            return false;
        }

        adformat = Constants.ADFORMAT_BANNER;
        isInterstitial = 0;
        this.baseActivity = baseActivity;

        RelativeLayout.LayoutParams params 	= new RelativeLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        params.addRule(RelativeLayout.CENTER_HORIZONTAL);

        adView = new BannerView(baseActivity);

        BannerAdSize adSize;
        switch(this.adSize) {
            case "XX_LARGE_320x50" :
                adSize	= BannerAdSize.XX_LARGE_320x50;
                params = new RelativeLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
                break;
            case "MEDIUM_RECTANGLE_300x250" :
                adSize 	= BannerAdSize.MEDIUM_RECTANGLE_300x250;
                break;
            case "LEADERBOARD_728x90" :
                adSize 	= BannerAdSize.LEADERBOARD_728x90;
                break;
            case "SKYSCRAPER_120x600" :
                adSize	= BannerAdSize.SKYSCRAPER_120x600;
                break;
            default :
                adSize	= BannerAdSize.XX_LARGE_320x50;
                params = new RelativeLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
                break;
        }

        adView.setLayoutParams(params);
        adView.setEventListener(this);
        parentAdView.addView(adView);
        adView.loadAd(adspaceId, adSize);

        Logger.writeLog(LogLevel.Debug, "Smaato load banner");

        return true;
    }

    @SuppressLint("MissingPermission")
    public boolean loadInterstitialAd(Activity baseActivity, RelativeLayout parentAdView) {
        if(adspaceId == null) {
            Logger.writeLog(LogLevel.Warn, "Smaato adspace id is empty.");
            return false;
        }

        adformat = Constants.ADFORMAT_BANNER;
        isInterstitial = 1;
        this.baseActivity = baseActivity;

        Interstitial.loadAd(adspaceId, this);

        return true;
    }

    public View getView() {
        return adView;
    }

    @Override
    public void onAdLoaded(@NonNull BannerView bannerView) {
        Logger.writeLog(LogLevel.Debug, "Smaato banner onAdLoaded");
        fireOnAdReceived();
    }

    @Override
    public void onAdFailedToLoad(@NonNull BannerView bannerView, @NonNull BannerError bannerError) {
        fireOnAdReceiveAdFailed(AX_ERR_ADAPTER, "onAdFailedToLoad("+bannerError.toString()+")");
    }

    @Override
    public void onAdImpression(@NonNull BannerView bannerView) {
        Logger.writeLog(LogLevel.Debug, "Smaato banner onAdImpression");
    }

    @Override
    public void onAdClicked(@NonNull BannerView bannerView) {
        Logger.writeLog(LogLevel.Debug, "Smaato banner onAdClicked");
    }

    @Override
    public void onAdTTLExpired(@NonNull BannerView bannerView) {
        Logger.writeLog(LogLevel.Debug, "Smaato banner onAdTTLExpired");
    }

    @Override
    public void onAdLoaded(@NonNull InterstitialAd interstitialAd) {
        Logger.writeLog(LogLevel.Debug, "Smaato interstitial onAdLoaded");
        fireOnAdReceived();

        interstitialAd.showAd(this.baseActivity);
    }

    @Override
    public void onAdFailedToLoad(@NonNull InterstitialRequestError interstitialRequestError) {
        fireOnAdReceiveAdFailed(AX_ERR_ADAPTER, "onAdFailedToLoad("+interstitialRequestError.toString()+")");
    }

    @Override
    public void onAdError(@NonNull InterstitialAd interstitialAd, @NonNull InterstitialError interstitialError) {
        Logger.writeLog(LogLevel.Debug, "Smaato interstitial onAdError");
    }

    @Override
    public void onAdOpened(@NonNull InterstitialAd interstitialAd) {
        Logger.writeLog(LogLevel.Debug, "Smaato interstitial onAdOpened");
        fireOnInterstitialAdShown();
    }

    @Override
    public void onAdClosed(@NonNull InterstitialAd interstitialAd) {
        Logger.writeLog(LogLevel.Debug, "Smaato interstitial onAdClosed");
        fireOnInterstitialAdClosed();
    }

    @Override
    public void onAdClicked(@NonNull InterstitialAd interstitialAd) {
        Logger.writeLog(LogLevel.Debug, "Smaato interstitial onAdClicked");
    }

    @Override
    public void onAdImpression(@NonNull InterstitialAd interstitialAd) {
        Logger.writeLog(LogLevel.Debug, "Smaato interstitial onAdImpression");
    }

    @Override
    public void onAdTTLExpired(@NonNull InterstitialAd interstitialAd) {
        Logger.writeLog(LogLevel.Debug, "Smaato interstitial onAdTTLExpired");
    }

    @Override
    public boolean canLoadOnly() {
        return false;
    }

}

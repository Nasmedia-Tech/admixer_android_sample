package com.admixer.sample.adapters;

import android.app.Activity;
import android.content.Context;
import android.content.pm.PackageManager;
import android.os.Handler;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.WebViewClient;
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
import com.mezzomedia.man.AdConfig;
import com.mezzomedia.man.AdEvent;
import com.mezzomedia.man.AdListener;
import com.mezzomedia.man.AdResponseCode;
import com.mezzomedia.man.data.AdData;
import com.mezzomedia.man.view.AdManPage;
import com.mezzomedia.man.view.AdManView;

import org.json.JSONObject;

public class ManAdapter extends BaseAdAdapter implements AdListener, OnCommandCompletedListener {

    String id;
    int publisherCode;
    int mediaCode;
    int sectionCode;
    String storeUrl;
    String appId;
    String appName;
    String isPopup;

    AdData adData;
    Activity baseActivity;

    AdManView adView;
    AdManPage interstitial;
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
            publisherCode = keyInfo.getInt("a_publisher");
            mediaCode = keyInfo.getInt("a_media");
            sectionCode = keyInfo.getInt("a_section");
        } catch (Exception e) {
            Logger.writeLog(LogLevel.Warn, "Man key info is missing");
        }

        try {
            JSONObject adapterAdInfo = adInfo.getAdapterAdInfo(getAdapterName());
            if (adapterAdInfo != null) {
                id = adapterAdInfo.has("id") ? adapterAdInfo.getString("id") : Constants.ADFORMAT_BANNER;
                storeUrl = adapterAdInfo.has("storeUrl") ? adapterAdInfo.getString("storeUrl") : "http://admixer.co.kr";
                appId = adapterAdInfo.has("appId") ? adapterAdInfo.getString("appId") : baseActivity.getPackageName();
                appName = adapterAdInfo.has("appName") ? adapterAdInfo.getString("appName") : (String) baseActivity.getPackageManager().getApplicationLabel(baseActivity.getPackageManager().getApplicationInfo(baseActivity.getPackageName(), PackageManager.GET_UNINSTALLED_PACKAGES));
                isPopup = adapterAdInfo.has("isPopup") ? adapterAdInfo.getString("isPopup") : AdConfig.USED;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void closeAdapter() {
        super.closeAdapter();
        Logger.writeLog(LogLevel.Debug, "Man close Adapter");

        onDestroyBanner();
        destroyInter();

        if (command != null) {
            command.cancel();
            command = null;
        }
    }

    @Override
    public boolean loadAd(Activity baseActivity, RelativeLayout parentAdView) {
        if (publisherCode == 0 || mediaCode == 0 || sectionCode == 0) {
            if (publisherCode == 0)
                Logger.writeLog(LogLevel.Warn, "Man publisher code is empty.");
            if (mediaCode == 0)
                Logger.writeLog(LogLevel.Warn, "Man media code is empty.");
            if (sectionCode == 0)
                Logger.writeLog(LogLevel.Warn, "Man section code is empty.");
            return false;
        }

        isInterstitial = 0;
        adformat = Constants.ADFORMAT_BANNER;
        this.baseActivity = baseActivity;

        if (adView == null) {
            try {
                this.parentAdView = parentAdView;
                adData = new AdData();

                adData.major(id, AdConfig.API_BANNER, publisherCode, mediaCode, sectionCode, storeUrl, appId, appName, getWidth(), getHeight());
                adData.isPermission(AdConfig.NOT_USED, AdConfig.NOT_USED);

                adView = new AdManView(baseActivity);

                ViewGroup.LayoutParams params = new ViewGroup.LayoutParams(
                        ViewGroup.LayoutParams.WRAP_CONTENT,
                        ViewGroup.LayoutParams.WRAP_CONTENT);
                adView.setVisibility(View.GONE);
                adView.setLayoutParams(params);
				parentAdView.addView(adView);

                adView.setData(adData, this);

                adView.request(new Handler());

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
        if (publisherCode == 0 || mediaCode == 0 || sectionCode == 0) {
            if (publisherCode == 0)
                Logger.writeLog(LogLevel.Warn, "Man publisher code is empty.");
            if (mediaCode == 0)
                Logger.writeLog(LogLevel.Warn, "Man media code is empty.");
            if (sectionCode == 0)
                Logger.writeLog(LogLevel.Warn, "Man section code is empty.");
            return false;
        }

        isInterstitial = 1;
        adformat = Constants.ADFORMAT_BANNER;
        this.baseActivity = baseActivity;

        if (interstitial == null) {
            try {
                adData = new AdData();

                adData.major(id, AdConfig.API_INTER, publisherCode, mediaCode, sectionCode, storeUrl, appId, appName, getWidth(), getHeight());
                adData.isPermission(AdConfig.NOT_USED, AdConfig.NOT_USED);
                adData.setPopup(isPopup);

                interstitial = new AdManPage(baseActivity);
                interstitial.setData(adData, this);
                interstitial.request(new Handler());
            } catch (Exception e) {
                interstitial = null;
                return false;
            }

            return true;
        }

        return false;
    }

    @Override
    public boolean canLoadOnly() {
        return false;
    }

    @Override
    public void onCommandCompleted(Command command) {
        int tag = command.getTag();
        switch (tag) {
            case CID_INTERSTITIAL_LOAD_COMPLETE:
                fireOnAdReceived();
                fireOnInterstitialAdShown();
                break;
            case CID_BANNER_LOAD_FAILED:
                String errorCode = command.getData().toString();
                String errorMsg = getErrorMsg(errorCode);
                fireOnAdReceiveAdFailed(AdMixer.AX_ERR_ADAPTER, "Man onFailedToReceive (banner): " + errorCode + ", " + errorMsg);
        }

    }

    @Override
    public void onAdSuccessCode(Object v, String id, final String type, final String status, String jsonDataString) {
        (baseActivity).runOnUiThread(() -> {
            if (AdResponseCode.Status.SUCCESS.equals(status)) {
                String strAdType = "banner";
                if (interstitial != null)
                    strAdType = "interstitial";

                if (!isResultFired) {
                    loadResult = true;
                    Logger.writeLog(LogLevel.Debug, "Man Load Success (" + strAdType + ")");

                    if (strAdType.equals("banner")) {
                        adView.setVisibility(View.VISIBLE);
                        fireOnAdReceived();
                    }

                    if (strAdType.equals("interstitial")) {

                        if (command != null)
                            command.cancel();

                        command = new DelayedCommand(1);
                        command.setTag(CID_INTERSTITIAL_LOAD_COMPLETE);
                        command.setOnCommandResult(this);
                        command.execute();
                    }
                }

            }
        });
    }

    /**
     * 광고 호출 실패 에러
     */
    @Override
    public void onAdFailCode(Object v, String id, String type, String status, String jsonDataString) {
        String strAdType = "banner";
        if (interstitial != null)
            strAdType = "interstitial";

        if (!isResultFired) {
            loadResult = false;
            if (strAdType.equals("banner")) {
                try {
                    ((Activity) adView.getContext()).runOnUiThread(() -> {
                        adView.setVisibility(View.GONE);
                        onDestroyBanner();
                    });
                } catch (Exception e) {
                    Logger.writeLog(LogLevel.Warn, "Man onAdFailCode (" + e.getMessage() + ")");
                }

                if (command != null)
                    command.cancel();

                command = new DelayedCommand(1);
                command.setTag(CID_BANNER_LOAD_FAILED);
                command.setData(status);
                command.setOnCommandResult(this);
                command.execute();
            } else {
                destroyInter();

                String errorMsg = getErrorMsg(status);
                fireOnAdReceiveAdFailed(AdMixer.AX_ERR_ADAPTER, "Man onFailedToReceive (" + strAdType + "): " + status + ", " + errorMsg);
            }
        }
    }

    /**
     * 광고 호출 실패 에러(웹뷰 에러)
     */
    @Override
    public void onAdErrorCode(Object v, String id, String type, String status, String failingUrl) {

        if (interstitial != null) {
            destroyInter();
        } else {
            onDestroyBanner();
        }

        int errorCode = Integer.parseInt(type);
        switch (errorCode) {
            case WebViewClient.ERROR_AUTHENTICATION:
                //서버에서 사용자 인증 실패
                break;
            case WebViewClient.ERROR_BAD_URL:
                //잘못된 URL
                break;
            case WebViewClient.ERROR_CONNECT:
                //서버로 연결 실패
                break;
            case WebViewClient.ERROR_FAILED_SSL_HANDSHAKE:
                //SSL handshake 수행 실패
                break;
            case WebViewClient.ERROR_FILE:
                //일반 파일 오류
                break;
            case WebViewClient.ERROR_FILE_NOT_FOUND:
                //파일을 찾을 수 없습니다
                break;
            case WebViewClient.ERROR_HOST_LOOKUP:
                //서버 또는 프록시 호스트 이름 조회 실패
                break;
            case WebViewClient.ERROR_IO:
                //서버에서 읽거나 서버로 쓰기 실패
                break;
            case WebViewClient.ERROR_PROXY_AUTHENTICATION:
                //프록시에서 사용자 인증 실패
                break;
            case WebViewClient.ERROR_REDIRECT_LOOP:
                //너무 많은 리디렉션
                break;
            case WebViewClient.ERROR_TIMEOUT:
                //연결 시간 초과
                break;
            case WebViewClient.ERROR_TOO_MANY_REQUESTS:
                //페이지 로드중 너무 많은 요청 발생
                break;
            case WebViewClient.ERROR_UNKNOWN:
                //일반 오류
                break;
            case WebViewClient.ERROR_UNSUPPORTED_AUTH_SCHEME:
                //지원되지 않는 인증 체계
                break;
            case WebViewClient.ERROR_UNSUPPORTED_SCHEME:
                //URI가 지원되지 않는 방식
                break;
        }
    }

    /**
     * 광고에서 발생하는 이벤트
     */
    @Override
    public void onAdEvent(Object v, String id, String type, String status, String jsonDataString) {
        if (AdEvent.Type.CLICK.equals(type)) {
            //광고 클릭 이벤트
            Logger.writeLog(LogLevel.Debug, "Man onAdClick");
        } else if (AdEvent.Type.CLOSE.equals(type)) {
            //광고 닫기 이벤트
            Logger.writeLog(LogLevel.Debug, "Man onInterClose");
            fireOnInterstitialAdClosed();
        } else if (AdEvent.Type.COMPLETE.equals(type)) {
            //동영상 q5 트래킹
        } else if (AdEvent.Type.IMP.equals(type)) {
            //동영상 노출 트래킹
        } else if (AdEvent.Type.SKIP.equals(type)) {
            //동영상 스킵 이벤트
        } else if (AdEvent.Type.START.equals(type)) {
            //동영상 q1 이벤트
        } else if (AdEvent.Type.FIRSTQ.equals(type)) {
            //동영상 q2 이벤트
        } else if (AdEvent.Type.MIDQ.equals(type)) {
            //동영상 q3 이벤트
        } else if (AdEvent.Type.THIRDQ.equals(type)) {
            //동영상 q4 이벤트
        } else if (AdEvent.Type.OBJSHOW.equals(type)) {
            //사용 안함
        } else if (AdEvent.Type.OBJHIDE.equals(type)) {
            //사용 안함
        } else if (AdEvent.Type.ENDED.equals(type)) {
            //동영상 광고 종료
        }
    }

    @Override
    public void onPermissionSetting(Object o, String s) {

    }

    public void onDestroyBanner() {
        if (adView != null) {
            adView.onDestroy();
        }
        adView = null;
        Logger.writeLog(LogLevel.Debug, "Man onDestroyBanner close banner");
    }

    public void destroyInter() {
        if (interstitial != null) {
            interstitial.onDestroy();
            interstitial = null;
            Logger.writeLog(LogLevel.Debug, "Man destroyInter close interstitial");
        }
    }


    /**
     * 광고 호출 실패 에러
     */
    private String getErrorMsg(String status) {
        String errorMsg = "";
        if (AdResponseCode.Status.NOAD.equals(status)) {
            //광고소진
            errorMsg = "NOAD";
        } else if (AdResponseCode.Status.TIMEOUT.equals(status)) {
            //네트워크 타임아웃
            errorMsg = "TIMEOUT";
        } else if (AdResponseCode.Status.ERROR_PARSING.equals(status)) {
            //파싱 에러
            errorMsg = "ERROR_PARSING";
        } else if (AdResponseCode.Status.DUPLICATIONCALL.equals(status)) {
            //중복호출
            errorMsg = "DUPLICATIONCALL";
        } else if (AdResponseCode.Status.ERROR.equals(status)) {
            //예외적이 오류
            errorMsg = "ERROR";
        } else if (AdResponseCode.Status.ERROR_NOTSUPPORT_BROWSER.equals(status)) {
            //미지원 브라우져
            errorMsg = "ERROR_NOTSUPPORT_BROWSER";
        } else if (AdResponseCode.Status.ERROR_NOTSUPPORT_IOS.equals(status)) {
            //미지원 Ios version
            errorMsg = "ERROR_NOTSUPPORT_IOS";
        } else if (AdResponseCode.Status.ERROR_NOTSUPPORT_ANDROID.equals(status)) {
            //미지원 Android version
            errorMsg = "ERROR_NOTSUPPORT_ANDROID";
        } else if (AdResponseCode.Status.NEEDSYNC.equals(status)) {
            //ajax error
            errorMsg = "NEEDSYNC";
        } else if (AdResponseCode.Status.DEVICE_NETWORK_ERROR.equals(status)) {
            //단말기 네트워크 상태 에러
            errorMsg = "DEVICE_NETWORK_ERROR";
        } else if (AdResponseCode.Status.DEVICE_RENDERING_TIMEOUT.equals(status)) {
            //광고 랜더링 타임아웃
            errorMsg = "DEVICE_RENDERING_TIMEOUT";
        } else if (AdResponseCode.Status.DEVICE_SETTING_ERROR.equals(status)) {
            //광고 데이터 잘못설정 에러
            errorMsg = "DEVICE_SETTING_ERROR";
        } else if (AdResponseCode.Status.DEVICE_AD_INTERVAL.equals(status)) {
            //광고 호출 이후에 설정된 시간 이내 호출 에러
            errorMsg = "DEVICE_AD_INTERVAL";
        }

        return errorMsg;
    }


}
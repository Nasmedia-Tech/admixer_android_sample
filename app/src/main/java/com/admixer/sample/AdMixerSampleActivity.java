package com.admixer.sample;

import android.app.Activity;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup.LayoutParams;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.Toast;

import com.admixer.ads.AdInfo;
import com.admixer.ads.AdMixer;
import com.admixer.ads.AdView;
import com.admixer.ads.AdViewListener;
import com.admixer.ads.InterstitialAd;
import com.admixer.ads.InterstitialAdListener;
import com.admixer.common.Logger;
import com.admixer.common.Logger.LogLevel;
import com.facebook.ads.AudienceNetworkAds;
import com.google.android.gms.ads.MobileAds;
import com.google.android.gms.ads.initialization.InitializationStatus;
import com.google.android.gms.ads.initialization.OnInitializationCompleteListener;
import com.mezzomedia.man.AdConfig;
import com.mezzomedia.man.view.AdManView;
import com.mopub.common.MoPub;
import com.mopub.common.SdkConfiguration;
import com.mopub.common.SdkInitializationListener;
import com.mopub.common.logging.MoPubLog;
import com.smaato.sdk.core.SmaatoSdk;

import java.util.ArrayList;
import java.util.Arrays;

public class AdMixerSampleActivity extends Activity implements InterstitialAdListener, AdViewListener {
	
	final static String TAG = "AdMixerSampleActivity";
	final static String mediaKey = "YOUR_MEDIA_KEY";

	final static String adunitID_320x50 = "YOUR_ADUNIT_ID";
	final static String adunitID_300x250 = "YOUR_ADUNIT_ID";
	final static String adunitID_320x480_fullscreen = "YOUR_ADUNIT_ID";
	final static ArrayList<String> adunits = new ArrayList<String> (
			Arrays.asList(adunitID_320x50, adunitID_300x250, adunitID_320x480_fullscreen)
	);

	RelativeLayout layout;
	AdView adView1;
	AdView adView2;
	InterstitialAd interstitialAd;

	/** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

		// 로그 레벨 설정
        Logger.setLogLevel(LogLevel.Verbose);

		// 필요한 adapter 등록
//		AdMixer.registerAdapter(AdMixer.ADAPTER_ADFIT, "com.admixer.sample.adapters.AdfitAdapter");
//		AdMixer.registerAdapter(AdMixer.ADAPTER_ADMOB, "com.admixer.sample.adapters.AdmobAdapter");
//		AdMixer.registerAdapter(AdMixer.ADAPTER_CAULY, "com.admixer.sample.adapters.CaulyAdapter");
//		AdMixer.registerAdapter(AdMixer.ADAPTER_DAWIN_CLICK, "com.admixer.sample.adapters.DawinClickAdapter");
//		AdMixer.registerAdapter(AdMixer.ADAPTER_FACEBOOK, "com.admixer.sample.adapters.FacebookAdapter");
//		AdMixer.registerAdapter(AdMixer.ADAPTER_MAN, "com.admixer.sample.adapters.ManAdapter");
//		AdMixer.registerAdapter(AdMixer.ADAPTER_MOPUB, "com.admixer.sample.adapters.MopubAdapter");
//		AdMixer.registerAdapter(AdMixer.ADAPTER_SMAATO, "com.admixer.sample.adapters.SmaatoAdapter");

		// AdMixer 초기화를 위해 반드시 광고호출 전에 앱에서 1회 호출해주셔야 합니다.
		// adunits 파라미터는 앱 내에서 사용할 모든 adunit_id를 배열형태로 넘겨주셔야 합니다.
	    // YOUR_ADUNIT_ID 는 Admixer 사이트 미디어 > 미디어관리 > 미디어 등록에서 발급받은 Adunit ID 입니다.

		AdMixer.init(this, mediaKey, adunits);

		// COPPA(아동보호법) 관련 항목 설정값 - 선택사항
		// Smaato 의 경우 테스트 광고 요청 시 False 로 변경해야 테스트 광고 송출 가능.
		AdMixer.setTagForChildDirectedTreatment(AdMixer.AX_TAG_FOR_CHILD_DIRECTED_TREATMENT_TRUE);

		// MANPLUS 초기화
		AdManView.init(this, new Handler());

		// Admob 적용 시에 SDK 초기화 호출이 필요
		MobileAds.initialize(this, initializationStatus -> { });

		// Facebook 적용 시에 SDK 초기화 호출이 필요
		AudienceNetworkAds.initialize(this);
		// AdSettings.addTestDevice(""); // Facebook 테스트 광고 설정, Facebook Audience 가이드 확인
		// AdSettings.setTestMode(true); // Facebook 테스트 모드로 광고시 해당 코드 필요.

		// Mopub 적용 시에 SDK 초기화 호출이 필요
		SdkConfiguration sdkConfiguration = new SdkConfiguration.Builder("YOUR_ANY_MOPUB_ADUNIT_ID")
													.withLogLevel(MoPubLog.LogLevel.DEBUG).build();
		MoPub.initializeSdk(this, sdkConfiguration, () -> { });

		// Smaato 적용 시에 SDK 초기화 호출이 필요
		SmaatoSdk.init(this.getApplication(), "YOUR_SMAATO_PUBLISHER_ID");

		layout = new RelativeLayout(this);
		layout.setBackgroundColor(Color.WHITE);
		RelativeLayout.LayoutParams param = new RelativeLayout.LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT);
		setContentView(layout, param);

        addButtons();
		addBannerView();
    }

    @Override
    protected void onPause() { // 생명주기에 따라 설정이 반드시 필요합니다.
		if(adView1 != null)
			adView1.onPause();
		if(adView2 != null)
			adView2.onPause();

    	super.onPause();
	}

	@Override
	protected void onDestroy() {
		if(interstitialAd != null) {
			interstitialAd.stopInterstitial();
			interstitialAd = null;
		}

    	super.onDestroy();
	}

	@Override
	protected void onResume() { // 생명주기에 따라 설정이 반드시 필요합니다.
		if(adView1 != null)
    		adView1.onResume();
		if(adView2 != null)
			adView2.onResume();

		super.onResume();
	}
        
    void addBannerView() {
    	/****************************/
    	/*** 일반배너 320*50 설정 ***/
		/****************************/

    	// 해당 영역의 adunit id값을 설정
    	AdInfo adInfo1 = new AdInfo(adunitID_320x50);
    	adInfo1.setMaxRetryCountInSlot(-1);
    	
		// Admob AdInfo
		adInfo1.setAdapterAdInfo(AdMixer.ADAPTER_ADMOB, "adSize", "BANNER");
        // Cauly AdInfo
		adInfo1.setAdapterAdInfo(AdMixer.ADAPTER_CAULY, "bannerHeight", "Fixed");
        // Facebook AdInfo
        adInfo1.setAdapterAdInfo(AdMixer.ADAPTER_FACEBOOK, "adSize", "BANNER_HEIGHT_50");
        // Smaato AdInfo
		adInfo1.setAdapterAdInfo(AdMixer.ADAPTER_SMAATO, "adSize", "XX_LARGE_320x50");
		
		// ManPlus AdInfo (ManPlus Banner 사용 시, 필수)
		adInfo1.setAdapterAdInfo(AdMixer.ADAPTER_MAN, "id", "testbanner");  // 입력 값은 테스트 값이므로 Manplus 가이드를 참조하여 입력하세요. 
		adInfo1.setAdapterAdInfo(AdMixer.ADAPTER_MAN, "storeUrl", "http://www.storeurl.com"); // 입력 값은 테스트 값이므로 Manplus 가이드를 참조하여 입력하세요. 
		adInfo1.setAdapterAdInfo(AdMixer.ADAPTER_MAN, "appId", getPackageName()); // 입력 값은 테스트 값이므로 Manplus 가이드를 참조하여 입력하세요.
		// 입력 값은 테스트 값이므로 Manplus 가이드를 참조하여 입력하세요. 
		adInfo1.setAdapterAdInfo(AdMixer.ADAPTER_MAN, "appName", getApplicationInfo().loadLabel(getPackageManager()).toString()); 
		
		adView1 = new AdView(this);
        adView1.setAdInfo(adInfo1, this);
        adView1.setAdViewListener(this);
        adView1.setBackgroundColor(Color.BLACK);

		RelativeLayout.LayoutParams params1 = new RelativeLayout.LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT); // 320*50사이즈 일때만, 가로를 디바이스에 맞추거나 영역사이즈로 설정하는 것중에 선택가능
        params1.addRule(RelativeLayout.CENTER_HORIZONTAL);
		params1.setMargins(0, 200, 0, 0);
        adView1.setLayoutParams(params1);
        layout.addView(adView1);

		/****************************/
		/*** 일반배너 300*250 설정 ***/
		/****************************/

		// 해당 영역의 adunit id값을 설정
		AdInfo adInfo2 = new AdInfo(adunitID_300x250);
		adInfo2.setMaxRetryCountInSlot(-1);

		adView2 = new AdView(this);
		adView2.setAdInfo(adInfo2, this);
		adView2.setAdViewListener(this);
		adView2.setBackgroundColor(Color.BLACK);

		// Admob AdInfo
		adInfo2.setAdapterAdInfo(AdMixer.ADAPTER_ADMOB, "adSize", "MEDIUM_RECTANGLE");

		RelativeLayout.LayoutParams params2 = new RelativeLayout.LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT); // 320*50을 제외한 사이즈일때는 영역사이즈로 설정해야 함
		params2.addRule(RelativeLayout.CENTER_HORIZONTAL);
		params2.setMargins(0, 400, 0, 0);
		adView2.setLayoutParams(params2);
        layout.addView(adView2);

    }
    
    void addInterstitialView() {
		/******************************************/
		/*** 전면배너 (fullscreen) 320*480 설정 ***/
		/******************************************/
    	if(interstitialAd != null) // 전면광고는 1회성 객체
			return;
		
		AdInfo adInfo = new AdInfo(adunitID_320x480_fullscreen);
		adInfo.setInterstitialTimeout(0); // 초단위로 전면 광고 타임아웃 설정 (기본값 : 0, 0 이면 서버 지정 시간(20)으로 처리됨)
		adInfo.setMaxRetryCountInSlot(-1); // 리로드 시간 내에 전체 AdNetwork 반복 최대 횟수(-1 : 무한, 0 : 반복 없음, n : n번 반복)
		//adInfo.setBackgroundAlpha(true); // 고수익 전면광고 노출 시 광고 외 영역 반투명처리 여부 (true: 반투명, false: 처리안함)

		/* 이 주석을 제거하시면 admixer 전면광고가 팝업형으로 노출됩니다. (admixer 네트워크만 사용가능)
		// 팝업형 전면광고 세부설정을 원하시면 아래 PopupInterstitialAdOption 설정하세요
		PopupInterstitialAdOption adConfig = new PopupInterstitialAdOption();
		// 팝업형 전면광고 노출 상태에서 뒤로가기 버튼 방지 (true : 비활성화, false : 활성화)
		adConfig.setDisableBackKey(true);
		// 왼쪽버튼. 디폴트로 제공되며, 광고를 닫는 기능이 적용되는 버튼 (버튼문구, 버튼색상)
		adConfig.setButtonLeft("광고종료", "#234234");
		// 오른쪽 버튼을 사용하고자 하면 반드시 설정하세요. 앱을 종료하는 기능을 적용하는 버튼. 미설정 시 위 광고종료 버튼만 노출
		adConfig.setButtonRight(null, null);
		// 버튼영역 색상지정
		adConfig.setButtonFrameColor(null);
		// 팝업형 전면광고 추가옵션 (com.admixer.AdInfo$InterstitialAdType.Basic : 일반전면, com.admixer.AdInfo$InterstitialAdType.Popup : 버튼이 있는 팝업형 전면)
		adInfo.setInterstitialAdType(AdInfo.InterstitialAdType.Popup, adConfig);
		*/
		
		// Manplus AdInfo (Manplus 전면광고 사용 시, 필수)
		// adInfo.setAdapterAdInfo(AdMixer.ADAPTER_MAN, "id", "testInter"); // 입력 값은 테스트 값이므로 Manplus 가이드를 참조하여 입력하세요. 
		// adInfo.setAdapterAdInfo(AdMixer.ADAPTER_MAN, "storeUrl", "http://www.storeurl.com"); // 입력 값은 테스트 값이므로 Manplus 가이드를 참조하여 입력하세요. 
		// adInfo.setAdapterAdInfo(AdMixer.ADAPTER_MAN, "appId", getPackageName()); // 입력 값은 테스트 값이므로 Manplus 가이드를 참조하여 입력하세요. 
		// 입력 값은 테스트 값이므로 Manplus 가이드를 참조하여 입력하세요. 
		// adInfo.setAdapterAdInfo(AdMixer.ADAPTER_MAN, "appName", getApplicationInfo().loadLabel(getPackageManager()).toString()); 
		// 입력 값은 테스트 값이므로 Manplus 가이드를 참조하여 입력하세요. 
		// (isPopup 설정은 AdConfig.NOT_USED, AdConfig.USED 2가지 모드 지원합니다. 전체 전면광고(AdConfig.USED) 또는 전체 팝업 광고(AdConfig.NOT_USED) 모드 2가지 입니다.)
		// adInfo.setAdapterAdInfo(AdMixer.ADAPTER_MAN, "isPopup", AdConfig.USED); // Default AdConfig.USED
		
		interstitialAd = new InterstitialAd(this);
		interstitialAd.setAdInfo(adInfo, this);
		interstitialAd.setInterstitialAdListener(this);
		interstitialAd.startInterstitial();
    }

    void addButtons() {
        Button button = new Button(this);

        button.setText("전면배너 호출");
        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT);
        params.setMargins(0, 0, 0, 0);
        button.setLayoutParams(params);
        button.setOnClickListener(v -> addInterstitialView());
        layout.addView(button);
    }

    // AdViewListener 인터페이스 구현
	@Override
	public void onReceivedAd(String adapterName, AdView adView) {
		Toast.makeText(this, "onReceivedAd", Toast.LENGTH_SHORT).show();
	}

	@Override
	public void onFailedToReceiveAd(int errorCode, String errorMsg, AdView adView) {
		Toast.makeText(this, "onFailedToReceiveAd", Toast.LENGTH_SHORT).show();
	}

	@Override
	public void onClickedAd(String arg0, AdView arg1) {
		Toast.makeText(this, "onClickedAd", Toast.LENGTH_SHORT).show();
	}

	//** InterstitialAd 이벤트들 *************
	@Override
	public void onInterstitialAdClosed(InterstitialAd arg0) {
    	interstitialAd = null;
	}

	@Override
	public void onInterstitialAdFailedToReceive(int arg0, String arg1, InterstitialAd arg2) {
		interstitialAd = null;
	}

	@Override
	public void onInterstitialAdReceived(String arg0, InterstitialAd arg1) {
		Toast.makeText(this, "onInterstitialAdReceived", Toast.LENGTH_SHORT).show();
	}

	@Override
	public void onInterstitialAdShown(String arg0, InterstitialAd arg1) {
		Toast.makeText(this, "onInterstitialAdShown", Toast.LENGTH_SHORT).show();
	}

	@Override
	public void onLeftClicked(String arg0, InterstitialAd arg1) {
		Toast.makeText(this, "onLeftClicked", Toast.LENGTH_SHORT).show();
	}

	@Override
	public void onRightClicked(String arg0, InterstitialAd arg1) {
		Toast.makeText(this, "onRightClicked", Toast.LENGTH_SHORT).show();
		finish();
	}

}
# AdMixerSample

- Admixer Android SDK Sample Project   
- Current Admixer SDK Version 2.3.1 / date. 2020.03.16

## AdMixer SDK Support Mediation Version

| AdNetwork | Version | Check Date | compatible | download
|---|:---:|---:|:---:|:---:|
| `AdMixer` | 2.1.3 | 2020.08.24 | O | [다운로드](http://admixer.co.kr/, "download link")
| `Adfit` | 3.4.0 | 2020.08.24 | O | [다운로드](https://github.com/adfit/adfit-android-sdk/, "download link")
| `Admob` | 19.3.0 | 2020.08.24 | O | [다운로드](https://developers.google.com/admob/android/sdk?hl=ko/, "download link")
| `Cauly` | 3.5.08 | 2020.08.24 | O | [다운로드](https://github.com/cauly/Android-SDK/, "download link")
| `Facebook` | 5.11.0 | 2020.08.24 | O | [다운로드](https://developers.facebook.com/docs/app-events/getting-started-app-events-android, "download link")
| `DawinClick` | 3.16.7 | 2020.08.24 | O | [다운로드](https://click.dawin.tv/poc/#/sdk, "download link")
| `MANPLUS` | 107 | 2020.08.24 | O | [다운로드](http://docs.meba.kr/s-plus/sdk/android/, "download link")
| `Mopub` | 5.13.1 | 2020.08.24 | O | [다운로드](https://developers.mopub.com/publishers/android/, "download link")
| `Smatto` | 21.5.2 | 2020.08.24 | O | [다운로드](https://www.smaato.com/resources/sdks/, "download link")

## Development Environment
- Android Studio 권장 (Recommended)
- targetSdkVersion 29 이상 권장 (to "29" or higher) (2020.11.02 부터 앱 업데이트 시 필수, [Link](https://developer.android.com/distribute/best-practices/develop/target-sdk?hl=ko))

How to use ?
=============

## Step 1. Gradle Setting

```
[build.gradle]
android {
    //...

    defaultConfig {
        //...
        multiDexEnabled true
    }
    
    // To support Java 8, add the language feature support:
    compileOptions {
        sourceCompatibility JavaVersion.VERSION_1_8
        targetCompatibility JavaVersion.VERSION_1_8
    }
    
    //...

    // resolve : OutOfMemoryError
    dexOptions {
        jumboMode = true
        javaMaxHeapSize "4g"
    }
}

//...

repositories {
    // jCenter, MavenCenter Repository 에서 배포한 것이 아닌, 사용자가 libs 폴더에 라이브러리를 추가한 경우.
    flatDir {
        dirs 'libs'
    }
}

```
##### (자세한 사항 각 미디에이션 가이드 및 샘플 프로젝트 소스코드를 참조하십시오.)
##### Please refer to each mediation guide and sample project source code for details.

## Step 2. Add Library

### 2-1. File library

#### (Required, 필수)   
- libs/AdMixer_x.y.z.jar   

#### (선택, Options)   
- libs/CaulySDK-x.y.z.arr   
- libs/DawinClickSDK_x.y.z.jar   
- libs/mplus_sdk.jar   

##### (자세한 사항 각 미디에이션 가이드 및 샘플 프로젝트 소스코드를 참조하십시오.)
##### Please refer to each mediation guide and sample project source code for details.

### 2-2. Gradle Library
```
[build.gradle]

//...

repositories {
    //...
    
    // for Adfit (Options)
    maven { url 'http://devrepo.kakao.com:8088/nexus/content/groups/public/' }
    // For smaato (Options)
    maven { url "https://s3.amazonaws.com/smaato-sdk-releases/" }
    
    //...
}

dependencies {
    //...
    
    // 공통 (Required, 필수)
    implementation 'com.android.support:multidex:1.0.3'
    implementation 'com.google.gms:google-services:4.3.4'
    
    // For AdMixer (Required, 필수)
    implementation files('libs/AdMixer_2.1.3.jar')

    // For Admob (선택, Options)
    implementation 'com.google.firebase:firebase-ads:19.3.0'
    
    // For Cauly (선택, Options)
    implementation name: 'CaulySDK-3.5.08', ext: 'aar'
    // For ManPlus (Man) (선택, Options)
    implementation files('libs/mplus_sdk.jar')
    // For DawinClick (선택, Options)
    implementation files('libs/DawinClickSDK_3.16.7.jar')

    // For Facebook (선택, Options)
    implementation "com.facebook.android:audience-network-sdk:5.11.0"
    implementation "com.android.support:support-annotations:28.0.0"

    // For Adfit (선택, Options)
    implementation "org.jetbrains.kotlin:kotlin-stdlib-jdk7:1.3.72"
    implementation "com.kakao.adfit:ads-base:3.4.0"

    // For Mopub (선택, Options)
    implementation('com.mopub:mopub-sdk:5.13.1@aar') {
        transitive = true
    }

    // For Smaato (선택, Options)
    implementation 'com.smaato.android.sdk:smaato-sdk:21.5.2'
}
//...

```

##### (자세한 사항 각 미디에이션 가이드 및 샘플 프로젝트 소스코드를 참조하십시오.)
##### Please refer to each mediation guide and sample project source code for details.


## Step 3. Project Setting

### 3.1 Android 11 업데이트에 따른 READ_PHONE_STATE 추가 설정 (Options, 옵션) [Link](https://developer.android.com/preview/privacy/permissions?hl=ko)   
- READ_PHONE_STATE 관련 처리   
Android 11 부터 앱에서 전화번호를 읽을 때 사용하는 전화 권한 변경됩니다.앱이 Android 11을 타겟팅하고 다음 목록에 표시된 전화번호 API에 액세스해야 하는 경우 READ_PHONE_STATE 권한 대신 READ_PHONE_NUMBERS 권한을 요청해야 합니다. 위의 조건에 해당하신다면, 애드믹서 내 MANPLUS, Facebook 등 READ_PHONE_STATE 권한을 사용하는 미디에이션은 다음과 같이 변경해주셔야 합니다.   
( * 자세한 사항은 각 미디에이션 가이드를 참조하십시오.)
 - TelephonyManager 클래스와 TelecomManager 클래스의 getLine1Number() 메서드
 - TelephonyManager 클래스에서 지원되지 않는 getMsisdn() 메서드
앱에서 READ_PHONE_STATE를 선언하여 이전 목록의 메서드 이외의 메서드를 호출하는 경우 모든 Android 버전에서 READ_PHONE_STATE를 계속 요청할 수 있습니다. 그러나 이전 목록의 메서드에만 READ_PHONE_STATE 권한을 사용하는 경우 다음과 같이 매니페스트 파일을 업데이트하세요.   
   
1. 앱이 Android 10(API 수준 29) 이하에서만 권한을 사용하도록 READ_PHONE_STATE 선언을 변경합니다.
2. READ_PHONE_NUMBERS 권한을 추가합니다.

다음 매니페스트 선언 스니펫이 이 프로세스를 보여줍니다.
```
[AndroidManifest.xml]

<manifest>
   <!-- Grants the READ_PHONE_STATE permission only on devices that run Android 10 (API level 29) and lower. -->
   <uses-permission android:name="READ_PHONE_STATE“ android:maxSdkVersion="29" />
   <uses-permission android:name="READ_PHONE_NUMBERS" />
</manifest> 
```

### 3.2 Google Play targetAPI 요구사항 충족하기 [Link](https://developer.android.com/distribute/best-practices/develop/target-sdk?hl=ko)   

- APK를 업로드하는 경우 Google Play의 타겟 API 수준 요구사항을 충족해야 합니다.   
  - 새 앱은 Android 10(API 수준 29) 이상을 타겟팅해야 하며 앱 업데이트는 Android 9(API 수준 28) 이상을 타겟팅해야 합니다.   
  - 참고: 2020년 11월 2일부터 앱 업데이트는 Android 10(API 수준 29) 이상을 타겟팅해야 합니다.   
  - 새로운 Android 버전이 출시될 때마다 보안 및 성능이 크게 개선되며 전반적으로 Android 사용자 환경이 향상됩니다.   
  - 이러한 변경사항 중 일부는 targetSdkVersion 매니페스트 속성(타겟 API 수준이라고도 함)을 통해 지원을 명시적으로 선언한 앱에만 적용됩니다.   
  
 - 최신 API 수준을 타겟팅하도록 앱을 구성하면 사용자가 이러한 개선사항의 혜택을 받을 수 있으며, 이전 Android 버전에서도 계속해서 앱을 실행할 수 있습니다. 최신 API 수준을 타겟팅하면 앱에서 플랫폼의 최신 기능을 활용해 사용자 환경을 개선할 수 있습니다. 또한 Android 10(API 수준 29) 현재 앱에서 Android 5.1(API 수준 22) 이하를 타겟팅하는 경우 사용자가 처음으로 앱을 시작할 때 경고가 표시됩니다.
   
 - 이 문서에서는 타겟 API 수준을 업데이트하여 Google Play 요구사항을 충족할 때 알아 두어야 하는 중요 사항에 관해 다룹니다.
 - 참고: Gradle 파일에 매니페스트 항목이 포함되어 있는 경우 빌드 구성의 설명대로 앱의 Gradle 파일에서 targetSdkVersion의 현재 값을 확인하거나 변경할 수 있습니다. 또는 <uses-sdk> 매니페스트 요소 문서에 설명되어 있는 대로 매니페스트 파일에 있는 android:targetSdkVersion 속성을 사용할 수 있습니다.
   
### 3-3 Android 9 (Pie) 업데이트에 따른 추가 설정 (Required, 필수)

ClearText HTTP traffic to not permitted 관련 처리   
- targetSdkVersion 28 부터 네트워크 통신 시 암호화 되지 않은 HTTP통신이 차단되도록 기본설정이 변경되었습니다. 애드믹서 내 모든 미디에이션과 고수익광고가 정상동작하기 위해서는 프로젝트 내 설정값 추가를 통해서 HTTP 통신을 허용해주셔야 하며, 방법은 아래와 같습니다.   
: AndroidManifest.xml 파일에서 application 항목의 속성값으로 usesCleartextTraffic을 true로 설정해야 합니다.   
(Android 9 부터 해당값이 default로 false 설정되어 HTTP 통신이 제한됩니다.)   

```
[AndroidManifest.xml]
<manifest>
    //...
    
    <application
        //...
        android:usesCleartextTraffic="true"
        //... 
        >
        
        //...
        <activity
     //...
</manifest> 
```

### 3-4 Google Play Service 적용 (Required, 필수)
 
- AdMixer를 사용하시기 위해서는 Google Play Service를 적용하셔야 합니다.   
Google Play Service 적용 방법은 다음과 같습니다.   
- Google Play Service Library 프로젝트에 Dependency를 걸어주는 방법   
프로젝트 레벨의 Build.gradle 파일에서 google-services를 dependency에 설정하고, App 레벨의 Build.gradle 파일에서 google-sevices plugin을 적용해주시면 됩니다.
자세한 내용은 AdMixerSample 프로젝트를 참고하시기 바랍니다. 

AndroidManifest.xml 적용 사항
```
[AndroidManifest.xml]
<manifest>

   //...
   <meta-data android:name="com.google.android.gms.version" android:value="@integer/google_play_services_version" />
   //...
     
</manifest> 
```


### 3-5 Update Your Android Manifest For Mediation Library (AndroidManifest.xml 설정)

```
[AndroidManifest.xml]

<manifest>

   //...

   <!-- Common, AdMob, Cauly, Facebook, Adfit(adam), Dawin Click(SyrupAd, T-ad), MAN(MANPLUS), Mopub, Smaato -->
   <uses-permission android:name="android.permission.ACCESS_NETWORK_STATE" />
   <!-- Common, AdMob, Cauly, Facebook, Adfit(adam), Dawin Click(SyrupAd, T-ad), MAN(MANPLUS), Mopub, Smaato -->
   <uses-permission android:name="android.permission.INTERNET" />

   <!-- Dawin Click(SyrupAd, T-ad) -->
   <uses-permission android:name="android.permission.WRITE_EXTERNAL_STORAGE" />

   <!-- Facebook -->
   <uses-permission android:name="android.permission.READ_EXTERNAL_STORAGE" />

   <!-- MAN(MANPLUS), Smaato(optional) -->
   <uses-permission android:name="android.permission.ACCESS_COARSE_LOCATION" />

   <!-- MAN(MANPLUS) -->
   <uses-permission android:name="android.permission.ACCESS_FINE_LOCATION" />

   <!-- MAN(MANPLUS), Facebook -->
   <!-- Grants the READ_PHONE_STATE permission only on devices that run Android 10 (API level 29) and lower. -->
   <!-- 자세한 내용은 다음 링크 및 각 미디에이션 가이드를 참고하십시오. https://developer.android.com/preview/privacy/permissions?hl=ko -->
   <!-- 1. 앱이 Android 11을 타겟팅하면, 아래와 같이 사용하십시오. -->
   <uses-permission android:name="READ_PHONE_STATE" android:maxSdkVersion="29" />
   <uses-permission android:name="READ_PHONE_NUMBERS" />

   <!-- 2. 앱이 Android 11을 타겟팅하지 않는다면, 아래와 같이 사용하십시오. -->
   <!-- <uses-permission android:name="READ_PHONE_STATE" /> -->

   <!-- Smaato -->
   <uses-feature android:name="android.hardware.location.network" />
   
   //...
   
   <!-- Required, 필수 -->
   <activity
      android:name="Show ads your Activity"
      android:hardwareAccelerated="true"
      android:configChanges="keyboard|keyboardHidden|orientation|screenLayout|uiMode|screenSize|smallestScreenSize">
   </activity>
        
   <!-- AdMob -->
   <meta-data
        android:name="com.google.android.gms.ads.APPLICATION_ID"
        android:value="ca-app-pub-xxxxxxxxxxxxxxxx~yyyyyyyyyy"/>
   <activity android:name="com.google.android.gms.ads.AdActivity"
        android:configChanges="keyboard|keyboardHidden|orientation|screenLayout|uiMode|screenSize|smallestScreenSize"
        android:theme="@android:style/Theme.Translucent" />

   <!-- For Facebook -->
   <activity android:name="com.facebook.ads.AudienceNetworkActivity"
        android:configChanges="keyboardHidden|orientation|screenSize" />

   <!-- For MANPLUS -->
   <activity
        android:name="com.mapps.android.view.InterstitialView"
        android:screenOrientation="portrait"
        android:theme="@android:style/Theme.Translucent.NoTitleBar.Fullscreen" />

   <!-- For DawinClick -->
   <receiver android:name="com.skplanet.tad.SyrupAdReceiver" >
        <intent-filter>
                <action android:name="com.skplanet.syrupad.action.SAID_CHANGED" />
        </intent-filter>
   </receiver>
   <activity android:name="com.skplanet.tad.AdActivity"
        android:configChanges="keyboard|keyboardHidden|orientation|screenLayout|uiMode|screenSize|smallestScreenSize"
        android:label="Ad Activity"
        android:theme="@android:style/Theme.NoTitleBar"/>

   <!-- For Cauly -->
   <activity
        android:name="com.fsn.cauly.blackdragoncore.LandingActivity"
        android:configChanges="keyboard|keyboardHidden|orientation|screenSize">
   </activity>

   <!-- For MoPub -->
   <!-- MoPub SDK 버전에 따른 변경사항은 다음 링크를 참고하십시오. https://developers.mopub.com/publishers/android/changelog/-->
   <!-- MoPub's consent dialog -->
   <activity android:name="com.mopub.common.privacy.ConsentDialogActivity" android:configChanges="keyboardHidden|orientation|screenSize"/>

   <!-- All ad formats -->
   <activity android:name="com.mopub.common.MoPubBrowser" android:configChanges="keyboardHidden|orientation|screenSize"/>

   <!-- Interstitials -->
   <activity android:name="com.mopub.mobileads.MraidVideoPlayerActivity" android:configChanges="keyboardHidden|orientation|screenSize"/>
   <activity android:name="com.mopub.mobileads.MoPubFullscreenActivity" android:configChanges="keyboardHidden|orientation|screenSize"/>
            
   </manifest> 
```

##### (자세한 사항 각 미디에이션 가이드 및 샘플 프로젝트 소스코드를 참조하십시오.)
##### Please refer to each mediation guide and sample project source code for details.
   
## Step 4. Initialize ( 모바일 광고 초기화 )   
- AdMixer 객체를 통해 반드시 1회 초기화 호출이 필요합니다.   
AdMixer 객체를 통해 필요한 adapter들을 등록해야 합니다.

```
[YourApplication]

public class YourApplication extends Application {
   void onCreate(Bundle savedInstanceState) {	
      super.onCreate(savedInstanceState);
      Logger.setLogLevel(LogLevel.Verbose); // 로그 레벨 설정

      // 필요한 adapter 등록
      // AdMixer.registerAdapter(AdMixer.ADAPTER_ADFIT, “com.admixer.sample.adapters.AdfitAdapter”);
      // AdMixer.registerAdapter(AdMixer.ADAPTER_ADMOB, “com.admixer.sample.adapters.AdmobAdapter”);
      // AdMixer.registerAdapter(AdMixer.ADAPTER_CAULY, “com.admixer.sample.adapters.CaulyAdapter”);
      // AdMixer.registerAdapter(AdMixer.ADAPTER_DAWIN_CLICK, “com.admixer.sample.adapters.DawinClickAdapter”);
      // AdMixer.registerAdapter(AdMixer.ADAPTER_FACEBOOK, “com.admixer.sample.adapters.FacebookAdapter”);
      // AdMixer.registerAdapter(AdMixer.ADAPTER_MAN, “com.admixer.sample.adapters.ManAdapter”);
      // AdMixer.registerAdapter(AdMixer.ADAPTER_MOPUB, “com.admixer.sample.adapters.MopubAdapter”);
      // AdMixer.registerAdapter(AdMixer.ADAPTER_SMAATO, “com.admixer.sample.adapters.SmaatoAdapter”);

      // AdMixer 초기화를 위해 반드시 광고 호출 전에 앱에서 1회 호출해주셔야 합니다.
      // adunits 파라미터는 앱 내에서 사용할 모든 adunit_id 를 배열 형태로 넘겨 주셔야 합니다.
      adunits = new ArrayList<String>(Arrays.asList(“111111", “222222", “333333"));

      AdMixer.init(this, mediaKey, adunits);

      // COPPA(아동보호법) 관련 항목 설정 값 – 선택사항
      // Smaato 의 경우 테스트 광고 요청 시 COPPA(아동보호법) False 로 변경해야 테스트 광고 송출 가능.
      AdMixer.setTagForChildDirectedTreatment(AdMixer.AX_TAG_FOR_CHILD_DIRECTED_TREATMENT_TRUE);

      // Admob 적용 시에 SDK 초기화 호출이 필요
      MobileAds.initialize(this, new OnInitializationCompleteListener() {…});

      // Facebook 적용 시에 SDK 초기화 호출이 필요
      AudienceNetworkAds.initialize(context);
      // AdSettings.setTestMode(true); // Facebook 테스트 모드로 광고 시 해당 코드 필요.

      // Mopub 적용 시에 SDK 초기화 호출이 필요
      SdkConfiguration sdkConfiguration = new SdkConfiguration.Builder(“my_any_mopub_adunit_id")
                                                .withLogLevel(MoPubLog.LogLevel.DEBUG).build();
      MoPub.initializeSdk(this, sdkConfiguration, new SdkInitializationListener() {
         @Override
         public void onInitializationFinished() {
         }
      });

      // Smaato 적용 시에 SDK 초기화 호출이 필요
      SmaatoSdk.init(this.getApplication(), “my_smaato_publisher_id");
   }
}

```

### 4-1 Banner 광고 추가 (광고 뷰 추가)

- 아래 코드는 Banner 광고를 RelativeLayout에 추가한 예제입니다.

```
[AdMixerSampleActivity.java]

AdView adView;

void addBannerView() {	
   // adunit id 값을 설정
   AdInfo adInfo = new AdInfo(“my_adunit_id"); 
   adInfo.setMaxRetryCountInSlot(-1);   // 리로드 시간 내에 전체 AdNetwork 반복 최대 횟수(-1 : 무한, 0 : 반복 없음, n : n번 반복)

   adView = new AdView(this); // 배너 광고 View 생성
   adView.setAdInfo(adInfo, this); // 광고 정보 설정 
   // 이 때 설정하신 banner의 부모 activity는 원활한 광고제공을 위해 hardwareAccelerated가 true 설정되오니 참고 부탁드립니다.
   adView.setAdViewListener(this); // 이벤트 리스너 설정
   adView.setAlwaysShowAdView(false); // 광고 로딩 전에도 영역을 차지할 것인지 설정(false – 기본값)

   RelativeLayout.LayoutParams params = new RelativeLayout.LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT);
   params.addRule(RelativeLayout.CENTER_HORIZONTAL);
   // 320*50사이즈 일때만, 가로를 디바이스에 맞추거나 영역사이즈로 설정하는 것중에 선택가능
   // 320*50을 제외한 사이즈일때는 영역사이즈로 설정해야 함

   // 각 애드네트워크별 광고설정값을 아래와 같이 설정할 수 있으며,
   // adunit 사이즈에 맞게 네트워크 사이즈를 설정하시기 바랍니다. (자세한 사항은 Ad Network별 추가 광고정보 설정 참고)
   adInfo.setAdapterAdInfo(AdMixer.ADAPTER_ADMOB, "adSize", " BANNER");
   adInfo.setAdapterAdInfo(AdMixer.ADAPTER_FACEBOOK, "adSize", "BANNER_HEIGHT_50");

   adView.setLayoutParams(params); // Layout 파라메터 설정
   layout.addView(adView); // 레이아웃에 AdView 추가
}

// 생명주기에 따라 아래 설정이 반드시 필요합니다. 
void onResume() {
   if(adView != null) 
      adView.onResume();

   super.onResume();
}
void onPause() {
   if(adView != null)
      adView.onPause();

   super.onPause();
}

```

### 4-2 Banner 광고 추가 (Layout 파일 이용 방법)

- 다음과 같이 Layout XML 파일에 광고를 추가하실 수도 있습니다.

```
[Example]

[main.xml]
<?xml version="1.0" encoding="utf-8"?>
<LinearLayout xmlns:android="http://schemas.android.com/apk/res/android"
        android:layout_width=“match_parent"    
	android:layout_height=“match_parent" 
	android:orientation="vertical">
        
	<com.admixer.ads.AdView 
		android:layout_width=“wrap_content"
                android:layout_height="wrap_content"        
		android:id="@+id/ad_view"/>        
                
	<TextView android:layout_width=“match_parent"        
		android:layout_height="wrap_content“
		android:text="@string/hello" />
                
</LinearLayout>

[LayoutSampleActivity.java Example]
public class LayoutSampleActivity extends Activity {   
   @Override
   public void onCreate(Bundle savedInstanceState) {        
      super.onCreate(savedInstanceState);
      setContentView(R.layout.main);
      AdInfo adInfo = new AdInfo(“my_adunit_id");        
      AdView adView = (AdView)findViewById(R.id.ad_view);        
      adView.setAdInfo(adInfo, this);    
   }
}

```

### 4-3 Banner 광고 추가 (이벤드 핸들러)
- 다음은 Banner광고에서 발생하는 이벤트를 Activity에서 받기 위해 AdViewListener 인터페이스를 구현한 것입니다. 
- AdViewListener 인터페이스를 구현한 객체만이 AdView의 setAdViewListener메소드의 파라메터로 지정되어 이벤트를 받을 수 있습니다.   


```
[AdMixerSampleActivity.java]

public class AdMixerSampleActivity extends Activity implements AdViewListener {
   @Override	
   public void onReceivedAd(String adapterName, AdView adView) { 
      // 광고 수신 성공
   }

   @Override	
   public void onFailedToReceiveAd(int errorCode, String errorMsg, AdView adView) {  
      // 광고 수신 실패	
   }
}

```

##### (자세한 사항 각 미디에이션 가이드 및 샘플 프로젝트 소스코드를 참조하십시오.)
##### Please refer to each mediation guide and sample project source code for details.


## Step 5. Interstitial 광고(전면광고) 추가
- 전면광고에는 두 가지 형태가 제공됩니다.
- 팝업형 전면광고의 경우 admixer 네트워크 전면배너만 사용 가능합니다.

| 일반 전면광고 예시 | 팝업형 전면광고 예시
|:---:|:---:|
| !(일반 전면광고 예시)(https://raw.github.com/dcurtis/markdown-mark/master/png/208x128.png) | !(팝업형 전면광고 예시)(https://raw.github.com/dcurtis/markdown-mark/master/png/208x128.png)


### 5-1 Interstitial 광고(전면광고) 추가 - 광고 뷰 추가

- Interstitial 광고는 1회성 객체입니다. Start 혹은 load 메소드 호출은 한 번만 가능합니다.
- showInterstitial은 로딩과 동시에 별도의 api 호출 없이 바로 전면 광고를 표시하는 방법입니다.
- loadInterstitial은 전면광고 바로 표시 방법과 거의 동일하나 광고 로딩 이벤트를 받은 후에 광고 표시 시점을 개발자가 조절할 수 있도록 하는 방법입니다.   
- 이 방법은 Load & Show가 지원되는 AdNetwork들만 호출하고 나머지는 Skip합니다.
- 처리 방법
  - 광고 로딩 : startIntersitial() 대신 loadInterstitial()을 호출
  - 광고 수신 이벤트 대기 : onInterstitialAdReceived() 호출 대기
  - 광고 표시 : showInterstitial() 호출

- 유의 사항
  - 광고 로딩이 성공한 이후 지나치게 시간이 많이 지나가면 showInterstitial()을 호출했을 때에 제대로 광고가 표시되지 않을 수 있습니다. 
  - showInterstitial()을 호출하지 않으면 광고가 표시되지 않습니다.
  - loadInterstitial()을 호출하고 일정시간이 지나면 광고가 노출이 되어도 유효노출로 처리되지 않는 경우가 있습니다. 이 경우는 애드네트워크별로 다르므로, 해당 애드네트워크사에 확인하여  loadInterstitial() 후 적절한 타임아웃을 걸어서 재호출 하는 방식으로 사용하시기 바랍니다. 애드네트워크별로 최소호출간격이 있는 경우도 있으므로, 적당한 타임아웃을 설정하시기 바랍니다. (ex. AdMob 광고노출인증유효시간 20분)

지원 AdNetwork : Admob, Cauly, DawinClick(SyrupAd, T-ad), Facebook, Mopub

- 아래 코드는 Interstitial 광고를 RelativeLayout에 추가한 예제입니다.
```
[AdMixerSampleActivity.java]

void addInterstitialAdView() {    	
   // adunit id 값 설정
   AdInfo adInfo = new AdInfo(“my_adunit_id＂); 
   // 초단위로 전면 광고 타임아웃 설정 (기본값 : 0, 0 이면 서버지정 시간으로 처리, 서버지정 시간 : 20s) 
   adInfo.setInterstitialTimeout(0);		
   // 리로드 시간 내에 전체 AdNetwork 반복 최대 횟수(-1 : 무한, 0 : 반복 없음, n : n번 반복)
   adInfo.setMaxRetryCountInSlot(-1); 
   // 고수익 전면광고 노출 시 광고 외 영역 반투명처리 여부 (true: 반투명, false: 처리안함) / 기본값 : true
   adInfo.setBackgroundAlpha(true);
   // (( 팝업형 전면광고를 원하실 경우 이 위치에 다음 페이지의 옵션을 추가하세요 ))
   interstitialAd = new InterstitialAd (this); // 전면 광고 생성
   interstitialAd.setAdInfo(adInfo, this); // 광고 정보 설정
   interstitialAd.setInterstitialAdListener(this); // 이벤트 리스너 설정  
   interstitialAd.startInterstitial(); // 광고 로딩 및 즉시 노출
   // interstitialAd.loadInterstitial(); // 광고 로딩 후 원하는 시점에 showInterstitial 호출
}

// 생명주기에 따라 아래 설정이 반드시 필요합니다.
void onDestroy() {	
   if(interstitialAd != null) {
      interstitialAd.stopInterstitial();
      interstitialAd = null;
   }
   super.onDestroy();
}

```

### 5-2 Interstitial 광고(전면광고) 추가 - 팝업형 설정

- 팝업형 전면광고 설정코드 (원하시는 조건만 추가하시면 됩니다.)

```
[AdMixerSampleActivity.java]

// 팝업형 전면광고 세부설정을 원하시면 아래 PopupInterstitialAdOption 설정
PopupInterstitialAdOption adConfig = new PopupInterstitialAdOption();
// 팝업형 전면광고 노출 상태에서 뒤로가기 버튼 방지 (true : 비활성화, false : 활성화)
adConfig.setDisableBackKey(true);
// 왼쪽버튼. 디폴트로 제공되며, 광고를 닫는 기능이 적용되는 버튼 (버튼문구, 버튼색상)
adConfig.setButtonLeft("광고종료", "#234234");
// 오른쪽 버튼을 사용하고자 하면 반드시 설정. 
// 앱을 종료하는 기능을 적용하는 버튼. 미 설정 시 위 광고종료 버튼만 노출
adConfig.setButtonRight(null, null);
// 버튼영역 색상지정
adConfig.setButtonFrameColor(null);
// 팝업형 전면광고 추가옵션
// (InterstitialAdType.Basic : 일반, InterstitialAdType.Popup : 팝업형)
adInfo.setInterstitialAdType(InterstitialAdType.Popup, adConfig);

```
- (참고 1) 팝업형 전면광고 오른쪽 버튼 설정 시 “onRightClicked” 리스너에서 앱 종료코드를 구현해주시기 바랍니다.
다양한 환경에서 종료가 가능하기 때문에 퍼블리셔가 직접 설정하게 되어있습니다. 일반적으로 finish() 함수 호출.   
- (참고 2) 팝업형 전면광고 소재 사이즈는 Full 전면광고 소재 사이즈의 85% 축소된 비율로 노출됩니다.   하단의 버튼 영역 사이즈는 팝업형 전면광고 소재 비율 대비 12% 비율로 노출됩니다.


##### (자세한 사항 각 미디에이션 가이드 및 샘플 프로젝트 소스코드를 참조하십시오.)
##### Please refer to each mediation guide and sample project source code for details.


### 5-2 Interstitial 광고(전면광고) 추가 - 이벤트 핸들러
- 다음은 전면광고에서 발생하는 이벤트를 Activity에서 받기 위해 InterstitialAdListener 인터페이스를 구현한 것입니다.
- InterstitialAdViewListener 인터페이스를 구현한 객체만이 InterstitialAd 의 setInterstitialAdListener 메소드의 파라메터로 지정되어 이벤트를 받을 수 있습니다.

```
[AdMixerSampleActivity.java]

public class AdMixerSampleActivity extends Activity implements InterstitialAdListener {
   @Override // 광고 수신
   public void onInterstitialAdReceived(String adapterName, InterstitialAd interstitialAd) {}

   @Override // 광고 수신 실패
   public void onInterstitialAdFailedToReceive(int errorCode, String errorMsg, InterstitialAd interstitialAd) {}	

   @Override // 광고 창이 닫혔을 때
   public void onInterstitialAdClosed(InterstitialAd interstitialAd) {}

   @Override // 광고가 화면에 보여졌을 때
   public void onInterstitialAdShown(String adapterName, InterstitialAd interstitialAd) {}

   @Override // (팝업형 전면) 왼쪽버튼이 클릭되었을 때
   public void onLeftClicked(String adapterName, InterstitialAd interstitialAd) {}

   @Override // (팝업형 전면) 오른쪽버튼이 클릭되었을 때 앱 종료코드 구현
   public void onRightClicked(String adapterName, InterstitialAd interstitialAd) {  finish();  }
}

```

### 5-3 Interstitial 광고(전면광고) 추가 - 닫기 (Close)
- 전면 광고 화면 노출 후 원하는 시점에 닫고 싶으시면 closeInterstital 메소드를 호출하시면 됩니다.
- 광고가 화면에 노출되기 전 closeInterstital 를 호출하면 동작하지 않습니다.
- 만약, Timer를 사용하여 광고를 제거 할 경우 반드시 Timer를 정지 해줘야 합니다.   
 (Timer 작동 이전에 사용자가  전면광고를 끄게 되면 closeInterstital 가 다음 전면광고에 영향을 줄 수 있습니다.)
- 지원 AdNetwork : Admixer, Cauly, DawinClick(SyrupAd, T-ad), HouseAd (미지원 AdNetwork는 Skip)

```
[AdMixerSampleActivity.java]

(변수 선언 부)
InterstitialAd interstitialAd;
(광고 요청)
addInterstitialView();
(리스너)
@Override	
public void onInterstitialAdShown() {
   // (( Timer 시작
   if(interstitialAd != null)
   interstitialAd.closeInterstitial();
   interstitialAd = null; ))
}

@Override
public void onInterstitialAdClosed(InterstitialAd interstitialAd) { 
   // ((Timer 정지))
}

```

##### (자세한 사항 각 미디에이션 가이드 및 샘플 프로젝트 소스코드를 참조하십시오.)
##### Please refer to each mediation guide and sample project source code for details.


## Step 6. Ad network 별 추가 광고정보 설정
- setAdapterAdInfo API는 AdNetwork 별로 제공되는 옵션을 설정하기 위한 API입니다.

```
[지원 옵션]

// For Admob
adInfo.setAdapterAdInfo(AdMixer.ADAPTER_ADMOB, "adSize", "BANNER");
// AdMob 배너 종류 설정 
// BANNER, SMART_BANNER, LARGE_BANNER, MEDIUM_RECTANGLE, FULL_BANNER, LEADERBOARD
// Default : "BANNER"

// For Cauly
adInfo.setAdapterAdInfo(AdMixer.ADAPTER_CAULY, "bannerHeight", "Proportional");
// Cauly 배너 종류 설정
// ("Fixed_50" – 320*50 고정사이즈 배너 / "Proportional" – 스크린 세로길이의 10% 사이즈 배너 [기본값] )
// Default : "Fixed_50"

// For Facebook
adInfo.setAdapterAdInfo(AdMixer.ADAPTER_FACEBOOK, "adSize", "BANNER_HEIGHT_50");
// facebook 배너 종류 설정
// BANNER_HEIGHT_50, BANNER_HEIGHT_90, RECTANGLE_HEIGHT_250
// Default : "BANNER_HEIGHT_50"

// For Mopub
adInfo.setAdapterAdInfo(AdMixer.ADAPTER_MOPUB, "adSize", "MATCH_VIEW");
// mopub 배너 종류 설정
// MATCH_VIEW, HEIGHT_50, HEIGHT_90, HEIGHT_250, HEIGHT_280
// Default : "MATCH_VIEW"

// For Smaato
adInfo.setAdapterAdInfo(AdMixer.ADAPTER_SMAATO, "adSize", "XX_LARGE_320x50");
// smaato 배너 종류 설정
// XX_LARGE_320x50, MEDIUM_RECTANGLE_300x250, LEADERBOARD_728x90, SKYSCRAPER_120x600
// Default : "XX_LARGE_320x50"

```

##### (자세한 사항 각 미디에이션 가이드 및 샘플 프로젝트 소스코드를 참조하십시오.)
##### Please refer to each mediation guide and sample project source code for details.

## Step 7. Proguard 설정
- 난독화를 위해 proguard를 설정하신 경우에는 앱 실행 시 광고가 나오지 않을 수 있습니다.   이를 위해서는 다음의 설정 내용을 proguard 파일에 추가해 주시면 됩니다. 
   (타 Ad Network의 proguard설정은 해당 네트워크사의 가이드를 참조 부탁드립니다.)

```
[xxxxxx-yyyyy.pro]

# AdMixer Setting
-keep class com.admixer.** { *; }

```

##### (자세한 사항 각 미디에이션 가이드 및 샘플 프로젝트 소스코드를 참조하십시오.)
##### Please refer to each mediation guide and sample project source code for details.


## Step 8. 자주하는 질문 (Q&A)

* 문제 확인을 위한 로그는 없나요?
  - Logger.setLogLevel(Logger.LogLevel.Verbose);
  - 위의 코드를 넣으시면 LogCat에 상세한 로그를 확인하실 수 있습니다. 문제 발생 시 해당 로그를 전달해 주시면 좀 더 정확한 원인 파악에 도움이 됩니다.
  
* 하나의 App에 복수 개의 Media Key를 적용해도 되나요?
  - 한 개의 App에는 한 개의 Media Key만 적용하셔야 합니다.
  
* 동일한 Adunit ID로 여러 개의 광고객체를 생성해도 되나요?
  - 한 개의 Adunit ID는 한 개의 광고객체에서만 사용가능합니다.
  
* 광고가 나오지 않습니다.
  - 먼저 Android Studio의 Logcat에 표시되는 로그를 확인해 주시기 바랍니다.
  - AdMixer 사이트에서 Ad Network 노출 설정 및 광고 키 설정에 문제가 없는지 확인 바랍니다.
  - AdMixer 객체를 통해 초기화 호출을 해주셨는지 확인 부탁드립니다. 초기화 시에 설정한 Media Key 및 adunit ID 배열과 일치하는 정보를 가진 광고객체만 정상적으로 동작합니다.
  - Adunit 생성 시에 입력한 fullscreen 정보에 따라서 사용가능한 광고객체가 다릅니다. banner 객체는 fullscreen이 off 일 때, interstitial객체는 fullscreen이 on 일 때 각각 사용할 수 있습니다.
  
* Adunit에 설정한 사이즈와 다른 사이즈의 광고가 노출됩니다
  - Adunit에 설정한 사이즈값은 admixer와 housead의 경우는 내부적으로 사이즈가 보장되지만, 타 Ad Network사의 경우, 해당 사이즈에 맞는 지면을 생성하셔서 전략에 키값을 설정하시고, 사이즈옵션이 있다면 코드상에서 setAdapterAdInfo를 통해 개별적으로 설정하셔야 정상 노출이 보장됩니다.
  
* 한 App내에서 많은 adunit을 사용해도 되나요?
  - 사용 가능한 adunit수에 제한은 없지만, 광고 객체를 설정하고 로딩하는데에 많은 메모리가 할당되기 때문에 앱 성능을 위해서 많은 광고객체 호출은 지양하시는 것이 좋습니다. 
  
* 빌드 시에 AndroidManifest.xml에서 오류가 발생합니다.
  - AndroidManifest.xml 파일의 형식이 맞지 않은 경우 오류가 발생할 수 있습니다.
  
* 광고 클릭 시 프로그램이 비정상 종료가 됩니다.
  - AndroidManifest.xml에 광고 플랫폼 별 Activity 설정이 제대로 되어 있지 않은 경우일 수 있습니다. AndroidManifest.xml 설정을 확인해 주시기 바랍니다.
  
* 라이브러리 크기를 줄일 수 있습니까?
  - 모든 광고를 적용하시는 편이 표시할 광고가 없는 상황을 줄일 수 있기 때문에 가급적이면 모든 광고를 포함하시는 편이 좋겠지만 프로그램 크기가 커져서 문제가 되신다면 꼭 필요한 Ad Network를 결정하시고 불필요한 adapter를 삭제하시고, 필요한  Ad Network adapter만 등록하시면 됩니다.
  
* 전면 광고가 자주 나오지 않습니다.
  - 전면 광고의 경우 배너 광고보다 광고 물량이 적어 설정하신  Ad Network가 적은 경우 광고가 나오지 않을 확률도 그 만큼 커집니다.
  
* Banner광고 좌우 여백 부분 배경색을 바꿀 수 있나요?-
  - AdView의 배경 혹은 AdView의 부모 View의 배경색을 설정하시면 됩니다.


 

# AdMixerSample

- Admixer Android SDK

## AdMixer Support Mediation Version


| AdNetwork | Version | Check Date | compatible
|---|:---:|---:|---:|
| `AdMixer` | 2.1.3 | 2020.08.24 | O
| `Adfit` | 3.4.0 | 2020.08.24 | O
| `Admob` | 19.3.0 | 2020.08.24 | O
| `Cauly` | 3.5.08 | 2020.08.24 | O
| `Facebook` | 5.11.0 | 2020.08.24 | O
| `DawinClick` | 3.16.7 | 2020.08.24 | O
| `MANPLUS` | 107 | 2020.08.24 | O
| `Mopub` | 5.13.1 | 2020.08.24 | O
| `Smatto` | 21.5.2 | 2020.08.24 | O


변경사항
=============

- Mopub   
v5.13.0 에서 배너, 전면 광고 및 보상 형 광고를 하나의 컨테이너로 통합합니다.
통합 mopub-sdk-interstitial, mopub-sdk-rewarded-video -> mopub-sdk-fullscreen

버전별 등록 방법

v 5.12.0
```
AndroidManifest.xml
<activity android:name="com.mopub.mobileads.MoPubActivity" android:configChanges="keyboardHidden|orientation|screenSize"/>
<activity android:name="com.mopub.mobileads.MraidActivity" android:configChanges="keyboardHidden|orientation|screenSize"/>
<activity android:name="com.mopub.mobileads.RewardedMraidActivity" android:configChanges="keyboardHidden|orientation|screenSize"/>

build.gradle    
        implementation('com.mopub:mopub-sdk:5.12.0@aar') {
        transitive = true
    }
```        
v 5.13.1
```
AndroidManifest.xml
<activity android:name="com.mopub.mobileads.MoPubFullscreenActivity" android:configChanges="keyboardHidden|orientation|screenSize"/>

build.gradle
    implementation('com.mopub:mopub-sdk:5.13.1@aar') {
        transitive = true
    }
``` 

- Adfit   
2020.08.06 일자로 다음애드핏이 종료되고 카카오애드핏으로 변경 사용 시 이전해야함.

- Smatto   
테스트 시 COPPA 를 False 로 두고 테스트해야 광고 송출받을 수 있음

- Facebook   
AdSettings.setTestMode(true); 추가해야 테스트 가능
    
    

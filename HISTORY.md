## Update History

| Version | Note 
|:---:|:---|
| `2.3.0` <br/> `2021.05.13` | 미디에이션 버전체크 <br/>- Admob 19.7.0 -> 20.1.0, ~~SMART_BANNER~~ -> ADAPTIVE_BANNER, <br/> - Cauly 3.5.14 -> 3.5.16, <br/> - Facebook 6.2.0 -> 6.4.0, <br/> - Mopub 5.15.0 -> 5.16.4, <br/> - Smaato 21.5.7 -> 21.5.9  |
| `2.3.0` <br/> `2021.04.29` | 구글 정책 변경 대응 AdMixer 업데이트 및 Manplus SDK lib 변경에 따른 ManAdapter, Sample Project 수정 |
| `2.2.0` <br/> `2021.04.16` | Manplus 배너 or 전면광고 클릭 or 닫기 시, onDestory 호출 |
| `2.2.0` <br/> `2021.04.09` | Manplus 를 최초 첫 호출 시, 광고 노출 안되는 부분 수정 (ManAdapter)<br/> (Manplus 광고 1순위 시, 가로/세로 Banner 영역 설정 오류)|
| `2.2.0` <br/> `2021.03.08` | - Admixer SDK 업데이트 2.1.3 -> 2.2.0 <br/> - 각 미디에이션 업데이트에 따른 Adapter 업데이트 <br/> - 가이드 문서 업데이트 <br/> - 각 build.gradle dependencies 라이브러리 업데이트 일 기준 최신버전으로 변경 <br/> - Admob, Cauly, Manplus Adapter 수정<br/> - Manplus 전면광고 2가지 모드 지원|
| `2.1.3` <br/> `2020.10.23` | README.md 가이드 문서 업데이트 |
| `2.1.3` <br/> `2020.10.06` | - Admob 버전 변경 (8월 호환 테스트 버전에 맞춤)<br/> 'com.google.firebase:firebase-ads:19.4.0' -> 'com.google.firebase:firebase-ads:19.3.0'<br/> - Admob adSizeList default BANNER 로 변경<br/> - Gradle version 3.2.1 -> 4.0.1 / google-service 4.2.0 -> 4.3.4

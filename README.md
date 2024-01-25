[![workflow](https://github.com/wafflestudio/siksha-android/actions/workflows/ci.yml/badge.svg)](https://github.com/wafflestudio/siksha-android/actions/workflows/ci.yml)
[![workflow](https://github.com/wafflestudio/siksha-android/actions/workflows/cd.yml/badge.svg)](https://github.com/wafflestudio/siksha-android/actions/workflows/cd.yml)


<div align="center">
  <a href="https://github.com/wafflestudio/siksha-android">
    <img width="115" src="https://github.com/wafflestudio/siksha-android/assets/68140623/ed03015c-0dd2-427f-bd67-9228a10c6a9b">
  </a>
  <h3 align="center">Siksha Android</h3>
  <p align="center">
    SNU Cafeteria Information Application
    <div style=" padding-bottom: 1rem;">
      <img src="https://img.shields.io/badge/Android-3DDC84?style=for-the-badge&logo=android&logoColor=white" />
      <img src="https://img.shields.io/badge/Kotlin-B125EA?style=for-the-badge&logo=kotlin&logoColor=white" />
      <img src="https://img.shields.io/badge/firebase-ffca28?style=for-the-badge&logo=firebase&logoColor=black" />
      <img src="https://img.shields.io/badge/GitHub_Actions-2088FF?style=for-the-badge&logo=github-actions&logoColor=white" />
    </div>
  </p>
</div>

## Features
- 교내 식당 정보 조회
- 교내 식당 메뉴 조회
- 메뉴 즐겨찾기 등록
- 메뉴 리뷰, 평점, 좋아요
- 커뮤니티(WIP)

## Tech Stacks
- MVVM
- Jetpack Compose
- Dependency Injection with Hilt
- Firebase crashylistics & app distribution

## Getting Started
### Secrets
Staging 빌드를 위해 다음 파일들이 필요합니다.
  - `app/src/staging/google-services.json`
  - `app/src/staging/res/values/secrets.xml`
Live 빌드를 위해 다음 파일들이 필요합니다.
  - `app/src/live/google-services.json` 
  - `app/src/live/res/values/secrets.xml`
필요시 maintainer에게 요청해주세요.

### Installation
1. [Android Studio](https://developer.android.com/studio)를 설치합니다.
2. 다음 명령어를 입력하여 repository를 clone하고, Android Studio로 프로젝트를 엽니다.
   ```
   git clone https://github.com/wafflestudio/siksha-android
   ```
4. JDK 17으로 프로젝트를 빌드하여 실행합니다.
### Deployment
1. `develop`에 모든 PR을 머지하고 `release-${version-code}` 브랜치를 생성합니다.
2. `version.properties`의 version code를 rc를 포함하여 입력하고(e.g. 3.4.0-rc1) 푸쉬하면 `cd.yml`에 따라 app distribution에 apk가 업로드되며, 테스트를 거치며 rc 버전을 올립니다.
3. 테스트가 완료되면 version code를 정식 버전으로 입력하고 푸쉬합니다. app distribution에 업로드된 apk를 플레이스토어에 등록합니다.
4. `release-${version-code}` 브랜치를 `develop`에 머지합니다. 출시된 버전이 가장 최근의 `release-${version-code}` 브랜치와 동일하도록 유지해주세요.

## Branch Conventions
- default branch는 `develop`입니다.
- PR 브랜치 이름은 `${username}/${changes}`(e.g. `sanggggg/renewal-table-ui`)으로 합니다.
- 머지 시 squash merge만을 사용합니다.

## Maintainers
- [@eastshine2741](https://github.com/eastshine2741)
- [@wjshim2003](https://github.com/wjshim2003)


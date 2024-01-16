# siksha-android
![workflow](https://github.com/wafflestudio/siksha-android/actions/workflows/ci.yml/badge.svg)
![workflow](https://github.com/wafflestudio/siksha-android/actions/workflows/cd.yml/badge.svg)

## Branch conventions

#### default branch: `develop`
- PR 브랜치 명칭: `${username}/${YYMMDD}-${changes}` (e.g.: `sanggggg/210404-ui-bug-fix`)
- merge convention: only squash merge

#### Release branch: `release`
- 매 출시 마다 develop 에서 `release-${version-code}` 브랜치 파고 version.properties 수정하여 커밋, pr 머지 (커밋 메세지는 `Release ${version-code}`)
- 출시된 버전 == release 브랜치 유지하기

## secrets
- `app/src/live/google-services.json` 
- `app/src/live/app-distribution-service-account.json`
- `app/src/live/res/values/secrets.xml`
- `app/src/staging/google-services.json`
- `app/src/staging/app-distribution-service-account.json`
- `app/src/staging/res/values/secrets.xml`
- maintainer 에게 요청하기

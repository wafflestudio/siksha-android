# siksha-android
![workflow](https://github.com/wafflestudio/siksha-android/actions/workflows/ci.yml/badge.svg)


## Branch conventions

### default branch `develop`
- PR 브랜치 명칭: `${username}/${YYMMDD}-${changes}` (e.g.: `sanggggg/210404-ui-bug-fix`)
- merge convention: only squash merge

### Release branch `release`
- 매 출시 마다 develop 에 [모든 작업 완료 + `app/build.gradle` version 코드를 변경한 commit] pr 머지 후 release 브랜치에 푸쉬하기 (merge commit 의 커밋 메세지는 `release ${version-code}`)

## secrets
- `app/google-services.json` 
- `app/src/main/res/value/secrets.xml` -> (`TODO: debug, release 분리 필요`)
- maintainer 에게 요청하기

## Maintainers (Contacts)
- **current** [veldic](https://github.com/veldic)
- [sanggggg](https://github.com/sanggggg)

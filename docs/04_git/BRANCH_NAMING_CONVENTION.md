# Branch Naming Convention

프로젝트의 브랜치 명명 규칙을 정의합니다.  
**이슈 기반 개발 워크플로우**를 따릅니다.

---

## 1. 기본 구조

```
<prefix>-<issue-number>/<short-description>
```

### 구성 요소

- **prefix**: 작업 타입 (feat, fix, refactor 등, 커밋 메시지 컨벤션과 동일)
- **issue-number**: GitHub Issue 번호
- **short-description**: 작업 내용 요약, 2-3 단어, 영어, kebab-case(소문자-하이픈), 명사형 권장

### 예시

```
feat-123/search-filter
fix-1/blank-screen
refactor-45/api-structure
docs-12/readme-update
```

---

## 2. Prefix 정의

커밋 메시지 컨벤션과 동일한 타입을 사용합니다.

| Prefix    | 설명            | 예시                      |
|-----------|----------------|---------------------------|
| **feat**  | 기능 추가       | feat-10/user-auth         |
| **fix**   | 버그 수정       | fix-1/blank-screen        |
| **refactor** | 리팩터링     | refactor-5/api-structure  |
| **perf**  | 성능 개선       | perf-8/query-optimization |
| **docs**  | 문서 변경       | docs-3/readme-update      |
| **style** | 스타일 변경     | style-7/button-color      |
| **test**  | 테스트 개선     | test-9/unit-test          |
| **build** | 빌드 변경       | build-11/webpack-config   |
| **ci**    | CI/CD 변경      | ci-6/github-actions       |
| **chore** | 기타 유지보수   | chore-4/dependency-update |

---

## 3. 작업 흐름 & 브랜치 생성 명령어

### 기본 흐름

```
1. 이슈 작성
   ↓
2. 브랜치 생성
   ↓
3. 작업 & 커밋
   ↓
4. PR 작성
   ↓
5. 코드 리뷰
   ↓
6. 승인 및 머지
```

### 브랜치 생성 예시

#### (A) 로컬에 리포지토리(clone)가 이미 있는 경우

```bash
git checkout main
git pull origin main
git checkout -b feat-123/search-filter
```

#### (B) 새로 프로젝트를 클론하는 경우

```bash
git clone <remote_url>
cd <project_name>
git checkout main
git pull origin main
git checkout -b feat-123/search-filter
```

#### (공통) 작업 후 푸시 및 PR

```bash
git add .
git commit -m "feat: 검색 필터 기능 추가"
git push -u origin feat-123/search-filter
```
> GitHub에서 Pull Request 생성 시 반드시 관련 이슈(Closes #123 등)와 연결할 것

---

## 4. Short Description 작성 가이드

- **2-3 단어**로 명확하게 핵심만 표현
- **영어, 소문자, 하이픈(kebab-case)** 사용 (snake_case, 대문자, 긴 문장, 한글 금지)
- **명사형** 권장

| 작업 내용             | Short Description     |
|----------------------|----------------------|
| 검색 필터 UI 추가    | search-filter        |
| 빈 화면 버그 수정    | blank-screen         |
| API 구조 리팩터링    | api-structure        |
| 사용자 권한 체크     | user-permission      |
| 로그인 에러 수정     | login-error          |

---

## 5. 체크리스트

브랜치 생성 전 다음을 반드시 확인하세요.

- [ ] 관련 이슈가 생성되어 있는가?
- [ ] Prefix가 작업 성격과 일치하는가?
- [ ] 이슈 번호가 정확한가?
- [ ] Short description이 kebab-case인가?
- [ ] main에서 최신 코드를 pull 받았는가?

> ※ 로컬에 이미 저장소가 있는 경우 git clone 단계는 생략
> ※ 항상 브랜치 생성 전에 main을 최신으로 업데이트하는 습관을 유지할 것

---

이 규칙을 준수하여 브랜치를 생성해 주세요.
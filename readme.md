# Content Hub

> 다양한 미디어 콘텐츠를 검색하고 관리하는 웹 애플리케이션

## 프로젝트 소개

Content Hub는 영화, 드라마, 만화 등 다양한 미디어 콘텐츠를 검색하고 위시리스트로 관리할 수 있는 풀스택 웹 애플리케이션입니다. 

### 주요 기능
- TMDB API, AniList API 기반 미디어 콘텐츠 검색
- 영화, TV 시리즈, 만화 등 다양한 미디어 타입 지원
- 위시리스트 관리
- 소셜 로그인 (Kakao, Naver)

## 기술 스택

### Backend
- **언어**: Java 21
- **프레임워크**: Spring Boot 3.4.4
- **데이터베이스**: PostgreSQL 17, Redis 8.4.0
- **캐시**: Caffein Cache
- **빌드 도구**: Gradle
- **코드 품질**:  SonarCloud

### Frontend
- **언어**:  TypeScript
- **프레임워크**:  React + Vite
- **상태 관리**: React Query, Zustand
- **UI**:  TailwindCSS
- **테스트**: Vitest
- **모니터링**: Sentry
- **자동 포맷팅**: prettier
- **코드 품질**: SonarCloud, eslint

### DevOps
- **컨테이너**: Docker, Docker Compose
- **CI/CD**: GitHub Actions
- **배포**:  AWS EC2
- **웹서버**: Nginx
- **SSL**: Let's Encrypt (Certbot)

## 프로젝트 구조

```
content-hub/
├── backend/
│   └── content-hub-backend/
│       └── content-hub/          # Spring Boot 애플리케이션
├── frontend/
│   └── content-hub-frontend/     # React 애플리케이션
├── docs/
│   └── 04_git/                   # Git 컨벤션 문서
├── .github/
│   ├── workflows/                # CI/CD 워크플로우
│   ├── ISSUE_TEMPLATE/           # 이슈 템플릿
│   └── PULL_REQUEST_TEMPLATE. md  # PR 템플릿
├── docker-compose.yml            # 운영 환경 설정
└── docker-compose.override.yml   # 운영 환경 추가 설정
```

## 개발 가이드

### 브랜치 전략
```
main (운영)
  ↑
feature/issue-based 브랜치
```

### Git 컨벤션
- [Commit Message Convention](docs/04_git/COMMIT_MESSAGE_CONVENTION.md)
- [Branch Naming Convention](docs/04_git/BRANCH_NAMING_CONVENTION.md)
- [Issue Convention](docs/04_git/ISSUE_CONVENTION.md)
- [Pull Request Convention](docs/04_git/PULL_REQUEST_CONVENTION.md)

### 작업 흐름
1. 이슈 작성 (Bug/Feature/Refactor)
2. 브랜치 생성 (`feat-123/search-filter`)
3. 작업 & 커밋
4. PR 작성 (`Closes #123`)
5. 코드 리뷰 & 승인
6. 머지 & 자동 배포

## 테스트

### 백엔드
```bash
cd backend/content-hub-backend/content-hub
./gradlew test
./gradlew jacocoTestReport  # 커버리지 리포트
```

### 프론트엔드
```bash
cd frontend/content-hub-frontend
npm run test        # 테스트 실행
npm run lint        # ESLint 검사
```

## CI/CD

- **Backend CI**:  코드 푸시 시 자동 테스트 & SonarCloud 분석
- **Frontend CI**: 코드 푸시 시 자동 테스트 & ESLint 검사 및 SonarCloud 분석
- **Deploy**: `main` 브랜치 푸시 시 EC2 자동 배포
  - 변경 감지 기반 선택적 배포 (Backend/Frontend/Full)

## 라이선스

이 프로젝트는 개인 프로젝트입니다. 

## 개발자

- **GitHub**: [@jaeyoung0207](https://github.com/jaeyoung0207)

## 링크

- [프로젝트 홈페이지](https://content-hub.info)
- [이슈 트래커](https://github.com/jaeyoung0207/content-hub/issues)
- [토론](https://github.com/jaeyoung0207/content-hub/discussions)


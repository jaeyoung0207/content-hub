# Stage 1 Build stage
# node 빌드 환경 설정
FROM node:22-alpine AS builder

# 도커 이미지 내부의 작업 디렉터리 설정
WORKDIR /app

# package*.json 파일을 도커 이미지 내부의 현재 작업 디렉터리로 복사
COPY package*.json ./

# package.json에 명시된 의존성 패키지들을 설치
# --legacy-peer-deps: 설치 과정에서 발생하는 peer dependency 충돌 오류를 무시하고 설치를 강행
# 필요 시 npm ci 대신 npm install 사용 가능
RUN if [ -f package-lock.json ]; then npm ci --legacy-peer-deps; else npm install --legacy-peer-deps; fi

# 로컬 컴퓨터의 현재 디렉터리에 있는 모든 파일과 폴더를 도커 이미지 내부의 현재 작업 디렉터리로 그대로 복사
COPY . .

# esbuild 바이너리 재빌드 (알파인 리눅스 호환성 문제 해결용)
RUN npm rebuild esbuild --update-binary || true

# build(메모리 확장 후 실행)
ENV NODE_OPTIONS=--max-old-space-size=4096
RUN npm run build

# build 후 dist 폴더에서 .map 파일 삭제
RUN find ./dist -name "*.map" -type f -delete

# Stage 2 Nginx to serve static files
# nginx 빌드 환경 설정
FROM nginx:stable-alpine

# Nginx의 기본 정적 파일 경로로 작업 디렉터리 설정
WORKDIR /usr/share/nginx/html

# 기존 기본 파일 삭제
RUN rm -rf ./*

# 빌드 스테이지에서 생성된 정적 파일들을 현재 스테이지 내부의 작업 디렉터리로 복사
COPY --from=builder /app/dist .

# 커스텀 nginx 설정 복사(현재 디렉토리에 'nginx.conf' 파일이 있어야 함)
COPY nginx.conf /etc/nginx/conf.d/default.conf

# Nginx 기본 포트 80 노출
EXPOSE 80

# Nginx 실행 명령어
CMD ["nginx", "-g", "daemon off;"]

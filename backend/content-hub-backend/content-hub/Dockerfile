# Stage 1: Build
# Gradle 빌드 환경 설정
FROM gradle:8.10.2-jdk21-alpine AS builder

# 도커 이미지 내부의 작업 디렉터리 설정
WORKDIR /workspace

# 필요한 파일들만 먼저 복사
COPY gradlew gradlew
COPY gradle gradle
COPY settings.gradle settings.gradle
COPY build.gradle build.gradle

# gradlew 파일에 실행 권한 부여
RUN chmod +x gradlew

# 의존성 다운로드 (cache mount)
RUN --mount=type=cache,target=/root/.gradle \
    ./gradlew --console=plain build -x test || true

# 로컬 컴퓨터의 현재 디렉터리에 있는 모든 파일과 폴더를 도커 이미지 내부의 현재 작업 디렉터리로 그대로 복사
COPY . .

# 위에서 gradlew 덮어쓸 위험이 있으면 다시 실행권한 부여
RUN [ ! -x gradlew ] && chmod +x gradlew || true

# 최종 빌드 (bootJar)
RUN --mount=type=cache,target=/root/.gradle \
    ./gradlew --console=plain clean bootJar -x test


# Stage 2: Runtime (JRE만)
# Spring Boot 애플리케이션 실행 환경 설정
FROM eclipse-temurin:21-jre

# 도커 이미지 내부의 작업 디렉터리 설정
WORKDIR /app

# 빌드 스테이지에서 생성된 JAR 파일을 현재 스테이지 내부의 작업 디렉터리로 복사
COPY --from=builder /workspace/build/libs/*.jar app.jar

# 메모리 최적화 옵션 주입 가능
ENV JAVA_OPTS="-XX:+UseContainerSupport -XX:MaxRAMPercentage=75.0"

# 애플리케이션이 8080 포트를 사용함을 도커에 알림
EXPOSE 8080

# 애플리케이션 실행 명령어
ENTRYPOINT ["sh","-c","java $JAVA_OPTS -jar /app/app.jar"]
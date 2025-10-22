# 1단계: 빌드된 JAR 파일을 실행할 기본 이미지를 선택합니다.
# Java 17을 지원하는 경량 리눅스 기반의 Eclipse Temurin JRE 이미지를 권장합니다.
FROM eclipse-temurin:17-jre-focal

# 2단계: 컨테이너 내부에서 JAR 파일이 위치할 경로를 지정합니다.
WORKDIR /app

# 3단계: 로컬에서 빌드한 JAR 파일을 컨테이너 내부로 복사합니다.
# build/libs 디렉토리에서 '-plain'이 없는 파일을 복사합니다.
COPY build/libs/item-service-0.0.1-SNAPSHOT.jar app.jar

# 4단계: Spring Boot 애플리케이션이 사용할 포트를 외부에 노출합니다.
# Spring Boot의 기본 포트는 8080입니다.
EXPOSE 8080

# 5단계: 컨테이너가 시작될 때 실행될 명령어(JAR 파일 실행)를 지정합니다.
ENTRYPOINT ["java", "-jar", "app.jar"]

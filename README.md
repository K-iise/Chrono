<center><img src="image/chronobot_rogo.png" alt="ChronoBots" width="100%" /></center>

# 🕒 우아한테크코스 오픈 미션
![Precourse Week 4](https://img.shields.io/badge/precourse-week4-green.svg)
![Version](https://img.shields.io/badge/version-1.0.0-brightgreen.svg)
![Duration](https://img.shields.io/badge/duration-3_weeks-blue)

> **ChronoBot**은 **Discord API(JDA)** 기반으로 구축된 **온라인 스터디 카페 시스템 지원 봇**입니다.<br>
> 포인트로 이용시간을 충전해 개인 공부방에서 공부 시간을 자동 기록할 수 있으며,<br>
> 이벤트 기간 동안의 누적 이용시간을 기준으로 랭킹·보상 시스템까지 제공하는 통합 학습 관리 플랫폼입니다.

---

## 📌 목차
* [기능 목록](#기능-목록)
* [기술 스택](#기술-스택)
* [실행 방법](#실행-방법)
* [배포 환경](#배포-환경)

---

## 🛠 기술 스택

![Java](https://img.shields.io/badge/Java-21-ED8B00?style=flat&logo=java&logoColor=white)
![Spring Boot](https://img.shields.io/badge/SpringBoot-6DB33F?style=flat&logo=spring&logoColor=white)
![JPA](https://img.shields.io/badge/JPA-Hibernate-59666C?style=flat&logo=hibernate&logoColor=white)
![JDA](https://img.shields.io/badge/JDA-7289DA?style=flat&logo=discord&logoColor=white)
![MySQL](https://img.shields.io/badge/MySQL-4479A1?style=flat&logo=mysql&logoColor=white)
![AWS](https://img.shields.io/badge/AWS-FF9900?style=flat&logo=amazonaws&logoColor=white)
![Maven](https://img.shields.io/badge/Maven-C71A36?style=flat&logo=apachemaven&logoColor=white)
![Git](https://img.shields.io/badge/Git-F05032?style=flat&logo=git&logoColor=white)

---

## 기능 목록

- **포인트 시스템**: 학습 활동으로 포인트 획득 및 이용시간 구매
- **이용시간 관리**: 포인트로 충전한 이용시간으로 타이머 실행 가능
- **개인 공부방**: 개인 전용 텍스트 채널 생성 및 학습 시간 기록
- **타이머 기능**: 공부 시작/종료 시 자동 기록
- **이벤트 관리**: 이벤트 생성, 참여, 기간 내 누적 이용시간 기반 랭킹 산정
- **보상 시스템**: 이벤트 상위권에게 포인트/상품 지급
- **확장 예정**: 포인트 기반 기프티콘 상점, 주간/월간 리더보드 등

---

## 실행 방법

1. **환경 변수 설정 (.env 또는 application.yml)**
    ```text
    DISCORD_TOKEN=your_discord_bot_token
    DB_URL=your_database_url
    DB_USER=your_database_user
    DB_PASSWORD=your_database_password
    ```
2. **의존성 설치 및 빌드**
    ```bash
    mvn clean install
    ```
3. **애플리케이션 실행**
    ```bash
    mvn spring-boot:run
    ```

---

## 배포 환경

- **AWS**: RDS(MySQL) 사용, EC2 혹은 컨테이너 기반 배포 가능
- **JVM**: Java 21
- **Spring Boot**: WebSocket/JDA 연동으로 디스코드 봇 실행
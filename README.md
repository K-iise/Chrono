<center><img src="image/chronobot_rogo.png" alt="ChronoBots" width="80%" /></center>

# 🕒 우아한테크코스 오픈 미션
![Precourse Week 4](https://img.shields.io/badge/precourse-week4-green.svg)
![Version](https://img.shields.io/badge/version-1.0.1-brightgreen.svg)
![Duration](https://img.shields.io/badge/duration-3_weeks-blue)
![Language](https://img.shields.io/badge/language-Java-orange?logo=java&logoColor=white)
![Framework](https://img.shields.io/badge/framework-Spring_Boot-green?logo=spring&logoColor=white)

> **ChronoBot**은 **Discord API(JDA)** 기반으로 구축된 **온라인 스터디 카페 시스템 지원 봇**입니다.<br>
> 포인트로 이용시간을 충전해 개인 공부방에서 공부 시간을 자동 기록할 수 있으며,<br>
> 이벤트 기간 동안의 누적 이용시간을 기준으로 랭킹·보상 시스템까지 제공하는 통합 학습 관리 플랫폼입니다.

## 🛠 기술 스택
![Java](https://img.shields.io/badge/Java-21-ED8B00?style=flat&logo=java&logoColor=white)
![Spring Boot](https://img.shields.io/badge/SpringBoot-6DB33F?style=flat&logo=spring&logoColor=white)
![JPA](https://img.shields.io/badge/JPA-Hibernate-59666C?style=flat&logo=hibernate&logoColor=white)
![JDA](https://img.shields.io/badge/JDA-7289DA?style=flat&logo=discord&logoColor=white)
![MySQL](https://img.shields.io/badge/MySQL-4479A1?style=flat&logo=mysql&logoColor=white)
![AWS](https://img.shields.io/badge/AWS-FF9900?style=flat&logo=amazonaws&logoColor=white)
---

## 📌 목차
* [프로젝트 개요](#프로젝트 개요)
* [주요 기능 목록](#주요 기능 목록)

# ⚙️ 주요 기능 목록

### 1. 사용자 구분

| 구분 | 설명 |
| --- | --- |
| **관리자(Admin)** | 서버장 또는 권한을 부여받은 사용자. 멤버 관리 및 통계 기능 사용 가능 |
| **멤버(Member)** | 일반 사용자. 공부 시간 기록 및 포인트 이용 가능 |

---

### 2. 관리자 기능

#### 2-1) 권한 및 멤버 관리
- 특정 멤버에게 명령어 사용 권한 부여/회수
- 특정 멤버에게 관리자 권한 부여 가능

#### 2-2) 멤버 자원/상태 관리
- 멤버의 포인트 부여(추가/수정)
- 멤버의 이용 시간 부여(추가/수정)
- 멤버의 현재 포인트 조회
- 멤버의 현재 이용 시간 조회

#### 2-3) 시스템 운영 및 통계
- 새로운 이벤트 기획 및 등록/시작
- 멤버 이용 기록 기반 통계 및 분석 자료 생성

---

### 3. 멤버 기능

#### 3-1) 봇 명령어 상호작용
- 현재 사용 가능한 명령어 목록 확인

#### 3-2) 자원 관리
- 포인트를 사용하여 이용 시간 구매
- 현재 포인트 확인
- 남은 이용 시간 확인

#### 3-3) 이용 기록 관리
- 과거 이용 기록 조회
- 기록 시작/종료 명령어를 통한 수동 기록
- 특정 음성 채널 입장 시 자동 기록 시작, 퇴장 시 기록 종료

---

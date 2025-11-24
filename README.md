<center><img src="image/chronobot_rogo.png" alt="ChronoBots" width="100%" /></center>

# 🕒 우아한테크코스 오픈 미션
![Precourse Week 4](https://img.shields.io/badge/precourse-week4-green.svg)
![Version](https://img.shields.io/badge/version-1.0.0-brightgreen.svg)
![Duration](https://img.shields.io/badge/duration-3_weeks-blue)

> **ChronoBot**은 **Discord API(JDA)** 기반으로 구축된 **온라인 스터디 카페 시스템 지원 봇**입니다.<br>
> 포인트로 이용시간을 충전해 개인 공부방에서 공부 시간을 자동 기록할 수 있으며,<br>
> 이벤트 기간 동안의 누적 이용시간을 기준으로 랭킹·보상 시스템까지 제공하는 통합 학습 관리 플랫폼입니다.

---

## 📖 목차 (Table of Contents)
* [💻 기술 스택](#-기술-스택-tech-stack)
* [⚙️ 주요 기능 목록](#-주요-기능-목록-core-features)
* [▶️ 실행 방법](#-실행-방법-how-to-run)
* [☁️ 배포 환경](#-배포-환경-deployment-environment)
* [📘 사용자 가이드](#-사용자-가이드-user-guide)
* [🛠️ 트러블 슈팅](#-트러블-슈팅-trouble-shooting)
---

## 💻 기술 스택 (Tech Stack)

![Java](https://img.shields.io/badge/Java-21-ED8B00?style=flat&logo=java&logoColor=white)
![Spring Boot](https://img.shields.io/badge/SpringBoot-6DB33F?style=flat&logo=spring&logoColor=white)
![JPA](https://img.shields.io/badge/JPA-Hibernate-59666C?style=flat&logo=hibernate&logoColor=white)
![JDA](https://img.shields.io/badge/JDA-7289DA?style=flat&logo=discord&logoColor=white)
![MySQL](https://img.shields.io/badge/MySQL-4479A1?style=flat&logo=mysql&logoColor=white)
![AWS](https://img.shields.io/badge/AWS-FF9900?style=flat&logo=amazonaws&logoColor=white)
![Git](https://img.shields.io/badge/Git-F05032?style=flat&logo=git&logoColor=white)

---

## ⚙️ 주요 기능 목록 (Core Features)

- **포인트 시스템**: 학습 활동으로 포인트 획득 및 이용시간 구매
- **이용시간 관리**: 포인트로 충전한 이용시간으로 타이머 실행 가능
- **개인 공부방**: 개인 전용 텍스트 채널 생성 및 학습 시간 기록
- **타이머 기능**: 공부 시작/종료 시 자동 기록
- **이벤트 관리**: 이벤트 생성, 참여, 기간 내 누적 이용시간 기반 랭킹 산정
- **보상 시스템**: 이벤트 상위권에게 포인트/이용 시간 지급
- **확장 예정**: 포인트 기반 기프티콘 상점, 주간/월간 리더보드 등

---

## ▶️ 실행 방법 (How to Run)

1. **환경 변수 설정 (.env 또는 application.yml)**
    ```text
    DISCORD_TOKEN=your_discord_bot_token
    DB_URL=your_database_url
    DB_USER=your_database_user
    DB_PASSWORD=your_database_password
    ```
2. **의존성 설치 및 빌드**
    ```bash
    ./gradlew clean build
    ```
    - Windows: `gradlew.bat clean build`

3. **애플리케이션 실행**
    ```bash
    ./gradlew bootRun
    ```
    - Windows: `gradlew.bat bootRun`

---

## ☁️ 배포-환경 (Deployment environment)

- **AWS**: RDS(MySQL) 사용, EC2 혹은 컨테이너 기반 배포 가능
- **JVM**: Java 21
- **Spring Boot**: WebSocket/JDA 연동으로 디스코드 봇 실행

---

## 📘 사용자 가이드 (User Guide)

### 🔑 관리자 기능 (Admin Features)

<details>
<summary><strong>📌 관리자 등록(/setup)</strong></summary>

#### 1. `/setup` 명령어 실행
서버 최초 1회, 아래 명령어 입력을 통해 **관리자 계정을 등록**합니다.

<img src="/image/setup_command.png" alt="최초 관리자 등록 명령어">

#### 2. 실행 결과 예시
성공적으로 등록되면 아래와 같은 메시지가 출력됩니다.

<img src="/image/setup_result.png" alt="최초 관리자 등록 결과">

---

#### ⚠️ 왜 setup 명령어가 필요한가?

- 처음 DB에는 **관리자 정보가 저장되어 있지 않음**
- 따라서 봇은 **어느 사용자가 관리자 역할인지 식별할 수 없음**
- 그 결과  
  → 관리자 전용 명령어들이 모두 **차단됨**  
  (예: `/points admin`, `/times admin`, 기타 관리 기능)

이를 해결하기 위해 **최초에 setup 명령어를 반드시 1회 실행**해야 합니다.

---

#### 🔒 실행 조건

| 조건 | 설명 |
|------|------|
| 관리자 수 = 0 | DB에 관리자가 1명이라도 존재하면 `setup` 명령어는 **실행 불가** |
| 최초 서버 설정 단계 | 디스코드 서버 생성 후 **첫 설정 과정에서만 실행 가능** |

즉, **서버 최초 1회만 유효**한 명령어입니다.
</details>

<br>

<details><summary><strong>📌 등급 변경(/role)</strong></summary>

> 관리자가 서버 내 멤버의 **DB 기반 역할(등급**)을 변경하는 명령어입니다. 
> 이 명령어는 **ChronoBot 시스템 내의 권한**을 변경하며, 디스코드 **서버 역할(Role**)도 함께 업데이트됩니다.

**① 명령어 입력**

`/role` 명령어를 입력하고, **user** 옵션으로 대상 사용자를, **role** 옵션으로 변경할 등급(예: **ADMIN, REGULAR**)을 선택합니다.

<img src="/image/role_command.png" alt="등급 변경 명령어">

**② 실행 결과 예시**

선택한 사용자의 등급이 성공적으로 변경되었음을 알리는 메시지가 출력됩니다.

<img src="/image/role_result.png" alt="등급 변경 결과">

</details>

<br>

<details><summary><strong>📌 사용자 등록(/register)</strong></summary>

> 관리자가 서버 내 멤버를 등록하는 명령어입니다.
> 이 명령어는 **개인 텍스트 채널**을 생성하며, 디스코드 **서버 역할(Role**)도 함께 업데이트됩니다.

**① 명령어 입력**

`/register` 명령어를 입력하고, **user** 옵션으로 대상 사용자를 선택합니다.

<img src="/image/register_command.png" alt="사용자 등록 명령어">

**② 실행 결과 예시**

선택한 사용자 성공적으로 등록되었음을 알리는 메시지가 출력됩니다.

<img src="/image/register_result.png" alt="사용자 등록 결과">

</details>

<br>

<details>
<summary><strong>📌 특정 멤버 포인트 관리 (/points admin)</strong></summary>

> 관리자가 특정 멤버의 **포인트를 조회, 추가 또는 설정**하는 명령어 그룹입니다.

---

### 1. 포인트 조회 (/points admin get)

특정 사용자의 **현재 포인트 잔액**을 조회합니다.

**① 명령어 입력**

`/points admin get` 명령어를 입력하고, **user** 옵션으로 조회할 대상을 선택합니다.

<img src="/image/points_admin_get_command.png" alt="관리자 포인트 조회 명령어">

**② 실행 결과 예시**

조회 대상 멤버가 보유한 현재 포인트가 표시됩니다.

<img src="/image/points_admin_get_result.png" alt="관리자 포인트 조회 결과">

---

### 2. 포인트 추가 (/points admin add)

특정 사용자에게 **포인트**를 추가합니다.

**① 명령어 입력**

`/points admin add` 명령어를 입력하고, **user** 옵션과 추가할 **amount** (포인트 값)를 입력합니다.

<img src="/image/points_admin_add_command.png" alt="관리자 포인트 추가 명령어">

**② 실행 결과 예시**

포인트가 성공적으로 추가되었으며, 해당 멤버의 최종 포인트 잔액이 표시됩니다.

<img src="/image/points_admin_add_result.png" alt="관리자 포인트 추가 결과">

---

### 3. 포인트 설정 (/points admin set)

특정 사용자의 포인트를 **특정 값으로 강제 설정**합니다. (기존 포인트를 덮어씁니다.)

**① 명령어 입력**

`/points admin set` 명령어를 입력하고, **user** 옵션과 설정할 **amount** (포인트 값)를 입력합니다.

<img src="/image/points_admin_set_command.png" alt="관리자 포인트 설정 명령어">

**② 실행 결과 예시**

포인트가 새로운 값으로 설정되었으며, 해당 멤버의 최종 포인트 잔액이 표시됩니다.

<img src="/image/points_admin_set_result.png" alt="관리자 포인트 설정 결과">

</details>

<br>

<details>
<summary><strong>📌 특정 멤버 이용 시간 관리 (/times admin)</strong></summary>

> 관리자가 특정 멤버의 **이용 시간을 조회, 추가 또는 설정**하는 명령어 그룹입니다.

---

### 1. 이용 시간 조회 (/times admin get)

특정 사용자의 **현재 잔여 이용 시간**을 조회합니다.

**① 명령어 입력**

`/times admin get` 명령어를 입력하고, **user** 옵션으로 조회할 대상을 선택합니다.

<img src="/image/times_admin_get_command.png" alt="관리자 이용 시간 조회 명령어">

**② 실행 결과 예시**

조회 대상 멤버의 잔여 이용 시간이 시·분·초 형태로 표시됩니다.

<img src="/image/times_admin_get_result.png" alt="관리자 이용 시간 조회 결과">

---

### 2. 이용 시간 추가 (/times admin add)

특정 사용자에게 **이용 시간**을 추가합니다.

**① 명령어 입력**

`/times admin add` 명령어를 입력하고, **user** 옵션과 추가할 **usagetime** (분 단위)를 입력합니다.

<img src="/image/times_admin_add_command.png" alt="관리자 이용 시간 추가 명령어">

**② 실행 결과 예시**

이용 시간이 성공적으로 추가되었으며, 해당 멤버의 최종 잔여 이용 시간이 표시됩니다.

<img src="/image/times_admin_add_result.png" alt="관리자 이용 시간 추가 결과">

---

### 3. 이용 시간 설정 (/times admin set)

특정 사용자의 이용 시간을 **특정 값으로 강제 설정**합니다. (기존 시간을 덮어씁니다.)

**① 명령어 입력**

`/times admin set` 명령어를 입력하고, **user** 옵션과 설정할 **amount** (분 단위)를 입력합니다.

<img src="/image/times_admin_set_command.png" alt="관리자 이용 시간 설정 명령어">

**② 실행 결과 예시**

이용 시간이 새로운 값으로 설정되었으며, 해당 멤버의 최종 잔여 이용 시간이 표시됩니다.

<img src="/image/times_admin_set_result.png" alt="관리자 이용 시간 설정 결과">

</details>

<br>

<details>
<summary><strong>📌 스터디 이벤트 등록 (Discord Events)</strong></summary>

> 관리자가 **이벤트 포럼 채널**에 스터디 목표나 일정을 이벤트 포스트로 등록하는 기능입니다.
> 봇은 이벤트 등록을 감지하여 알림 메시지를 출력합니다.

**① 이벤트 포스트 및 상세 정보 생성**

**1. 포스트 및 이벤트 생성 시작:** 이벤트 포럼 채널에서 **새 포스트(게시물)** 생성을 시작하며, 이벤트의 제목과 내용을 입력하고 해당 **게시물의 링크**를 복사합니다.
<img src="/image/event_record1.png" alt="이벤트 포럼 채널 내 포스트 및 이벤트 생성 시작">

**2. 이벤트 위치 설정:** 이벤트 생성 모달 창에서 이벤트 유형을 **'채널 채팅, 웹훅, 링크'** 중 하나로 선택한 후, **복사한 게시물 링크**를 이벤트가 진행될 위치로 입력합니다.
<img src="/image/event_record2.png" alt="이벤트 위치 설정 단계">

**3. 이벤트 상세 정보 설정:** 이벤트의 **주제, 시작/종료 시간** 등을 상세히 설정합니다.
<img src="/image/event_record3.png" alt="이벤트 주제 및 시간 설정 단계">

**4. 최종 확인 및 등록:** 설정된 내용을 미리 확인하고, **'이벤트 만들기'** 버튼을 클릭하여 최종 등록을 완료합니다.
<img src="/image/event_record4.png" alt="이벤트 최종 확인 단계">

**② 등록 결과 및 봇 알림**

이벤트 등록이 완료되면, `ChronoBot`은 이 이벤트를 감지하고, 아래와 같이 **이벤트가 성공적으로 등록되었음**을 알리는 메시지를 출력합니다.

<img src="/image/event_record_result.png" alt="이벤트 등록 결과 봇 알림">
</details>

<br>

<details>
<summary><strong>📌 이벤트 결과 조회 (/event result)</strong></summary>

> 관리자가 진행 중이거나 종료된 이벤트의 **참여자별 누적 이용 시간**을 조회하여 **랭킹**을 출력하는 명령어입니다. 이 명령어는 **이벤트 포스트(스레드)** 내에서만 사용 가능합니다.

**① 명령어 입력**

`/event result` 명령어를 입력합니다. 이 명령어는 **이벤트가 진행된 포럼 스레드** 내에서 실행해야 해당 이벤트를 인식합니다.

<img src="/image/event_result_command.png" alt="이벤트 결과 조회 명령어 입력">

**② 실행 결과 예시**

명령어 실행 후, **`ChronoBot`**은 해당 이벤트에 참여한 멤버들의 **총 이용 시간**을 기준으로 순위를 매겨 출력합니다.

<img src="/image/event_result.png" alt="이벤트 결과 조회 결과">

</details>

---

### 👤 일반 사용자 기능 (General User Features)

<details><summary><strong>📌 포인트 조회 (/points user get)</strong></summary>

> 사용자가 현재 보유하고 있는 **포인트**를 조회하는 명령어입니다.

**① 명령어 입력**

`ChronoBot`을 멘션하여 `/points user get` 명령어를 입력합니다.

<img src="/image/points_user_get_command.png" alt="유저 포인트 조회 명령어">

**② 실행 결과 예시**

사용자의 보유 포인트가 표시됩니다. 포인트는 학습 활동(공부 기록)을 통해 획득하며, 이 포인트로 이용 시간을 구매합니다.

<img src="/image/points_user_get_result.png" alt="유저 포인트 조회 결과">

</details>

<br>

<details><summary><strong>📌 포인트 사용 (/points user use)</strong></summary>

> 사용자가 현재 보유하고 있는 **포인트**를 사용하여 **이용 시간**을 구매하는 명령어입니다.

**① 명령어 입력**

`ChronoBot`을 멘션하여 `/points user use` 명령어를 입력합니다.
`amount` 옵션으로 구매 하고자 하는 포인트 구매 단위를 입력합니다.

<img src="/image/points_user_use_command.png" alt="유저 포인트 사용 명령어">

**② 실행 결과 예시**

이용 시간이 추가되었음을 알리는 메시지가 출력되고, 사용자의 남은 이용 시간이 표시됩니다. 

<img src="/image/points_user_use_result.png" alt="유저 포인트 사용 결과">

</details>

<br>

<details><summary><strong>📌 이용 시간 조회 (/times user get)</strong></summary>

> 사용자가 현재 보유하고 있는 **잔여 이용 시간**을 조회하는 명령어입니다.

**① 명령어 입력**

`ChronoBot`을 멘션하여 `/times user get` 명령어를 입력합니다.

<img src="/image/times_user_get_command.png" alt="유저 이용 시간 조회 명령어">

**② 실행 결과 예시**

현재 남아 있는 이용 시간이 시·분 형태로 포맷팅되어 표시됩니다.
해당 시간은 스터디룸 또는 공부 기록 가능 시간을 의미합니다.

<img src="/image/times_user_get_result.png" alt="유저 이용 시간 조회 결과">

</details>

<br>

<details>
<summary><strong>📌 공부 기록 시작/종료 (/record)</strong></summary>

> 이용 시간을 사용하여 개인 **공부 기록을 시작하고 종료**하는 명령어 그룹입니다.

---

### 1. 공부 시작 (/record start)

**잔여 이용 시간**을 소모하여 개인 공부 기록을 시작합니다. 기록 시작 시, 이용 시간 소모가 시작되고 타이머가 작동합니다.

**① 명령어 입력**

`/record start` 명령어를 입력합니다.

<img src="/image/record_start_command.png" alt="공부 기록 시작 명령어">

**② 실행 결과 예시**

공부 기록이 시작되었음을 알리는 메시지와 함께, 잔여 이용 시간이 표시됩니다. 이 시간만큼 기록이 자동 종료되도록 예약됩니다.

<img src="/image/record_start_result.png" alt="공부 기록 시작 결과">

---

### 2. 공부 종료 (/record end)

진행 중이던 공부 기록을 수동으로 종료하고, **실제 공부한 시간**만큼 이용 시간을 차감합니다.

**① 명령어 입력**

`/record end` 명령어를 입력합니다.

<img src="/image/record_end_command.png" alt="공부 기록 종료 명령어">

**② 실행 결과 예시**

공부 기록이 종료되었음을 알리는 메시지와 함께, **이번 세션의 공부 시간**이 표시됩니다.

<img src="/image/record_end_result.png" alt="공부 기록 종료 결과">

</details>

<br>

<details>
<summary><strong>📌 이용 시간 통계 조회 (/usage)</strong></summary>

> 사용자가 자신의 **누적 이용 시간 기록**을 주간, 월간, 연간 단위로 조회하는 명령어 그룹입니다.

---

### 1. 주간 이용 시간 조회 (/times week)

최근 **1주일** 동안의 누적 이용 시간을 조회합니다.

**① 명령어 입력**

`/times week` 명령어를 입력합니다.

<img src="/image/times_week_command.png" alt="주간 이용 시간 조회 명령어">

**② 실행 결과 예시**

지난 1주간의 총 공부 시간이 시·분 형태로 표시됩니다.

<img src="/image/times_week_result.png" alt="주간 이용 시간 조회 결과">

---

### 2. 월간 이용 시간 조회 (/times month)

최근 **1개월** 동안의 누적 이용 시간을 조회합니다.

**① 명령어 입력**

`/times month` 명령어를 입력합니다.

<img src="/image/times_month_command.png" alt="월간 이용 시간 조회 명령어">

**② 실행 결과 예시**

지난 1개월간의 총 공부 시간이 시·분 형태로 표시됩니다.

<img src="/image/times_month_result.png" alt="월간 이용 시간 조회 결과">

---

### 3. 연간 이용 시간 조회 (/times year)

최근 **1년** 동안의 누적 이용 시간을 조회합니다.

**① 명령어 입력**

`/times year` 명령어를 입력합니다.

<img src="/image/times_year_command.png" alt="연간 이용 시간 조회 명령어">

**② 실행 결과 예시**

지난 1년간의 총 공부 시간이 시·분 형태로 표시됩니다.

<img src="/image/times_year_result.png" alt="연간 이용 시간 조회 결과">

</details>

<br>

<details>
<summary><strong>📌 스터디 이벤트 참여 (Event Participation)</strong></summary>

> 등록된 스터디 이벤트에 참여 의사를 표시하여, 이벤트 기간 동안의 학습 기록을 랭킹 산정에 포함시키는 기능입니다. 봇은 참여를 감지하여 알림 메시지를 출력합니다.

**① 이벤트 참여 방법**

이벤트 포럼 채널에서 원하는 이벤트 포스트를 클릭하여 상세 정보를 확인합니다. 다음 두 가지 방법 중 하나를 통해 이벤트에 참여할 수 있습니다.

**1. '관심 있음' 버튼으로 참여**
이벤트 세부사항 모달 창 하단에 있는 **'관심 있음'** 버튼을 클릭하여 참여합니다.
<img src="/image/event_participate1.png" alt="이벤트 세부 사항 모달 창에서 관심 있음 버튼으로 참여">

**2. '팔로우하기' 옵션으로 참여 (대체 방법)**
이벤트 포스트 자체의 메뉴(우측 상단 점 세 개)를 열고 **'팔로우하기'** 옵션을 선택하여 이벤트에 참여합니다. 이 방법은 이벤트 알림을 받음과 동시에 참여 의사를 표시합니다.
<img src="/image/event_participate2.png" alt="이벤트 포스트 메뉴에서 팔로우하기 옵션으로 참여">

**② 참여 결과 및 봇 알림**

성공적으로 이벤트에 참여하면, `ChronoBot`이 이를 감지하고 해당 이벤트 채널에 아래와 같이 참여 알림 메시지를 출력합니다.

<img src="/image/event_participate_result.png" alt="이벤트 참여 결과 봇 알림">

</details>

## 🛠️ 트러블 슈팅 (Trouble Shooting)

<details>
<summary><strong>⚡ Slash Command 3초 응답 제한 초과 오류 (Timeout Error)</strong></summary>

### ❗ 문제 현상 (Symptom)
데이터베이스 조회, 복잡한 계산 등 **처리 시간이 3초를 초과**하는 작업 수행 시, 최종 응답(reply)에서 아래와 같은 오류가 발생합니다.

`java.lang.IllegalStateException: Interaction has already been acknowledged`

---

### 🔍 원인 (Cause)
Discord API는 슬래시 커맨드(`Slash Command`)와 같은 **모든 상호작용(Interaction)** 발생 시, **최대 3초 이내에 봇이 응답했다는 것을 반드시 알려야**(`Acknowledge`, ACK) 합니다.

* **ACK 실패:** 3초 안에 응답하지 않으면 디스코드는 해당 Interaction을 **만료됨(Expired)** 상태로 간주하고, 최종 응답을 위한 `event.reply()` 호출 시 **이미 응답되었거나 만료된 상호작용**이라는 오류가 발생합니다.

---

### ✅ 해결 방법 (Solution)

작업에 시간이 걸리는 경우, **실제 작업 실행 전에 먼저 디스코드에 응답을 미룬다는 ACK**를 보냅니다. 이후 최종 응답은 **웹훅(Webhook**)을 통해 안전하게 처리합니다.

#### 1. `deferReply()`로 즉시 응답 ACK 처리

시간이 걸리는 로직을 실행하기 **직전에** `deferReply()`를 호출하여 디스코드에게 **응답 처리 중입니다. 잠시 기다려주세요.** 라는 상태를 즉시 알립니다.

```java
event.deferReply(true).queue(); // 즉시 ACK 처리.
```

#### 2. getHook().sendMessage()로 안전하게 최종 응답
오래 걸리는 작업이 완료된 후, event.getHook()을 사용하여 응답 웹훅을 통해 최종 메시지를 보냅니다. 웹훅을 사용하면 3초 제한을 우회하여 최대 15분 이내에 안전하게 응답할 수 있습니다.

```java
Member member = memberService.findMember(userId); // 시간이 오래 걸리는 DB 조회/로직

// getHook()을 사용하여 웹훅 채널로 최종 메시지를 전송
event.getHook().sendMessage(member.getPoint() + " 포인트 보유").queue();
```

> ⭐ 핵심 요약: deferReply()로 3초 제한을 회피하고, event.getHook()으로 reply 중복 호출 문제를 동시에 해결합니다.
</details>
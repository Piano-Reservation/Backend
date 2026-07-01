# Backend Piano — 실행 가이드

> **Docker만 있으면 됩니다.** Java, Gradle, MySQL을 별도로 설치할 필요 없습니다.

---

## 목차

1. [백엔드 실행 (with Docker)](#1-백엔드-실행-with-docker)
   - [사전 준비](#사전-준비)
   - [저장소 클론](#저장소-클론)
   - [서버 시작](#서버-시작)
   - [서버 종료](#서버-종료)
   - [로그 확인](#로그-확인)
2. [백엔드 실행 중 자주 발생하는 오류](#2-백엔드-실행-중-자주-발생하는-오류)
   - [Cannot connect to the Docker daemon](#cannot-connect-to-the-docker-daemon--is-the-docker-daemon-running)
   - [Port 8080 already in use](#port-8080-already-in-use)
   - [Port 3306 already in use](#port-3306-already-in-use)
   - [서버가 시작됐다가 바로 꺼지는 경우](#서버가-시작됐다가-바로-꺼지는-경우)
   - [코드를 수정했는데 반영이 안 될 때](#코드를-수정했는데-반영이-안-될-때)
3. [데이터베이스 초기화 및 테스트 계정](#3-데이터베이스-초기화-및-테스트-계정)
   - [테스트 계정](#테스트-계정)
   - [서버 재시작 시 데이터 초기화 여부 (ddl-auto)](#서버-재시작-시-데이터-초기화-여부-ddl-auto)
   - [초기 데이터 수정 방법 (data.sql)](#초기-데이터-수정-방법-datasql)

---

## 1. 백엔드 실행 (with Docker)

### 사전 준비

[Docker Desktop](https://www.docker.com/products/docker-desktop/) 설치 후 실행해 두세요.

설치 확인:
```bash
docker --version
```

---

### 저장소 클론

```bash
git clone https://github.com/Piano-Reservation/Backend_Piano.git
cd Backend_Piano
```

---

### 서버 시작

```bash
docker-compose up --build
```

- 처음 실행 시 이미지 빌드와 DB 세팅으로 **5~10분** 정도 걸립니다.
- 아래 로그가 뜨면 준비 완료입니다:

```text
app  | Started BackendPianoApplication in X.XXX seconds
```

> 터미널을 닫아도 계속 실행하려면:
> ```bash
> docker-compose up --build -d
> ```

**접속 확인**

| 항목 | URL |
|------|-----|
| API Base URL | `http://localhost:8080` |
| API 명세 (Swagger) | `http://localhost:8080/swagger-ui/index.html` |

> Swagger에서 어떤 API가 있는지, 어떤 값을 주고받는지 직접 확인하고 테스트해볼 수 있습니다.

---

### 서버 종료

```bash
docker-compose down
```

---

### 로그 확인


서버가 이상하게 동작할 때 로그를 확인하세요.  
(사실 보셔도 잘 모를 수도 있으니 캡쳐해서 백엔드에게 뎐져주시길)

```bash
# 앱 로그 실시간 확인
docker-compose logs -f app

# DB 로그 실시간 확인
docker-compose logs -f mysql
```

---

## 2. 백엔드 실행 중 자주 발생하는 오류

### `Cannot connect to the Docker daemon` / `Is the docker daemon running?`

Docker Desktop이 실행되지 않은 상태입니다.

Mac 런치패드 또는 Spotlight(⌘ + Space)에서 **Docker**를 검색해 앱을 실행하세요.
상단 메뉴바에 고래 아이콘이 뜨고 완전히 로딩된 뒤 다시 명령어를 실행하면 됩니다.

---

### `Port 8080 already in use`

8080 포트를 다른 프로그램이 이미 사용 중입니다.

```bash
# 어떤 프로그램이 쓰고 있는지 확인
lsof -i :8080
```

확인 후 해당 프로그램을 종료하거나, `docker-compose.yml` 파일에서 `"8080:8080"`을 `"9090:8080"`으로 바꿔서 포트를 변경하세요.

---

### `Port 3306 already in use`

MySQL이 내 컴퓨터에 이미 설치되어 실행 중인 경우입니다.

로컬 MySQL을 종료하거나, `docker-compose.yml` 파일에서 `"3306:3306"`을 `"3307:3306"`으로 바꾸세요.

---

### 서버가 시작됐다가 바로 꺼지는 경우

MySQL 준비가 끝나기 전에 앱이 먼저 뜨려다 실패하는 경우입니다. 잠시 기다리면 자동으로 재시도하며 정상 실행됩니다.

---

### 코드를 수정했는데 반영이 안 될 때

```bash
docker-compose down
docker-compose up --build
```

---

## 3. 데이터베이스 초기화 및 테스트 계정

### 테스트 계정

서버를 시작하면 아래 계정이 자동으로 만들어집니다. 로그인 테스트에 사용하세요.

| 학번      | 이름     | 학년  | 비밀번호 | 비고         |
|-----------|----------|-------|----------|--------------|
| 202312001 | 김테스트 | 1학년 | 000000   |              |
| 202212001 | 이테스트 | 2학년 | 000000   |              |
| 202112001 | 박테스트 | 3학년 | 000000   |              |
| 202012001 | 최테스트 | 4학년 | 000000   |              |
| 202512001 | 제한테스트 | 1학년 | 000000  | 이용제한 계정 |
| 202412001 | 이용기록테스트 | 2학년 | 000000 | 2026-06-30 이용기록(COMPLETED) 보유 |

---

### 서버 재시작 시 데이터 초기화 여부 (ddl-auto)

지금은 **서버를 껐다 켜면 DB 데이터가 전부 초기화**되도록 설정되어 있습니다.

직접 추가한 데이터가 있어도 서버를 재시작하면 사라지고, 위 테스트 계정만 다시 생성됩니다.

**데이터를 유지하면서 개발하고 싶다면** 아래 파일에서 두 값을 수정하세요.

파일 경로: `src/main/resources/application.properties`

```properties
# 변경 전 (현재 설정 — 재시작 시 DB 초기화)
spring.jpa.hibernate.ddl-auto=create
spring.sql.init.mode=always

# 변경 후 (재시작해도 DB 데이터 유지)
spring.jpa.hibernate.ddl-auto=update
spring.sql.init.mode=never
```

수정 후에는 서버를 재시작하면 됩니다:
```bash
docker-compose down
docker-compose up --build
```

이 상태에서 DB를 완전히 초기화하고 싶을 때는 아래 명령어를 사용하세요:
```bash
docker-compose down -v   # 볼륨까지 삭제 → 다음 시작 시 DB 전체 새로 생성
docker-compose up --build
```

> `docker-compose down -v` 는 `ddl-auto=update` 상태일 때만 의미 있습니다.
> 현재 기본값(`create`)에서는 어차피 서버 켤 때마다 DB가 새로 만들어지므로 `-v` 없이 `down`만 해도 동일합니다.

---

### 초기 데이터 수정 방법 (data.sql)

서버 시작 시 자동으로 삽입되는 데이터는 아래 파일에서 관리합니다.

```
src/main/resources/data.sql
```

이 파일을 열면 테스트 계정 등의 초기 데이터가 SQL로 작성되어 있습니다.
계정을 추가하거나 값을 바꾸고 싶다면 이 파일을 수정하세요.

- `ddl-auto=create` (기본값) 상태에서는 서버를 재시작하면 자동으로 반영됩니다.
- `ddl-auto=update` 로 변경한 경우에는 `docker-compose down -v` 로 DB를 초기화한 뒤 재시작해야 반영됩니다.

---
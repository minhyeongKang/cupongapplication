# Cupong 🛍️

쿠팡 스타일의 쇼핑몰 웹 애플리케이션입니다.

## 기술 스택

| 분류 | 기술 |
|---|---|
| Backend | Spring Boot 3.5, Java 17 |
| View | Thymeleaf, Bootstrap 5 |
| Security | Spring Security 6 |
| Database | MySQL, Spring Data JPA |
| Build | Gradle |
| 외부 API | 네이버 쇼핑 검색 API, 카카오 주소 검색 |

## 주요 기능

### 회원
- 회원가입 / 로그인 / 로그아웃
- 마이페이지 (프로필 수정, 비밀번호 변경)
- 카카오 주소 검색 연동

### 상품
- 네이버 쇼핑 API 연동 — 앱 시작 시 실제 상품 자동 로드
- 카테고리별 분류 / 키워드 검색 / 페이징

### 주문 흐름

```
고객 주문 → 관리자 승인 → 운송장 입력 → 배송기사 완료 처리
 (PENDING)    (PAID)       (SHIPPING)        (DELIVERED)
```

### 역할별 기능

| 역할 | 기능 |
|---|---|
| USER | 상품 조회, 장바구니, 주문, 리뷰 |
| ADMIN | 상품·카테고리·주문 관리, 주문 승인, 배송 시작 |
| DELIVERY | 배송 중 목록 조회, 배송 완료 처리 |

## 시작하기

### 요구사항

- Java 17
- MySQL 8.0+
- 네이버 개발자 계정 (쇼핑 검색 API)

### 설정

```bash
# 시크릿 파일 복사
cp src/main/resources/application-secret.yaml.example \
   src/main/resources/application-secret.yaml
```

`application-secret.yaml`을 열어 실제 값으로 수정합니다.

```yaml
spring:
  datasource:
    username: YOUR_DB_USERNAME
    password: YOUR_DB_PASSWORD

naver:
  api:
    client-id: YOUR_NAVER_CLIENT_ID
    client-secret: YOUR_NAVER_CLIENT_SECRET
```

> 네이버 API 키 발급: https://developers.naver.com → 쇼핑 검색 API 신청

### DB 생성

```sql
CREATE DATABASE cupongdb CHARACTER SET utf8mb4;
```

### 실행

```bash
./gradlew bootRun
```

앱 시작 시 자동으로:
- 관리자 계정 생성 (`admin@cupong.com` / `admin1234`)
- 배송기사 계정 생성 (`driver@cupong.com` / `driver1234`)
- 네이버 API로 상품 75개 로드 (5개 카테고리 × 3키워드 × 5개)

### 접속

| URL | 설명 |
|---|---|
| http://localhost:8080 | 메인 페이지 |
| http://localhost:8080/admin | 관리자 페이지 |
| http://localhost:8080/delivery | 배송기사 페이지 |

## 프로젝트 구조

```
src/main/java/com/hellomeen/cupongapplication/
├── config/         # Security, DataInitializer 등 설정
├── controller/     # MVC 컨트롤러
├── dto/            # 요청 DTO
├── entity/         # JPA 엔티티
├── exception/      # 커스텀 예외
├── repository/     # Spring Data JPA 리포지토리
└── service/        # 비즈니스 로직

src/main/resources/
├── application.yaml                  # 공개 설정
├── application-secret.yaml.example   # 시크릿 설정 템플릿
└── templates/                        # Thymeleaf 템플릿
```

## 보안

민감 정보(`application-secret.yaml`)는 `.gitignore`에 등록되어 저장소에 포함되지 않습니다.

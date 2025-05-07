# 주식 커뮤니티 웹사이트 <br/>(도메인명 : www.stockburning.shop)

## 👨‍🏫 프로젝트 소개
사용자들이 특정 종목에 대한 의견을 나누는 토론 게시판을 제공하고 개인의 상승 및 하락 의견을 게시글과 함께 표현 가능하며 게시글의 작성 개수와 상승/하락 의견을 통해 투자 심리를 분석하여 지표 형식으로 보여주는 주식 커뮤니티 플랫폼입니다.<br/>



## ⏲️ 개발 기간
- 2024/12/30 ~
  
## 🧑‍🤝‍🧑 개발자 소개
- 최동훈
  
## 💻 개발환경
- Version : Java 17
- IDE : IntelliJ
- Framework : SpringBoot 3.4.0
- ORM : JPA
  
## ⚙️ 기술 스택
- 프론트엔드 : Vue.js
- 백엔드 : Spring Boot
- WS/WAS : Tomcat
- 실시간 데이터 처리 : Kafka, Redis
- 캐시 : Redis
- 서버 배포 : Amazon EC2
- 데이터베이스 : Amazon RDS(mysql)
- 이미지 파일 저장 및 관리 : Amazon S3
- 프록시 : nginx
- CI/CD : Jenkins
- 인증 및 권한 부여 : Oauth2, JWT
---
## 🔸Kafka
### 1️⃣ 실시간 종목 검색 이벤트를 처리하는데 사용
- Prodcuer : 사용자가 검색한 티커를 Kafka의 "stock_search_topic" 토픽으로 비동기 전송한다.
- Consumer : Kafka 에서 "stock_search_topic" 토픽을 구독하여 새로운 검색 이벤트가 발생하면 자동으로 수신한다 -> 수신한 ticker 데이터를 Redis를 사용하여 검색 횟수 증가 및 검색 순위를 출력한다.
## 🔸Redis
### 1️⃣ 회원가입 시 이메일로부터 받는 인증코드를 3분 동안 유지 후 자동 삭제하기 위해 사용 (일회성 인증코드 저장소)
- Key : 사용자가 입력한 이메일, Value : 생성된 인증 코드, Timeout : 3분동안 유지 후 자동 삭제 
### 2️⃣ QQQ, SPY 종목 지수를 출력하기 위해 사용
- 사용자마다 API를 호출하는 경우를 방지하기 위해 서버가 호출하고 이를 Redis에 저장 후 출력
### 3️⃣ 실시간 검색어 순위 기능에 사용
- Kafka로 이벤트를 수신하면 검색 티커에 대해 횟수를 증가 시켜 검색어 순위 기능을 구현
---
## 🚀 StockBurning 웹사이트 주요 기능

1️⃣ 📊 실시간 주식 정보 제공
- 나스닥 IT 대형주에 투자하는 ETF 인 'QQQ' 와 S&P 500 에 투자하는 ETF 인 'SPY' 의 지수를 2분 지연된 지수로 출력

2️⃣ 🗣️ 주식 토론 커뮤니티
- 특정 종목에 대한 사용자 의견과 상승 및 하락 의견을 함께 표출 가능
- 게시글에 이미지 첨부 가능
- 일주일 간 게시글의 수와 상승/하락 의견 수 변화량을 차트형식으로 사용자들에게 제공

3️⃣ 📌 인기 검색 종목 순위 분석
- 사용자들의 검색량을 기반으로 검색량 급등하는 종목 순위 제공
- Kafka & Redis를 활용해 10분 단위로 검색량 랭킹 반영
- 사용자들이 상승 의견에 배팅한 상위 3 종목 순위 제공

## 📝 시스템 아키텍쳐
![stockburning drawio](https://github.com/user-attachments/assets/b5531c2d-fee1-4389-9743-e237ea40946a)

## 📝 ER 다이어그램
![Untitled](https://github.com/user-attachments/assets/2adf2b70-2f55-40cc-81e6-f360a93d4940)

## 📝 로그인 후 API 요청까지 표현한 시퀀스 다이어그램
![image](https://github.com/user-attachments/assets/8faa5ded-a37a-4e11-8bcc-de38734ce433)

## 📝 와이어프레임(화면 구성)
![image](https://github.com/user-attachments/assets/b91347f6-1896-4c67-817a-19519892d63b)

## 📝 구현(이미지)
1️⃣ 메인화면 (비로그인 상태)
![1](https://github.com/user-attachments/assets/3f820c2c-b010-46fb-9e14-db44defb3ac2)

2️⃣ 메인화면 (로그인 상태)
![2](https://github.com/user-attachments/assets/46b6c8aa-c023-4ce9-82ac-ee8bed7378df)

3️⃣ 티커 입력 후 화면
![image](https://github.com/user-attachments/assets/dfda2536-8d13-4c99-981d-7b9d58919094)

4️⃣ 로그인 화면
![image](https://github.com/user-attachments/assets/d9cd722d-5bb2-498d-b004-3bf7b7171c8f)

5️⃣ 회원가입 화면
![image](https://github.com/user-attachments/assets/d1abe1ea-ecb5-4336-96d3-2728ef2964ea)

6️⃣ 아이디 찾기 화면
![image](https://github.com/user-attachments/assets/e8d1bed6-9938-4beb-8098-6f0b95205ecc)

7️⃣ 비밀번호 찾기 화면
![image](https://github.com/user-attachments/assets/3acc4b62-6a38-4357-baad-39b6f6d57ded)

8️⃣ 정보 수정 화면
![image](https://github.com/user-attachments/assets/d5a4549a-b95a-4fbb-92f4-b0d3485affe9)

9️⃣ 비밀번호 수정 화면
![image](https://github.com/user-attachments/assets/b6e7dde4-4894-4e8d-87c5-e0946f2a4d80)

1️⃣0️⃣ 설정 화면
![image](https://github.com/user-attachments/assets/abc130ab-8b0a-4aff-b113-441231857dd1)

## 📝 구현(동영상)

---
### 1️⃣ 회원가입

https://github.com/user-attachments/assets/3d96fb9c-b3d7-4a29-ab03-ff5b64fc9b4f



---
### 2️⃣ 게시글 작성 및 티커 검색

https://github.com/user-attachments/assets/59115b4f-cd25-4f5f-8781-b923d30da1ad



---
### 3️⃣ 게시글 삭제 및 수정

https://github.com/user-attachments/assets/65216202-f400-4c50-acb3-b51d6cce08b5



---
### 4️⃣ 아이디 찾기

https://github.com/user-attachments/assets/f2032d45-e00d-4ba0-9b78-e20b6ffe1722


---
### 5️⃣ 비밀번호 찾기

https://github.com/user-attachments/assets/015e8d13-cf49-487a-addb-bb947f470c04


---
### 6️⃣ 비밀번호 변경

https://github.com/user-attachments/assets/7a9a03c4-e5de-4bf2-877d-9fba60c39661



---
### 7️⃣ 닉네임 변경

https://github.com/user-attachments/assets/53ca2484-366e-4b55-811f-0161ebcf8ee8


---
### 8️⃣ 프로필 사진 변경

https://github.com/user-attachments/assets/810fc231-b5c7-477b-b2f2-1053d857dcc7


---
### 9️⃣ 계정 탈퇴

https://github.com/user-attachments/assets/efe74dc0-174c-4f41-aef4-d7a38c7e0f94



---
### 9️⃣ 좋아요 기능 및 댓글 작성

https://github.com/user-attachments/assets/fb03c64a-4649-4321-8eae-8379a5231f54





## 📌 배운점
---
#### ✅ 스프링 시큐리티의 기본적인 구조를 피그마로 직접 그려가며 이해하였다.
![image](https://github.com/user-attachments/assets/8f9afccf-6311-4f86-ba3e-30290bc0580b)

---
#### ✅ Jwt의 기본적인 구조와 accessToken, refreshToken을 메모리, 세션 스토리지, 로컬 스토리지, 쿠키 중 어디에 저장하는게 보안상 안전한지, Jwt를 사용하면서 보안상 조심해야할 점이 저장 위치뿐만 다음과 같은 항목들을 유의해야한다는 점
1. 서명 알고리즘(none 사용금지)
2. 유효 기간(길게 하면 보안에 취약)
3. Payload는 데이터가 노출되므로 민감한 정보는 저장하지 않기
4. CORS 설정
5. CSRF 공격 방지를 위해 SameSite = strict로 설정![image](https://github.com/user-attachments/assets/3b7a48e4-4e71-4bba-b259-b33e6326f4fa)
6. refreshToken은 HttpOnly Secure Cookie 사용 ![image](https://github.com/user-attachments/assets/5fea2313-12a7-421e-af52-b98d2af4824b)
![image](https://github.com/user-attachments/assets/4bf89764-523b-4bb9-9ead-8c64cb140958)
---
#### ✅ 사용자가 OAuth2 를 통해 로그인할 때 어떤 로직을 통해 인증을 하는지 피그마로 직접 그려가며 이해하였으며 맨 처음 카카오 로그인페이지에 접속하는 것부터 Authorization Server 에 AccessToken을 요청하고 발급받고 다시 Resource Server에 AccessToken을 통해 사용자 정보를 요청하는 과정 모두 직접 구현하지 않아도 SecurityConfig 에 추가적으로 Oauth2 관련 코드를 작성하면 소셜 로그인을 구현할 수 있다는 것을 알게 되었다.  
![image](https://github.com/user-attachments/assets/1be14263-8b78-4921-87cf-0b3f30a62282)



## ✒️ 사용한 API
- QQQ, SPY 지수 : [https://finnhub.io/](https://finnhub.io/)


# 주식 커뮤니티 웹사이트 (도메인명 : www.stockburning.shop)

## 👨‍🏫 프로젝트 소개
사용자들이 특정 종목에 대한 의견을 나누는 토론 게시판을 제공하고 게시글의 작성 개수와 개인의 상승 및 하락 의견을 게시글과 함께 표현 가능하며 이를 통해 투자 심리를 분석하여 지표 형식으로 보여주는 주식 커뮤니티 플랫폼입니다.<br/>



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

## 📝 프로젝트 아키텍쳐


## 📝 와이어프레임(화면 구성)
- 전체 구성
![image](https://github.com/user-attachments/assets/b91347f6-1896-4c67-817a-19519892d63b)

- 세부 구성
![image](https://github.com/user-attachments/assets/28e8bf42-082d-438f-86fb-b9b26d9201a9)
![image](https://github.com/user-attachments/assets/e2f64f50-945e-4b04-9f88-c969d7b2da80)
![image](https://github.com/user-attachments/assets/9d59f301-728b-4726-880e-183130875d0a)
![image](https://github.com/user-attachments/assets/5384869c-1a8e-4dde-a4d3-83d1349d09de)
![image](https://github.com/user-attachments/assets/4aa8f516-9ba0-43c5-ba48-ba99f5fdd115)
![image](https://github.com/user-attachments/assets/b954317e-d64e-4ed7-a64e-64214e24f935)
![image](https://github.com/user-attachments/assets/70a391fe-d34f-4feb-8c06-00b60d75327b)
![image](https://github.com/user-attachments/assets/00c0b3f0-3d6e-4998-b4f1-b71107ddce1b)


## 📝 구현(화면 구성)


## 📌 배운점






## ✒️ 사용한 API
- QQQ, SPY 지수 : [https://finnhub.io/](https://finnhub.io/)


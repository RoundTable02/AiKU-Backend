<h1 align="center" style="font-weight: bold;">지각을 방지하는 약속 관리 어플리케이션 AIKU ✨</h1>
<p align="center">
  <img align='center' src='https://github.com/user-attachments/assets/d14b7984-07a7-47f5-9b0c-0453487fa9dd' width="700"/></img>
</p>
<p align="center">
  기존 모놀리식 코드 : https://github.com/kyoona/AiKu_backend
</p>
<p align="center">기간 | 2024.08.05 - 진행중</p>
<p align="center">팀원 | 곽유나, 최원탁</p>

### 아키텍처
<p align="center">
  <img src="https://github.com/user-attachments/assets/7c67ca53-005a-48a9-8422-72638ccf1b57"/>
</p>

### ER Diagram
<p align="center">
  <img src="https://github.com/user-attachments/assets/379ede92-bd98-4af0-8e3c-542b458f70de"/>
</p>

*자세한 기획 및 기능은 팀 레포지토리를 참고해주세요.*   
https://github.com/AiKU-Dev/Backend



<h2 id="technologies">🛠️ 기술</h2>

| Category | Stack |
| --- | --- |
| Language | Java |
| Framework | Spring Boot |
| Library | Spring Data JPA, Spring Cloud Gateway, Query DSL |
| Database | MySQL, Redis |
| Infra | AWS EC2, S3, nginx, Apache Kafka, Docker |
| Cloud Service | Firebase Messaging |

[![My Skills](https://skillicons.dev/icons?i=java,spring,kafka,docker,firebase,aws,redis,mysql,nginx)](https://skillicons.dev)

</br>

---
### 개인 개발 파트 (트러블 슈팅)

- 아키텍처 및 ERD 설계   
  [[VELOG : MSA로 전환하기]](https://velog.io/@_roundtable/MSA%EB%A1%9C-%EC%A0%84%ED%99%98%ED%95%98%EA%B8%B0)   
  [[VELOG : MSA에서 DDD 활용하기]](https://velog.io/@_roundtable/MSA%EC%97%90%EC%84%9C-DDD-%ED%99%9C%EC%9A%A9%ED%95%98%EA%B8%B0)
    
- Spring Security 기반의 로그인 및 회원가입 로직 작성, JWT 토큰 발급 및 갱신
     
- Main 서버 유저 도메인 관련 서비스 개발
  
- 실시간 위치 공유 및 레이싱 서비스 개발   
  [[VELOG : 실시간 위치 공유 구현기]](https://velog.io/@_roundtable/%EC%8B%A4%EC%8B%9C%EA%B0%84-%EC%9C%84%EC%B9%98-%EA%B3%B5%EC%9C%A0-%EA%B5%AC%ED%98%84%EA%B8%B0)
  
- Alarm 서버 알림 리스트 저장/조회 및 Firebase Cloud Message 전송 로직 작성
  
- 이벤트 기반 칭호 부여 로직 개발
  
- 멤버 포인트 출입 관련 로직 개발   
  [[VELOG : 포인트의 변화는 민감한 비즈니스 로직이다 (락, 보상 트랜잭션)]](https://velog.io/@_roundtable/%ED%8F%AC%EC%9D%B8%ED%8A%B8%EC%9D%98-%EB%B3%80%ED%99%94%EB%8A%94-%EB%AF%BC%EA%B0%90%ED%95%9C-%EB%B9%84%EC%A6%88%EB%8B%88%EC%8A%A4-%EB%A1%9C%EC%A7%81%EC%9D%B4%EB%8B%A4-%EB%9D%BD-%EB%B3%B4%EC%83%81-%ED%8A%B8%EB%9E%9C%EC%9E%AD%EC%85%98)
  
- SAGA 패턴을 이용한 보상 트랜잭션 구현   
  [[VELOG : Enum 처리를 위한 끝 없는 switch문에 관하여… (전략 패턴 사용기)]](https://velog.io/@_roundtable/Enum-%EC%B2%98%EB%A6%AC%EB%A5%BC-%EC%9C%84%ED%95%9C-%EB%81%9D-%EC%97%86%EB%8A%94-switch%EB%AC%B8%EC%97%90-%EA%B4%80%ED%95%98%EC%97%AC-%EC%A0%84%EB%9E%B5-%ED%8C%A8%ED%84%B4-%EC%82%AC%EC%9A%A9%EA%B8%B0)
  
- OIDC 소셜 로그인 개발   
  [[VELOG : 카카오 SDK 사용 시 유의 사항, OIDC 적용기]](https://velog.io/@_roundtable/%EC%B9%B4%EC%B9%B4%EC%98%A4-SDK-%EC%82%AC%EC%9A%A9-%EC%8B%9C-%EC%9C%A0%EC%9D%98-%EC%82%AC%ED%95%AD-OIDC-%EC%A0%81%EC%9A%A9%EA%B8%B0)   
  [[VELOG : 애플 로그인을 추가해보자]](https://velog.io/@_roundtable/%EC%95%A0%ED%94%8C-%EB%A1%9C%EA%B7%B8%EC%9D%B8%EC%9D%84-%EC%B6%94%EA%B0%80%ED%95%B4%EB%B3%B4%EC%9E%90)
  
- CS 관리용 Gmail SMTP를 활용한 이메일 저장 구현   
  [[VELOG : 구글 SMTP 이메일 전송이 종료되다]](https://velog.io/@_roundtable/%EA%B5%AC%EA%B8%80-SMTP-%EC%9D%B4%EB%A9%94%EC%9D%BC-%EC%A0%84%EC%86%A1%EC%9D%B4-%EC%A2%85%EB%A3%8C%EB%90%98%EB%8B%A4)


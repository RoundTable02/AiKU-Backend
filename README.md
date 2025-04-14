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




<h2 id="technologies">🛠️ 기술</h2>

| Category | Stack |
| --- | --- |
| Language | Java |
| Framework | Spring Boot |
| Library | Spring Data JPA, Spring Cloud Gateway, Query DSL |
| Database | MySQL, Redis |
| Infra | AWS EC2, S3, nginx, Apache Kafka, Docker |
| Cloud Service | Firebase Messaging |

</br>
<h2>💻주요 화면 및 기능</h2>

### 1. 그룹 생성 및 그룹 내 약속 생성
- 그룹을 생성하고 카카오톡 url 공유를 통해 사용자를 초대할 수 있습니다.
- 그룹 내 약속을 생성할 수 있고 사용자는 자유롭게 약속에 참가할 수 있습니다.
- 참가자는 약속 시간의 30분 전까지 '꼴찌 고르기' 베팅을 할 수 있습니다.

### 2. 맵 & 실시간 위치 공유
- 약속 시간 30분 전 알림과 함께 맵 기능이 열립니다.
- 맵에서 참가자들의 실시간 위치를 확인할 수 있습니다.
- 참가자끼리 포인트를 걸고 '레이싱'게임을 진행할 수 있습니다.
- 참가자 모두가 약속 장소에 도착하거나, 약속 시간 30분 후에 알림과 함께 맵이 종료됩니다.

### 3. 결과 분석
- 맵이 종료된 후 도착 순위와 베팅 결과를 확인할 수 있습니다.
- 레이싱, 베팅 및 지각비 정산이 진행됩니다.
- 그룹 내 모든 약속 결과(지각 순위, 베팅 승률 등)를 분석한 결과를 확인할 수 있습니다.

<h2>🌊진행 과정</h2>

### 1. 이벤트 스토밍
이벤트 스토밍을 통해 서비스 기획과 로직에 대해 이해관계를 일치시키는 과정을 거쳤습니다.
1. 이벤트를 탐색한다.
2. 타임라인에 맞게 이벤트를 정리한다.
3. 이벤트를 발생시키는 커멘드를 탐색한다.
4. 이벤트에 해당하는 정책을 추가한다.
5. 외부 시스템을 보강한다.
6. 어그리거트를 탐색한다.
7. 바운디드 컨텍스트의 경계를 찾는다.
<img width="791" alt="스크린샷 2024-10-02 오전 1 42 22" src="https://github.com/user-attachments/assets/07875df6-7639-43ba-a4cf-4859af1f117d">

상세히 확대하면 다음과 같습니다.
![Group 2](https://github.com/user-attachments/assets/98a1b5de-6ff5-4d44-9f8b-99132ecc0ce9)

### 2. 바운디드 컨텍스트 분리
1. 서로 연관된 어그리거트를 찾고 경계를 구분한다.
2. 어그리거트 루트를 지정한다.

![Group 3](https://github.com/user-attachments/assets/bfa06bc3-6e59-4ce6-9727-c11824c805d1)

### 3. MSA 설계를 한다.
- 바운디드 컨택스트를 기준으로 서비스를 분리합니다.
- 비용과 효율을 고려하여 알림, 공통, 지도 총 세 가지의 역할을 하는 서버로 분리하였습니다.
- 아키텍처 구조상 필요한 Kafka와 Gateway 서버를 추가합니다.
- 코드 접근과 관리가 편리한 모노레포 방식을 채택하였습니다.

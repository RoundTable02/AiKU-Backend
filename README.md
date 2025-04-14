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


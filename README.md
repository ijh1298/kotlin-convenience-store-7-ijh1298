# kotlin-convenience-store-precourse

---

## 이번 프리코스 목표
- 요구사항 만족
- 문자열 포맷 사용

## 요구사항 Check
### 프로그래밍 요구사항
- [x] indent 2까지만 허용
- [x] else 지양
- [x] enum class 사용
- [x] 함수 길이 10 라인 이내
- [x] 입출력 클래스 별도 구현 (InputView, OutputView)

### 기능 요구사항
- [x] 영수증 출력 후 추가 구매 or 종료 선택 가능
- [x] 잘못된 값을 입력할 경우 `IllegalArgumentException` 발생 후 재입력 받기
- [x] 명확한 `Exception` 유형 처리

#### 재고 관리
- [x] 재고 수량을 고려하여 결제 가능 여부 확인
- [x] 구매할 때마다 재고에서 차감
- [x] 다음 구매에서 재고가 반영

#### 프로모션 할인
- [x] 오늘 날짜가 프로모션 기간 내 포함된 경우만 할인
- [x] N개 구매시 1개 무료 증정(Buy N Get 1 Free)
- [x] 동일 상품에 여러 프로모션 적용 X
- [x] 프로모션 재고 우선 차감, 프로모션 재고 부족한 경우 일반 재고 사용
- [x] 프로모션 상품을 고객이 적게 가져온 경우, 혜택 안내
- [x] 프로모션 재고가 부족해서 혜택 없이 결제할 경우, 그 수량에 대해 정가 결제 안내

#### 멤버십 할인
- [x] 프로모션 미적용 금액의 30% 할인
- [x] 프로모션 적용 후 남은 금액에 멤버십 할인
- [x] 최대 한도 8천원

#### 영수증 출력
- [x] 정렬

## 기능
- InputView
  - 구매할 상품, 수량 입력
  - 프로모션 증정 받을 수 있는 상품 추가할지 여부
  - 재고 부족하여 혜택 없이 결제할 때 정가로 결제할지 여부
  - 멤버십 할인 적용 여부
  - 추가 구매 여부
- OutputView
  - 환영 & 재고 출력
  - 영수증 출력
- Validator
  - 구매할 상품 문자열 유효성 검사
  - 상품명 존재 여부 검사
  - 상품 개수 정수 검사
  - Y\N 문자열 유효성 검사
- Service
  - Output할 영수증 제작
  - 구매할 상품 문자열에서 PurchaseInfo를 반환
    - 프로모션 재고를 먼저 사도록하고, 중간 사용자 응답이 필요한지 Status를 담아 반환
    - 프로모션 재고 없으면 일반 재고를 사서 반환
- Controller
  - PurchaseInfo에서 PurchaseStatus를 확인하여 추가 응답이 필요하면 실행
    - 프로모션 상품 필요하면 추가, 혹은 추가하지 않음
    - 재고 부족하면 혜택 받는 만큼만 사기, 혹은 전부 사기
    - Service에게 응답을 다시 보내 PurchaseInfo를 받아 옴
  - 구매 프로세스 이후 재고 변화 목록인 StockChanges 받아오기
- Store
  - Repository로부터 재고 Fetch (init)
  - 영수증 제작
  - StockChanges를 통해 재고 업데이트
- Repository
  - MD 파일로부터 products, promotion 입력
## 모델
- data class Product
  - 이름
  - 가격
  - 재고
  - 프로모션 유형
- data class Promotion
  - 이름
  - 구매 개수
  - 증정 개수
  - 시작 날짜
  - 종료 날짜
- data class PurchaseInfo
  - Product
  - 구매 개수
  - 증정받은 개수
  - 중간에 구매 중단 여부
- enum class PurchaseStatus
  - SUCCESS - 구매 성공
  - SUCCESS_WITHOUT_PROMO - 프로모션 기간이 지난 상태로 구매 성공
  - FAILURE_NULL_PRODUCT - null 프로덕트 구매 실패
  - REQUEST_EXTRA_RESPONSE - 추가 증정 상품 필요한지 요청
  - REQUEST_WITHOUT_PROMO - 프로모션 없이 결제할 건지 요청
- data class PurchaseResult
  - PurchaseStatus
  - Product
- data class Receipt
  - 아이템 이름
  - 산 개수
  - 증정 개수
  - 할인받은 가격
  - 총 가격
  - 정가로 구매한 가격
  - 혜택가로 구매한 가격
- data class StockChange
  - 아이템 이름
  - List\<PurchaseInfo>

## InputItemValidator 테스트
[x] 입력 형식 예외 처리
[x] 존재하지 않는 상품 예외 처리
[x] 0개 구매 예외 처리
[x] 동일 상품 중복 입력 예외 처리

## ProductValidatorTest 테스트
[x] products.md 파일 문자열 유효성 예외 처리
[x] 상품 가격, 재고 정수 유효성 검사

## ConvenienceStore 테스트
[x] 프로모션 재고 업데이트 확인
[x] 일반 재고 업데이트 확인
[x] 재고 동시 업데이트 확인

## ConvenienceStoreService 테스트
[x] 결제 상태 성공 확인
[x] 결제 상태 추가 상품 받을 건지 확인
[x] 결제 상태 재고 부족인데 정가로 살지 확인

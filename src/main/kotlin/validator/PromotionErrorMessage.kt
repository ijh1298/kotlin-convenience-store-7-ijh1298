package validator

enum class PromotionErrorMessage(val msg: String) {
    INVALID_STRING("[ERROR] Promotion 문자열이 유효하지 않습니다."),
    INVALID_INT_BUY("[ERROR] Promotion 구매 개수가 1 이상의 유효한 정수가 아닙니다."),
    INVALID_INT_GET("[ERROR] Promotion 증정 개수가 1 이상의 유효한 정수가 아닙니다."),
    INVALID_START_DATE("[ERROR] Promotion 시작 날짜가 유효하지 않습니다."),
    INVALID_END_DATE("[ERROR] Promotion 종료 날짜가 유효하지 않습니다.")
}
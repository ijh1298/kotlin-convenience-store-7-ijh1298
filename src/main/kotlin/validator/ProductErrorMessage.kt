package validator

enum class ProductErrorMessage(val msg: String) {
    INVALID_STRING("[ERROR] Product 문자열이 유효하지 않습니다."),
    INVALID_INT_BUY("[ERROR] Product 가격이 유효한 정수가 아닙니다."),
    INVALID_INT_STOCK("[ERROR] Product 재고가 유효한 정수가 아닙니다."),
}
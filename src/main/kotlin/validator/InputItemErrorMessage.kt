package validator

enum class InputItemErrorMessage(val msg: String) {
    INVALID_TYPE("[ERROR] 올바르지 않은 형식으로 입력했습니다. 다시 입력해 주세요."),
    NOT_EXIST_ITEM("[ERROR] 존재하지 않는 상품입니다. 다시 입력해 주세요."),
    OVER_STOCK("[ERROR] 재고 수량을 초과하여 구매할 수 없습니다. 다시 입력해 주세요."),
    WRONG_INPUT("[ERROR] 잘못된 입력입니다. 다시 입력해 주세요."),
    DUPLICATE_NAME("[ERROR] 같은 상품명을 중복하여 입력하였습니다. 다시 입력해 주세요.")
}
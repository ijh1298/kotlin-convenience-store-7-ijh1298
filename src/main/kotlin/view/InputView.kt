package view

import camp.nextstep.edu.missionutils.Console

class InputView {
    fun inputItem(): String {
        println(INPUT_ITEM_MSG)
        return Console.readLine()
    }

    fun inputNeedPromotion(itemName: String): String {
        println(INPUT_NEED_PROMOTION_MSG.format(itemName))
        return Console.readLine()
    }

    fun inputBuyWithoutPromotion(itemName: String, quantity: Int): String {
        println(INPUT_BUY_WITHOUT_PROMOTION_MSG.format(itemName, quantity))
        return Console.readLine()
    }

    fun inputUseMembership(): String {
        println(INPUT_USE_MEMBERSHIP)
        return Console.readLine()
    }

    fun inputTryAgain(): String {
        println(INPUT_TRY_AGAIN)
        return Console.readLine()
    }

    companion object {
        const val INPUT_ITEM_MSG = "구매하실 상품명과 수량을 입력해 주세요. (예: [사이다-2],[감자칩-1])"
        const val INPUT_NEED_PROMOTION_MSG = "\n현재 %s은(는) 1개를 무료로 더 받을 수 있습니다. 추가하시겠습니까? (Y/N)"
        const val INPUT_BUY_WITHOUT_PROMOTION_MSG = "\n현재 %s %d개는 프로모션 할인이 적용되지 않습니다. 그래도 구매하시겠습니까? (Y/N)"
        const val INPUT_USE_MEMBERSHIP = "\n멤버십 할인을 받으시겠습니까? (Y/N)"
        const val INPUT_TRY_AGAIN = "\n감사합니다. 구매하고 싶은 다른 상품이 있나요? (Y/N)"
    }
}
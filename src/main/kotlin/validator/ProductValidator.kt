package validator

import service.ConvenienceStoreService

object ProductValidator {
    fun isValid(product: List<String>): Boolean {
        return try {
            require(product.size == 4) { ProductErrorMessage.INVALID_STRING }
            require(product[1].all { it.isDigit() }) { ProductErrorMessage.INVALID_INT_BUY }
            require(product[2].all { it.isDigit() }) { ProductErrorMessage.INVALID_INT_STOCK }
            true
        } catch (e: IllegalArgumentException) {
            false
        }
    }
}
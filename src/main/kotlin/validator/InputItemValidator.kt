package validator

import data.ConvenienceStore

object InputItemValidator {
    fun isValid(inputItem: String): Boolean {
        val items = inputItem.split(',').map { it }
        require(items.all { Regex("^\\[.+-\\d+]$").matches(it) }) { InputItemErrorMessage.INVALID_TYPE.msg }

        val detailedItems: List<List<String>> = items.map { it.removePrefix("[").removeSuffix("]").split('-') }
        require(detailedItems.all { it[1].toInt() != 0 }) { InputItemErrorMessage.WRONG_INPUT.msg}
        if (detailedItems.any { ConvenienceStore.getQuantity(it[NAME_IDX]) < it[QUANTITY_IDX].toInt() })
            throw IllegalArgumentException(InputItemErrorMessage.OVER_STOCK.msg)
        return true
    }

    private const val NAME_IDX = 0
    private const val QUANTITY_IDX = 1
}
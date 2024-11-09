package validator

import service.ConvenienceStoreService

object InputItemValidator {
    fun validate(inputItem: String) {
        val unparsedItems = inputItem.split(',').map { it }
        requireValidTypeInput(unparsedItems)

        val detailedItems: List<List<String>> = unparsedItems.map { it.removePrefix("[").removeSuffix("]").split('-') }
        requireValidInput(detailedItems)
        requireExistItem(detailedItems)
        requireValidStock(detailedItems)
    }

    private fun requireValidTypeInput(unparsedItems: List<String>) {
        require(unparsedItems.all { Regex("^\\[.+-\\d+]$").matches(it) }) { InputItemErrorMessage.INVALID_TYPE.msg }
    }

    private fun requireValidInput(detailedItems: List<List<String>>) {
        require(detailedItems.all { it[1].toInt() != 0 }) { InputItemErrorMessage.WRONG_INPUT.msg }
    }

    private fun requireExistItem(detailedItems: List<List<String>>) {
        val detailedItemsNames = detailedItems.map { it[NAME_IDX] }
        require(detailedItemsNames.all { it in ConvenienceStoreService.getItemNames() }) { InputItemErrorMessage.NOT_EXIST_ITEM.msg }
    }

    private fun requireValidStock(detailedItems: List<List<String>>) {
        if (detailedItems.any { ConvenienceStoreService.getQuantity(it[NAME_IDX]) < it[QUANTITY_IDX].toInt() })
            throw IllegalArgumentException(InputItemErrorMessage.OVER_STOCK.msg)
    }

    private const val NAME_IDX = 0
    private const val QUANTITY_IDX = 1
}
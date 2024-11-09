package controller

import data.ConvenienceStore
import validator.InputItemValidator
import view.InputView
import view.OutputView

class ConvenienceStoreController(
    private val inputView: InputView,
    private val outputView: OutputView,
) {
    private lateinit var buyItems: List<Pair<String, Int>>

    fun run() {
        outputView.showStock(ConvenienceStore.products)
        loopUntilValid { tryInputItem() }
    }

    private fun tryInputItem(): Boolean {
        try {
            val inputItem = inputView.inputItem()
            InputItemValidator.validate(inputItem)
            buyItems = inputItem.toBuyItemsList()
            return true
        } catch (e: IllegalArgumentException) {
            println(e.message)
            return false
        }
    }

    private fun loopUntilValid(action: () -> Boolean) {
        while (true) {
            if (action()) break
        }
    }

    companion object {
        private fun String.toBuyItemsList(): List<Pair<String, Int>> {
            val unparsedItems = this.replace("[", "").replace("]", "").split(',')
            val pairedItems = unparsedItems.map { it.split('-') }.map { it[NAME_IDX] to it[QUANTITY_IDX].toInt() }
            return pairedItems
        }

        private const val NAME_IDX = 0
        private const val QUANTITY_IDX = 1
    }
}
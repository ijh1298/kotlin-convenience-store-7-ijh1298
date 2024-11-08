package controller

import data.ConvenienceStore
import validator.InputItemValidator
import view.InputView
import view.OutputView

class ConvenienceStoreController(
    private val inputView: InputView,
    private val outputView: OutputView,
) {
    fun run() {
        outputView.showStock(ConvenienceStore.products)
        loopUntilValid { tryInputItem() }
    }

    private fun tryInputItem(): Boolean {
        try {
            val inputItem = inputView.inputItem()
            return InputItemValidator.isValid(inputItem)
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
}
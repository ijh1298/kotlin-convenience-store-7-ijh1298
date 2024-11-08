package controller

import data.ConvenienceStore
import view.InputView
import view.OutputView

class ConvenienceStoreController(
    private val inputView: InputView,
    private val outputView: OutputView,
) {
    fun run() {
        outputView.showStock(ConvenienceStore.products)
    }
}
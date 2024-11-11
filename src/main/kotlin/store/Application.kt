package store

import controller.ConvenienceStoreController
import view.InputView
import view.OutputView

fun main() {
    ConvenienceStoreController(InputView(), OutputView()).run()
}

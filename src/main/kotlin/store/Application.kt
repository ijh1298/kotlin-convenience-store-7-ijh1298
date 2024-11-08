package store

import controller.ConvenienceStoreController
import view.InputView
import view.OutputView

fun main() {
    // TODO: 프로그램 구현
    ConvenienceStoreController(InputView(), OutputView()).run()
}

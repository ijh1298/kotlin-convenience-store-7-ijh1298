package controller

import data.ConvenienceStore
import data.model.PurchaseInfo
import data.model.PurchaseResult
import data.model.Receipt
import data.model.StockChange
import service.ConvenienceStoreService
import util.PurchaseStatus
import validator.InputItemValidator
import view.InputView
import view.OutputView

class ConvenienceStoreController(
    private val inputView: InputView,
    private val outputView: OutputView,
) {
    private lateinit var buyItems: List<Pair<String, Int>>
    private lateinit var toBeUpdatedStocks: List<StockChange>
    private var receipts: List<Receipt> = emptyList()
    private var needPromotion: Boolean = false
    private var okWithoutPromo: Boolean = false
    private var tryAgain: Boolean = false

    fun run() {
        do {
            outputView.showStock(ConvenienceStore.products)
            loopUntilValid { tryInputItem() }
            buyItems()
            // TODO 멤버십 할인
            if (receipts.isNotEmpty()) outputView.showReceipts(receipts)
            loopUntilValid { tryInputTryAgain() }
        } while (tryAgain)
    }

    private fun buyItems() {
        toBeUpdatedStocks = getAllStockChanges()
        println(toBeUpdatedStocks)
        receipts = ConvenienceStoreService.getReceiptByBuyProcess(toBeUpdatedStocks)
    }

    private fun getAllStockChanges(): List<StockChange> {
        val purchaseInfos = mutableListOf<StockChange>()
        buyItems.forEach { (item, buyQuantity) ->
            val itemPurchaseInfo = getEachProductPurchaseInfos(item, buyQuantity)
            purchaseInfos += StockChange(item, itemPurchaseInfo)
        }
        return purchaseInfos.toList()
    }

    private fun getEachProductPurchaseInfos(itemName: String, buyQuantity: Int): List<PurchaseInfo> {
        val (promoQuantity, normalQuantity) = ConvenienceStoreService.separateBuyingQuantities(itemName, buyQuantity)
        val promoPurchaseInfo = getEachPurchasedPromoItem(itemName, promoQuantity)
        val normalPurchaseInfo = getEachPurchasedNormalItem(itemName, normalQuantity)

        if (promoPurchaseInfo == null && normalPurchaseInfo.product == null) return emptyList()
        if (promoPurchaseInfo == null || promoPurchaseInfo.buyQuantity == 0) return listOf(normalPurchaseInfo)
        if (promoPurchaseInfo.stopPurchase) return listOf(promoPurchaseInfo)
        return listOf(promoPurchaseInfo, normalPurchaseInfo)
    }

    private fun getEachPurchasedNormalItem(itemName: String, buyQuantity: Int): PurchaseInfo {
        val product = ConvenienceStoreService.getNormalProduct(itemName)
        return PurchaseInfo(product, buyQuantity, 0, false)
    }

    private fun getEachPurchasedPromoItem(itemName: String, buyQuantity: Int): PurchaseInfo? {
        val promoPurchaseResult = ConvenienceStoreService.processToGetPromoPurchaseResult(itemName, buyQuantity)

        return when (promoPurchaseResult.status) {
            PurchaseStatus.REQUEST_EXTRA_RESPONSE -> requestExtraGet(itemName, buyQuantity)
            PurchaseStatus.REQUEST_WITHOUT_PROMO -> requestWithoutPromo(itemName, buyQuantity)
            PurchaseStatus.SUCCESS_WITHOUT_PROMO -> ConvenienceStoreService.getPromoSuccessWithoutPromotionResult(itemName, buyQuantity)
            PurchaseStatus.SUCCESS -> ConvenienceStoreService.getPromoSuccessResult(itemName, buyQuantity)
            else -> null
        }
    }

    private fun requestExtraGet(itemName: String, buyQuantity: Int): PurchaseInfo {
        loopUntilValid { tryInputNeedPromotion() }
        return ConvenienceStoreService.getResultByExtraResponse(itemName, buyQuantity, needPromotion)
    }

    private fun requestWithoutPromo(itemName: String, buyQuantity: Int): PurchaseInfo {
        loopUntilValid { tryInputBuyWithoutPromotion() }
        val (promoQuantity, normalQuantity) = ConvenienceStoreService.separateBuyingQuantities(itemName, buyQuantity)
        return ConvenienceStoreService.getResultByPromoProcess(itemName, promoQuantity, okWithoutPromo)
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

    private fun tryInputNeedPromotion(): Boolean {
        try {
            val inputNeedPromotion = inputView.inputNeedPromotion()
            require(inputNeedPromotion == "Y" || inputNeedPromotion == "N") { "[ERROR] 잘못된 입력입니다. 다시 입력해 주세요." }
            needPromotion = inputNeedPromotion.answerToBoolean()
            return true
        } catch (e: IllegalArgumentException) {
            println(e.message)
            return false
        }
    }

    private fun tryInputBuyWithoutPromotion(): Boolean {
        try {
            val inputBuyWithoutPromotion = inputView.inputBuyWithoutPromotion()
            require(inputBuyWithoutPromotion == "Y" || inputBuyWithoutPromotion == "N") { "[ERROR] 잘못된 입력입니다. 다시 입력해 주세요." }
            okWithoutPromo = inputBuyWithoutPromotion.answerToBoolean()
            return true
        } catch (e: IllegalArgumentException) {
            println(e.message)
            return false
        }
    }

    private fun tryInputTryAgain(): Boolean {
        try {
            val inputTryAgain = inputView.inputTryAgain()
            require(inputTryAgain == "Y" || inputTryAgain == "N") { "[ERROR] 잘못된 입력입니다. 다시 입력해 주세요." }
            tryAgain = inputTryAgain.answerToBoolean()
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

        private fun String.answerToBoolean(): Boolean {
            if (this == "Y")
                return true
            return false
        }

        private const val NAME_IDX = 0
        private const val QUANTITY_IDX = 1
    }
}
package controller

import store.ConvenienceStore
import data.model.PurchaseInfo
import data.model.Receipt
import data.model.StockChange
import service.ConvenienceStoreService
import service.ConvenienceStoreService.getPromotionByProductName
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
    private var useMembership: Boolean = false
    private var tryAgain: Boolean = false

    fun run() {
        ConvenienceStore.init()
        do {
            outputView.showStock(ConvenienceStore.products)
            loopUntilValid { tryInputItem() }
            buyItems()
            loopUntilValid { tryInputUseMembership() }
            if (receipts.isNotEmpty()) outputView.showReceipts(receipts, useMembership)
            loopUntilValid { tryInputTryAgain() }
        } while (tryAgain)
    }

    private fun buyItems() {
        toBeUpdatedStocks = getAllStockChanges()
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
        val promoPurchaseInfo = getEachPurchasedPromoItem(itemName, promoQuantity, normalQuantity)
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

    private fun getEachPurchasedPromoItem(itemName: String, promoQuantity: Int, normalQuantity: Int): PurchaseInfo? {
        val promoPurchaseResult = ConvenienceStoreService.processToGetPromoPurchaseResult(itemName, promoQuantity)

        return when (promoPurchaseResult.status) {
            PurchaseStatus.REQUEST_EXTRA_RESPONSE -> requestExtraGet(itemName, promoQuantity)
            PurchaseStatus.REQUEST_WITHOUT_PROMO -> requestWithoutPromo(itemName, promoQuantity, normalQuantity)
            PurchaseStatus.SUCCESS_WITHOUT_PROMO -> ConvenienceStoreService.getPromoSuccessWithoutPromotionResult(itemName, promoQuantity)
            PurchaseStatus.SUCCESS -> ConvenienceStoreService.getPromoSuccessResult(itemName, promoQuantity)
            else -> null
        }
    }

    private fun requestExtraGet(itemName: String, buyQuantity: Int): PurchaseInfo {
        loopUntilValid { tryInputNeedPromotion(itemName) }
        return ConvenienceStoreService.getResultByExtraResponse(itemName, buyQuantity, needPromotion)
    }

    private fun requestWithoutPromo(itemName: String, promoQuantity: Int, normalQuantity: Int): PurchaseInfo {
        val promo = getPromotionByProductName(itemName)
        var buyAndGet = 0
        if (promo != null) buyAndGet = promo.buy + promo.get
        loopUntilValid { tryInputBuyWithoutPromotion(itemName, (promoQuantity % buyAndGet) + normalQuantity) }
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

    private fun tryInputNeedPromotion(itemName: String): Boolean {
        try {
            val inputNeedPromotion = inputView.inputNeedPromotion(itemName)
            require(inputNeedPromotion == "Y" || inputNeedPromotion == "N") { WRONG_INPUT_MSG }
            needPromotion = inputNeedPromotion.answerToBoolean()
            return true
        } catch (e: IllegalArgumentException) {
            println(e.message)
            return false
        }
    }

    private fun tryInputBuyWithoutPromotion(itemName: String, noPromoQuantity: Int): Boolean {
        try {
            val inputBuyWithoutPromotion = inputView.inputBuyWithoutPromotion(itemName, noPromoQuantity)
            require(inputBuyWithoutPromotion == "Y" || inputBuyWithoutPromotion == "N") { WRONG_INPUT_MSG }
            okWithoutPromo = inputBuyWithoutPromotion.answerToBoolean()
            return true
        } catch (e: IllegalArgumentException) {
            println(e.message)
            return false
        }
    }

    private fun tryInputUseMembership(): Boolean {
        try {
            val inputUseMembership = inputView.inputUseMembership()
            require(inputUseMembership == "Y" || inputUseMembership == "N") { WRONG_INPUT_MSG }
            useMembership = inputUseMembership.answerToBoolean()
            return true
        } catch (e: IllegalArgumentException) {
            println(e.message)
            return false
        }
    }

    private fun tryInputTryAgain(): Boolean {
        try {
            val inputTryAgain = inputView.inputTryAgain()
            require(inputTryAgain == "Y" || inputTryAgain == "N") { WRONG_INPUT_MSG }
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

        private fun String.answerToBoolean(): Boolean = this == "Y"

        private const val WRONG_INPUT_MSG = "[ERROR] 잘못된 입력입니다. 다시 입력해 주세요."
        private const val NAME_IDX = 0
        private const val QUANTITY_IDX = 1
    }
}
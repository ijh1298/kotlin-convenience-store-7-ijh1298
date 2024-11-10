package controller

import data.ConvenienceStore
import data.model.PurchaseInfo
import data.model.PurchaseResult
import data.model.StockChange
import service.ConvenienceStoreService
import util.PurchaseStatus
import validator.InputItemValidator
import view.InputView
import view.OutputView
import java.security.Provider.Service

class ConvenienceStoreController(
    private val inputView: InputView,
    private val outputView: OutputView,
) {
    private lateinit var buyItems: List<Pair<String, Int>>
    private lateinit var toBeUpdatedStocks: List<StockChange>
    private var needPromotion: Boolean = false
    private var okWithoutPromo: Boolean = false

    fun run() {
        outputView.showStock(ConvenienceStore.products)
        loopUntilValid { tryInputItem() }
        buyItems()
    }

    fun buyItems() {
        toBeUpdatedStocks = getAllStockChanges()
        println(toBeUpdatedStocks)
        // Service.updateStocks(toBeUpdatedStocks)
        // Service.가격 반영
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

        if (promoPurchaseInfo.buyQuantity == 0) return listOf(normalPurchaseInfo) // promo 상품이 null인 경우
        if (promoPurchaseInfo.stopPurchase) return listOf(promoPurchaseInfo)
        return listOf(promoPurchaseInfo, normalPurchaseInfo)
    }

    private fun getEachPurchasedNormalItem(itemName: String, buyQuantity: Int): PurchaseInfo =
        PurchaseInfo(buyQuantity, 0, false)

    private fun getEachPurchasedPromoItem(itemName: String, buyQuantity: Int): PurchaseInfo {
        val promoPurchaseResult = ConvenienceStoreService.processToGetPromoPurchaseResult(itemName, buyQuantity)

        return when (promoPurchaseResult.status) {
            PurchaseStatus.REQUEST_EXTRA_RESPONSE -> requestExtraGet(itemName, buyQuantity)
            PurchaseStatus.REQUEST_WITHOUT_PROMO -> requestWithoutPromo(itemName, buyQuantity)
            PurchaseStatus.SUCCESS_WITHOUT_PROMO -> PurchaseInfo(buyQuantity, 0, false) // 일반적인 구매
            PurchaseStatus.SUCCESS -> ConvenienceStoreService.getPromoSuccessResult(itemName, buyQuantity)
            else -> PurchaseInfo(0, 0, false)
        }
    }

    private fun requestExtraGet(itemName: String, buyQuantity: Int): PurchaseInfo {
        loopUntilValid { tryInputNeedPromotion() }
        return ConvenienceStoreService.getResultByExtraResponse(itemName, buyQuantity, needPromotion)
    }

    private fun requestWithoutPromo(itemName: String, buyQuantity: Int): PurchaseInfo {
        loopUntilValid { tryInputBuyWithoutPromotion() }
        val (promoQuantity, normalQuantity) = ConvenienceStoreService.separateBuyingQuantities(itemName, buyQuantity)
        return ConvenienceStoreService.getResultByPromoProcess(itemName, promoQuantity, normalQuantity, okWithoutPromo)
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
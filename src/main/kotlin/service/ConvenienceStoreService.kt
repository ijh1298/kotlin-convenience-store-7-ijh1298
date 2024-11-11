package service

import camp.nextstep.edu.missionutils.DateTimes
import store.ConvenienceStore.getReceipt
import store.ConvenienceStore.products
import store.ConvenienceStore.promotions
import store.ConvenienceStore.updateStock
import data.model.*
import util.PurchaseStatus

object ConvenienceStoreService {
    // TODO 멤버십 할인 적용

    fun getReceiptByBuyProcess(stockChanges: List<StockChange>): List<Receipt> {
        val receipts = mutableListOf<Receipt>()
        stockChanges.forEach {
            updateStock(it)
            receipts += getReceipt(it)
        }
        return receipts
    }

    fun processToGetPromoPurchaseResult(itemName: String, promoBuyQuantity: Int): PurchaseResult {
        val promoProduct = getPromoProduct(itemName)
        if (promoProduct == null)
            return PurchaseResult(PurchaseStatus.FAILURE_NULL_PRODUCT, promoProduct)
        return getPromoPurchaseResult(promoProduct, promoBuyQuantity)
    }

    fun getPromoSuccessWithoutPromotionResult(itemName: String, buyQuantity: Int): PurchaseInfo {
        val product = getPromoProduct(itemName)!!
        return PurchaseInfo(product, buyQuantity, 0, false)
    }

    fun getPromoSuccessResult(itemName: String, buyQuantity: Int): PurchaseInfo {
        val product = getPromoProduct(itemName)!!
        val (_, nBuy, nGet, _, _) = getPromotion(itemName)!!
        val freeQuantity = buyQuantity / (nBuy + nGet)
        return PurchaseInfo(product, buyQuantity - freeQuantity, freeQuantity, false)
    }

    fun getResultByPromoProcess(
        itemName: String,
        promoQuantity: Int,
        response: Boolean,
    ): PurchaseInfo {
        val product = getPromoProduct(itemName)!!
        val (_, nBuy, nGet, _, _) = getPromotion(itemName)!!
        val freeQuantity = promoQuantity / (nBuy + nGet)
        val payQuantity = freeQuantity * nBuy

        if (response)
            return PurchaseInfo(product, promoQuantity - freeQuantity, freeQuantity, false)
        return PurchaseInfo(product, payQuantity, freeQuantity, true) // 프로모션 적용 품목만 구매, 최초 함수 돌아가면 중단
    }

    fun getResultByExtraResponse(itemName: String, buyQuantity: Int, response: Boolean): PurchaseInfo {
        val product = getPromoProduct(itemName)!!
        val (_, nBuy, nGet, _, _) = getPromotion(itemName)!!
        val freeQuantity = buyQuantity / (nBuy + nGet)
        val payQuantity = freeQuantity * nBuy

        if (response)
            return PurchaseInfo(product, buyQuantity - freeQuantity, freeQuantity + nGet, false)
        return PurchaseInfo(product, buyQuantity - freeQuantity, freeQuantity, false)
    }

    private fun getPromoPurchaseResult(product: Product, promoQuantity: Int): PurchaseResult {
        if (!product.promotion!!.isDateValid(DateTimes.now().toLocalDate())) // 프로모션 기간이 아니면 일반 구매
            return PurchaseResult(PurchaseStatus.SUCCESS_WITHOUT_PROMO, product)
        return getValidPromoPurchaseResult(product, promoQuantity)
    }

    private fun getValidPromoPurchaseResult(product: Product, promoQuantity: Int): PurchaseResult {
        val (_, _, stock, promo) = product
        val (_, nBuy, nGet, _, _) = promo!!

        if (canGetExtra(promoQuantity, stock, nBuy, nGet))
            return PurchaseResult(PurchaseStatus.REQUEST_EXTRA_RESPONSE, product)
        if (needPromotionProcess(promoQuantity, stock, nBuy, nGet))
            return PurchaseResult(PurchaseStatus.REQUEST_WITHOUT_PROMO, product)
        return PurchaseResult(PurchaseStatus.SUCCESS, product)
    }

    private fun getPromotion(itemName: String): Promotion? {
        val product = getPromoProduct(itemName) ?: return null
        return product.promotion
    }

    private fun canGetExtra(buyQuantity: Int, stock: Int, nBuy: Int, nGet: Int): Boolean =
        buyQuantity % (nBuy + nGet) == nBuy && stock >= buyQuantity + nGet

    private fun needPromotionProcess(buyQuantity: Int, stock: Int, nBuy: Int, nGet: Int): Boolean =
        stock == buyQuantity && buyQuantity % (nBuy + nGet) != 0 // 지급할 프로모션 재고가 없는 경우

    fun getItemNames(): List<String> = products.map { it.name }

    fun getPromotionNames(): List<String> = promotions.map { it.name }

    fun getQuantity(productName: String): Int = products.filter { it.name == productName }.sumOf { it.quantity }

    fun getPromotionByName(promotionName: String): Promotion? = promotions.find { it.name == promotionName }

    fun getPromotionByProductName(productName: String): Promotion? = products.find { it.name == productName && it.promotion != null }?.promotion

    // **Validator에서 buyQuantity만큼 구매가 가능함을 이미 확인한 상태임.**
    fun separateBuyingQuantities(productName: String, buyQuantity: Int): Pair<Int, Int> {
        val stock = getPromoProduct(productName) to getNormalProduct(productName)

        if (stock.first == null) return 0 to buyQuantity
        if (stock.second == null) return buyQuantity to 0

        val promoQuantity = minOf(buyQuantity, stock.first!!.quantity)
        return promoQuantity to buyQuantity - promoQuantity
    }

    private fun getPromoProduct(productName: String) = products.find { it.name == productName && it.promotion != null }

    fun getNormalProduct(productName: String) = products.find { it.name == productName && it.promotion == null }
}
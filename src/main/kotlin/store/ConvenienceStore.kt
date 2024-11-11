package store

import data.model.*
import data.repository.ProductRepository
import data.repository.PromotionRepository

object ConvenienceStore {
    private val productRepository = ProductRepository()
    private val promotionRepository = PromotionRepository()

    var promotions = promotionRepository.fetchPromotions().toMutableList()
    var products = productRepository.fetchProducts().toMutableList()

    fun init() {
        promotions = promotionRepository.fetchPromotions().toMutableList()
        products = productRepository.fetchProducts().toMutableList()
    }

    fun getReceipt(stockChange: StockChange): Receipt {
        val bills = mutableListOf<Bill>()
        stockChange.purchaseInfos.forEach { bills += infoToBill(it) }
        return createReceipt(bills, stockChange)
    }

    fun updateStock(stockChange: StockChange) {
        val (itemName, purchaseInfos) = stockChange
        var sellAmount = purchaseInfos.sumOf { it.buyQuantity + it.getQuantity }
        val promoStock = products.find { it.name == itemName && it.promotion != null }
        val normalStock = products.find { it.name == itemName && it.promotion == null }

        sellAmount = updatePromoStockGetRemainder(promoStock, sellAmount)
        updateNormalStock(normalStock, sellAmount)
    }

    // (정가로 산 개수, 프로모 적용가로 산 개수) 구분하는 함수
    private fun separateRegularPromoAmount(stockChange: StockChange): Pair<Int, Int> {
        val (itemName, purchaseInfos) = stockChange
        val promotion = products.find { it.name == itemName && it.promotion != null }?.promotion

        val totalBuyAmount = purchaseInfos.sumOf { it.buyQuantity + it.getQuantity }
        if (promotion == null)
            return totalBuyAmount to 0
        val getAmount = purchaseInfos.sumOf { it.getQuantity }
        val promoAmount = getAmount + getAmount * promotion.buy // 프로모 적용가로 산 개수
        return totalBuyAmount - promoAmount to promoAmount
    }

    private fun createReceipt(bills: List<Bill>, change: StockChange): Receipt {
        val buyNum = bills.sumOf { it.buyQuantity }
        val getNum = bills.sumOf { it.getQuantity }
        val discount = bills.sumOf { it.price * it.getQuantity }
        val (regularBuyAmount, promoBuyAmount) = separateRegularPromoAmount(change)
        val price = bills.first { it.price != 0 }.price

        return Receipt(change.item, buyNum, getNum, discount, price, regularBuyAmount * price, promoBuyAmount * price)
    }

    private fun infoToBill(info: PurchaseInfo): Bill {
        val isPromo = info.product?.promotion != null
        return Bill(isPromo, info.buyQuantity, info.getQuantity, info.product?.price ?: 0)
    }

    private fun updatePromoStockGetRemainder(promo: Product?, sellAmount: Int): Int {
        if (promo == null || promo.quantity == 0) return sellAmount

        val sellRemainder = maxOf(sellAmount - promo.quantity, 0)
        promo.quantity = maxOf(promo.quantity - sellAmount, 0)

        return sellRemainder
    }

    private fun updateNormalStock(normal: Product?, sellAmount: Int) {
        if (normal == null || normal.quantity == 0) return
        normal.quantity = maxOf(normal.quantity - sellAmount, 0)
    }
}

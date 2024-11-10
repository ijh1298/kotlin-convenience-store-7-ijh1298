package data

import data.model.Product
import data.model.Promotion
import data.model.StockChange
import data.repository.ProductRepository
import data.repository.PromotionRepository

object ConvenienceStore {
    private val productRepository = ProductRepository()
    private val promotionRepository = PromotionRepository()

    val promotions = promotionRepository.fetchPromotions().toMutableList()
    val products = productRepository.fetchProducts().toMutableList()

    fun updateStock(stockChange: StockChange) {
        val (itemName, purchaseInfos) = stockChange
        var sellAmount = purchaseInfos.map { it.buyQuantity + it.getQuantity }.sum()
        val promoStock = products.find { it.name == itemName && it.promotion != null }
        val normalStock = products.find { it.name == itemName && it.promotion == null }

        val remainder = updatePromoStockGetRemainder(promoStock, sellAmount)
        updateNormalStock(normalStock, remainder)
    }

    private fun updatePromoStockGetRemainder(promo: Product?, sellAmount: Int): Int {
        if (promo == null || promo.quantity == 0)
            return sellAmount
        promo.quantity = maxOf(promo.quantity - sellAmount, 0)
        return maxOf(sellAmount - promo.quantity, 0)
    }

    private fun updateNormalStock(normal: Product?, sellAmount: Int) {
        if (normal == null || normal.quantity == 0)
            return
        normal.quantity = maxOf(normal.quantity - sellAmount, 0)
        return
    }
}
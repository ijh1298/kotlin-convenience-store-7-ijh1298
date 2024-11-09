package service

import data.ConvenienceStore.products
import data.ConvenienceStore.promotions
import data.model.Promotion

object ConvenienceStoreService {
    private fun checkBuyItems(buyItems: List<Pair<String, Int>>) {
        buyItems.forEach { item ->

        }
    }

    fun getItemNames(): List<String> = products.map { it.name }

    fun getPromotionNames(): List<String> = promotions.map { it.name }

    fun getQuantity(productName: String): Int {
        return products.filter { it.name == productName }.sumOf { it.quantity }
    }

    fun getPromotionByName(promotionName: String): Promotion? {
        return promotions.find { it.name == promotionName }
    }
}
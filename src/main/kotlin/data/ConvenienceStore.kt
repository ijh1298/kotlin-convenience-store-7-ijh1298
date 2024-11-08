package data

import data.repository.ProductRepository
import data.repository.PromotionRepository

object ConvenienceStore {
    private val productRepository = ProductRepository()
    private val promotionRepository = PromotionRepository()

    val products = productRepository.fetchProducts().toMutableList()
    val promotions = promotionRepository.fetchPromotions().toMutableList()

    fun getItemNames(): List<String> = products.map { it.name }

    fun getQuantity(productName: String): Int {
        return products.filter { it.name == productName }.sumOf { it.quantity }
    }
}
package data

import data.model.Promotion
import data.repository.ProductRepository
import data.repository.PromotionRepository

object ConvenienceStore {
    private val productRepository = ProductRepository()
    private val promotionRepository = PromotionRepository()

    val promotions = promotionRepository.fetchPromotions().toMutableList()
    val products = productRepository.fetchProducts().toMutableList()
}
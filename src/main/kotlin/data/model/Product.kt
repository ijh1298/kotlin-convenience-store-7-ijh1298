package data.model

import service.ConvenienceStoreService
import validator.ProductValidator

data class Product(
    val name: String,
    val price: Int,
    var quantity: Int,
    val promotion: Promotion?,
) {
    companion object {
        fun List<String>.toProduct(): Product? {
            if (!ProductValidator.isValid(this))
                return null
            return Product(
                this[0],
                this[1].toInt(),
                this[2].toInt(),
                ConvenienceStoreService.getPromotionByName(this[3])
            )
        }
    }
}
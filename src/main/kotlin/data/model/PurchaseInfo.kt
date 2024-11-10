package data.model

data class PurchaseInfo(
    val product: Product?,
    val buyQuantity: Int,
    val getQuantity: Int,
    val stopPurchase: Boolean
)

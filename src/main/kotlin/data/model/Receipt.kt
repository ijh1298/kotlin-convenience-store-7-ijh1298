package data.model

data class Receipt(
    val itemName: String,
    val buyQuantity: Int,
    val getQuantity: Int,
    val discount: Int,
    val price: Int,
    val regularCost: Int,
    val promoCost: Int
)

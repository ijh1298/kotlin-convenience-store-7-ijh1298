package data.model

data class StockChange(
    val item: String,
    val purchaseInfos: List<PurchaseInfo>,
)
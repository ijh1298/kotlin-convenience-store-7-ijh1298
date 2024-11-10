package data.model

import util.PurchaseStatus

data class PurchaseResult(
    val status: PurchaseStatus,
    val product: Product?,
)


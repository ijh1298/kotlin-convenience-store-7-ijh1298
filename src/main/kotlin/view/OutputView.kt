package view

import data.model.Product
import data.model.Receipt
import java.text.DecimalFormat

class OutputView {
    fun showStock(products: List<Product>) {
        println(WELCOME_MSG)
        products.forEach { println(productDetail(it)) }
        println()
    }

    fun showReceipts(receipts: List<Receipt>) {
        showReceiptUpper(receipts)
        showReceiptMiddle(receipts)
        showReceiptDowner(receipts)
    }

    private fun showReceiptUpper(receipts: List<Receipt>) {
        println(RECEIPT_TITLE)
        receipts.forEach {
            val totalNumber = it.buyQuantity + it.getQuantity
            print(String.format(RECEIPT_BUY_FORMAT, it.itemName, totalNumber))
            println(format.format(totalNumber * it.price))
        }
    }

    private fun showReceiptMiddle(receipts: List<Receipt>) {
        println(RECEIPT_FREE_TITLE)
        receipts.filter { it.getQuantity != 0 }.forEach {
            println(String.format(RECEIPT_BUY_FORMAT, it.itemName, it.getQuantity))
        }
    }

    private fun showReceiptDowner(receipts: List<Receipt>) {
        val totalQuantity = receipts.sumOf { it.buyQuantity + it.getQuantity }
        val totalCost = receipts.sumOf { (it.buyQuantity + it.getQuantity) * it.price }
        val totalDiscount = receipts.sumOf { it.discount }
        println(RECEIPT_DOWN_TITLE)
        println(RECEIPT_TOTAL_COST.format(totalQuantity) + format.format(totalCost))
        println(RECEIPT_DISCOUNT + format.format(totalDiscount))
        // TODO 멤버십 할인
        println(RECEIPT_COST + format.format(totalCost - totalDiscount))
    }

    private fun productDetail(product: Product): String {
        val nameAndPrice = String.format(ITEM_FORMAT, product.name, format.format(product.price))
        val namePriceAndStock = msgAttachNum(nameAndPrice, product.quantity)
        if (product.promotion != null)
            return "$namePriceAndStock ${product.promotion.name}"
        return namePriceAndStock
    }

    private fun msgAttachNum(msg: String, quantity: Int): String {
        if (quantity == 0)
            return "$msg $NO_STOCK"
        return "$msg ${format.format(quantity)}개"
    }

    companion object {
        private val format = DecimalFormat("#,###")

        const val WELCOME_MSG = "안녕하세요. W편의점입니다.\n현재 보유하고 있는 상품입니다.\n"
        const val ITEM_FORMAT = "- %s %s원"
        const val NO_STOCK = "재고 없음"

        const val RECEIPT_TITLE = "==============W 편의점================\n상품명\t\t수량\t\t금액"
        const val RECEIPT_BUY_FORMAT = "%s\t\t%d\t\t"
        const val RECEIPT_FREE_TITLE = "=============증\t정==============="
        const val RECEIPT_DOWN_TITLE = "===================================="
        const val RECEIPT_TOTAL_COST = "총구매액\t\t%d\t\t"
        const val RECEIPT_DISCOUNT = "행사할인\t\t\t\t-"
        const val RECEIPT_COST = "내실돈\t\t\t\t "
    }
}
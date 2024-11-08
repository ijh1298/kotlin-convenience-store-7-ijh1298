package view

import data.model.Product
import java.text.DecimalFormat

class OutputView {
    fun showStock(products: List<Product>) {
        println(WELCOME_MSG)
        products.forEach { println(productDetail(it)) }
    }

    private fun productDetail(product: Product): String {
        val nameAndPrice = String.format(ITEM_FORMAT, product.name, format.format(product.price))
        val namePriceAndStock = msgAttachNum(nameAndPrice, product.quantity)
        if (product.promotion != "null")
            return "$namePriceAndStock ${product.promotion}"
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
    }
}
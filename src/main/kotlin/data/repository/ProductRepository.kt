package data.repository

import data.model.Product
import data.model.Product.Companion.toProduct
import java.io.BufferedReader
import java.io.InputStreamReader

class ProductRepository {
    private val productFile = readFile()

    fun fetchProducts() = makeProducts(productFile)

    private fun readFile(): List<String> {
        val inputStream = this::class.java.classLoader.getResourceAsStream("products.md")
            ?: throw IllegalArgumentException("[ERROR] 'products.md' 파일을 찾을 수 없습니다.")
        return BufferedReader(InputStreamReader(inputStream)).use { it.readLines() }
    }

    private fun makeProduct(string: String): Product? {
        val productDetail = string.split(',').map { it.trim() }
        return productDetail.toProduct()
    }

    private fun makeProducts(productFile: List<String>): List<Product> {
        val products = productFile.drop(1).mapNotNull { makeProduct(it) }
        return addEmptyStockForPromoOnlyProducts(products)
    }

    private fun addEmptyStockForPromoOnlyProducts(products: List<Product>): List<Product> {
        val productsByName = products.groupBy { it.name }
        return productsByName.values.flatMap { processProductGroup(it) }
    }

    private fun processProductGroup(productList: List<Product>): List<Product> {
        return if (isPromoOnlyProduct(productList)) {
            createPromoAndEmptyStock(productList[0])
        } else {
            sortProductsByPromotion(productList)
        }
    }

    private fun isPromoOnlyProduct(productList: List<Product>): Boolean {
        return productList.size == 1 && productList[0].promotion != null
    }

    private fun createPromoAndEmptyStock(product: Product): List<Product> {
        val emptyStock = product.copy(promotion = null, quantity = 0)
        return listOf(product, emptyStock)
    }

    private fun sortProductsByPromotion(productList: List<Product>): List<Product> {
        return productList.sortedByDescending { it.promotion != null }
    }
}

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
        val productDetail = string.split(',').map { it }
        return productDetail.toProduct()
    }

    private fun makeProducts(productFile: List<String>): List<Product> {
        return productFile.mapNotNull { makeProduct(it) }
    }
}

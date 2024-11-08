package data.repository

import data.model.Product
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
        return try {
            productDetail.toProduct()
        } catch (e: IllegalArgumentException) {
            null
        }
    }

    private fun makeProducts(productFile: List<String>): List<Product> {
        return productFile.mapNotNull { makeProduct(it) }
    }

    companion object {
        private fun List<String>.toProduct(): Product {
            val productName = this.first()
            require(this.size == 4) { "[ERROR] $productName Product 문자열이 유효하지 않습니다." }
            require(this[1].all { it.isDigit() }) { "[ERROR] $productName Product 가격이 유효한 정수가 아닙니다." }
            require(this[2].all { it.isDigit() }) { "[ERROR] $productName Product 재고가 유효한 정수가 아닙니다." }
            return Product(this[0], this[1].toInt(), this[2].toInt(), this[3])
        }
    }
}

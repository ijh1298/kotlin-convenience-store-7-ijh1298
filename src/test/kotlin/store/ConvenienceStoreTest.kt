package store

import data.model.PurchaseInfo
import data.model.StockChange
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.params.ParameterizedTest
import org.junit.jupiter.params.provider.CsvSource
import service.ConvenienceStoreService as service

class ConvenienceStoreTest {
    private val testPromoProduct = service.getPromoProduct("콜라")
    private val testNormalProduct = service.getNormalProduct("콜라")

    @BeforeEach
    fun `상품 재고 초기화`() {
        ConvenienceStore.init()
    }

    @ParameterizedTest
    @CsvSource(value = ["2,1,7", "4,2,4"])
    @DisplayName("n개를 사고, n개를 증정받은 후 남은 재고를 확인하는 테스트")
    fun `프로모션 재고 업데이트 확인`(nBuy: Int, nGet: Int, leftStock: Int) {
        val testStockChange = StockChange("콜라", listOf(PurchaseInfo(testPromoProduct, nBuy, nGet, false)))
        ConvenienceStore.updateStock(testStockChange)
        assertThat(ConvenienceStore.products.find { it.name == "콜라" && it.promotion != null }!!.quantity)
            .isEqualTo(leftStock)
    }

    @ParameterizedTest
    @CsvSource(value = ["5,5", "10, 0"])
    @DisplayName("n개를 산 후 남은 재고를 확인하는 테스트")
    fun `일반 재고 업데이트 확인`(nBuy: Int, leftStock: Int) {
        val testStockChange = StockChange("콜라", listOf(PurchaseInfo(testNormalProduct, nBuy, 0, false)))
        // 프로모션 재고 우선 차감하기 때문에, 프로모션 재고 0으로 만들고 테스트
        ConvenienceStore.products.find { it.name == "콜라" && it.promotion != null }?.quantity = 0

        ConvenienceStore.updateStock(testStockChange)
        assertThat(ConvenienceStore.products.find { it.name == "콜라" && it.promotion == null }!!.quantity)
            .isEqualTo(leftStock)
    }

    @ParameterizedTest
    @CsvSource(value = ["10,3,2,5"])
    @DisplayName("프로모션, 일반 재고를 동시에 구매하는 경우 테스트")
    fun `재고 동시 업데이트 확인`(promoBuy: Int, promoGet: Int, normalBuy: Int, leftPromoAndNormalStock: Int) {
        val testStockChange = StockChange(
            "콜라",
            listOf(
                PurchaseInfo(testPromoProduct, promoBuy, promoGet, false),
                PurchaseInfo(testNormalProduct, normalBuy, 0, false),
            )
        )
        ConvenienceStore.updateStock(testStockChange)
        assertThat(ConvenienceStore.products.filter { it.name == "콜라" }.sumOf { it.quantity })
            .isEqualTo(leftPromoAndNormalStock)
    }
}
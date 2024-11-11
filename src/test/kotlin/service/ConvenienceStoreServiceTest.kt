package service

import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.params.ParameterizedTest
import org.junit.jupiter.params.provider.CsvSource
import store.ConvenienceStore
import util.PurchaseStatus

class ConvenienceStoreServiceTest {
    @BeforeEach
    fun `상품 초기화`() {
        ConvenienceStore.init()
    }

    @ParameterizedTest
    @CsvSource(value = ["콜라,3"])
    @DisplayName("프로모션 상품이 추가 절차없이 바로 사질 때")
    fun `결제 상태 성공 확인`(itemName: String, promoBuyQuantity: Int) {
        val purchaseInfo = ConvenienceStoreService.processToGetPromoPurchaseResult(itemName, promoBuyQuantity)
        assertThat(purchaseInfo.status).isEqualTo(PurchaseStatus.SUCCESS)
    }

    @ParameterizedTest
    @CsvSource(value = ["콜라,5"])
    @DisplayName("프로모션 상품을 하나 더 받을 수 있을 때")
    fun `결제 상태 추가 상품 받을 건지 확인`(itemName: String, promoBuyQuantity: Int) {
        val purchaseInfo = ConvenienceStoreService.processToGetPromoPurchaseResult(itemName, promoBuyQuantity)
        assertThat(purchaseInfo.status).isEqualTo(PurchaseStatus.REQUEST_EXTRA_RESPONSE)
    }

    @ParameterizedTest
    @CsvSource(value = ["탄산수,5"])
    @DisplayName("프로모션 상품 재고 부족으로 정가로 사야할 때")
    fun `결제 상태 재고 부족인데 정가로 살지 확인`(itemName: String, promoBuyQuantity: Int) {
        val purchaseInfo = ConvenienceStoreService.processToGetPromoPurchaseResult(itemName, promoBuyQuantity)
        assertThat(purchaseInfo.status).isEqualTo(PurchaseStatus.REQUEST_WITHOUT_PROMO)
    }
}
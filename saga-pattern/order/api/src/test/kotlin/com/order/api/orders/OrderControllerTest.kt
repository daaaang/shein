package com.order.api.orders

import com.google.gson.Gson
import com.ninjasquad.springmockk.MockkBean
import com.order.adapter.entity.OrderEntity
import com.order.adapter.entity.OrderEntityRepository
import com.order.adapter.entity.OrderProductEntity
import com.order.adapter.entity.OrderProductEntityRepository
import com.order.domain.model.Order
import com.order.domain.model.OrderProduct
import com.order.domain.model.StepStatus
import com.order.domain.usecase.OrderUseCase
import io.kotest.core.extensions.Extension
import io.kotest.core.spec.IsolationMode
import io.kotest.core.spec.style.BehaviorSpec
import io.kotest.core.test.TestCaseOrder
import io.kotest.extensions.spring.SpringExtension
import io.kotest.matchers.shouldBe
import io.mockk.every
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.http.MediaType
import org.springframework.test.context.ActiveProfiles
import org.springframework.test.web.servlet.MockMvc
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders

@AutoConfigureMockMvc
@ActiveProfiles("test")
@SpringBootTest
class OrderControllerTest(
    orderProductEntityRepository: OrderProductEntityRepository,
    orderEntityRepository: OrderEntityRepository,
    private val mockMvc: MockMvc,
) : BehaviorSpec() {

    override fun extensions(): List<Extension> = listOf(SpringExtension)
    override fun isolationMode() = IsolationMode.InstancePerTest
    override fun testCaseOrder() = TestCaseOrder.Sequential

    @MockkBean
    private lateinit var orderUseCase: OrderUseCase

    init {

        this.given("주문 요청에 대한 기본 정보가 주어져요") {

            val orderId = 1L
            val userId = 100L
            val txId = "12345678"
            val order = Order(
                userId = userId,
                stepStatus = StepStatus.PENDING,
                txId = txId,
                id = orderId,
            )

            val orderProducts = listOf(
                OrderProduct(
                    id = 1L,
                    orderId = 1L,
                    productId = 1L,
                    amount = 100L
                ),
                OrderProduct(
                    id = 2L,
                    orderId = 1L,
                    productId = 2L,
                    amount = 120L
                ),
            )

            this.beforeTest {
                orderEntityRepository.saveAndFlush(OrderEntity.fromDomainModel(order))
                orderProductEntityRepository.saveAllAndFlush(orderProducts.map { OrderProductEntity.fromDomainModel(it) })
            }

            `when`("유저가 /order/create 주문 요청을 수행해요") {
                every { orderUseCase.createOrder(any()) } returns order

                val resultBuilder = MockMvcRequestBuilders.post("$BASE_URL/create")
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(
                        Gson().toJson(
                            OrderController.OrderApiRequest(
                                userId = userId,
                                productItems = listOf(
                                    OrderController.ProductItemApiRequest(
                                        productId = orderProducts[0].id,
                                        amount = orderProducts[0].amount,
                                    ),
                                    OrderController.ProductItemApiRequest(
                                        productId = orderProducts[1].id,
                                        amount = orderProducts[1].amount,
                                    ),
                                )
                            )
                        )
                    )

                then("수행된 결과는 create에요") {
                    mockMvc.perform(resultBuilder)
                        .andExpect { it.response.status shouldBe 201 }
                }
            }
        }
    }

    companion object {
        private const val BASE_URL = "http://localhost:8080/order"
    }
}

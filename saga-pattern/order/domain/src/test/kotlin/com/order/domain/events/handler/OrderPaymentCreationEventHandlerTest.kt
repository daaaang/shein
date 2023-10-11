package com.order.domain.events.handler

import com.order.domain.events.OrderKitchenTicketStatusUpdatePublishEvent
import com.order.domain.events.OrderKitchenTicketCreationConsumeEvent
import com.order.domain.events.OrderPaymentCreationPublishEvent
import com.order.domain.events.publisher.EventPublisher
import com.order.domain.model.kitchen.KitchenTicketCreationType
import com.order.domain.model.kitchen.KitchenTicketStatusType
import com.order.domain.model.payment.Payment
import com.order.domain.usecase.OrderKitchenUseCase
import com.order.domain.usecase.OrderPaymentUseCase
import io.kotest.core.spec.IsolationMode
import io.kotest.core.spec.style.BehaviorSpec
import io.mockk.Runs
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.just
import io.mockk.mockk

class OrderPaymentCreationEventHandlerTest(
    private val orderKitchenUseCase: OrderKitchenUseCase = mockk(),
    private val orderPaymentUseCase: OrderPaymentUseCase = mockk(),
    private val eventPublisher: EventPublisher = mockk(),
) : BehaviorSpec({

    isolationMode = IsolationMode.InstancePerLeaf

    val sut = OrderPaymentCreationEventHandler(
        orderKitchenUseCase = orderKitchenUseCase,
        orderPaymentUseCase = orderPaymentUseCase,
        eventPublisher = eventPublisher,
    )

    given("각 상황에 대한 mockk 응답이 주어져요") {
        val txId = "1234"
        val orderId = 1L
        val userId = 1L

        coEvery { orderPaymentUseCase.createPaymentCreditEvent(any(), any(), any()) } returns OrderPaymentCreationPublishEvent(
            txId = txId,
            orderId = orderId,
            payment = Payment(
                userId = userId,
                totalPrice = 100.0,
            ),
        )
        coEvery { orderKitchenUseCase.rejectOrderKitchenEvent(any()) } returns OrderKitchenTicketStatusUpdatePublishEvent(
            txId = txId,
            orderId = orderId,
            kitchenStatus = KitchenTicketStatusType.REJECT,
        )

        coEvery { eventPublisher.publish(any(), any()) } just Runs

        `when`("OrderKitchenTicketCreationConsumeEvent 상태가 APPROVAL 일 떄,") {

            val event = OrderKitchenTicketCreationConsumeEvent(
                txId = txId,
                orderId = orderId,
                kitchenStatus = KitchenTicketCreationType.PENDING,
                productPrices = listOf(),
            )

            sut.process(event)

            then("결제 진행 이벤트를 생성하고, 이벤트를 발행해요") {
                coVerify(exactly = 1) { orderPaymentUseCase.createPaymentCreditEvent(any(), any(), any()) }
                coVerify(exactly = 1) { eventPublisher.publish(any(), any()) }
                coVerify(exactly = 0) { orderKitchenUseCase.rejectOrderKitchenEvent(any()) }
            }
        }

        `when`("OrderKitchenTicketCreationConsumeEvent 상태가 Reject 일 떄,") {

            val event = OrderKitchenTicketCreationConsumeEvent(
                txId = txId,
                orderId = orderId,
                kitchenStatus = KitchenTicketCreationType.REJECTED_FULL_WAITING,
                productPrices = listOf(),
            )

            sut.process(event)

            then("주방 티켓 거절 이벤트를 생성하고, 이벤트를 발행해요") {
                coVerify(exactly = 0) { orderPaymentUseCase.createPaymentCreditEvent(any(), any(), any()) }
                coVerify(exactly = 1) { eventPublisher.publish(any(), any()) }
                coVerify(exactly = 1) { orderKitchenUseCase.rejectOrderKitchenEvent(any()) }
            }
        }

        `when`("reject가 호출되면") {

            sut.reject(txId = txId)

            then("주방 티켓 거절 이벤트를 생성하고, 이벤트를 발행해요") {
                coVerify(exactly = 0) { orderPaymentUseCase.createPaymentCreditEvent(any(), any(), any()) }
                coVerify(exactly = 1) { eventPublisher.publish(any(), any()) }
                coVerify(exactly = 1) { orderKitchenUseCase.rejectOrderKitchenEvent(any()) }
            }
        }
    }

})

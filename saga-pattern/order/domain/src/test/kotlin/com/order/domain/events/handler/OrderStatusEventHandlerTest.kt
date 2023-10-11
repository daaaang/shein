package com.order.domain.events.handler

import com.order.domain.events.OrderKitchenStatusConsumeEvent
import com.order.domain.events.OrderPaymentStatusPublishEvent
import com.order.domain.events.publisher.EventPublisher
import com.order.domain.model.kitchen.KitchenTicketStatusType
import com.order.domain.model.payment.PaymentStatusType
import com.order.domain.usecase.OrderPaymentUseCase
import com.order.domain.usecase.OrderUseCase
import io.kotest.core.spec.IsolationMode
import io.kotest.core.spec.style.BehaviorSpec
import io.mockk.Runs
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.just
import io.mockk.mockk

class OrderStatusEventHandlerTest(
    private val orderUseCase: OrderUseCase = mockk(),
    private val eventPublisher: EventPublisher = mockk(),
    private val orderPaymentUseCase: OrderPaymentUseCase = mockk(),
) : BehaviorSpec({

    isolationMode = IsolationMode.InstancePerLeaf

    val sut = OrderStatusEventHandler(
        orderUseCase = orderUseCase,
        eventPublisher = eventPublisher,
        orderPaymentUseCase = orderPaymentUseCase,
    )

    given("각 상황에 대한 mockk 응답이 주어져요") {
        val txId = "1234"
        val orderId = 1L

        coEvery { orderUseCase.approvalOrder(any()) } just Runs

        coEvery { orderPaymentUseCase.rejectPaymentCreditEvent(any()) } returns OrderPaymentStatusPublishEvent(
            txId = txId,
            orderId = orderId,
            paymentStatus = PaymentStatusType.REJECT,
        )

        coEvery { eventPublisher.publish(any(), any()) } just Runs

        `when`("OrderKitchenStatusConsumeEvent 상태가 APPROVAL 일 떄,") {

            val event = OrderKitchenStatusConsumeEvent(
                txId = txId,
                orderId = orderId,
                kitchenStatus = KitchenTicketStatusType.APPROVAL,
            )

            sut.process(event)

            then("주문 완료를 수행해요") {
                coVerify(exactly = 1) { orderUseCase.approvalOrder(any()) }
                coVerify(exactly = 0) { eventPublisher.publish(any(), any()) }
                coVerify(exactly = 0) { orderPaymentUseCase.rejectPaymentCreditEvent(any()) }
            }
        }

        `when`("OrderKitchenStatusConsumeEvent 상태가 Reject 일 떄,") {

            val event = OrderKitchenStatusConsumeEvent(
                txId = txId,
                orderId = orderId,
                kitchenStatus = KitchenTicketStatusType.REJECT,
            )

            sut.process(event)

            then("결제 취소 이벤트를 생성하고 이벤트를 발행해요.") {
                coVerify(exactly = 0) { orderUseCase.approvalOrder(any()) }
                coVerify(exactly = 1) { eventPublisher.publish(any(), any()) }
                coVerify(exactly = 1) { orderPaymentUseCase.rejectPaymentCreditEvent(any()) }
            }
        }

        `when`("reject가 호출되면") {

            sut.reject(txId = txId)

            then("주방 티켓 거절 이벤트를 생성하고, 이벤트를 발행해요") {
                coVerify(exactly = 0) { orderUseCase.approvalOrder(any()) }
                coVerify(exactly = 1) { eventPublisher.publish(any(), any()) }
                coVerify(exactly = 1) { orderPaymentUseCase.rejectPaymentCreditEvent(any()) }
            }
        }
    }


})

package com.order.domain.events.handler

import com.order.domain.events.OrderKitchenTicketStatusUpdatePublishEvent
import com.order.domain.events.OrderPaymentStatusConsumeEvent
import com.order.domain.events.publisher.EventPublisher
import com.order.domain.model.kitchen.KitchenTicketStatusType
import com.order.domain.model.payment.PaymentStatusType
import com.order.domain.usecase.OrderKitchenUseCase
import io.kotest.core.spec.IsolationMode
import io.kotest.core.spec.style.BehaviorSpec
import io.mockk.Runs
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.just
import io.mockk.mockk

class OrderKitchenTicketStatusHandlerTest(
    private val orderKitchenUseCase: OrderKitchenUseCase = mockk(),
    private val eventPublisher: EventPublisher = mockk()
) : BehaviorSpec({

    isolationMode = IsolationMode.InstancePerLeaf

    val sut = OrderKitchenTicketStatusHandler(
        orderKitchenUseCase = orderKitchenUseCase,
        eventPublisher = eventPublisher,
    )

    given("각 상황에 대한 mockk 응답이 주어져요") {
        val txId = "1234"
        val orderId = 1L

        coEvery { orderKitchenUseCase.approvalOrderKitchenEvent(any()) } returns OrderKitchenTicketStatusUpdatePublishEvent(
            txId = txId,
            orderId = orderId,
            kitchenStatus = KitchenTicketStatusType.APPROVAL,
        )
        coEvery { orderKitchenUseCase.rejectOrderKitchenEvent(any()) } returns OrderKitchenTicketStatusUpdatePublishEvent(
            txId = txId,
            orderId = orderId,
            kitchenStatus = KitchenTicketStatusType.REJECT,
        )

        coEvery { eventPublisher.publish(any(), any()) } just Runs

        `when`("PaymentStatus 상태가 APPROVAL 일 떄,") {

            val event = OrderPaymentStatusConsumeEvent(
                txId = txId,
                orderId = orderId,
                paymentStatus = PaymentStatusType.APPROVAL,
            )

            sut.process(event)

            then("주방 티켓 승인 이벤트를 생성하고, 이벤트를 발행해요") {
                coVerify(exactly = 1) { orderKitchenUseCase.approvalOrderKitchenEvent(any()) }
                coVerify(exactly = 1) { eventPublisher.publish(any(), any()) }
                coVerify(exactly = 0) { orderKitchenUseCase.rejectOrderKitchenEvent(any()) }
            }
        }

        `when`("PaymentStatus 상태가 Reject 일 떄,") {

            val event = OrderPaymentStatusConsumeEvent(
                txId = txId,
                orderId = orderId,
                paymentStatus = PaymentStatusType.REJECT_DURING_PAYMENT,
            )

            sut.process(event)

            then("주방 티켓 거절 이벤트를 생성하고, 이벤트를 발행해요") {
                coVerify(exactly = 0) { orderKitchenUseCase.approvalOrderKitchenEvent(any()) }
                coVerify(exactly = 1) { eventPublisher.publish(any(), any()) }
                coVerify(exactly = 1) { orderKitchenUseCase.rejectOrderKitchenEvent(any()) }
            }
        }

        `when`("reject가 호출되면") {

            sut.reject(txId = txId)

            then("주방 티켓 거절 이벤트를 생성하고, 이벤트를 발행해요") {
                coVerify(exactly = 0) { orderKitchenUseCase.approvalOrderKitchenEvent(any()) }
                coVerify(exactly = 1) { eventPublisher.publish(any(), any()) }
                coVerify(exactly = 1) { orderKitchenUseCase.rejectOrderKitchenEvent(any()) }
            }
        }
    }

})

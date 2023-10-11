package com.order.domain.events.handler

import com.order.domain.events.OrderKitchenCreationPublishEvent
import com.order.domain.events.TargetEventMessage
import com.order.domain.events.UserStatusConsumeEvent
import com.order.domain.events.publisher.EventPublisher
import com.order.domain.events.publisher.EventTarget
import com.order.domain.model.Order
import com.order.domain.model.StepStatus
import com.order.domain.model.user.UserStatusType
import com.order.domain.usecase.OrderKitchenUseCase
import com.order.domain.usecase.OrderUseCase
import io.kotest.core.spec.IsolationMode
import io.kotest.core.spec.style.BehaviorSpec
import io.kotest.matchers.shouldBe
import io.mockk.Runs
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.just
import io.mockk.mockk

class OrderKitchenTicketCreationHandlerTest(
    private val orderUseCase: OrderUseCase = mockk(),
    private val orderKitchenUseCase: OrderKitchenUseCase = mockk(),
    private val eventPublisher: EventPublisher = mockk(),
) : BehaviorSpec({

    isolationMode = IsolationMode.InstancePerLeaf

    val sut = OrderKitchenTicketCreationHandler(
        orderUseCase = orderUseCase,
        orderKitchenUseCase = orderKitchenUseCase,
        eventPublisher = eventPublisher,
    )

    given("각 상황에 대한 mockk 응답이 주어져요") {
        val txId = "1234"
        val orderId = 1L
        val userId = 1L

        coEvery { orderKitchenUseCase.createOrderKitchenEvent(any()) } returns OrderKitchenCreationPublishEvent(
            txId = txId,
            orderId = orderId,
            productItems = listOf(),
        )
        coEvery { orderUseCase.rejectOrder(any(), any()) } just Runs
        coEvery { eventPublisher.publish(any(), any()) } just Runs

        `when`("UserStatusConsumeEvent의 상태가 Normal 일 떄,") {

            val event = UserStatusConsumeEvent(
                txId = txId,
                userId = userId,
                userStatus = UserStatusType.NOMAL,
            )

            sut.process(event)

            then("주방 티켓 생성 이벤트를 생성하고, 이벤트를 발행해요") {
                coVerify(exactly = 1) { orderKitchenUseCase.createOrderKitchenEvent(any()) }
                coVerify(exactly = 1) { eventPublisher.publish(any(), any()) }
                coVerify(exactly = 0) { orderUseCase.rejectOrder(any(), any()) }
            }
        }

        `when`("UserStatusConsumeEvent의 상태가 AbNormal 일 떄,") {

            val event = UserStatusConsumeEvent(
                txId = txId,
                userId = userId,
                userStatus = UserStatusType.ABNOMAL,
            )

            sut.process(event)

            then("주문 취소를 수행해요") {
                coVerify(exactly = 0) { orderKitchenUseCase.createOrderKitchenEvent(any()) }
                coVerify(exactly = 0) { eventPublisher.publish(any(), any()) }
                coVerify(exactly = 1) { orderUseCase.rejectOrder(any(), any()) }
            }
        }

        `when`("reject가 호출되면") {

            sut.reject(txId = txId)

            then("주문 취소를 수행해요") {
                coVerify(exactly = 0) { orderKitchenUseCase.createOrderKitchenEvent(any()) }
                coVerify(exactly = 0) { eventPublisher.publish(any(), any()) }
                coVerify(exactly = 1) { orderUseCase.rejectOrder(any(), any()) }
            }
        }
    }

})

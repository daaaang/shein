package com.order.domain.events

import com.order.domain.events.publisher.EventTarget
import org.springframework.stereotype.Component
import java.lang.Exception

@Component
object EventMessageCreator {

    fun createMessage(eventTarget: EventTarget, txId: String, eventAction: () -> Event): EventMessage<Event> {
        return try {
            val event = eventAction.invoke()

            TargetEventMessage(
                target = eventTarget,
                txId = event.txId,
                message = event,
            )
        } catch (e: Exception) {
            ErrorEventMessage(
                target = eventTarget,
                txId = txId,
                errorMessage = e.message.toString()
            )
        }
    }
}
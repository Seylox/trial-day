package com.bitmovin.trialday.eventbus

import androidx.annotation.VisibleForTesting
import java.util.concurrent.ConcurrentHashMap
import javax.inject.Inject

class EventBus @Inject constructor() {

    // TODO concurrency & thread safety

    @VisibleForTesting(otherwise = VisibleForTesting.PRIVATE)
    val listeners: ConcurrentHashMap<Listener, Set<Pair<Class<*>, Priority>>> = ConcurrentHashMap()

    @Synchronized
    fun <T : Event> register(
        listener: Listener,
        eventType: Class<T>,
        priority: Priority = Priority.DEFAULT
    ) {
        if (listeners.containsKey(listener)) {
            val eventTypes = listeners[listener]?.toMutableSet() ?: mutableSetOf()
            val eventTypeExists = eventTypes.firstOrNull { eventPriorityPair ->
                eventType == eventPriorityPair.first
            }

            if (eventTypeExists != null && eventTypeExists.second != priority) {
                eventTypes.remove(eventTypeExists)
                eventTypes.add(Pair(eventType, priority))
            } else {
                eventTypes.add(Pair(eventType, priority))
            }

            listeners[listener] = eventTypes
        } else {
            val eventPriorityPair = Pair(eventType, priority)
            listeners[listener] = setOf(eventPriorityPair)
        }
    }

    @Synchronized
    fun <T : Event> unregister(
        listener: Listener,
        eventType: Class<T>
    ) {
        if (listeners.containsKey(listener)) {
            val eventTypes = listeners[listener]?.toMutableSet() ?: mutableSetOf()
            val eventTypeExists = eventTypes.firstOrNull { eventPriorityPair ->
                eventType == eventPriorityPair.first
            }

            if (eventTypeExists != null) {
                eventTypes.remove(eventTypeExists)
                listeners[listener] = eventTypes
            }

            if (eventTypes.isEmpty()) {
                listeners.remove(listener)
            }
        }
    }

    @Synchronized
    fun <T : Event> notifyListeners(event: T) {
        val relevantListeners = listeners.filterValues { eventPriorityPairSet ->
            eventPriorityPairSet.any { eventPriorityPair ->
                event.javaClass == eventPriorityPair.first
            }
        }
        val sortedListeners = relevantListeners.toList().sortedBy {
            (_, eventPriorityPairSet) -> eventPriorityPairSet.first { eventPriorityPair ->
                event.javaClass == eventPriorityPair.first
        }.second
        }.asReversed().toMap()
        sortedListeners.forEach {
            it.key.onEvent(event)
        }
    }
}

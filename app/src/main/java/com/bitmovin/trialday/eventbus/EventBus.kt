package com.bitmovin.trialday.eventbus

import android.util.Log
import androidx.annotation.VisibleForTesting
import java.util.concurrent.ConcurrentHashMap
import javax.inject.Inject

class EventBus @Inject constructor() {

    // TODO sort by priority
    // TODO concurrency & thread safety

    @VisibleForTesting(otherwise = VisibleForTesting.PRIVATE)
    val listeners: ConcurrentHashMap<Listener, Set<Pair<Class<*>, Priority>>> = ConcurrentHashMap()

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
                Log.d("EventBus", "--- EventType exists! ${eventTypeExists.first.simpleName} Priority updated! ---")
            } else {
                eventTypes.add(Pair(eventType, priority))
                Log.d("EventBus", "--- EventType doesn't exist! Adding ${eventType.simpleName} ---")
            }

            listeners[listener] = eventTypes
            Log.d("EventBus", "--- Listener exists ---")
        } else {
            val eventPriorityPair = Pair(eventType, priority)
            listeners[listener] = setOf(eventPriorityPair)
            Log.d("EventBus", "--- Listener doesn't exist. Adding $eventType ---")
        }
    }

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
                Log.d("EventBus", "--- Removing added EventType! ${eventTypeExists.first.simpleName} ---")
                eventTypes.remove(eventTypeExists)
                listeners[listener] = eventTypes
            }

            if (eventTypes.isEmpty()) {
                Log.d("EventBus", "--- Last Event, removing Listener! ---")
                listeners.remove(listener)
            }
        }
    }

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

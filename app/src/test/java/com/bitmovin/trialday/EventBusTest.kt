package com.bitmovin.trialday

import com.bitmovin.trialday.eventbus.Event
import com.bitmovin.trialday.eventbus.EventBus
import com.bitmovin.trialday.eventbus.Listener
import com.bitmovin.trialday.example.ConcreteEvent
import org.junit.After
import org.junit.Before
import org.junit.Test
import org.junit.Assert.assertEquals

class EventBusTest {

    private val sut = EventBus()

    @Test
    fun `Register listener to EventBus`() {
        val listener = object : Listener {
            override fun <T : Event> onEvent(event: T) {

            }
        }

        sut.register(listener, ConcreteEvent::class.java)
        assertEquals(1, sut.listeners.size)
        sut.unregister(listener, ConcreteEvent::class.java)
    }

    @Test
    fun `Notify registered listener`() {
        val testEvent = ConcreteEvent("testdata")

        val listener = object : Listener {
            override fun <T : Event> onEvent(event: T) {
                assertEquals(testEvent, event)
            }
        }

        sut.register(listener, ConcreteEvent::class.java)
        sut.notifyListeners(testEvent)
        sut.unregister(listener, ConcreteEvent::class.java)
    }

    @Test
    fun `Unregister a previously registered listener`() {
        val listener = object : Listener {
            override fun <T : Event> onEvent(event: T) {

            }
        }

        sut.register(listener, ConcreteEvent::class.java)
        assertEquals(1, sut.listeners.size)
        sut.unregister(listener, ConcreteEvent::class.java)
        assertEquals(0, sut.listeners.size)
    }
}

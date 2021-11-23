package com.bitmovin.trialday

import com.bitmovin.trialday.example.ConcreteEvent
import org.junit.Test
import org.junit.Assert.assertEquals

class ConcreteEventTest {

    @Test
    fun createConreteEvent_eventIsCreated() {
        val data = "concrete data"
        val concreteEvent = ConcreteEvent(data)
        assertEquals(data, concreteEvent.concreteData)
    }
}

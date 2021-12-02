package com.bitmovin.trialday.example

import com.bitmovin.trialday.eventbus.Event

data class ConcreteEvent(
    val concreteData: String = "data"
) : Event() {

}

package com.bitmovin.trialday.example

import com.bitmovin.trialday.eventbus.Event

data class SecondEvent(
    val concreteData: String = "data"
) : Event() {

}

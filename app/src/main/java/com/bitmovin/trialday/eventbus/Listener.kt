package com.bitmovin.trialday.eventbus

interface Listener {
    fun <T: Event> onEvent(event: T)
}

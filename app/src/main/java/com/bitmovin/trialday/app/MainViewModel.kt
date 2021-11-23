package com.bitmovin.trialday.app

import android.util.Log
import androidx.lifecycle.ViewModel
import com.bitmovin.trialday.eventbus.Event
import com.bitmovin.trialday.eventbus.EventBus
import com.bitmovin.trialday.eventbus.Listener
import com.bitmovin.trialday.eventbus.Priority
import com.bitmovin.trialday.example.ConcreteEvent
import com.bitmovin.trialday.example.SecondEvent
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class MainViewModel @Inject constructor(
    private val eventBus: EventBus
) : ViewModel(), Listener {

    init {
        eventBus.register(this, ConcreteEvent::class.java)
        eventBus.register(this, ConcreteEvent::class.java, Priority.HIGH)
        eventBus.notifyListeners(ConcreteEvent("real data"))
        eventBus.register(this, SecondEvent::class.java)

        eventBus.notifyListeners(SecondEvent("second data"))
    }

    override fun onCleared() {
        eventBus.unregister(this, SecondEvent::class.java)
        eventBus.unregister(this, ConcreteEvent::class.java)
        super.onCleared()
    }

    override fun <T : Event> onEvent(event: T) {
        if (event is ConcreteEvent) {
            Log.d("MainViewModel", "--- Event is ConcreteEvent ---")
            Log.d("MainViewModel", "--- data is ${event.concreteData} ---")
        } else {
            Log.d("MainViewModel", "--- Event has been called, but is not ConcreteEvent ---")
        }
    }
}

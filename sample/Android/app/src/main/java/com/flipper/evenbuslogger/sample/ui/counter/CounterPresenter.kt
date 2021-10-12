package com.flipper.evenbuslogger.sample.ui.counter

import androidx.lifecycle.*
import com.flipper.evenbuslogger.sample.base.EventBasedPresenter
import com.flipper.evenbuslogger.sample.eventbus.EventBusInstance
import org.greenrobot.eventbus.Subscribe

class CounterPresenter(private val view: CounterView) : EventBasedPresenter(), LifecycleObserver {

    private var counterCurrentValue: Int = 0

    @OnLifecycleEvent(Lifecycle.Event.ON_RESUME)
    fun onResume() {
        startEventListening()
        view.setValue(counterCurrentValue)
    }

    @OnLifecycleEvent(Lifecycle.Event.ON_PAUSE)
    fun onPause() {
        stopEventListening()
    }

    @Subscribe
    fun onEvent(event: CounterEvent) {
        when (event.operation) {
            CounterViewModel.Modification.Increment -> counterCurrentValue++
            CounterViewModel.Modification.Decrement -> counterCurrentValue--
        }
        view.setValue(counterCurrentValue)
    }

    private fun postEvent(operation: CounterViewModel.Modification) =
        EventBusInstance.post(CounterEvent(operation))

    private fun postStickyEvent() =
        EventBusInstance.postSticky(CounterStickyEvent(counterCurrentValue))

    private fun removeStickyEvent() = EventBusInstance.removeSticky(CounterStickyEvent::class.java)

    fun increment() = postEvent(CounterViewModel.Modification.Increment)

    fun decrement() = postEvent(CounterViewModel.Modification.Decrement)

    fun save() = postStickyEvent()

    fun remove() {
        removeStickyEvent()
        counterCurrentValue = 0
        view.setValue(counterCurrentValue)
    }
}

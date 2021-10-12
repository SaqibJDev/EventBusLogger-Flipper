package com.flipper.evenbuslogger.sample.ui.counter

import androidx.hilt.lifecycle.ViewModelInject
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleObserver
import androidx.lifecycle.OnLifecycleEvent
import com.flipper.evenbuslogger.sample.eventbus.EventBusInstance
import com.flipper.evenbuslogger.sample.domain.GetAndSetCounter
import com.flipper.evenbuslogger.sample.domain.models.Counter
import com.flipper.evenbuslogger.sample.ui.BaseViewModel
import kotlinx.coroutines.channels.BroadcastChannel
import kotlinx.coroutines.channels.sendBlocking
import kotlinx.coroutines.flow.*
import org.greenrobot.eventbus.Subscribe
import org.greenrobot.eventbus.ThreadMode
import timber.log.Timber

data class CounterStickyEvent(val counter: Int)
class CounterEvent(val operation: CounterViewModel.Modification)

class CounterViewModel
@ViewModelInject constructor(
    private val counterDomain: GetAndSetCounter
) : BaseViewModel(), LifecycleObserver {

    private val counterModifier = BroadcastChannel<Modification>(1)
    private val counterCurrentValue = MutableStateFlow(Counter(0))
    private val errors = BroadcastChannel<Throwable>(1)

    init {
        counterDomain.get()
            .onEach {
                counterCurrentValue.value = it
            }
            .launchIn(ioScope)

        counterModifier
            .asFlow()
            .onEach { click ->
                var valueToEdit = counterDomain.get().first()

                when (click) {
                    Modification.Increment -> valueToEdit++
                    Modification.Decrement -> valueToEdit--
                }

                counterDomain.editCounter(valueToEdit)
                    .onFailure { errors.send(it) }
            }
            .launchIn(ioScope)
    }

    @OnLifecycleEvent(Lifecycle.Event.ON_RESUME)
    private fun registerEventBus() {
        EventBusInstance.register(this)
    }

    @OnLifecycleEvent(Lifecycle.Event.ON_PAUSE)
    private fun unregisterEventBus(counterViewModel: CounterViewModel){
        EventBusInstance.unregister(this)
    }

    @Subscribe
    fun onEvent(event: CounterEvent) {
        counterModifier.sendBlocking(event.operation)
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    fun onEvent(event: CounterStickyEvent) {
        Timber.i("EventBus: StickyEvent received, counter value = ${event.counter}")
    }

    // OutPuts
    fun value(): Flow<Counter> = counterCurrentValue
    fun errors(): Flow<Throwable> = errors.asFlow()

    private fun postEvent(operation: Modification) {
        EventBusInstance.post(CounterEvent(operation))
    }

    private fun postStickyEvent() {
        EventBusInstance.postSticky(CounterStickyEvent(counterCurrentValue.value.value))
    }

    private fun removeStickyEvent() {
        EventBusInstance.removeSticky(CounterStickyEvent::class.java)
    }

    fun increment() {
        postEvent(Modification.Increment)
    }

    fun decrement() {
        postEvent(Modification.Decrement)
    }

    fun save() {
        postStickyEvent()
    }

    fun remove() {
        removeStickyEvent()
    }

    enum class Modification {
        Increment, Decrement
    }
}

package com.flipper.evenbuslogger.sample.data

import android.app.Activity
import android.content.SharedPreferences
import com.flipper.evenbuslogger.sample.EvenBusRegistry
import com.flipper.evenbuslogger.sample.domain.models.Counter
import com.flipper.evenbuslogger.sample.eventbus.IEventBus
import com.flipper.evenbuslogger.sample.ui.counter.CounterStickyEvent
import org.greenrobot.eventbus.Subscribe
import org.greenrobot.eventbus.ThreadMode
import timber.log.Timber

class CounterEventBasedRepository(activity: Activity) : EvenBusRegistry.EventBusSubscriber {

    private val counterPreferences: SharedPreferences =
        activity.applicationContext.getSharedPreferences("counter_repo", Activity.MODE_PRIVATE)

    fun getValue(): Int = counterPreferences.getInt(COUNTER_KEY, 0)

    fun setValue(counter: Counter) {
        counterPreferences.edit().putInt(COUNTER_KEY, counter.value).apply()
    }

    companion object {
        const val COUNTER_KEY: String = "counter_value"
    }

    override fun register(eventBus: IEventBus): Any {
        return eventBus.register(this)
    }

    override fun unregister(eventBus: IEventBus) {
        eventBus.unregister(this)
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    fun onEvent(event: CounterStickyEvent) {
        Timber.i("EventBus: StickyEvent received, counter value = ${event.counter}")
    }
}

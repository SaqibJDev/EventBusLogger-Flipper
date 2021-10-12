package com.flipper.evenbuslogger.sample

import android.app.Activity
import com.flipper.evenbuslogger.sample.data.CounterEventBasedRepository
import com.flipper.evenbuslogger.sample.eventbus.EventBusInstance
import com.flipper.evenbuslogger.sample.eventbus.IEventBus
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class EvenBusRegistry(private val activity: Activity) {

    interface EventBusSubscriber {
        fun register(eventBus: IEventBus = EventBusInstance): Any
        fun unregister(eventBus: IEventBus = EventBusInstance)
    }

    suspend fun registerSubscribers() = withContext(Dispatchers.IO) {
        getSubscribers().forEach { it.register() }
    }

    private fun getSubscribers() =
        mutableListOf<EventBusSubscriber>(CounterEventBasedRepository(activity))
}

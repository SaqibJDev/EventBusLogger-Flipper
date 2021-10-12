package com.flipper.evenbuslogger.sample.eventbus

import org.greenrobot.eventbus.EventBus

interface IEventBus {
    fun post(event: Any)
    fun postSticky(event: Any)
    fun <T> removeSticky(eventType: Class<T>): T?
    fun removeSticky(event: Any): Boolean
    fun <T> getSticky(eventType: Class<T>): T?
    fun isRegistered(subscriber: Any): Boolean
    fun register(subscriber: Any)
    fun unregister(subscriber: Any)

    var listener: OnEventListener?

    interface OnEventListener {
        fun onPost(event: Any)
        fun onPostSticky(event: Any)
        fun onRemoveSticky(event: Any)
    }
}

class EventBusImpl : IEventBus {

    override var listener: IEventBus.OnEventListener? = null
    private var eventBus = EventBus()

    override fun post(event: Any) {
        eventBus.post(event)
        listener?.onPost(event)
    }

    override fun postSticky(event: Any) {
        eventBus.postSticky(event)
        listener?.onPostSticky(event)
    }

    override fun <T> removeSticky(eventType: Class<T>): T {
        val event = eventBus.removeStickyEvent(eventType)
        listener?.onRemoveSticky(event ?: "$eventType doesn't exist")
        return event
    }

    override fun removeSticky(event: Any): Boolean {
        val success = eventBus.removeStickyEvent(event)
        listener?.onRemoveSticky(event)
        return success
    }

    override fun <T> getSticky(eventType: Class<T>): T {
        return eventBus.getStickyEvent(eventType)
    }

    override fun isRegistered(subscriber: Any): Boolean = eventBus.isRegistered(subscriber)
    override fun register(subscriber: Any) = eventBus.register(subscriber)
    override fun unregister(subscriber: Any) = eventBus.unregister(subscriber)
}

object EventBusInstance : IEventBus by EventBusImpl()

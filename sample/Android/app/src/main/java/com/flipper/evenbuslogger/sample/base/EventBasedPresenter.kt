package com.flipper.evenbuslogger.sample.base

import com.flipper.evenbuslogger.sample.eventbus.EventBusInstance

abstract class EventBasedPresenter {

    open fun startEventListening() {
        EventBusInstance.register(this)
    }

    open fun stopEventListening() {
        EventBusInstance.unregister(this)
    }
}

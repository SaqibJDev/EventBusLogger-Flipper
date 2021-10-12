package com.flipper.evenbuslogger.sample.domain.models

data class Counter(val value: Int) {
    operator fun inc() = Counter(value + 1)

    operator fun dec() = Counter(value - 1)

    override fun toString() = value.toString()
}

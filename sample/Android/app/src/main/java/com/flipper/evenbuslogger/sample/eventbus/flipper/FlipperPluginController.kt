package com.flipper.evenbuslogger.sample.eventbus.flipper

import android.content.Context
import com.facebook.flipper.android.AndroidFlipperClient
import com.facebook.flipper.android.utils.FlipperUtils
import com.facebook.flipper.core.FlipperClient
import com.facebook.flipper.plugins.inspector.DescriptorMapping
import com.facebook.flipper.plugins.inspector.InspectorFlipperPlugin
import com.facebook.soloader.SoLoader
import com.flipper.evenbuslogger.sample.eventbus.EventBusInstance

class FlipperCLinet(private val application: Context) {

    fun init() {
        SoLoader.init(application, false)
        if (FlipperUtils.shouldEnableFlipper(application)) {
            val client = AndroidFlipperClient.getInstance(application)
            client.addPlugin(InspectorFlipperPlugin(application, DescriptorMapping.withDefaults()))
            setupEventBusLogger(client)
            client.start()
        }
    }

    private fun setupEventBusLogger(client: FlipperClient) {
        val eventBusLoggerPlugin = EventBusLoggerFlipperPlugin()
        client.addPlugin(eventBusLoggerPlugin)
        EventBusInstance.listener = eventBusLoggerPlugin
    }
}

package com.flipper.evenbuslogger.sample.eventbus.flipper

import com.facebook.flipper.android.AndroidFlipperClient
import com.facebook.flipper.core.FlipperConnection
import com.facebook.flipper.core.FlipperObject
import com.facebook.flipper.core.FlipperPlugin
import com.google.gson.Gson
import com.flipper.evenbuslogger.sample.eventbus.EventBusInstance
import com.flipper.evenbuslogger.sample.eventbus.IEventBus
import io.bloco.template.eventbus.getStackTraceString
import java.lang.Thread.currentThread
import java.text.SimpleDateFormat
import java.util.*
import java.util.concurrent.atomic.AtomicInteger

enum class EventType(val value: String) {
    POST("Post"), POST_STICKY("Post Sticky"), REMOVE_STICKY("Remove Sticky")
}

private const val PLUGIN_ID = "eventbus-logger"

class EventBusLoggerFlipperPlugin : FlipperPlugin, IEventBus.OnEventListener {

    private var id: AtomicInteger = AtomicInteger(0)
    private val gson = Gson()
    private val dateTimeFormatter = SimpleDateFormat("dd-MM-yyyy HH:mm:ss.SSS", Locale.getDefault())
    private var connection: FlipperConnection? = null

    override fun onConnect(connection: FlipperConnection?) {
        this.connection = connection
        EventBusInstance.listener = this
    }

    override fun onDisconnect() {
        connection = null
        EventBusInstance.listener = null
    }

    override fun runInBackground(): Boolean = false

    private val stackTrace: String?
        get() = getStackTraceString(currentThread().stackTrace, 6)

    override fun getId(): String = PLUGIN_ID

    override fun onPost(event: Any) {
        sendData(event, stackTrace, EventType.POST.value, createTimestamp())
    }

    override fun onPostSticky(event: Any) {
        sendData(event, stackTrace, EventType.POST_STICKY.value, createTimestamp())
    }

    override fun onRemoveSticky(event: Any) {
        sendData(event, stackTrace, EventType.REMOVE_STICKY.value, createTimestamp())
    }

    private fun sendData(event: Any, stackTrace: String?, eventType: String, timestamp: String) {
        val className = event.javaClass.simpleName
        connection?.let {
            val evenBody = try {
                gson.toJson(event)
            } catch (error: Throwable) {
                event.toString()
            }
            val id = id.incrementAndGet()
            val keyValueMap = FlipperObject.Builder()
                .put("id", id)
                .put("name", className)
                .put("eventType", eventType)
                .put("timestamp", timestamp)

            val request = FlipperObject.Builder()
                .put("id", id)
                .put("name", className)
                .put("eventType", eventType)
                .put("eventBody", evenBody)
                .put("timestamp", timestamp)
                .put("stackTrace", stackTrace)
                .put("keyValueMap", keyValueMap)
                .build()
            AndroidFlipperClient.getInstanceIfInitialized() ?: return
            it.send("newEvent", request)
        }
    }

    private fun createTimestamp(): String {
        return dateTimeFormatter.format(Calendar.getInstance().time)
    }
}


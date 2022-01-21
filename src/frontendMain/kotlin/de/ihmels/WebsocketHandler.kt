package de.ihmels

import de.ihmels.ConnectionState.*
import de.ihmels.ws.WebsocketService
import io.kvision.state.ObservableValue
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.channels.ReceiveChannel
import kotlinx.coroutines.channels.SendChannel
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.launch

enum class ConnectionState {
    ESTABLISHING, CONNECTED, DISCONNECTED
}

class WebsocketHandler(
    private val messageHandler: (SMessage) -> Unit
) : CoroutineScope by CoroutineScope(Dispatchers.Default) {

    internal val connectionState = ObservableValue(DISCONNECTED)

    private val websocketService: WebsocketService = WebsocketService()

    private val outgoingChannel: Channel<CMessage> = Channel()

    fun send(msg: CMessageType) = launch { outgoingChannel.send(CMessage(msg)) }

    fun connect() = launch {

        connectionState.value = ESTABLISHING

        websocketService.socketConnection { output, input ->

            connectionState.value = CONNECTED

            connectChannels(output, input)
        }

        connectionState.value = DISCONNECTED
    }

    private suspend fun connectChannels(
        output: SendChannel<CMessage>,
        input: ReceiveChannel<SMessage>
    ) {
        coroutineScope {
            launch {
                for (msg in outgoingChannel) {
                    output.send(msg)
                }
            }
            launch {
                for (msg in input) {
                    messageHandler.invoke(msg)
                }
            }
        }
    }
}
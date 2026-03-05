package de.ihmels

import de.ihmels.ConnectionState.*
import de.ihmels.ws.IWebsocketService
import dev.kilua.rpc.getService
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
    private val messageHandler: (ResponseMessage) -> Unit
) : CoroutineScope by CoroutineScope(Dispatchers.Default) {

    internal val connectionState = ObservableValue(DISCONNECTED)

    private val websocketService: IWebsocketService = getService()

    private val outgoingChannel: Channel<RequestMessage> = Channel()

    fun send(msg: RequestMessageType) = launch { outgoingChannel.send(RequestMessage(msg)) }

    fun connect() = launch {

        connectionState.value = ESTABLISHING

        websocketService.socketConnection { output, input ->

            connectionState.value = CONNECTED

            connectChannels(output, input)
        }

        connectionState.value = DISCONNECTED
    }

    private suspend fun connectChannels(
        output: SendChannel<RequestMessage>,
        input: ReceiveChannel<ResponseMessage>
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
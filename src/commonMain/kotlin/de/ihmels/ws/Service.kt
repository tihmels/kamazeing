package de.ihmels.ws

import de.ihmels.RequestMessage
import de.ihmels.ResponseMessage
import dev.kilua.rpc.annotations.RpcService
import kotlinx.coroutines.channels.ReceiveChannel
import kotlinx.coroutines.channels.SendChannel

@RpcService
interface IWebsocketService {
    suspend fun socketConnection(input: ReceiveChannel<RequestMessage>, output: SendChannel<ResponseMessage>) {}
    suspend fun socketConnection(handler: suspend (SendChannel<RequestMessage>, ReceiveChannel<ResponseMessage>) -> Unit) {}
}

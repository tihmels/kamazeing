package de.ihmels.ws

import de.ihmels.CMessage
import de.ihmels.SMessage
import dev.kilua.rpc.annotations.RpcService
import kotlinx.coroutines.channels.ReceiveChannel
import kotlinx.coroutines.channels.SendChannel

@RpcService
interface IWebsocketService {
    suspend fun socketConnection(input: ReceiveChannel<CMessage>, output: SendChannel<SMessage>) {}
    suspend fun socketConnection(handler: suspend (SendChannel<CMessage>, ReceiveChannel<SMessage>) -> Unit) {}
}

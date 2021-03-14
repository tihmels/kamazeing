package de.ihmels.ws

import de.ihmels.CMessage
import de.ihmels.SMessage
import io.kvision.annotations.KVService
import kotlinx.coroutines.channels.ReceiveChannel
import kotlinx.coroutines.channels.SendChannel

@KVService
interface IWebsocketService {
    suspend fun socketConnection(input: ReceiveChannel<CMessage>, output: SendChannel<SMessage>)
}
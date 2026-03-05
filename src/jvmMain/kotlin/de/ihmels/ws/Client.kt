package de.ihmels.ws

import de.ihmels.RequestMessage
import de.ihmels.ResponseMessage
import de.ihmels.ResponseMessageType
import kotlinx.coroutines.channels.ReceiveChannel
import kotlinx.coroutines.channels.SendChannel
import java.util.*
import java.util.UUID.randomUUID

data class Client(val input: ReceiveChannel<RequestMessage>, val output: SendChannel<ResponseMessage>) {

    val uuid: UUID = randomUUID()

    suspend fun send(msg: ResponseMessageType) = output.send(ResponseMessage(msg))

    override fun toString(): String = uuid.toString()

}
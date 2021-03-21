package de.ihmels.ws

import de.ihmels.CMessage
import de.ihmels.SMessage
import de.ihmels.SMessageType
import kotlinx.coroutines.channels.ReceiveChannel
import kotlinx.coroutines.channels.SendChannel
import java.util.*
import java.util.UUID.randomUUID

data class Client(val input: ReceiveChannel<CMessage>, val output: SendChannel<SMessage>) {

    val uuid: UUID = randomUUID()

    suspend fun send(msg: SMessageType) = output.send(SMessage(msg))

    override fun toString(): String = uuid.toString()

}
package de.ihmels.ws

import de.ihmels.CMessage
import de.ihmels.Logging
import de.ihmels.SMessage
import de.ihmels.logger
import kotlinx.coroutines.channels.ReceiveChannel
import kotlinx.coroutines.channels.SendChannel

actual class WebsocketService : IWebsocketService, Logging {

    private val log = logger()

    override suspend fun socketConnection(input: ReceiveChannel<CMessage>, output: SendChannel<SMessage>) {

        val client = Client(input, output).also { log.info("Client ${it.uuid} connected") }

        ClientHandler(client).start()

        log.info("Client ${client.uuid} disconnected")

    }

}

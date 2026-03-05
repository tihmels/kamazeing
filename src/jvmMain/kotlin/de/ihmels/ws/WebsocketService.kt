package de.ihmels.ws

import de.ihmels.RequestMessage
import de.ihmels.Logging
import de.ihmels.ResponseMessage
import de.ihmels.logger
import kotlinx.coroutines.channels.ReceiveChannel
import kotlinx.coroutines.channels.SendChannel

class WebsocketService : IWebsocketService, Logging {

    private val log = logger()

    override suspend fun socketConnection(input: ReceiveChannel<RequestMessage>, output: SendChannel<ResponseMessage>) {

        val client = Client(input, output).also { log.info("Client ${it.uuid} connected") }

        ClientHandler(client).start()

        log.info("Client ${client.uuid} disconnected")

    }

}

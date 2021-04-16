package de.ihmels.ws

import de.ihmels.CMessageType

interface ClientMessageHandler {

    suspend fun handle(cMessage: CMessageType)

}
package de.ihmels.ws

import de.ihmels.CMessage
import de.ihmels.SMessage
import de.ihmels.SMessageType
import de.ihmels.maze.Maze
import de.ihmels.maze.generator.WilsonGenerator
import kotlinx.coroutines.channels.ReceiveChannel
import kotlinx.coroutines.channels.SendChannel

actual class WebsocketService : IWebsocketService {

    override suspend fun socketConnection(input: ReceiveChannel<CMessage>, output: SendChannel<SMessage>) {

        val maze = Maze(20, 20)

        output.send(SMessage(SMessageType.NewMaze(maze.toDto())))

        for (message in input) {
            println(message)
        }

    }

}
package de.ihmels.ws

import de.ihmels.maze.Maze
import de.ihmels.maze.generator.AldousBroderGenerator
import de.ihmels.maze.generator.MazeGenerator
import de.ihmels.maze.solver.AstarSolver
import de.ihmels.maze.solver.BreathFirstSolver
import de.ihmels.maze.solver.MazeSolver

class ClientState(
    var maze: Maze = Maze(rows = 10, columns = 10),
    var generator: MazeGenerator = AldousBroderGenerator(),
    var solver: MazeSolver = AstarSolver()
)
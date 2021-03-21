package de.ihmels.ws

import de.ihmels.maze.Maze
import de.ihmels.maze.generator.AldousBroderGenerator
import de.ihmels.maze.generator.MazeGenerator
import de.ihmels.maze.solver.DepthFirstSolver
import de.ihmels.maze.solver.IMazeSolver

class ClientState(
    var maze: Maze = Maze(),
    var generator: MazeGenerator = AldousBroderGenerator(),
    var solver: IMazeSolver = DepthFirstSolver()
)
package de.ihmels.maze.solver

import de.ihmels.maze.Maze
import de.ihmels.maze.Point2D
import de.ihmels.tree.TreeNode

interface IMazeSolver {

    fun solve(maze: Maze): Node<Point2D>?

}
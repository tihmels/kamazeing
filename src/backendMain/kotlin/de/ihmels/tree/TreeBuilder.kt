package de.ihmels.tree

object TreeBuilder {

    fun <T> createTree(initial: T, successors: (T) -> List<T>): TreeNode<T> =
        createTreeRecursive(initial, successors)

    private fun <T> createTreeRecursive(
        value: T,
        successors: (T) -> List<T>,
        visited: MutableList<T> = mutableListOf(value)
    ): TreeNode<T> {

        val node = TreeNode(value)

        val unvisitedSuccessors = successors(value).filter { it !in visited }

        for (successor in unvisitedSuccessors) {
            visited.add(successor)

            val childNode = createTreeRecursive(successor, successors, visited)
            node.add(childNode)
        }

        return node
    }

}
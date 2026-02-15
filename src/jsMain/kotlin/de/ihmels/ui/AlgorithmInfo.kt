package de.ihmels.ui

import io.kvision.core.Container
import io.kvision.html.button
import io.kvision.html.div
import io.kvision.html.h5
import io.kvision.html.p
import io.kvision.html.span
import io.kvision.html.ul
import io.kvision.html.li
import io.kvision.modal.Modal
import io.kvision.utils.perc
import io.kvision.utils.px

/**
 * Algorithm information and characteristics
 *
 * Provides educational information about each maze algorithm
 */
data class AlgorithmDetail(
    val name: String,
    val type: String, // "Generator" or "Solver"
    val description: String,
    val timeComplexity: String,
    val spaceComplexity: String,
    val characteristics: List<String>,
    val optimal: Boolean = false,
    val biased: Boolean = true
)

/**
 * Algorithm information database
 */
object AlgorithmDatabase {

    val generators = mapOf(
        "Aldous-Broder" to AlgorithmDetail(
            name = "Aldous-Broder Algorithm",
            type = "Generator",
            description = "Random walk-based maze generation that carves passages by visiting random unvisited cells.",
            timeComplexity = "O(n²)",
            spaceComplexity = "O(n)",
            characteristics = listOf(
                "✓ Unbiased - equally distributed solution paths",
                "✓ Slow - requires many random walks",
                "✓ Produces intricate, naturally-looking mazes",
                "✗ May take long time for large mazes"
            ),
            optimal = false,
            biased = false
        ),
        "Binary Tree" to AlgorithmDetail(
            name = "Binary Tree Algorithm",
            type = "Generator",
            description = "Simple and fast algorithm that creates biased mazes with paths favoring north and east.",
            timeComplexity = "O(n)",
            spaceComplexity = "O(1)",
            characteristics = listOf(
                "✓ Very fast - O(n) time complexity",
                "✓ Minimal memory usage",
                "✗ Biased - creates diagonal corridors",
                "✗ Less interesting maze structure"
            ),
            optimal = false,
            biased = true
        ),
        "Depth-First Search" to AlgorithmDetail(
            name = "Depth-First Search (DFS)",
            type = "Generator",
            description = "Recursive backtracking algorithm that creates long corridors with minimal branching.",
            timeComplexity = "O(n)",
            spaceComplexity = "O(n)",
            characteristics = listOf(
                "✓ Efficient - O(n) time",
                "✓ Creates long, winding corridors",
                "✓ Visually interesting mazes",
                "✗ Requires stack for recursion"
            ),
            optimal = false,
            biased = false
        ),
        "Sidewinder" to AlgorithmDetail(
            name = "Sidewinder Algorithm",
            type = "Generator",
            description = "Row-by-row generation with hybrid carving pattern creating varied maze structure.",
            timeComplexity = "O(n)",
            spaceComplexity = "O(n)",
            characteristics = listOf(
                "✓ Fast - O(n) time",
                "✓ Creates interesting, balanced patterns",
                "✓ Biased but not as strongly as Binary Tree",
                "✓ No recursion needed"
            ),
            optimal = false,
            biased = true
        ),
        "Wilson" to AlgorithmDetail(
            name = "Wilson's Algorithm",
            type = "Generator",
            description = "Loop-erased random walk that produces unbiased, perfectly balanced mazes.",
            timeComplexity = "O(n²)",
            spaceComplexity = "O(n)",
            characteristics = listOf(
                "✓ Unbiased - uniform distribution",
                "✓ Mathematically elegant",
                "✓ Produces aesthetically pleasing mazes",
                "✗ Slower than other algorithms"
            ),
            optimal = false,
            biased = false
        )
    )

    val solvers = mapOf(
        "Breadth-First Search" to AlgorithmDetail(
            name = "Breadth-First Search (BFS)",
            type = "Solver",
            description = "Explores maze level by level, guaranteeing the shortest path.",
            timeComplexity = "O(n)",
            spaceComplexity = "O(n)",
            characteristics = listOf(
                "✓ Guarantees shortest path",
                "✓ Efficient exploration",
                "✓ Complete - always finds solution",
                "✓ Optimal for unweighted graphs"
            ),
            optimal = true,
            biased = false
        ),
        "Depth-First Search" to AlgorithmDetail(
            name = "Depth-First Search (DFS)",
            type = "Solver",
            description = "Explores deeply into maze branches before backtracking.",
            timeComplexity = "O(n)",
            spaceComplexity = "O(n)",
            characteristics = listOf(
                "✓ Memory efficient with iterative version",
                "✓ Complete - always finds solution",
                "✗ Path is not guaranteed shortest",
                "✗ May explore many unnecessary cells"
            ),
            optimal = false,
            biased = false
        ),
        "A-Star" to AlgorithmDetail(
            name = "A* Algorithm",
            type = "Solver",
            description = "Heuristic-based pathfinding using Manhattan distance to efficiently find paths.",
            timeComplexity = "O(n log n)",
            spaceComplexity = "O(n)",
            characteristics = listOf(
                "✓ Very efficient - uses Manhattan heuristic",
                "✓ Finds optimal/near-optimal paths",
                "✓ Explores fewer cells than BFS",
                "✓ Industry standard for pathfinding"
            ),
            optimal = true,
            biased = false
        )
    )

    fun getGeneratorInfo(name: String) = generators[name]
    fun getSolverInfo(name: String) = solvers[name]
}

/**
 * Display algorithm information modal
 */
fun showAlgorithmInfo(algorithmName: String, type: String) {
    val info = when (type) {
        "Generator" -> AlgorithmDatabase.getGeneratorInfo(algorithmName)
        "Solver" -> AlgorithmDatabase.getSolverInfo(algorithmName)
        else -> return
    } ?: return

    Modal("Algorithm Information", closeButton = true).apply {
        div {
            h5(info.name)
            p(info.description) {
                addCssStyle(io.kvision.core.Style { marginTop = 10.px })
            }

            // Complexity info
            div(className = "algorithm-stats") {
                div("Time Complexity: ${info.timeComplexity}")
                div("Space Complexity: ${info.spaceComplexity}")
                div {
                    if (info.optimal) {
                        span("✓ Optimal solution") {
                            addCssClass("badge badge-success")
                        }
                    }
                    if (!info.biased) {
                        span("✓ Unbiased") {
                            addCssClass("badge badge-info")
                        }
                    }
                }
            }

            // Characteristics
            h5("Characteristics") {
                addCssStyle(io.kvision.core.Style { marginTop = 20.px })
            }
            ul {
                info.characteristics.forEach { char ->
                    li(char)
                }
            }

            button("Close") {
                addCssClass("btn btn-secondary")
            }.onClick {
                // Close modal
            }
        }
    }.show()
}

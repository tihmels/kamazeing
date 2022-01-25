# Kamazeing

A web application visualizing different maze-making and pathfinding algorithms. The project is written entirely in
Kotlin and implemented as a websocket-based client-server application using
the [KVision](https://github.com/rjaros/kvision) web framework.

## Generators

### Aldous Broder

Start anywhere in the grid, choose a random neighbor. Move to that neighbor, if it hasn’t previously been visited, link
it to the prior cell. Repeat until every cell has been visited.

### Binary Tree

For each cell in the grid, decide whether to carve a passage north or east.
Repeat until every cell has been visited.

### Depth-First

Start at a random cell. Mark the current cell as visited, and get a list of its neighbors. For each neighbor, starting
with a randomly selected neighbor: If that neighbor hasn't been visited, remove the wall between this cell and that
neighbor, and then recurse with that neighbor as the current cell.

### Sidewinder

_Similar to the Binary Tree algorithm._

For each cell randomly decide whether to carve a passage leading East.
If the passage is carved add the cell to the current run set.
If the passage is not carved, randomly pick one cell from the route set, carve a passage leading North and empty the current run set

### Wilson

Start at a random cell.
Then choose any unvisited cell in the grid and do a loop-erased random walks until a visited cell is encountered.
Then add the path to the maze, marking as visited each of the cells along that path, and then repeat.
The process repeats until all the cells in the grid have been visited.

## Pathfinders

Note: One can change the start and target position in the grid by dragging them to another position!

### Breath-First

### Depth-First

### A-Star

## Gradle Tasks

### Resource Processing

* generatePotFile - Generates a `src/frontendMain/resources/i18n/messages.pot` translation template file.

### Compiling

* compileKotlinFrontend - Compiles frontend sources.
* compileKotlinBackend - Compiles backend sources.

### Running

* frontendRun - Starts a webpack dev server on port 3000
* backendRun - Starts a dev server on port 8080

### Packaging

* frontendBrowserWebpack - Bundles the compiled js files into `build/distributions`
* frontendJar - Packages a standalone "web" frontend jar with all required files into `build/libs/*.jar`
* backendJar - Packages a backend jar with compiled source files into `build/libs/*.jar`
* jar - Packages a "fat" jar with all backend sources and dependencies while also embedding frontend resources
  into `build/libs/*.jar`

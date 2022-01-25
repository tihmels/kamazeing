# Kamazeing

A web application visualizing different maze-making and pathfinding algorithms.
The project is written entirely in Kotlin and implemented as a websocket-based client-server application using the [KVision](https://github.com/rjaros/kvision) web framework.

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
* jar - Packages a "fat" jar with all backend sources and dependencies while also embedding frontend resources into `build/libs/*.jar`

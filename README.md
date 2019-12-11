# MapNodes [![Github Actions](https://github.com/ewized/MapNodes/workflows/Build/badge.svg)](https://github.com/ewized/MapNodes)

- [Year4000](https://www.year4000.net)
- [Resources](https://resources.year4000.net/)
- [Docs](https://ewized.github.io/MapNodes/)
- [Discord](https://discord.gg/ySj69qR): #year4000

This is the plugin that handles the games that `Year4000` runs.
There is a submodule within this project so its recommended to clone the repo with the following command.
With this in mind you should also have SSH keys linked with your GitHub account.

> git clone --recursive git@github.com:Year4000/MapNodes.git

## Modules

The application is broken into modules that are seperated into their own tasks; `bridge`, `core`, and `sponge`.

### Bridge

The bridge is the underlying bindings from the platform in this case `java` to the game engine `javascript`.
We are using the V8 engine as the JavaScript engine.

### Core

The core is the main game engine, where most of of the game engine is in JavaScript.
While the other languages are shared code for other modules.

### Sponge

This is the implementation of the game engine bindings for the Sponge platform.

## Building

### Gradle

To compile the project we use Gradle and this project contains `gradlew`.
You can compile the entire project with a single command.

> ./gradlew

### Docker

To build the Docker image all you need to do is run the command with `docker-compose`.
Docker compose will build the image with the needed tags and environment vars.
You must also have the compiled version of `MapNodes` before you can build the Docker image.

> docker-compose build

## Running / Development

### Maps

We have included a git submodule for the maps in the directory `run/maps`.
You do not have to keep this directory up to date unless you are testing maps.
Though you do have to have the submodule *inited* and *updated* before you run the development Docker image.

> git submodule init && git submodule update

### Docker

We use Docker to test the MapNodes plugin.
We have added a gradle task that will build the project and run the `docker-compose` image.

> ./gradlew runDocker

## License

MapNodes is copyright &copy; 2019 [Year4000](https://www.year4000.net/)

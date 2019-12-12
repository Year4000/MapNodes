FROM year4000/utilities:spongevanilla as utilities
FROM openjdk:8 AS v8

# Update the system to include needed depends
RUN apt -y update && apt -y upgrade
RUN apt -y install \
    bison \
    cdbs \
    flex \
    curl \
    g++ \
    git \
    python \
    pkg-config -yqq

# Install depot_tools
RUN git clone https://chromium.googlesource.com/chromium/tools/depot_tools.git
ENV PATH="/depot_tools:${PATH}"

# Build v8 https://v8.dev/docs/build
WORKDIR /opt/year4000/mapnodes/bridge/.gradle
RUN fetch v8 && \
    cd v8 && \
    ./tools/dev/gm.py x64.release

# Copy source files
WORKDIR /opt/year4000/mapnodes
COPY . .

# Build mapnodes bridge module with the included v8 install
RUN ./gradlew :bridge:mapnodesSharedLibrary

# Build the mapnodes project
FROM openjdk:8 AS mapnodes

# Build the mapnodes project
WORKDIR /opt/year4000/mapnodes
COPY . .
COPY --from=v8 /opt/year4000/mapnodes/bridge/build/libs/mapnodes/shared/ core/src/generated/resources/
RUN ./gradlew assemble

# Image that runs the server
FROM openjdk:8-alpine AS minecraft

WORKDIR /opt/year4000/minecraft

# Download the Sponge and Minecraft jar
ENV LAUNCH_VERSION=1.12
ENV MC_VERSION=1.12.1
ENV SPONGE_VERSION=${MC_VERSION}-7.0.0-BETA-316
ADD https://libraries.minecraft.net/net/minecraft/launchwrapper/${LAUNCH_VERSION}/launchwrapper-${LAUNCH_VERSION}.jar libraries/net/minecraft/launchwrapper/${LAUNCH_VERSION}/launchwrapper-${LAUNCH_VERSION}.jar
ADD https://repo.spongepowered.org/maven/org/spongepowered/spongevanilla/${SPONGE_VERSION}/spongevanilla-${SPONGE_VERSION}.jar spongevanilla.jar
ADD https://s3.amazonaws.com/Minecraft.Download/versions/${MC_VERSION}/minecraft_server.${MC_VERSION}.jar minecraft_server.${MC_VERSION}.jar

# Copy the defaults into the root of the folder
COPY --from=mapnodes /opt/year4000/mapnodes/sponge/build/libs/mapnodes-*-all.jar mods/mapnodes.jar
COPY --from=utilities /opt/year4000/minecraft/mods/utilities.jar mods/utilities.jar
COPY sponge/docker/ ./

EXPOSE 25565
CMD ["java", "-jar", "spongevanilla.jar"]

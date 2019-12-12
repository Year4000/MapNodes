FROM openjdk:8

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
WORKDIR /opt/mapnodes/bridge/.gradle
RUN fetch v8 && \
    cd v8 && \
    ./tools/dev/gm.py x64.release

RUN ls -lash /opt/mapnodes/bridge/.gradle/v8/out/x64.release/obj

# Copy source files
WORKDIR /opt/mapnodes
COPY . .

# Build mapnodes bridge module with the included v8 install
ENTRYPOINT [ "/opt/mapnodes/gradlew" ]
CMD [ ":bridge:mapnodesSharedLibrary" ]
FROM debian:bullseye-slim

ARG V8_VERSION=8.1.12

RUN apt-get update && apt-get upgrade -yqq

RUN DEBIAN_FRONTEND=noninteractive \
    apt-get install curl \
                    g++ \
                    git \
                    python \
                    pkg-config -yqq

RUN git clone https://chromium.googlesource.com/chromium/tools/depot_tools.git

ENV PATH="/depot_tools:${PATH}"

RUN fetch v8 && \
    cd /v8 && \
    git checkout ${V8_VERSION} && \
    gclient sync && \
    ./build/install-build-deps.sh && \
    ./tools/dev/gm.py x64.release



#!/bin/bash

# Git Config User
git config --global user.email "year4000@year4000.net"
git config --global user.name "Year4000"

# MapNodes git SHA
SHA=$(git log --pretty=format:'%h' -1)

# GAMES var is travis var
git clone $GAMES
cp ./target/MapNodes.jar ./Games/plugins/MapNodes.jar
cd ./Games/
git add ./plugins/MapNodes.jar
git commit -m "Update MapNodes to $SHA"
git push origin master
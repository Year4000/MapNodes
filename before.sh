#!/bin/bash

# Git Config User
git config --global user.email "year4000@year4000.net"
git config --global user.name "Year4000"

# Year4000 Utilities
git clone https://github.com/Year4000/Utilities.git
cd Utilities/
mvn clean install
cd ../

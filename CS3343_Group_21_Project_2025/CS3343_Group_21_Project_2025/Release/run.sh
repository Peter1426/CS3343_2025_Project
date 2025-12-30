#!/bin/bash
# Joker Card Game Startup Script
# This script should be run from the Release directory

# Get the script directory (Release folder)
SCRIPT_DIR="$(cd "$(dirname "${BASH_SOURCE[0]}")" && pwd)"
PROJECT_ROOT="$(cd "$SCRIPT_DIR/.." && pwd)"

# Change to project root for compilation
cd "$PROJECT_ROOT"

echo "Compiling..."
javac -encoding UTF-8 \
    Source/model/*.java \
    Source/service/*.java \
    Source/data/*.java \
    Source/ui/*.java \
    Source/main/Main.java \
    -d . 2>&1

if [ $? -eq 0 ]; then
    echo "Build succeeded!"
    echo "Launching game..."
    echo ""
    java main.Main
else
    echo "Build failed, please check the error messages."
    exit 1
fi


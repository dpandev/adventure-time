#!/bin/bash
# Run the Adventure Time client with a specific worldpack
# Usage: ./run-client.sh [worldpack-name]
# Example: ./run-client.sh jurassic

WORLDPACK="${1:-example}"

# Change to the script's directory (repo root)
SCRIPT_DIR="$(cd "$(dirname "$0")" && pwd)"
cd "$SCRIPT_DIR"

# Build the application if needed
./gradlew :client:installDist --quiet --no-configuration-cache

# Run the application directly from repo root (not through Gradle)
# Working directory stays in repo root so saves go to ./saves/
exec client/build/install/client/bin/client --world="$WORLDPACK"


#!/bin/bash
# Build script to create Balatro.jar release file in Release folder
# This script should be run from the Release directory

echo "Building Balatro release..."

# Get the script directory (Release folder)
SCRIPT_DIR="$(cd "$(dirname "${BASH_SOURCE[0]}")" && pwd)"
PROJECT_ROOT="$(cd "$SCRIPT_DIR/.." && pwd)"

# Ensure we're in the Release directory
cd "$SCRIPT_DIR"

# Clean up old build artifacts
echo "Cleaning old files..."
rm -f Balatro.jar
rm -rf build-temp/

# Create temporary build directory
mkdir -p build-temp

# Compile all Java source files from project root
echo "Compiling Java source files..."
javac -encoding UTF-8 -d build-temp \
    "$PROJECT_ROOT"/Source/model/*.java \
    "$PROJECT_ROOT"/Source/service/*.java \
    "$PROJECT_ROOT"/Source/data/*.java \
    "$PROJECT_ROOT"/Source/ui/*.java \
    "$PROJECT_ROOT"/Source/main/Main.java

if [ $? -ne 0 ]; then
    echo "Error: Compilation failed!"
    rm -rf build-temp/
    exit 1
fi

echo "Compilation successful!"

# Create MANIFEST.MF with Main-Class entry
echo "Creating manifest..."
mkdir -p build-temp/META-INF
cat > build-temp/META-INF/MANIFEST.MF << EOF
Manifest-Version: 1.0
Main-Class: main.Main
EOF

# Package into JAR file in current directory (Release folder)
echo "Packaging JAR file..."
cd build-temp
jar cfm ../Balatro.jar META-INF/MANIFEST.MF *
cd ..

# Clean up temporary files
echo "Cleaning up..."
rm -rf build-temp/

echo "Build complete!"
echo "JAR file created: Balatro.jar"
echo ""
echo "To run the game:"
echo "  java -jar Balatro.jar          (GUI mode)"
echo "  java -jar Balatro.jar --cli    (CLI mode)"


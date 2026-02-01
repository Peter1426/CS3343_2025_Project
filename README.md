# 🃏Balatro-Style Joker Card Game

## Project Overview  

*This project was developed by a team of 6 students for **CS3343 Software Engineering Practice** at City University of Hong Kong. It is a Java-based roguelike poker game inspired by *Balatro*, featuring GUI/CLI interfaces, a joker card system, and a shop system. Players combine joker cards to modify scoring rules, achieve high scores within limited rounds, and visit the shop between rounds to adjust their deck.*

### My Role / Contributions:
- **Role:** Testing Engineer & Assistant Developer  
- **Testing:** Wrote **1/3 of all test cases** using JUnit (285 test cases in total) for core classes including `AnimatedButton`, `GameWindow`, `Main`, and UI components.
- **Bug Investigation & Fixing:**  
  - Fixed joker card logic bugs (e.g., *Ace Master* bonus not affected by multipliers, level 10 completion bug).  
  - Debugged game logic and UI issues (e.g., *Chaos Joker* incorrect stacking, game window size glitch).
- **Requirements & Design:**  
  - Contributed to **use case diagrams** and **use case specifications**.  
  - Assisted in **code refactoring** (extracted `GameLogicManager` and `DialogManager` from `GameWindow`).
- **Quality Assurance:**  
  - Applied **MC/DC coverage** principles and used **EclEmma** for coverage analysis.  
  - Tracked bugs via **GitHub Issues** with proper labeling and status workflows.

### Technologies Used:
- **Language:** Java  
- **Testing:** JUnit 5, EclEmma  
- **Tools:** Eclipse IDE, Git, GitHub  
- **GUI:** Java Swing  
- **Methodology:** Prototype model, Bottom-up testing, MC/DC coverage

### 🎮 Screenshots

| Start Screen | Gameplay |
|--------------|----------|
| <img width="786" height="593" alt="StartScreen" src="https://github.com/user-attachments/assets/4951522c-ef13-4761-971f-cc4cc5339353" /> | <img width="948" height="634" alt="Gameplay" src="https://github.com/user-attachments/assets/63882491-52c0-4973-a010-6ad4ebc7ac44" />
|

| Shop Interface | CLI mode |
|-------------|----------------|
| <img width="506" height="493" alt="CardShop" src="https://github.com/user-attachments/assets/2fa76feb-d902-4627-9208-f1e38fe2fefe" /> | <img width="892" height="502" alt="CLImode" src="https://github.com/user-attachments/assets/83168732-99d2-4098-8887-55a2caef986a" /> |

## Game Rules

### Basic Gameplay Round

- **Rounds**: 10 rounds per level (10 levels total)
- **Hand Size**: 5 cards (can be modified by joker cards)
- **Cards Per Round**: 5 cards drawn
- **Target Score**: Varies by level (50 to 800 points)

### Game Flow

1. At the start of each level, you randomly receive 3 joker cards
2. Each round, draw cards from the deck to fill your hand
3. Select 1-5 cards to form a poker hand and play them
4. The system calculates score based on hand type base score + joker card effects
5. If the current level doesn't reach the target score within the limited rounds, you fail immediately and cannot skip the level
6. When the level score reaches the target (and you haven't completed all levels), a "Skip Level" button appears, allowing you to advance early
7. In GUI mode, each round you can use "Discard" and "Draw" buttons, with a maximum of 2 card operations per round (discard removes cards, draw copies selected cards)
8. You can enter the shop from the main menu at any time to acquire new jokers or adjust your deck
9. Unused cards can be kept for the next round (up to hand size limit)
10. Achieve the target score to complete the level

### Hand Type Scoring

- **High Card**: 5 points
- **Pair**: 15 points
- **Two Pair**: 35 points
- **Three of a Kind**: 70 points
- **Straight**: 120 points
- **Flush**: 150 points
- **Full House**: 250 points
- **Four of a Kind**: 400 points
- **Straight Flush**: 600 points
- **Royal Flush**: 800 points

# Joker Card System

Each joker card has unique effects that modify scoring rules or game mechanics. Effects automatically stack.

### Available Joker Cards (14 cards)

1. \*Double Vision\*\*

   - All scores ×2

2. \*Greedy Joker\*\*

   - First play each round: score ×3
   - Subsequent plays: score ÷2

3. \*The Gambler\*\*

   - If hand contains only high cards (no pairs), gain 200 points

4. \*Flower Child\*\*

   - Club cards are treated as wild cards (can be any suit/rank)

5. \*Copycat\*\*

   - Three of a kind and above: score ×2

6. \*Chaos Joker\*\*

   - All scores +30%

7. \*Mirror Joker\*\*

   - Flush hands: score ×1.5

8. \*Rogue Joker\*\*

   - Cannot play pairs or lower
   - Three of a kind and above: score ×3

9. \*Red Card Joker\*\*

   - Red cards: score ×2

10. \*Ace Master\*\*

    - Each Ace played: +50 points

11. \*Straight Runner\*\*

    - Straight, Straight Flush, Royal Flush: score ×2

12. \*Lucky Sevens\*\*

    - Each 7 played: +70 points

13. \*Draw Maestro\*\*

    - Draw +1 card when replenishing

14. \*Pocket Collector\*\*
    - Hand size +1

## How to Run The balatro card game

### ⚠️ Important Notes

### Getting Started from GitHub

If you're using this project for the first time, follow these steps:

```bash
# 1. Clone the repository
git clone https://github.com/HenryKWOK77/cs3343-project21.git
cd cs3343-project21

# 2. Verify you have the following:
#    - run.sh (run script)
#    - src/ directory (contains all Java source files)
#    - Release/ directory (contains build scripts and JAR file)
```

### System Requirements

- **Java**: JDK 8 or higher (**must have `javac` compiler**)
- **Operating System**: macOS, Linux, or Windows
- Verify Java installation:
  ```bash
  java -version
  javac -version    # This is important! Must have javac to compile
  ```

### Quick Start

**Method 1: Using Run Script (Easiest)**

From project root directory:

```bash
# macOS/Linux
chmod +x run.sh    # Add execute permission (first time only)
./run.sh

# Windows or any system
bash run.sh
```

This script will automatically:

1. Compile all Java source files
2. Launch the game (default: GUI mode)

**Method 2: Using Release Scripts**

For development/testing from Release folder:

```bash
cd Release
chmod +x run.sh build-release.sh    # macOS/Linux only
./run.sh                            # Compile and run (development mode)
```

**Method 3: Build Release Version**

To create a distributable JAR file:

**macOS/Linux:**

```bash
cd Release
./build-release.sh                  # Builds Balatro.jar
java -jar Balatro.jar              # Run the JAR (GUI mode)
java -jar Balatro.jar --cli        # Run the JAR (CLI mode)
```

**Windows:**

```cmd
cd Release
build-release.bat                   # Builds Balatro.jar
Balatro.bat                         # Run the JAR (GUI mode)
Balatro.bat --cli                   # Run the JAR (CLI mode)
```

**Method 4: Manual Compilation (If scripts don't work)**

```bash
# Important: Must compile before running!

# Step 1: Compile all source files (including UI components)
javac -encoding UTF-8 src/model/*.java src/service/*.java src/data/*.java src/ui/*.java src/main/Main.java -d .

# Step 2: Run the game (only after successful compilation)
java main.Main          # Launch GUI (default)
# or
java main.Main --cli    # Launch CLI version
```

**⚠️ Common Errors:**

- ❌ `Error: Could not find or load main class Main` → Not compiled yet, run the `javac` command above. Note: Use `java main.Main` (not `java Main`) since Main is in the `main` package.
- ❌ `Error: Could not find or load main class main.Main` → Make sure you compiled with the correct path: `src/main/Main.java`
- ✅ After successful compilation, you'll see many `.class` files in the project directory (this is normal)

### Two Running Modes

1. **Graphical Interface Mode (Default)**

   - Run `./run.sh` or `java main.Main`
   - Use mouse and keyboard for interaction
   - More intuitive and visually appealing interface

2. **Command Line Interface Mode**
   - Run `java main.Main --cli`
   - Use keyboard to input number options
   - Suitable for terminal environments

### Windows Users

** Important for Windows Users:**

Windows cannot execute `.sh` scripts directly. Follow these steps:

**Step 1: Build the JAR file**

First, you need to build `Balatro.jar`. You have two options:

**Option A: Use the Windows build script (Recommended)**

```cmd
cd Release
build-release.bat
```

This will compile the source code and create `Balatro.jar` in the Release folder.

**Option B: Manual build (If build script doesn't work)**

Open Command Prompt (CMD) or PowerShell in the project root directory and run:

```cmd
cd Release
javac -encoding UTF-8 -d build-temp ..\src\model\*.java ..\src\service\*.java ..\src\data\*.java ..\src\ui\*.java ..\src\main\Main.java

mkdir build-temp\META-INF
echo Manifest-Version: 1.0 > build-temp\META-INF\MANIFEST.MF
echo Main-Class: main.Main >> build-temp\META-INF\MANIFEST.MF

cd build-temp
jar cfm ..\Balatro.jar META-INF\MANIFEST.MF *
cd ..
rmdir /s /q build-temp
```

**Step 2: Run the game**

After `Balatro.jar` is created, you can run the game:

```cmd
cd Release
Balatro.bat        # Run JAR file (GUI mode)
Balatro.bat --cli   # Run JAR file (CLI mode)
```

**Troubleshooting:**

- ❌ `Error: Balatro.jar not found in current directory` → You need to build the JAR first using `build-release.bat`
- ❌ `Error: Java is not installed` → Install Java JDK 8 or higher and add it to PATH
- ❌ `'javac' is not recognized` → Make sure JDK (not just JRE) is installed and in PATH
- ❌ `Error: Could not find or load main class main.Main` → The JAR was built incorrectly. Rebuild using `build-release.bat` (this has been fixed in the latest version)
- ❌ `Error: Invalid filename` (Windows) → Make sure you're using the latest `build-release.bat` which handles paths with spaces and special characters correctly

## 📁 Project Structure

```
Balatro/
├── src/
│   ├── main/
│   │   └── Main.java            # Main program entry
│   ├── model/
│   │   ├── Card.java            # Playing card model
│   │   ├── Hand.java            # Hand management
│   │   └── Joker.java           # Joker card model and effect system
│   ├── service/
│   │   ├── HandEvaluator.java   # Hand type evaluation
│   │   ├── ScoreCalculator.java # Scoring system
│   │   └── GameLogicManager.java # Game logic management
│   ├── data/
│   │   └── JokerDeck.java       # Joker card database
│   ├── ui/
│   │   ├── GameWindow.java      # Main game window
│   │   ├── StartScreen.java     # Start screen
│   │   ├── CardPanel.java        # Card display component
│   │   ├── AnimatedButton.java   # Animated button component
│   │   ├── ScoreAnimation.java   # Score animation effect
│   │   └── DialogManager.java    # Dialog management
│   └── tests/
│       ├── TestCard.java         # Card tests
│       ├── TestHand.java         # Hand tests
│       ├── TestJoker.java        # Joker tests
│       └── ...                   # Other test files
├── Release/
│   ├── Balatro.jar              # Compiled JAR file
│   ├── Balatro.bat              # Windows launcher script
│   ├── build-release.bat        # Windows build script (creates JAR)
│   ├── build-release.sh         # macOS/Linux build script (creates JAR)
│   └── run.sh                   # Development run script (macOS/Linux)
├── run.sh                       # Root-level run script
└── README.md
```

## Shop System

- **Access**: CLI main menu option "Enter Shop" anytime; GUI top info bar has "🛒 Shop" button
- **Random Jokers**: Display up to 3 unowned jokers, choose 1 to add (takes effect immediately)
- **Remove Jokers**: Select and remove currently owned joker cards, immediately update all effects
- **Purchase Limit**: Maximum 1 new joker card per round/level (removal unlimited)
- **Instant Feedback**: Adding/removing immediately recalculates hand size, draw count, and display panel

### Running Tests

```bash
# Compile test files
javac -encoding UTF-8 -cp .:src src/tests/*.java

# Run specific test (example)
java -cp .:src tests.TestCard
```

### Building Release

**macOS/Linux:**

```bash
cd Release
./build-release.sh    # Creates Balatro.jar in Release folder
```

**Windows:**

```cmd
cd Release
build-release.bat     # Creates Balatro.jar in Release folder
```

**Note:** The build scripts automatically set the correct Main-Class (`main.Main`) in the JAR manifest. If you manually build, make sure to use `Main-Class: main.Main` in the MANIFEST.MF file.

## License

This project is part of a course assignment (CS3343 Project 21).

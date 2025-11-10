#!/bin/bash
# Demo script for Adventure Time game showcasing all features
# This script demonstrates the game's capabilities using the example.json worldpack
# Features demonstrated:
# - Movement (GO)
# - Looking around (LOOK)
# - Inspecting items and rooms (INSPECT)
# - Inventory management (PICKUP, DROP, INVENTORY)
# - Equipment system (EQUIP, UNEQUIP)
# - Combat (ATTACK, IGNORE)
# - Healing and consumables (USE, HEAL)
# - Puzzles (SOLVE)
# - Map viewing (MAP)
# - Stats viewing (STATS)
# - Save/Load system

SCRIPT_DIR="$(cd "$(dirname "$0")" && pwd)"
cd "$SCRIPT_DIR"

# Colors for output
GREEN='\033[0;32m'
BLUE='\033[0;34m'
YELLOW='\033[1;33m'
CYAN='\033[0;36m'
NC='\033[0m' # No Color

echo -e "${CYAN}╔════════════════════════════════════════════════════════════════╗${NC}"
echo -e "${CYAN}║         Adventure Time - Interactive Game Demo                ║${NC}"
echo -e "${CYAN}║                 Example World Showcase                         ║${NC}"
echo -e "${CYAN}╚════════════════════════════════════════════════════════════════╝${NC}"
echo ""
echo -e "${YELLOW}This demo will showcase all game features using the example worldpack.${NC}"
echo -e "${YELLOW}Press ENTER to continue through each section...${NC}"
echo ""
read -p "Press ENTER to start the demo..."

# Function to show section header
show_section() {
    echo ""
    echo -e "${GREEN}═══════════════════════════════════════════════════════════${NC}"
    echo -e "${GREEN}  $1${NC}"
    echo -e "${GREEN}═══════════════════════════════════════════════════════════${NC}"
    echo ""
    read -p "Press ENTER to continue..."
}

# Function to explain and execute a command
run_command() {
    echo -e "${BLUE}→ Explanation: $1${NC}"
    echo -e "${CYAN}→ Command: $2${NC}"
    echo ""
}

# Build the game first
echo -e "${YELLOW}Building the game...${NC}"
./gradlew :client:installDist --quiet --no-configuration-cache

# Create the demo input commands
cat > /tmp/adventure_demo_commands.txt << 'EOF'
help
look
stats
inventory
pickup mystic_key
pickup steel_sword
pickup iron_helmet
inventory
inspect mystic_key
inspect steel_sword
equip steel_sword
equip iron_helmet
stats
look
north
look
map
inspect ice_troll
attack
stats
use healing_potion
pickup ice_shard
north
look
solve echo
look
pickup healing_potion
south
east
look
inspect ancient_scroll
pickup ancient_scroll
pickup healing_potion
attack
ignore
north
look
solve fire
look
pickup frozen_crown
pickup frostbite_blade
pickup ice_plate
pickup frost_helm
pickup greater_healing_potion
inventory
unequip steel_sword
equip frostbite_blade
unequip iron_helmet
equip frost_helm
equip ice_plate
stats
south
west
south
west
look
attack
stats
heal greater_healing_potion
stats
north
north
look
attack
stats
map
save
quit
EOF

echo ""
show_section "SECTION 1: Game Startup & Help"
run_command "The game starts and shows welcome message and initial room" "Starting game..."
echo ""
echo -e "${YELLOW}Commands that will be demonstrated:${NC}"
echo "  - help    : Show available commands"
echo "  - look    : Examine current room"
echo "  - stats   : View player statistics"
echo ""

show_section "SECTION 2: Inventory Management"
echo -e "${YELLOW}Demonstrating:${NC}"
echo "  - inventory : Check what you're carrying"
echo "  - pickup    : Pick up items from the ground"
echo "  - inspect   : Examine items closely"
echo "  - drop      : Drop items (not shown, but available)"
echo ""

show_section "SECTION 3: Equipment System"
echo -e "${YELLOW}Demonstrating:${NC}"
echo "  - equip   : Equip weapons and armor"
echo "  - unequip : Remove equipped items"
echo "  - stats   : See how equipment affects your stats"
echo ""

show_section "SECTION 4: Movement & Exploration"
echo -e "${YELLOW}Demonstrating:${NC}"
echo "  - go north/south/east/west : Move between rooms"
echo "  - look                     : Examine your surroundings"
echo "  - map                      : View the world map"
echo ""

show_section "SECTION 5: Combat System"
echo -e "${YELLOW}Demonstrating:${NC}"
echo "  - inspect monster : Examine enemy before fighting"
echo "  - attack         : Engage in combat"
echo "  - ignore         : Avoid combat (if possible)"
echo "  - stats          : Monitor your health during combat"
echo ""

show_section "SECTION 6: Puzzle Solving"
echo -e "${YELLOW}Demonstrating:${NC}"
echo "  - solve [answer] : Solve riddles and puzzles"
echo "  - Rewards        : Get items for solving puzzles"
echo ""

show_section "SECTION 7: Healing & Consumables"
echo -e "${YELLOW}Demonstrating:${NC}"
echo "  - use [item] : Use consumable items"
echo "  - heal [item]: Restore health with potions"
echo ""

show_section "SECTION 8: Save System"
echo -e "${YELLOW}Demonstrating:${NC}"
echo "  - save : Save your game progress"
echo "  - quit : Exit the game"
echo "  - load : (Available on startup to restore saves)"
echo ""

echo ""
echo -e "${GREEN}════════════════════════════════════════════════════════${NC}"
echo -e "${GREEN}  Starting Interactive Demo...${NC}"
echo -e "${GREEN}════════════════════════════════════════════════════════${NC}"
echo ""
read -p "Press ENTER to begin the gameplay demonstration..."
echo ""

# Run the actual game with the demo commands
echo -e "${CYAN}Running Adventure Time with demo commands...${NC}"
echo -e "${YELLOW}(Game output will be shown below)${NC}"
echo ""
echo "━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━"
echo ""

cat /tmp/adventure_demo_commands.txt | ./client/build/install/client/bin/client --world=example

echo ""
echo "━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━"
echo ""
echo -e "${GREEN}╔════════════════════════════════════════════════════════════════╗${NC}"
echo -e "${GREEN}║                    Demo Complete!                              ║${NC}"
echo -e "${GREEN}╚════════════════════════════════════════════════════════════════╝${NC}"
echo ""
echo -e "${YELLOW}Features Demonstrated:${NC}"
echo "  ✓ Help system and command reference"
echo "  ✓ Room exploration with detailed descriptions"
echo "  ✓ Item inspection and pickup mechanics"
echo "  ✓ Equipment system (weapons and armor)"
echo "  ✓ Character stats and attributes"
echo "  ✓ Combat encounters with multiple monsters"
echo "  ✓ Puzzle solving with riddles"
echo "  ✓ Consumable items and healing"
echo "  ✓ World navigation and mapping"
echo "  ✓ Save/Load game functionality"
echo ""
echo -e "${CYAN}Game Features:${NC}"
echo "  • Dynamic combat with critical hits"
echo "  • Equipment bonuses affect stats"
echo "  • Multiple item types (weapons, armor, consumables, quest items)"
echo "  • Puzzle rewards and progression"
echo "  • Persistent save system"
echo "  • Rich world with interconnected rooms"
echo ""
echo -e "${YELLOW}Available Commands:${NC}"
echo "  Movement  : go [direction], north, south, east, west"
echo "  Look      : look, inspect [target], map"
echo "  Items     : pickup [item], drop [item], inventory"
echo "  Equipment : equip [item], unequip [item]"
echo "  Combat    : attack, ignore"
echo "  Healing   : use [item], heal [item]"
echo "  Puzzles   : solve [answer]"
echo "  System    : stats, help, save, load, quit"
echo ""
echo -e "${GREEN}Try exploring the world yourself:${NC}"
echo "  ./run-client.sh example"
echo ""
echo -e "${CYAN}Or try the Jurassic Park worldpack:${NC}"
echo "  ./run-client.sh jurassic"
echo ""

# Clean up
rm -f /tmp/adventure_demo_commands.txt


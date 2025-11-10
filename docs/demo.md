# Adventure Time Demo Scripts

This directory contains automated demo scripts that showcase all features of the Adventure Time game using the example worldpack.

## Available Demo Scripts

### Unix/Linux/macOS: `demo.sh`
```bash
./demo.sh
```

### Windows: `demo.bat`
```batch
demo.bat
```

## What the Demo Showcases

The demo script runs through a complete gameplay session demonstrating:

### 1. **Game Startup & Help System**
- Welcome message and initial room description
- `help` - Display all available commands
- `look` - Examine your surroundings
- `stats` - View player statistics

### 2. **Inventory Management**
- `inventory` - Check what you're carrying
- `pickup [item]` - Pick up items from rooms
- `inspect [item]` - Examine items in detail
- `drop [item]` - Drop unwanted items

### 3. **Equipment System**
- `equip [weapon/armor]` - Equip gear to boost stats
- `unequip [item]` - Remove equipped items
- Demonstrates how equipment affects attack/defense stats
- Shows different armor types (helmet, chestplate, leggings, boots)

### 4. **Movement & Exploration**
- `go [direction]` or `north/south/east/west` - Navigate between rooms
- `look` - Re-examine your current location
- `map` - View the world layout and connections
- Explores multiple interconnected rooms

### 5. **Combat System**
- `inspect [monster]` - Examine enemies before fighting
- `attack` - Engage in combat with monsters
- `ignore` - Attempt to avoid combat
- Demonstrates:
  - Health tracking during battles
  - Critical hits and combat mechanics
  - Equipment bonuses in action
  - Multiple monster encounters

### 6. **Puzzle Solving**
- `solve [answer]` - Answer riddles and puzzles
- Demonstrates two puzzle types:
  - **Riddle 1**: "I speak without a mouth..." → Answer: `echo`
  - **Riddle 2**: "I am always hungry..." → Answer: `fire`
- Shows puzzle rewards (items)

### 7. **Healing & Consumables**
- `use [item]` - Use consumable items
- `heal [potion]` - Restore health with potions
- Demonstrates:
  - Regular Healing Potion (20 HP)
  - Greater Healing Potion (50 HP)
  - Health management strategy

### 8. **Save System**
- `save` - Save your current game progress
- `quit` - Exit the game
- `load` - Restore a saved game (shown at startup)

## Demo Flow

The demo follows this path through the example world:

```
Frozen Gate (entrance)
    ↓ north
Crystal Hall (fight Ice Troll)
    ↓ north
Ice Cavern (solve riddle: "echo")
    ↓ back south, then east
Chamber of Secrets (fight Frost Sprite)
    ↓ north
Treasure Vault (solve riddle: "fire", get legendary items)
    ↓ back to explore
Frozen Library (fight Ice Wraith)
    ↓ north
Frost Throne Room (final boss: Glacier Golem)
```

## Items Collected During Demo

### Weapons
- Steel Sword (Attack +10) → upgraded to
- Frostbite Blade (Attack +18)

### Armor
- Iron Helmet (Defense +5) → upgraded to
- Frost Helm (Defense +8)
- Ice Plate Armor (Defense +12)

### Consumables
- Healing Potion (restores 20 HP)
- Greater Healing Potion (restores 50 HP)

### Quest Items
- Mystic Key
- Ice Shard
- Ancient Scroll
- Frozen Crown (puzzle reward)

## Running the Demo

### Quick Start

**macOS/Linux:**
```bash
./demo.sh
```

**Windows:**
```batch
demo.bat
```

### What to Expect

1. The script will build the game automatically
2. Each major feature section is explained before demonstration
3. You'll see the actual game output as commands are executed
4. The full playthrough takes about 2-3 minutes
5. At the end, you'll see a summary of all demonstrated features

### Interactive Pauses

The demo includes pauses between sections so you can:
- Read the explanations of what's about to happen
- Understand which features are being demonstrated
- See the connection between commands and results

Press ENTER to continue through each section.

## After the Demo

Once you've seen the demo, try playing yourself:

**macOS/Linux:**
```bash
# Play the example world
./run-client.sh example

# Play the Jurassic Park world
./run-client.sh jurassic
```

**Windows:**
```batch
# Play the example world
run-client.bat example

# Play the Jurassic Park world
run-client.bat jurassic
```

## Command Reference

### Movement
- `go [direction]` - Move in a direction
- `north`, `south`, `east`, `west` - Quick directional movement

### Observation
- `look` - Examine current room
- `inspect [target]` - Examine item or monster
- `map` - View world layout

### Inventory
- `inventory` - List items you're carrying
- `pickup [item]` - Pick up an item
- `drop [item]` - Drop an item

### Equipment
- `equip [item]` - Equip weapon or armor
- `unequip [item]` - Remove equipped item

### Combat
- `attack` - Attack the monster in the room
- `ignore` - Try to avoid fighting

### Healing
- `use [item]` - Use a consumable item
- `heal [item]` - Use a healing potion

### Puzzles
- `solve [answer]` - Answer a puzzle/riddle

### System
- `stats` - View your current statistics
- `help` - Show available commands
- `save` - Save your game
- `load` - Load a saved game (at startup)
- `quit` - Exit the game

## Worldpack Information

### Example World
- Theme: Frozen/Ice realm
- Rooms: 10 interconnected locations
- Monsters: 6 different enemies (from Frost Sprite to Glacier Golem)
- Items: 17 items (weapons, armor, consumables, quest items)
- Puzzles: 2 riddles with rewards

### Jurassic World
- Theme: Jurassic Park
- Dinosaurs as monsters
- Prehistoric themed items and locations

## Tips for Exploration

1. **Always inspect before attacking** - Know your enemy's stats
2. **Equip better gear** - Higher stats = easier combat
3. **Solve puzzles early** - Get powerful rewards
4. **Manage health** - Keep healing potions in inventory
5. **Use the map** - Plan your route through the world
6. **Save often** - Don't lose progress

## Technical Details

- Demo automatically builds the game before running
- Commands are piped from a temporary file
- Game runs in non-interactive mode for the demo
- All saves are stored in `./saves/` directory
- Demo works with both example and jurassic worldpacks

## Troubleshooting

### "Permission denied" error
```bash
chmod +x demo.sh
chmod +x run-client.sh
```

### Windows: Script not running
- Make sure you're running from the project root directory
- Ensure Java is installed and in your PATH
- Try running `gradlew.bat :client:build` first

### Out of Memory
- The demo uses the same JVM settings as normal gameplay
- Already configured for 2GB heap in `gradle.properties`

## Creating Your Own Demo

You can modify the demo by editing the commands in the script:

1. Open `demo.sh` or `demo.bat`
2. Find the section that creates `adventure_demo_commands.txt`
3. Add, remove, or modify commands
4. Save and run the demo again

Example commands to add:
- Different movement paths
- More combat encounters
- Additional puzzle attempts
- Item management scenarios


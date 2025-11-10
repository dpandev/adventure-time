# Player Quickstart Guide

Welcome to Adventure Time! This guide will get you started on your adventure.

---

## Getting Started

### Running the Game

**Option 1: Using Gradle (Recommended)**
```bash
./gradlew :client:run
```

**Option 2: Using a pre-built JAR**
```bash
java -jar adventure-time-client.jar
```

---

## Your First Steps

### 1. Start the Game
When you launch the game, you'll see a welcome message and your starting location.

### 2. Look Around
Type `look` to see where you are:
```
> look
You are in Village Square.
A bustling town square with merchants and travelers.
You see: rusty sword
Exits: north, east, west
```

### 3. Pick Up Items
Grab useful items you find:
```
> pickup rusty sword
You picked up: Rusty Sword
```

### 4. Check Your Inventory
See what you're carrying:
```
> inventory
You are carrying:
  - Rusty Sword
```

### 5. Equip Items
Equip items to get their bonuses:
```
> equip rusty sword
You equipped: Rusty Sword

> stats
=== Hero's Stats ===
Health: 100/100
Attack: 15 (Base: 10)
...
```

### 6. Explore
Move to different rooms:
```
> go north
> n
> north
```

---

## Essential Commands

Here are the most important commands to get started:

| Command | What It Does |
|---------|--------------|
| `help` | Show all available commands |
| `look` | Look around the current room |
| `go <direction>` | Move (north, south, east, west, up, down) |
| `inventory` (or `i`) | Check your inventory |
| `pickup <item>` | Pick up an item |
| `equip <item>` | Equip an item for bonuses |
| `inspect <item>` | Examine an item or monster |
| `attack <monster>` | Fight a monster |
| `stats` | View your character stats |
| `save` | Save your game |
| `quit` | Save and exit |

**For the complete command list, see [Commands Guide](commands.md)**

---

## Combat Basics

When you encounter a monster:

### Option 1: Fight
```
> look
There is a Goblin here! (HP: 30/30)

> inspect goblin
Goblin: A small creature...
[HP: 30/30, Attack: 15, Defense: 5]

> attack goblin
You engage in combat with the Goblin!
```

### Option 2: Ignore
```
> ignore goblin
You choose to ignore the Goblin and it disappears forever.
```

**During Combat:**
- `attack` - Attack the monster
- `heal` - Use a health potion
- `stats` - Check HP
- `equip`/`unequip` - Change gear mid-battle

---

## Saving Your Game

### Auto-Save
The game automatically saves when you type `quit`.

### Manual Save
Save anytime with:
```
> save
Game saved successfully.
```

### Loading
Restart from your last save:
```
> load
```

**Tip:** Save before difficult battles or important decisions!

---

## Tips for New Players

1. **Inspect Everything** - Use `inspect` on items and monsters to see their stats
2. **Equip Your Gear** - Don't forget to equip items you pick up!
3. **Save Often** - Save before fights and after completing objectives
4. **Check Your Stats** - Use `stats` to see your current HP and bonuses
5. **Read Descriptions** - Room and item descriptions often contain hints
6. **Manage Your Health** - Pick up health potions and use them when needed
7. **Scout Before Fighting** - Inspect monsters before attacking to see if you're ready

---

## Example Play Session

```
> look
You are in Dark Cave.
A damp cave with stalactites.
There is a Goblin here! (HP: 30/30)
You see: health potion

> pickup health potion
You picked up: Health Potion

> inspect goblin
Goblin: A small, green creature.
[HP: 30/30, Attack: 15, Defense: 5]

> stats
=== Hero's Stats ===
Health: 100/100
Attack: 10 (Base: 10)
Defense: 0 (Base: 0)

> attack goblin
You engage in combat with the Goblin!
=== COMBAT STARTED ===
─────────────────────────────
Player HP: 100/100
Goblin HP: 30/30
─────────────────────────────

> attack
You attack the Goblin for 5 damage!
The Goblin attacks you for 15 damage!

> attack
You attack the Goblin for 5 damage!
*** You have defeated the Goblin! ***

> save
Game saved successfully.

> go north
...
```

---

## Where to Find Things

- **Saved Games** - Stored in `/saves/` directory
- **World Content** - Game worlds are in `client/src/main/resources/worldpacks/`

---

## Need Help?

- **In-Game Help** - Type `help` anytime
- **Full Command List** - See [Commands Guide](commands.md)
- **Troubleshooting** - See [FAQ](faq.md)
- **Developer Docs** - See [Dev Setup](../dev/dev-setup.md)

---

## Ready to Play?

Start your adventure with:
```bash
./gradlew :client:run
```

Good luck, adventurer!



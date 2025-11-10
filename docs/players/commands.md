# Player Commands Guide

Complete reference for all available commands in Adventure Time.

---

## Navigation Commands

### `look` (alias: `l`)
Look around your current location to see the room description, items, monsters, and available exits.

**Example:**
```
> look
You are in Dark Cave.
A damp, dark cave with stalactites hanging from the ceiling.
There is a Goblin here! (HP: 30/30)
You see: rusty sword, health potion
Exits: north, south
```

### `go <direction>` (alias: `move`)
Move to an adjacent room in the specified direction.

**Directions:** `north` (n), `south` (s), `east` (e), `west` (w), `up` (u), `down` (d)

**Examples:**
```
> go north
> n
> go up
```

**Shortcut:** You can type just the direction without "go":
```
> north
> s
```

### `map`
Display a visual map showing your current location and all adjacent rooms with their connecting exits.

**Example:**
```
> map

╔════════════════════════════════════════════╗
║                  AREA MAP                  ║
╠════════════════════════════════════════════╣

                    ↑ NORTH
              Frozen Library
                    |
Ice Cave        ← [ Ice Throne Room ] →      Frozen Lake
                    |
              Glacier Depths
                    ↓ SOUTH

╚════════════════════════════════════════════╝

  [ ] = Current Location
  Available exits: east, north, south, west
```

**Note:** The map shows rooms directly connected to your current location. Your current room is highlighted with brackets `[ ]`.

---

## Inventory Commands

### `inventory` (alias: `i`)
Display all items currently in your inventory.

**Example:**
```
> inventory
You are carrying:
  - Steel Sword
  - Health Potion
  - Iron Helmet
```

### `pickup <item>` (aliases: `take`, `grab`)
Pick up an item from the current room and add it to your inventory.

**Examples:**
```
> pickup rusty sword
> take health potion
> grab iron helmet
```

### `drop <item>` (alias: `discard`)
Drop an item from your inventory into the current room.

**Example:**
```
> drop rusty sword
```

### `inspect <item>` (alias: `examine`)
Examine an item in detail to see its description and bonuses.

**Examples:**
```
> inspect steel sword
Steel Sword: A sharp blade for combat.
[+15 Attack]

> inspect iron helmet
Iron Helmet: A sturdy iron helmet.
[+3 Defense]

> inspect health potion
Health Potion: A red potion that restores health.
[Restores 20 HP]
```

**Note:** You can inspect items in your inventory or items in the current room.

---

## Equipment Commands

### `equip <item>`
Equip an item from your inventory to gain its bonuses.

**Equipment Slots:**
- **Weapon** - Increases attack damage
- **Helmet** - Increases defense
- **Chestplate** - Increases defense
- **Leggings** - Increases defense
- **Boots** - Increases defense

**Examples:**
```
> equip steel sword
> equip iron helmet
```

### `unequip <item>`
Remove an equipped item and return it to your inventory.

**Example:**
```
> unequip steel sword
```

**Note:** You can change equipment even during combat!

---

## Combat Commands

### `attack <monster>` (alias: `fight`)
Initiate combat with a monster in your current room.

**Examples:**
```
> attack goblin
> fight Ice Troll
> attack frost sprite
```

**Combat Flow:**
1. You attack the monster (damage = your attack - monster defense)
2. Monster counter-attacks (damage = monster attack - your defense)
3. Repeat until one of you dies

**During Combat:**
- Use `attack` to attack the monster
- Use `heal` to drink a health potion
- Use `stats` to check your HP and the monster's HP
- Use `inventory`, `equip`, `unequip` to manage your gear

**Critical Hits:** Monsters have a chance to deal **2x damage** on their attacks!

**Example Combat:**
```
> attack goblin
You engage in combat with the Goblin!
=== COMBAT STARTED ===
─────────────────────────────
Player HP: 100/100
Goblin HP: 30/30
─────────────────────────────

> attack
You attack the Goblin for 12 damage!
The Goblin attacks you for 8 damage!

─────────────────────────────
Player HP: 92/100
Goblin HP: 18/30
─────────────────────────────

> attack
You attack the Goblin for 12 damage!
*** You have defeated the Goblin! ***
Victory! You may continue your adventure.
```

### `ignore <monster>` (aliases: `flee`, `run`)
Choose to ignore a monster encounter. The monster will **permanently disappear** from the room.

**Important:** You can only ignore a monster **before** starting combat. Once combat begins, you cannot flee!

**Example:**
```
> look
There is a Goblin here! (HP: 30/30)

> ignore goblin
You choose to ignore the Goblin and it disappears forever.
```

### `heal` (aliases: `drink`, `consume`)
Use a health potion to restore your HP. Can be used during or outside of combat.

**Example:**
```
> heal
You drink a Health Potion and restore 20 HP!
```

---

## Character Commands

### `stats` (aliases: `status`, `health`)
View your character's current stats including HP, attack, defense, and equipped items.

**Example:**
```
> stats
=== Hero's Stats ===
Health: 85/100
Attack: 25 (Base: 10)
Defense: 8 (Base: 0)

Equipped Items:
  WEAPON: Steel Sword (+15 ATK)
  HELMET: Iron Helmet (+3 DEF)
  CHESTPLATE: Leather Armor (+5 DEF)
```

### `inspect <monster>`
Examine a monster in your current room to see its stats before deciding whether to attack or ignore it.

**Example:**
```
> inspect goblin
Goblin: A small, green creature with sharp teeth.
[HP: 30/30, Attack: 15, Defense: 5]
```

---

## Puzzle Commands

### `solve <answer>`
Attempt to solve a puzzle in the current room.

**Example:**
```
> solve the moon
```

**Note:** Some rooms have puzzles that must be solved to progress. Read the puzzle description carefully!

---

## System Commands

### `help` (alias: `?`)
Display a quick reference of available commands.

### `save`
Manually save your game progress.

**Note:** The game also auto-saves when you quit.

### `load`
Load your previously saved game.

### `quit`
Save your game and exit.

---

## Tips & Tricks

### Combat Strategy
1. **Inspect before attacking** - Check monster stats to see if you're ready
2. **Equip before combat** - Make sure you have the best gear equipped
3. **Manage HP** - Keep health potions ready for tough fights
4. **Ignore wisely** - Some monsters might be too tough early on

### Item Management
- **Equipment stacks** - Equipping armor in multiple slots increases defense
- **Inspect everything** - Items show their bonuses when inspected
- **Drop excess** - You can drop items you don't need to reduce clutter

### Exploration
- **Use `look` often** - The game state changes as you play
- **Check your `map`** - Get oriented and see where you can go
- **Save regularly** - Don't lose progress!
- **Read descriptions** - They often contain hints

---

## Command Aliases Reference

| Command | Aliases |
|---------|---------|
| `look` | `l` |
| `go` | `move`, or just type direction |
| `inventory` | `i` |
| `pickup` | `take`, `grab` |
| `drop` | `discard` |
| `inspect` | `examine` |
| `attack` | `fight` |
| `ignore` | `flee`, `run` |
| `heal` | `drink`, `consume` |
| `stats` | `status`, `health` |
| `solve` | `answer` |
| `help` | `?` |

---

## Game Over & Death

If your HP reaches 0, you die and the game is over.

**Options:**
- Type `quit` to exit the game
- Type `load` to reload your last saved game

**Important:** Save often to avoid losing progress!

---

For more information, see:
- [Quickstart Guide](quickstart.md) - Get started quickly
- [FAQ](faq.md) - Frequently asked questions


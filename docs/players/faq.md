# Frequently Asked Questions (FAQ)

Common questions and troubleshooting for Adventure Time players.

---

## Gameplay Questions

### How do I start the game?
Run `./gradlew :client:run` from the project root directory.

### What are the basic commands?
Type `help` in-game for a quick reference, or see the [Commands Guide](commands.md) for complete documentation.

### How do I save my game?
- **Auto-save:** Type `quit` to save and exit
- **Manual save:** Type `save` at any time

Your save file is stored in the `/saves/` directory.

### Can I have multiple save files?
Currently, the game supports one save per player. Saving overwrites your previous save.

### How do I load a saved game?
Type `load` when you start the game to continue from your last save.

---

## Combat Questions

### How does combat work?
Combat is turn-based:
1. You attack first (your attack - monster defense = damage dealt)
2. Monster counter-attacks (monster attack - your defense = damage taken)
3. Repeat until one dies

See the [Commands Guide - Combat Section](commands.md#combat-commands) for details.

### What are critical hits?
Monsters have a chance to deal **2x damage** on their attacks. Each monster has a different critical hit chance.

### Can I flee from combat?
No, once combat starts, you must fight to the death. However, you can **ignore** a monster before attacking to make it disappear permanently.

### Can I change equipment during combat?
Yes! You can `equip` and `unequip` items even during battle to adjust your stats.

### What happens if I die?
You'll see a "Game Over" screen with two options:
- `quit` - Exit the game
- `load` - Load your last saved game

**Tip:** Save before difficult battles!

### How do I heal during combat?
Use the `heal` command to consume a health potion from your inventory.

---

## Items & Inventory Questions

### How do I see item bonuses?
Use `inspect <item>` to see the item's description and stat bonuses.

**Example:**
```
> inspect steel sword
Steel Sword: A sharp blade for combat.
[+15 Attack]
```

### What's the difference between "pickup" and "equip"?
- `pickup` - Takes an item from the room and puts it in your inventory
- `equip` - Equips an item from your inventory to gain its bonuses

You must pickup an item before you can equip it!

### How many items can I carry?
Currently, there's no inventory limit.

### What are the equipment slots?
- **Weapon** - 1 slot (increases attack)
- **Helmet** - 1 slot (increases defense)
- **Chestplate** - 1 slot (increases defense)
- **Leggings** - 1 slot (increases defense)
- **Boots** - 1 slot (increases defense)

### Do bonuses stack?
Yes! Equipping armor in multiple slots adds all the defense bonuses together.

---

## Monster Questions

### How do I know if I can beat a monster?
Use `inspect <monster>` to see its HP, attack, and defense stats. Compare them to your own stats (use `stats` command).

### What happens when I defeat a monster?
The monster is removed from the room permanently and you can continue exploring.

### What happens when I ignore a monster?
The monster disappears permanently from the room. You won't encounter it again if you return to that room.

### Do monsters respawn?
No, defeated or ignored monsters do not respawn.

---

## Movement & Exploration Questions

### What directions can I move?
- `north` (n), `south` (s), `east` (e), `west` (w)
- `up` (u), `down` (d)

### Can I see a map?
Not currently. Use the `look` command to see available exits from each room.

### I'm stuck in a room with no exits!
This shouldn't happen. All rooms should have at least one exit. If this occurs, it may be a bug - please report it.

### How do I solve puzzles?
Read the puzzle description carefully, then use `solve <answer>` to attempt a solution.

---

## Technical Questions

### What Java version do I need?
Java 21 (Temurin LTS) or higher.

### Where are save files stored?
In the `/saves/` directory in the project root.

### Can I edit the game content?
Yes! Game content is stored in JSON files at `client/src/main/resources/worldpacks/`. You can create custom worlds, items, monsters, and puzzles. See the [Admin - Content Packs](../admin/content-packs.md) guide.

### The game won't start. What should I do?
1. Make sure you have Java 21 installed: `java -version`
2. Try rebuilding: `./gradlew clean build`
3. Check for error messages in the console
4. See [Dev Setup](../dev/dev-setup.md) for detailed installation steps

### I found a bug. How do I report it?
Please create an issue in the repository with:
- Description of the bug
- Steps to reproduce
- Expected vs actual behavior
- Any error messages

---

## Command Troubleshooting

### "Unknown command" error
- Check spelling and spacing
- Type `help` to see available commands
- See [Commands Guide](commands.md) for complete list

### "You don't see X to inspect/pickup"
- Use `look` to see what's in the current room
- Item names are case-insensitive but must match exactly
- Some items might be in your inventory, not the room

### "You don't have X to equip"
- Use `inventory` to see what you're carrying
- You must `pickup` an item before you can `equip` it

### "There is no monster here to attack"
- Use `look` to see if there's a monster in the room
- Make sure you haven't already defeated or ignored the monster

### Multi-word names not working
Multi-word names should work for all commands. Examples:
- `attack Ice Troll` ✓
- `pickup steel sword` ✓
- `inspect iron helmet` ✓

If they don't work, it may be a bug.

---

## Strategy Tips

### Early Game
1. Explore thoroughly and pick up all items
2. Equip any weapons/armor you find
3. Inspect monsters before fighting
4. Save often!

### Combat
1. Check your stats before battle: `stats`
2. Inspect the monster: `inspect <monster>`
3. Equip your best gear
4. Keep health potions ready
5. If a monster looks too tough, consider ignoring it and coming back later with better gear

### Item Management
1. Inspect items to see their bonuses
2. Equip items immediately after picking them up
3. Keep health potions in inventory for emergencies
4. Drop items you don't need to reduce clutter

### Exploration
1. Use `look` frequently to stay oriented
2. Read room descriptions for hints and lore
3. Save before entering unknown areas
4. Map rooms mentally (or on paper) if needed

---

## Still Need Help?

- **Quick Reference** - Type `help` in-game
- **Full Commands** - [Commands Guide](commands.md)
- **Getting Started** - [Quickstart Guide](quickstart.md)
- **Developer Info** - [Dev Documentation](../dev/)

---

## Known Issues

(This section can be updated as issues are discovered)

Currently, there are no known major issues. If you encounter a bug, please report it!

---

*Last Updated: November 9, 2025*


@echo off
REM Demo script for Adventure Time game showcasing all features
REM This script demonstrates the game's capabilities using the example.json worldpack

setlocal enabledelayedexpansion

echo ========================================================================
echo          Adventure Time - Interactive Game Demo
echo                  Example World Showcase
echo ========================================================================
echo.
echo This demo will showcase all game features using the example worldpack.
echo Press any key to continue through each section...
echo.
pause

REM Build the game first
echo Building the game...
call gradlew.bat :client:installDist --quiet --no-configuration-cache

REM Create the demo input commands
(
echo help
echo look
echo stats
echo inventory
echo pickup mystic_key
echo pickup steel_sword
echo pickup iron_helmet
echo inventory
echo inspect mystic_key
echo inspect steel_sword
echo equip steel_sword
echo equip iron_helmet
echo stats
echo look
echo north
echo look
echo map
echo inspect ice_troll
echo attack
echo stats
echo use healing_potion
echo pickup ice_shard
echo north
echo look
echo solve echo
echo look
echo pickup healing_potion
echo south
echo east
echo look
echo inspect ancient_scroll
echo pickup ancient_scroll
echo pickup healing_potion
echo attack
echo ignore
echo north
echo look
echo solve fire
echo look
echo pickup frozen_crown
echo pickup frostbite_blade
echo pickup ice_plate
echo pickup frost_helm
echo pickup greater_healing_potion
echo inventory
echo unequip steel_sword
echo equip frostbite_blade
echo unequip iron_helmet
echo equip frost_helm
echo equip ice_plate
echo stats
echo south
echo west
echo south
echo west
echo look
echo attack
echo stats
echo heal greater_healing_potion
echo stats
echo north
echo north
echo look
echo attack
echo stats
echo map
echo save
echo quit
) > adventure_demo_commands.txt

cls
echo ========================================================================
echo   SECTION 1: Game Startup and Help
echo ========================================================================
echo.
echo Demonstrating:
echo   - help    : Show available commands
echo   - look    : Examine current room
echo   - stats   : View player statistics
echo.
pause

cls
echo ========================================================================
echo   SECTION 2: Inventory Management
echo ========================================================================
echo.
echo Demonstrating:
echo   - inventory : Check what you're carrying
echo   - pickup    : Pick up items from the ground
echo   - inspect   : Examine items closely
echo.
pause

cls
echo ========================================================================
echo   SECTION 3: Equipment System
echo ========================================================================
echo.
echo Demonstrating:
echo   - equip   : Equip weapons and armor
echo   - unequip : Remove equipped items
echo   - stats   : See how equipment affects your stats
echo.
pause

cls
echo ========================================================================
echo   SECTION 4: Movement and Exploration
echo ========================================================================
echo.
echo Demonstrating:
echo   - go north/south/east/west : Move between rooms
echo   - look                     : Examine your surroundings
echo   - map                      : View the world map
echo.
pause

cls
echo ========================================================================
echo   SECTION 5: Combat System
echo ========================================================================
echo.
echo Demonstrating:
echo   - inspect monster : Examine enemy before fighting
echo   - attack         : Engage in combat
echo   - ignore         : Avoid combat (if possible)
echo   - stats          : Monitor your health during combat
echo.
pause

cls
echo ========================================================================
echo   SECTION 6: Puzzle Solving
echo ========================================================================
echo.
echo Demonstrating:
echo   - solve [answer] : Solve riddles and puzzles
echo   - Rewards        : Get items for solving puzzles
echo.
pause

cls
echo ========================================================================
echo   SECTION 7: Healing and Consumables
echo ========================================================================
echo.
echo Demonstrating:
echo   - use [item] : Use consumable items
echo   - heal [item]: Restore health with potions
echo.
pause

cls
echo ========================================================================
echo   SECTION 8: Save System
echo ========================================================================
echo.
echo Demonstrating:
echo   - save : Save your game progress
echo   - quit : Exit the game
echo   - load : (Available on startup to restore saves)
echo.
pause

cls
echo ========================================================================
echo   Starting Interactive Demo...
echo ========================================================================
echo.
echo Running Adventure Time with demo commands...
echo (Game output will be shown below)
echo.
echo ========================================================================
echo.

REM Run the actual game with the demo commands
type adventure_demo_commands.txt | client\build\install\client\bin\client.bat --world=example

echo.
echo ========================================================================
echo.
echo ========================================================================
echo                     Demo Complete!
echo ========================================================================
echo.
echo Features Demonstrated:
echo   * Help system and command reference
echo   * Room exploration with detailed descriptions
echo   * Item inspection and pickup mechanics
echo   * Equipment system (weapons and armor)
echo   * Character stats and attributes
echo   * Combat encounters with multiple monsters
echo   * Puzzle solving with riddles
echo   * Consumable items and healing
echo   * World navigation and mapping
echo   * Save/Load game functionality
echo.
echo Game Features:
echo   * Dynamic combat with critical hits
echo   * Equipment bonuses affect stats
echo   * Multiple item types (weapons, armor, consumables, quest items)
echo   * Puzzle rewards and progression
echo   * Persistent save system
echo   * Rich world with interconnected rooms
echo.
echo Available Commands:
echo   Movement  : go [direction], north, south, east, west
echo   Look      : look, inspect [target], map
echo   Items     : pickup [item], drop [item], inventory
echo   Equipment : equip [item], unequip [item]
echo   Combat    : attack, ignore
echo   Healing   : use [item], heal [item]
echo   Puzzles   : solve [answer]
echo   System    : stats, help, save, load, quit
echo.
echo Try exploring the world yourself:
echo   run-client.bat example
echo.
echo Or try the Jurassic Park worldpack:
echo   run-client.bat jurassic
echo.

REM Clean up
del adventure_demo_commands.txt

pause


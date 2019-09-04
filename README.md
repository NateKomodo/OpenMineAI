# OpenMineAI

OpenMineAI is a work-in-progress Minecraft pathfinder and automation tool, with the eventual goal to automate the majority of activities in Minecraft

## Current features

As of 0.7.9, the following is done:

- Full player control
- Path-finding
- Path-execution
- Forge integration for 1.12.2
- Modular chat commands
- (Not yet finished) Auto mining

## Todo

Pathfinder:
- Get to open areas or some high level selection system to route via (For highways or similar) or segment into sections for improved accuracy or some waypoint system
- Make pathfinder handle not entirely diagonal parkour jumps
- Ant path with bias
- Descend mine falling into pit/lava
- Fix adj lava not triggering correctly

Path executor:
- Shortcuts
- Diagonal centering?
- Descend mine and strafing centering issues
- Confirm still possible
- RTP fall out issues (Move instead of descend mine, etc)

Mining:
- Make available to all ores and wider search area

Modules:
- Combat
- Recon
- Other tasks that can be automated such as farming 

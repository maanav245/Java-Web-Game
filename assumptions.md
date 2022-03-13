# Assumptions for Milestone 1 and 2
Down below is a list of general assumptions we have made up to Milestone 2:

    - All potions can be drunk once
    - Items have no meaningful position while player/enemy moves, unless taken out of inventory it will drop in the same position as the Inventory carrier
    - Choose to build inventory
    - For wielding things, in Battle, we check if its in inventory, then then player/character will automatically wield it (e.g. amour/ swords)
    - keys are automaticlly used 
    - You can stand on door spaces 
    - Only players can pick up items 
    - Keys are automatically used 
    - All maps will have at least one goal 
    - The characters will try to move into blockable items if they do they will just not move 
    - If a player cant move off a portal in the same direction they are already were moving they just stay on top of the portal 
    - Only one potion can be active at a time 
    - Layer in Position doesn't matter, i.e. z - layer values all set to 0;
    - Zombie Toast Spawners can not be destoyed until all the enemes are defeated. 
    - Spiders will spawn a random amount at the begining and spawn until max amount then stop spawning.
    - Characters will have exactly the same player movements unless specified otherwise (i.e. Zombies and Mercenaries can push boulders).
    - Zombies always move (i.e. they cannot ever stand still on one tick).

Down below there is a breakdown of some specified assumptions and why we have carried to go with them.

## Enemies Movement in PeacefulMode
- same as in standard and hard mode but battles will not be triggered

## Goal Assumptions
The dungeon will only be deemed complete if all of its subgoals are complete, for games with multiple goals. We deciced this due to the current front-end implementation not allowing for side quests.

## Bomb Assumptions
Whether a bomb is 'adjacent' to a switch in order to explode if a boulder is placed on switch, is defined by the definition of 'adjacent' i.e. cardinally adjacent in the Position helper "isAdjacent".

## Battle Assumptions
Our battle mechanics and how the intend to work are displayed below.

    - Sequential battles if more than 1 enemy on the same square, player (and allies) will battle 1 enemy as one battle until all enemies gone or player dies.
    - Only player and enemies can battle, and they will use any items that will increase their attack/defence automatically as part of the battle (thus reducing its durability).
    - Enemy attacks player first, then player attacks enemy, then any player allies attack enemy. If there is an ally in battle radius, enemy health is decreased twice as per spec
    - all battles are player's side versus 1 enemy. If there are multiple enemies in the battle vincinity, one enemy will battle the player, then the next and so on until all battles are completed.
    - Enemies will attack player only, not ally, in 1 on 1 battle. Ally does damage only.
    - If an enemy loses the battle, it will drop all its items in its inventory in that position
    - However, a player will retain its items in inventory if the player respawns in a battle, EXCEPT the "one_ring" that allows it to respawn will be removed from inventory.
    - Battles CAN be started when the player has drunk an invisibility potion - only the movements of some of the characters will be affected because the characters wont know how to move towards the player

We went with these game mechanics to make sense of how the player and enemy battles change within the game, so the gameplay becomes interesting over time, instead of each level having a set difficulty and battle. This adds more variability into our gameplay as a player progresses within a level.

### Player battling older version of the player
- If both have invicibility potions in effect, the effects of both will be cancelled out.
- both older player and current player can have their allies attack in the battle
- You cant build before you have moved at least 1 tick 

# Assumptions for Milestone 3

## Spider Spawning
Assume that a spider can sometimes spawn at the begginning or not.

## Enemy Movement
For this milestone, the assumption was changed for mercenaries and assassins where they cannot move boulders like the player, unlike the assumption made in the last milestone. All other ssumptions related to movement remain the same from Milestone 2.

### Mercenary and Assassins
- Moves after player has moved. Calculates djikstra on original position of player 
- Mercenary cannot pass through most collectibles and cannot push boulders
- Mercenary cannot compute negative positions, player should not be in a negative position.

## Hydra
- Hydras can only spawn in hard mode, and if there is a loaded dungeon with "hydra" in the saved result, in a non-hard mode, they will not appear on the map
- Hydras will move randomly. since in hard mode, invicibility potion has no effect, they will not change movement strategy for any potion
- Hydras can gain more than their initial HP, if they are attacked by a player without the AndurilSword, they could in theory increase their HP (via the 50% chance) above initial

## Sunstones
Sunstones are removed when used to build items. If a recipe requires a sunstone (all ingredients for a recipe are inside the inventory) it can not be used as treasure, otherwise it can be used as a treasure item.

## Bribing
Bribing takes precedence of what is in the players inventory in sequence i.e. if you have collected gold first then you have to
deplete your gold etc.

### Sceptre
Sceptres can only take control of one enemy at a time and it can be used indefinitely (it is not disposable). It is also only triggered when you click an enemy and when you have no other items to use to bribe enemies with in your inventory. When someone
is under the spell of the sceptre, potions do not effect them.

## Time Travel (Extension Task 1)
Portals
- Cant enter if game state doesnt have more than 30 ticks 
- position of player doesnt move when the player goes through the time portal
- 1 portal only 
- 1 use and then disappears
Time Turner 
- 1 time turner 

## Prim's Random Maze Generator (Extension Task 2)
- Bounds for the map are (0, 0), (0, 49), (49, 0) and (49, 49) (size 50 x 50 grid)
- Player cannot start nor end at the boundary
- The only goal for this map is the exit goal

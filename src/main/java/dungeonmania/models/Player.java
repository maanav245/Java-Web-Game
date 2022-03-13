package dungeonmania.models;

import dungeonmania.util.Direction;
import dungeonmania.util.Position;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.stream.Collector;
import java.util.stream.Collectors;
import org.json.JSONArray;
import org.json.JSONObject;

import dungeonmania.LoadAndSaveDungeon;
import dungeonmania.exceptions.*;
import dungeonmania.models.StaticEntities.*;
import dungeonmania.models.MovingEntities.Character;
import dungeonmania.models.MovingEntities.Mercenary;
import dungeonmania.models.MovingEntities.Bribe;
import dungeonmania.models.Battle.Battle;
import dungeonmania.models.Battle.DoBattle;
import dungeonmania.models.Movement.MoveWith;

public class Player extends Entity implements DoBattle, MovementSubject {

    private Inventory inventory = new Inventory();
    private int initialHP = 25;
    private int HP; // health points
    private int AP; // attack points
    private Dungeon dungeon;
    private Position tempNewPosition;
    private boolean instantLose = false;
    private List<Entity> allyList = new ArrayList<>();
    private List<String> allyListID = new ArrayList<>();
    private List<MovementObserver> enemies = new ArrayList<>();
    private boolean battleEnabled = true;
    private Potion potionInUse = null;
    private int potionCounter = 0;
    private Entity sceptredEnemy = null;
    private String sceptredEnemyID = "null";
    private int sceptreCounter = 0;
    private Random oneRingSeed = new Random();

    /**
     * Constructor for player if not from loading a game
     * 
     * @param id
     * @param position
     */
    public Player(String id, Position position) {
        super(id, "player", position, false);
        setHP(50);
        setAP(10);

    }

    /**
     * Constructor for player if loading game, we use json
     * 
     * @param json
     */
    public Player(JSONObject json) {
        super(json);
        this.AP = json.getInt("AP");
        this.HP = json.getInt("HP");
        this.potionCounter = json.getInt("potionCounter");
        JSONObject potion = json.getJSONObject("potionInUse");
        if (!potion.isEmpty())
            this.potionInUse = (Potion) LoadAndSaveDungeon.createNewEntity(json.getJSONObject("potionInUse"));

        JSONObject jsonInventory = (JSONObject) json.get("inventory");
        JSONArray jsonInventoryList = jsonInventory.getJSONArray("inventoryInfo");

        for (Object e : jsonInventoryList) {
            Entity itemToAdd = LoadAndSaveDungeon.createNewEntity((JSONObject) e);
            this.inventory.addToInventory(itemToAdd);
        }

        JSONArray jsonAllyList = json.getJSONArray("allyList");
        for (Object ally : jsonAllyList) {
            this.allyListID.add((String) ally);
        }

        // Add sceptred
        this.sceptreCounter = json.getInt("sceptredCount");
        this.sceptredEnemyID = json.getString("sceptredEnemy");
    }

    /**
     * Set the sceptred enemy to track
     * 
     * @param sceptredEnemy
     */
    public void setSceptredEnemy(Entity sceptredEnemy) {
        this.sceptredEnemy = sceptredEnemy;
        this.sceptredEnemyID = sceptredEnemy.getId();
    }

    /**
     * Get sceptred enemy
     * 
     * @return sceptred enemy
     */
    public Entity getSceptredEnemy() {
        return this.sceptredEnemy;
    }

    /**
     * Get getSceptredEnemyID to regenerate from load file
     * 
     * @return sceptred id
     */
    public String getSceptredEnemyID() {
        return this.sceptredEnemyID;
    }

    /**
     * Remove sceptred once tick count is over
     */
    public void removeSceptredEnemy() {
        removeAlly(getSceptredEnemy().getId());
        this.sceptredEnemy = null;
        this.sceptredEnemyID = "null";
    }

    /**
     * Sets the time in which sceptre is active
     * 
     * @param sceptreCounter
     */
    public void setSceptreCounter(int sceptreCounter) {
        this.sceptreCounter = sceptreCounter;
    }

    /**
     *
     * @return Potion
     */
    public Potion getPotionInUse() {
        return potionInUse;
    }

    /**
     * @param potionInUse
     */
    public void setPotionInUse(Potion potionInUse) {
        this.potionInUse = potionInUse;
    }

    /**
     * @return int
     */
    public int getPotionCounter() {
        return potionCounter;
    }

    /**
     * @param potionCounter
     */
    public void setPotionCounter(int potionCounter) {
        this.potionCounter = potionCounter;
    }

    /**
     * Getter for Player's intialHP
     * 
     * @return initialJP
     */
    public int getInitialHP() {
        return initialHP;
    }

    /**
     * Setter for Player's intialHP
     * 
     * @return initialJP
     */
    public void setInitalHP(int HP) {
        this.initialHP = HP;
    }

    /**
     * For saving a game with detailed information, convert additonal fields to JSON
     * 
     * @return initialJP
     */
    @Override
    public JSONObject getJSON() {
        JSONObject jsonEntity = super.getJSON();
        jsonEntity.put("inventory", getInventory().toJson());
        if (potionInUse != null)
            jsonEntity.put("potionInUse", potionInUse.getJSON());
        else
            jsonEntity.put("potionInUse", new JSONObject());
        jsonEntity.put("HP", getHP());
        jsonEntity.put("AP", getAP());
        jsonEntity.put("potionCounter", potionCounter);
        JSONArray jsonAllyList = new JSONArray();
        this.allyListID.forEach(ally -> jsonAllyList.put(ally));
        jsonEntity.put("allyList", jsonAllyList);
        jsonEntity.put("sceptredEnemy", this.sceptredEnemyID);
        jsonEntity.put("sceptredCount", this.sceptreCounter);
        return jsonEntity;
    }

    /**
     * Getter for attack points
     * 
     * @return
     */
    public int getAP() {
        return AP;
    }

    /**
     * Setter for health points
     */
    @Override
    public void setHP(int HP) {
        this.HP = HP;
    }

    /**
     * Getter for current health points
     * 
     * @return current health
     */
    @Override
    public int getHP() {
        return HP;
    }

    /**
     * Reset player to full health, used when respawning
     */
    public void resetHP() {
        setHP(getInitialHP());
    }

    /**
     * Setter for the attack power
     * 
     * @param aP
     */
    public void setAP(int aP) {
        this.AP = aP;
    }

    /**
     * Calculates how much damage would the player do against an enemy
     * 
     * @return ((player's HealthPpoints * Cplayers AttackPoings) / 5
     */
    @Override
    public int calculateAttackDamageAgainstOpponent() {
        return (getHP() * getAP()) / 5;
    }

    /**
     * If attacked by an enemy, reduce health
     * 
     * @pre decreasedHealthAmount >= 0
     * @param decreasedHealthAmount, what the enemy has done in damage to the player
     */
    @Override
    public void updateHealthAfterAttack(int decreasedHealthAmount, boolean againstLegendaryWieldable) {
        HP = HP - decreasedHealthAmount;
    }

    /**
     * Get inventory
     * 
     * @return player inventory
     */
    @Override
    public Inventory getInventory() {
        return this.inventory;
    }

    /**
     * Adds ally to list if possible
     */
    public void addAlly(Entity entity) throws InvalidActionException {
        // Load ally from save game
        if (this.allyListID.contains(entity.getId()) && !this.allyList.contains(entity)) {
            this.allyListID.remove(entity.getId());
        } else {
            // Else for new allies
            if (!((Bribe) entity).withinBribeableDistance(this.getPosition())) {
                throw new InvalidActionException(String.format("%s is too far", entity.getClass().getName()));
            }
            // Bribe type
            Entity treasure = ((Bribe) entity).bribeWith(this.getInventory().getInventoryInfo());
            if (treasure instanceof Sceptre) {
                // Do not sceptre more enemies
                if (sceptredEnemy != null) {
                    return;
                }
                setSceptreCounter(10);
                setSceptredEnemy(entity);
            } else {
                this.inventory.useFromInventory(treasure);
            }
            entity.setIsInteractable(false);
        }
        // TODO: Assume all ally types are of mercenary?
        ((Mercenary) entity).changeMovementStrategy(new MoveWith());
        ((Mercenary) entity).setBattleEnabled(false);
        // Dont remove them from the observer (unless with treasure)
        // create another list of observers (similar to enemies) (only sceptre)
        // Call update strategy of everything inside that list (once the counter zero)
        // add observer once sceptre finished
        this.removeObserver((MovementObserver) entity);
        this.allyList.add(entity);
        this.allyListID.add(entity.getId());
    }

    /**
     * Remove allies
     * 
     * @param entityID
     */
    public void removeAlly(String entityID) {
        Entity ally = this.allyList.stream().filter(e -> e.getId().equals(entityID)).collect(Collectors.toList())
                .get(0);
        this.allyList.remove(ally);
        this.allyListID.remove(entityID);
    }

    /**
     * Get ally list ids
     * 
     * @return
     */
    public List<String> getAlliesID() {
        return this.allyListID;
    }

    /**
     * Get ally list
     * 
     * @return
     */
    public List<Entity> getAllies() {
        return this.allyList;
    }

    /**
     * Add to inventory
     * 
     * @param item
     * @throws InventoryException
     */
    public void addToInventory(Entity item) throws InvalidActionException {
        this.inventory.addToInventory(item);
    }

    /**
     * Sets the current dungeon to that oof the players
     * 
     * @param dungeon
     */
    public void setDungeon(Dungeon dungeon) {
        this.dungeon = dungeon;
    }

    /**
     * Provides a list of dropped battle items
     * 
     * @return List<Entity>
     */
    public List<Entity> loseBattleItemsToBeDropped() {
        Inventory i = getInventory();
        List<Entity> toDrop = i.getInventoryInfo();
        // clear inventory to prevent respawn with full inventory
        this.inventory.clearInventory();
        return toDrop;
    }

    /**
     * Strategy which determines how the player moves
     * 
     * @param movementDirection
     * @return boolean
     */
    public boolean playerMove(Direction movementDirection) {

        List<Entity> entities = dungeon.getEntities();
        Position newPosition = getPosition().translateBy(movementDirection);
        List<Entity> entitiesAtPosition = dungeon.getEntitiesAtPosition(newPosition);
        // List<Entity> AdjaceEntities = dungeon.getAdjacentEntities();

        this.tempNewPosition = newPosition;

        // Do blockable stuff first because nothing should happen if they are blocked
        Boolean isBlocked = false;
        for (Entity e : entitiesAtPosition) {
            if (e instanceof Blockable) {
                if (((Blockable) e).isBlocking(this, entities))
                    isBlocked = true;
            }
        }

        if (!isBlocked) {
            for (Entity entity : entitiesAtPosition) {  
                if(!(potionInUse instanceof InvisibilityPotion)){
                    if (entity instanceof Character) {
                        Character enemy = (Character) entity;
                        if (enemy.isBattleEnabled()) {
                            Entity toRemove = Battle.startBattle((DoBattle) this, (DoBattle) entity);
                            if (!(toRemove instanceof Player))
                                ((Character) toRemove).setShouldRemove(true);
                            else
                                return true;
                        }
                    } else if (entity instanceof Player && !(entity.equals(this))){
                        if (this.getInventory().getIfContains("sun_stone").size() == 0 && this.getInventory().getIfContains("midnight_armour").size() == 0){
                            if (((Player)entity).getInventory().getIfContains("sun_stone").size() == 0 && ((Player)entity).getInventory().getIfContains("midnight_armour").size() == 0){
                                Entity toRemove = Battle.startBattle((DoBattle) this, (DoBattle) entity);
                                toRemove.setShouldRemove(true);
                            }
                        }       
                    }
                }
                
                if (entity instanceof Collectable) {
                    // is a boolean because you cant pick up 2 keys
                    if (((Collectable) entity).addToInventory(this))
                        dungeon.removeEntity(entity);
                }
            }
            super.setPosition(newPosition);
        }

        // do portal stuff last
        for (Entity e : entitiesAtPosition) {
            if (e instanceof Portal) {
                ((Portal) e).teleport(this, movementDirection, dungeon);
            }
        }

        // updates the movement strategy of all characters
        if (sceptreCounter > 0) {
            sceptreCounter--;
        } else if (sceptreCounter == 0 && sceptredEnemy != null) {
            // Re-register enemy and set to true
            sceptreCounter--;
            registerObserver((MovementObserver) getSceptredEnemy());
            getSceptredEnemy().setIsInteractable(true);
            ((Mercenary) getSceptredEnemy()).setBattleEnabled(true);
            removeSceptredEnemy();
        }

        if (potionCounter > 0)
            potionCounter--;
        else {
            potionInUse = null;
            notifyObservers();
        }
        
        return false;
    }

    /**
     * List out interactions here
     * 
     * @param entityId
     * @throws InvalidActionException
     */
    public void playerInteracts(String entityId) throws InvalidActionException {
        // TODO: Add interact interface?
        Entity entity = this.dungeon.getEntity(entityId);
        if (entity instanceof Bribe) {
            this.addAlly(entity);
        } else if (entity instanceof ZombieToasterSpawner && !getInventory().getWieldables().isEmpty()
                && Position.isAdjacent(entity.getPosition(), this.getPosition())) {
            dungeon.removeEntity(entity);
        } else {
            throw new InvalidActionException("Invalid interaction");
        }

    }

    public Position getTempNewPosition() {
        return this.tempNewPosition;
    }

    /**
     * @param inventory the inventory to set
     */
    public void setInventory(Inventory inventory) {
        this.inventory = inventory;
    }

    /**
     * @return Dungeon return the dungeon
     */
    public Dungeon getDungeon() {
        return dungeon;
    }

    /**
     * @param tempNewPosition the tempNewPosition to set
     */
    public void setTempNewPosition(Position tempNewPosition) {
        this.tempNewPosition = tempNewPosition;
    }

    /**
     * respawn player because it lost a battle
     * 
     * @pre Player has lost battle
     * @post If player has one ring in inventory, one will be removed Player's
     *       position will be updated in the map & client call this instead of
     *       deleting the player as an Entity in the Dungeon
     * @return true IF player is able to respawn, false if not;
     */
    public boolean useOneRingToRespawn() {
        // remove one ring
        List<Entity> oneRingUsedE = this.getInventory().getIfContains("one_ring");
        if (oneRingUsedE.size() == 0) {
            return false;
        }
        getInventory().useFromInventory(oneRingUsedE.get(0));
        // reset players health back to full
        this.resetHP();
        this.setShouldRemove(false);
        // reset player's position in a random location
        int x = (oneRingSeed).nextInt(dungeon.getWidth());
        int y = (oneRingSeed).nextInt(dungeon.getHeight());
        Position pos = new Position(x, y);
        while (dungeon.getEntitiesAtPosition(pos).stream()
                .filter(e -> (e instanceof Blockable) && ((Blockable) e).isBlocking(this, dungeon.getEntities()))
                .collect(Collectors.toList()).size() > 0) {

            x = (oneRingSeed).nextInt(dungeon.getWidth());
            y = (oneRingSeed).nextInt(dungeon.getHeight());
            pos = new Position(x, y);
        }

        this.setPosition(pos);
        return true;
    }

    /**
     * Player places a bomb in its current position
     * 
     * @pre tick has been called to use bomb
     * @pos bomb is taken from Inventory if one exists and placed on Dungeon 'map'
     */
    public void placeBomb(Dungeon curr) {
        Position bombPlacePos = this.getPosition();
        // get bomb from player's inventory, remove it & place in dungeon
        List<Entity> bombE = this.getInventory().getIfContains("bomb");
        if (bombE.size() > 0) {
            Entity bomb = bombE.get(0);
            bomb.setPosition(bombPlacePos);
            // place bomb in dungeon
            curr.addEntity(bomb);
            this.getInventory().useFromInventory(bomb);
            // if there is a switch adjacent, set status as "active"
            List<Switch> activeSwitches = curr.getEntities().stream().filter(
                    e -> Position.isAdjacent(e.getPosition(), bomb.getPosition()) && e.getType().equals("switch"))
                    .map(e -> (Switch) e).collect(Collectors.toList());
            activeSwitches.forEach(activeSwitch -> activeSwitch.setActive(true));
        }
    }

    @Override
    public void notifyObservers() {
        for (MovementObserver character : enemies)
            character.updateMovement(potionInUse);
    }

    @Override
    public void registerObserver(MovementObserver o) {
        enemies.add(o);
    }

    @Override
    public void removeObserver(MovementObserver o) {
        enemies.remove(o);
    }

    @Override
    public boolean isInstantLose() {
        return instantLose;
    }

    @Override
    public void setInstantLose(boolean instantLose) {
        this.instantLose = instantLose;
    }

    @Override
    public boolean isBattleEnabled() {
        return battleEnabled;
    }

    @Override
    public void setBattleEnabled(boolean battleEnabled) {
        this.battleEnabled = battleEnabled;
    }

    /**
     * Used for testing cases
     * 
     * @param randomSeed
     */
    public void setNewRandomPositionSeed(long randomSeed) {
        this.oneRingSeed = new Random(randomSeed);
    }

}

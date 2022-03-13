package dungeonmania.models.MovingEntities;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.List;
import java.util.Random;
import java.util.UUID;

import dungeonmania.LoadAndSaveDungeon;
import dungeonmania.models.Player;
import dungeonmania.models.Dungeon;
import dungeonmania.models.Entity;
import dungeonmania.models.Inventory;
import dungeonmania.models.MovementObserver;
import dungeonmania.models.Battle.Battle;
import dungeonmania.models.Battle.DoBattle;
import dungeonmania.models.Movement.Circle;
import dungeonmania.models.Movement.MoveAway;
import dungeonmania.models.Movement.MoveRandom;
import dungeonmania.models.Movement.MoveTowards;
import dungeonmania.models.Movement.MoveWith;
import dungeonmania.models.Movement.MovementStrategy;
import dungeonmania.models.StaticEntities.Blockable;
import dungeonmania.models.StaticEntities.InvisibilityPotion;
import dungeonmania.models.StaticEntities.Portal;
import dungeonmania.models.StaticEntities.SwampTile;
import dungeonmania.models.StaticEntities.TheOneRing;
import dungeonmania.util.Direction;
import dungeonmania.util.Position;

public abstract class Character extends Entity implements DoBattle, MovementObserver {
    private Inventory inventory = new Inventory();
    private int HP; // health points
    private int AP; // attack points
    private Position tempNewPosition;
    private Dungeon dungeon;
    private MovementStrategy strategy;
    private Boolean shouldRemove = false;
    private boolean instantLose = false;
    private boolean battleEnabled = true;
    private int cantMoveFactor = 0; 

    /**
     * The constructor used when the Character is created for the first time
     * 
     * @param id       The id of the character
     * @param type     The type of character it is
     * @param position Where the character is located
     * @param strategy The Deafult movedment strategy of the Character
     */
    public Character(String id, String type, Position position, MovementStrategy strategy) {
        super(id, type, position, false);
        this.strategy = strategy;
    }

    /**
     * The constructor used when the Character loaded from a saved game
     * 
     * @param json The coresponding json object
     */
    public Character(JSONObject json) {
        
        super(json);
        this.AP = json.getInt("AP");
        this.HP = json.getInt("HP");
        this.cantMoveFactor = json.getInt("cantMoveFactor");

        JSONObject jsonInventory = (JSONObject) json.get("inventory");
        JSONArray jsonInventoryList = jsonInventory.getJSONArray("inventoryInfo");

        for (Object e : jsonInventoryList) {
            this.inventory.addToInventory(LoadAndSaveDungeon.createNewEntity((JSONObject) e));
        }

        String strategyString = json.getString("strategy");
        if (strategyString.equals("Circle")) {
            strategy = new Circle();
        } else if (strategyString.equals("MoveAway")) {
            strategy = new MoveAway();
        } else if (strategyString.equals("MoveTowards")) {
            strategy = new MoveTowards();
        } else if (strategyString.equals("MoveRandom")) {
            strategy = new MoveRandom();
        } else {
            strategy = new MoveWith();
        }
    }

    // ------------------------------- getters and setters --------------------------------------//

    @Override
    public boolean isBattleEnabled() {
        return battleEnabled;
    }

    public void setBattleEnabled(boolean battleEnabled) {
        this.battleEnabled = battleEnabled;
    }

    @Override
    public boolean isInstantLose() {
        return instantLose;
    }

    public void setInstantLose(boolean instantLose) {
        this.instantLose = instantLose;
    }

    public Boolean getShouldRemove() {
        return this.shouldRemove;
    }

    public void setShouldRemove(Boolean shouldRemove) {
        this.shouldRemove = shouldRemove;
    }

    public int getAP() {
        return AP;
    }

    // TODO: remove this method?
    public void setAP(int aP) {
        this.AP = aP;
    }

    public int getHP() {
        return HP;
    }

    public void setHP(int HP) {
        this.HP = HP;
    }

    public void changeMovementStrategy(MovementStrategy strategy) {
        this.strategy = strategy;
    }

    public MovementStrategy getStrategy() {
        return strategy;
    }

    public void setStrategy(MovementStrategy strategy) {
        this.strategy = strategy;
    }

    public Inventory getInventory() {
        return this.inventory;
    }

    public Position getTempNewPosition() {
        return this.tempNewPosition;
    }

    public void setDungeon(Dungeon dungeon) {
        this.dungeon = dungeon;
    }

    // ------------------------------- other functions --------------------------------------//


    /**
     * 4% of the time a character will spawn with a rare object
     */
    public void spawnWithRareObject(Random random) {
        int rand = random.nextInt(100);
        TheOneRing onering = new TheOneRing(UUID.randomUUID().toString(), this.getPosition());
        if (rand < 5) {
            inventory.addToInventory(onering);
        }
    }

    /**
     * Moves the character
     * 
     * @param movementDirection The direction the character should move
     */
    public void characterMove(Direction movementDirection) {
        Position newPosition = getPosition().translateBy(movementDirection);
        List<Entity> entityAtPosition = dungeon.getEntitiesAtPosition(newPosition);
        List<Entity> entities = dungeon.getEntities();

        this.tempNewPosition = newPosition;
        boolean hasGameEnded = false;

        // Do blockable stuff first because nothing should happen if they are blocked
        Boolean isBlocked = false;
        for (Entity e : entityAtPosition) {
            if (e instanceof Blockable) {
                if (((Blockable) e).isBlocking(this, entities))
                    isBlocked = true;
            } else if (e instanceof SwampTile && cantMoveFactor == 0){
                cantMoveFactor = ((SwampTile)e).getMovementFactor(); 
                setPosition(newPosition);
            }
        }


        if (!isBlocked) {
            if (cantMoveFactor > 0){
                cantMoveFactor--; 
            } else {
                for (Entity entity : entityAtPosition) {
                    if (entity instanceof Player && battleEnabled && !(((Player)entity).getPotionInUse() instanceof InvisibilityPotion)) {
                        Entity toRemove = Battle.startBattle((DoBattle) entity, (DoBattle) this);
                        if (toRemove == null) {
                            continue;
                        } else if (!(toRemove instanceof Player)) {
                            this.shouldRemove = true;
                        } else {
                            hasGameEnded = true;
                            toRemove.setShouldRemove(true);
                            break;
                        }
                    }
                }
                setPosition(newPosition);
            } 
        }

        if (!hasGameEnded) {
            // do portal stuff last
            for (Entity e : entityAtPosition) {
                if (e instanceof Portal)
                    ((Portal) e).teleport(this, movementDirection, dungeon);
            }
        }
    }


    public abstract void spawnWithArmour(Random random);

    // ------------------------------- overrides --------------------------------------//

    /**
     * Decreases the health of the character
     */
    @Override
    public void updateHealthAfterAttack(int decreasedHealthAmount, boolean againstLegendaryWieldable) {
        HP = HP - decreasedHealthAmount;
    }

    /**
     * Calculates the oponents attack
     */
    @Override
    public int calculateAttackDamageAgainstOpponent() {
        return (getHP() * getAP()) / 10;
    }

    /**
     * Cretes a json object of the character
     */
    @Override
    public JSONObject getJSON() {
        JSONObject jsonEntity = super.getJSON();
        jsonEntity.put("cantMoveFactor", cantMoveFactor);
        jsonEntity.put("inventory", getInventory().toJson());
        jsonEntity.put("HP", getHP());
        jsonEntity.put("AP", getAP());
        jsonEntity.put("strategy", strategy.getClass().getSimpleName());
        return jsonEntity;
    }

    /**
     * updates the movement of the character
     */
    @Override
    public void updateMovement() {
        characterMove(strategy.getDirection(dungeon, this));
    }

    /**
     * Updates the movement startegy of the character
     */
    @Override
    public abstract void updateMovement(Object o);

}

package dungeonmania.models.MovingEntities;

import java.util.Random;
import java.util.Arrays;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

import org.json.JSONObject;

import dungeonmania.util.Position;
import dungeonmania.models.Inventory;
import dungeonmania.models.Movement.MoveAway;
import dungeonmania.models.Movement.MoveRandom;
import dungeonmania.models.Movement.MoveTowards;
import dungeonmania.models.StaticEntities.*;
import dungeonmania.models.Entity;
import dungeonmania.exceptions.InvalidActionException;

public abstract class Mercenary extends Character implements Bribe {
    
    // private Random assassinRandom = new Random();

    /**
     * The constructor used when a mercenary is created for the first time
     * 
     * @param id       The id of the mercenary
     * @param position Where the mercenary is located
     */
    public Mercenary(String id, Position position) {
        super(id, "mercenary", position, new MoveTowards());
        this.setIsInteractable(true);
        setHP(30);
        setAP(10);
    }

    /**
     * The constructor used when a mercenary is created for the first time
     * allows different string text to describe for subtype
     * 
     * @param id       The id of the mercenary
     * @param position Where the mercenary is located
     */
    public Mercenary(String id, Position position, String subtype) {
        super(id, subtype, position, new MoveTowards());
        this.setIsInteractable(true);
    }
    
    /**
     * The constructor used when the Mercenary is loaded from a saved game
     * 
     * @param json
     */
    public Mercenary(JSONObject json) {
        super(json);
        this.setIsInteractable(json.getBoolean("isInteractable"));
    }

    @Override
    public JSONObject getJSON() {
        JSONObject superJSON = super.getJSON();
        superJSON.put("isInteractable", isInteractable());
        return superJSON;
    }

    /**
     * Randomises the spawn of this Mercenary if it spawns with armour
     * 
     * @param random
     */
    public void spawnWithArmour(Random random) {
        Inventory inventory = getInventory();
        int rand = random.nextInt(100);
        BodyArmour armour = new BodyArmour(UUID.randomUUID().toString(), this.getPosition());
        if (rand < 50) {
            inventory.addToInventory(armour);
        }
    }

    @Override
    public Entity bribeWith(List<Entity> playerInventory) throws InvalidActionException {
        boolean bribeCurrency = playerInventory.stream().anyMatch(e -> useToBribe().contains(e.getType()));
        if (bribeCurrency) {
            return playerInventory.stream().filter(e -> useToBribe().contains(e.getType()))
            .collect(Collectors.toList()).get(0);
        } else {
            throw new InvalidActionException("No items to bribe with");
        }
    }

    @Override
    public boolean withinBribeableDistance(Position playerPos) {
        int xDiff = this.getPosition().getX() - playerPos.getX();
        int yDiff = this.getPosition().getY() - playerPos.getY();
        int cardinalDistance = Math.abs(xDiff) + Math.abs(yDiff);
        return cardinalDistance <= 2;
    }

    @Override
    public List<String> useToBribe() {
        return Arrays.asList("sun_stone", "treasure", "sceptre");
    }

    /**
     * Updates the movement strategy of the mercenary
     */
    @Override
    public void updateMovement(Object o) {
        if (o instanceof InvincibilityPotion && ((Potion) o).instantWinBattle()) {
            super.setInstantLose(true);
            super.setStrategy(new MoveAway());
        } else if (o instanceof InvisibilityPotion) {
            super.setStrategy(new MoveRandom());
            super.setInstantLose(false);
        } else if (o == null) {
            super.setStrategy(new MoveTowards());
            super.setInstantLose(false);
        } 
    }
}
package dungeonmania.models.MovingEntities;

import java.util.Random;
import java.util.UUID;

import org.json.JSONObject;

import dungeonmania.models.Movement.MoveAway;
import dungeonmania.models.Movement.MoveRandom;
import dungeonmania.util.Position;
import dungeonmania.models.Inventory;
import dungeonmania.models.StaticEntities.*;

public abstract class Zombie extends Character {

    /**
     * The constructor used when a zombie is created for the first time
     * 
     * @param id       The id of the zombie
     * @param position Where the zombie is located
     */
    public Zombie(String id, Position position) {
        super(id, "zombie_toast", position, new MoveRandom());
        setHP(10);
        setAP(5);
    }

    /**
     * The constructor used when the zombie is loaded from a saved game
     * 
     * @param json
     */
    public Zombie(JSONObject json) {
        super(json);
    }

    /**
     * Randomises the spawn of Zombie if it spawns with armour
     * 
     * @param ran
     */
    public void spawnWithArmour(Random ran) {
        Inventory inventory = getInventory();
        int random = ran.nextInt(100);
        System.out.println(random);
        BodyArmour armour = new BodyArmour(UUID.randomUUID().toString(), this.getPosition());
        if (random < 20) {
            inventory.addToInventory(armour);
        }
    }

    /**
     * Updates the movement strategy of the zombie
     */
    @Override
    public void updateMovement(Object o) {
        if (o instanceof InvincibilityPotion && ((Potion) o).instantWinBattle()) {
            super.setInstantLose(true);
            super.setStrategy(new MoveAway());
        } else {
            super.setInstantLose(false);
            super.setStrategy(new MoveRandom());
        }
    }
}

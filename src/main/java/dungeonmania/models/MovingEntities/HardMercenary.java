package dungeonmania.models.MovingEntities;

import java.util.Random;
import java.util.UUID;

import org.json.JSONObject;

import dungeonmania.util.Position;
import dungeonmania.models.Inventory;
import dungeonmania.models.Movement.MoveRandom;
import dungeonmania.models.Movement.MoveTowards;
import dungeonmania.models.StaticEntities.*;

public class HardMercenary extends Mercenary {
    /**
     * The constructor used when a mercenary is created for the first time
     * 
     * @param id       The id of the mercenary
     * @param position Where the mercenary is located
     */
    public HardMercenary(String id, Position position) {
        super(id, position);
        this.setHP(70);
        this.setAP(50);
        this.setIsInteractable(true);
        setBattleEnabled(true);
    }

    /**
     * The constructor used when the Mercenary is loaded from a saved game
     * 
     * @param json
     */
    public HardMercenary(JSONObject json) {
        super(json);
        setBattleEnabled(true);
    }

    /**
     * The constructor used when a mercenary is created for the first time
     * allows different string text to describe for subtype
     * 
     * @param id       The id of the mercenary
     * @param position Where the mercenary is located
     */
    public HardMercenary(String id, Position position, String subtype) {
        super(id, position, subtype);
        this.setIsInteractable(true);
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

      /**
     * Updates the movement strategy of the mercenary
     */
    @Override
    public void updateMovement(Object o) {
        if (o instanceof InvincibilityPotion && ((Potion) o).instantWinBattle()) {
            // invincibility_potion has no effect in hard mode
            super.setInstantLose(false);
            super.setStrategy(new MoveTowards());
        } else if (o instanceof InvisibilityPotion) {
            super.setStrategy(new MoveRandom());
            super.setInstantLose(false);
        } else if (o == null) {
            super.setStrategy(new MoveTowards());
            super.setInstantLose(false);
        } 
    }

}
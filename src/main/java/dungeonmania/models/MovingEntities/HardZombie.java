package dungeonmania.models.MovingEntities;

import java.util.Random;
import java.util.UUID;

import org.json.JSONObject;

import dungeonmania.models.Movement.MoveRandom;
import dungeonmania.util.Position;
import dungeonmania.models.Inventory;
import dungeonmania.models.StaticEntities.*;

public class HardZombie extends Zombie {

    /**
     * The constructor used when a zombie is created for the first time
     * 
     * @param id       The id of the zombie
     * @param position Where the zombie is located
     */
    public HardZombie(String id, Position position) {
        super(id, position);
        setHP(15);
        setAP(5);
        setBattleEnabled(true);
    }

    /**
     * The constructor used when the zombie is loaded from a saved game
     * 
     * @param json
     */
    public HardZombie(JSONObject json) {
        super(json);
        setBattleEnabled(true);
    }

    /**
     * Updates the movement strategy of the zombie
     */
    @Override
    public void updateMovement(Object o) {
        // invincibility_potion has no effect in hard mode
        super.setInstantLose(false);
        super.setStrategy(new MoveRandom());
    }
}

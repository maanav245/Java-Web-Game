package dungeonmania.models.MovingEntities;

import org.json.JSONObject;

import dungeonmania.models.Movement.MoveRandom;
import dungeonmania.models.Movement.MoveTowards;
import dungeonmania.models.StaticEntities.InvisibilityPotion;
import dungeonmania.util.Position;

public class HardAssassin extends Mercenary implements Boss {
    /**
     * The constructor used when a mercenary is created for the first time
     * 
     * @param id       The id of the mercenary
     * @param position Where the mercenary is located
     */
    public HardAssassin(String id, Position position) {
        super(id, position, "assassin");
        this.setIsInteractable(true);
        setHP(40);
        setAP(15);
    }

    /**
     * The constructor used when the Mercenary is loaded from a saved game
     * 
     * @param json
     */
    public HardAssassin(JSONObject json) {
        super(json);
    }

    /**
     * Updates the movement strategy of the assassin
     */
    @Override
    public void updateMovement(Object o) {
        // note invincibility_potion has no effect in hard mode
        if (o instanceof InvisibilityPotion) {
            super.setStrategy(new MoveRandom());
            super.setInstantLose(false);
        } else {
            super.setStrategy(new MoveTowards());
            super.setInstantLose(false);
        } 
    }


}
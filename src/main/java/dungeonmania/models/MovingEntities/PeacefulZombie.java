package dungeonmania.models.MovingEntities;

import java.util.Random;
import java.util.UUID;

import org.json.JSONObject;

import dungeonmania.util.Position;
import dungeonmania.models.Inventory;
import dungeonmania.models.StaticEntities.*;

public class PeacefulZombie extends Zombie {

    /**
     * The constructor used when a zombie is created for the first time
     * 
     * @param id       The id of the zombie
     * @param position Where the zombie is located
     */
    public PeacefulZombie(String id, Position position) {
        super(id ,position);
        setHP(50);
        setAP(30);
        super.setBattleEnabled(false);
    }

    /**
     * The constructor used when the zombie is loaded from a saved game
     * 
     * @param json
     */
    public PeacefulZombie(JSONObject json) {
        super(json);
    }

}

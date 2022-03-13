package dungeonmania.models.MovingEntities;

import java.util.Random;
import java.util.UUID;

import org.json.JSONObject;

import dungeonmania.util.Position;
import dungeonmania.models.Inventory;
import dungeonmania.models.StaticEntities.*;

public class PeacefulMercenary extends Mercenary {
    /**
     * The constructor used when a mercenary is created for the first time
     * 
     * @param id       The id of the mercenary
     * @param position Where the mercenary is located
     */
    public PeacefulMercenary(String id, Position position) {
        super(id, position);
        this.setIsInteractable(true);
        super.setBattleEnabled(false);
    }

    /**
     * The constructor used when the Mercenary is loaded from a saved game
     * 
     * @param json
     */
    public PeacefulMercenary(JSONObject json) {
        super(json);
    }


}
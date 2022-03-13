package dungeonmania.models.MovingEntities;

import java.util.Random;
import java.util.UUID;

import org.json.JSONObject;

import dungeonmania.util.Position;
import dungeonmania.models.Inventory;
import dungeonmania.models.StaticEntities.*;

public class StandardMercenary extends Mercenary {
    /**
     * The constructor used when a mercenary is created for the first time
     * 
     * @param id       The id of the mercenary
     * @param position Where the mercenary is located
     */
    public StandardMercenary(String id, Position position) {
        super(id, position);
        this.setIsInteractable(true);
        setBattleEnabled(true);
    }

    /**
     * The constructor used when the Mercenary is loaded from a saved game
     * 
     * @param json
     */
    public StandardMercenary(JSONObject json) {
        super(json);
        setBattleEnabled(true);
    }

}
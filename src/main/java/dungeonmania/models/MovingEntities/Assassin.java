package dungeonmania.models.MovingEntities;

import org.json.JSONObject;
import java.util.List;
import java.util.Arrays;

import dungeonmania.util.Position;

public abstract class Assassin extends Mercenary implements Boss {
    /**
     * The constructor used when a mercenary is created for the first time
     * 
     * @param id       The id of the mercenary
     * @param position Where the mercenary is located
     */
    public Assassin(String id, Position position) {
        super(id, position, "assassin");
        this.setIsInteractable(true);
        setHP(40);
        setAP(10);
    }

    /**
     * The constructor used when the Mercenary is loaded from a saved game
     * 
     * @param json
     */
    public Assassin(JSONObject json) {
        super(json);
    }

    @Override
    public List<String> useToBribe() {
        return Arrays.asList("sun_stone", "treasure", "sceptre", "one_ring");
    }

}
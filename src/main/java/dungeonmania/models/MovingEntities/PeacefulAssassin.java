package dungeonmania.models.MovingEntities;

import org.json.JSONObject;

import dungeonmania.util.Position;

public class PeacefulAssassin extends Assassin {
    /**
     * The constructor used when a mercenary is created for the first time
     * 
     * @param id       The id of the mercenary
     * @param position Where the mercenary is located
     */
    public PeacefulAssassin(String id, Position position) {
        super(id, position);
        this.setIsInteractable(true);
        this.setBattleEnabled(false);
    }

    /**
     * The constructor used when the Mercenary is loaded from a saved game
     * 
     * @param json
     */
    public PeacefulAssassin(JSONObject json) {
        super(json);
    }


}
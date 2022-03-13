package dungeonmania.models.StaticEntities;

import org.json.JSONObject;

import dungeonmania.models.Entity;
import dungeonmania.util.Position;

public class TheOneRing extends Entity implements Collectable {
    /**
     * Constructor
     * @param id
     * @param position
     */
    public TheOneRing(String id, Position position) {
        super(id, "one_ring", position, false);
    }

    /**
     * Constructor for load/save
     * @param json
     */
    public TheOneRing(JSONObject json) {
        super(json);
    }

    @Override
    public void entitityInteraction(Entity e1) {
        // Fill out later
    }

}
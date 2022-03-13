package dungeonmania.models.StaticEntities;

import org.json.JSONObject;

import dungeonmania.models.Entity;
import dungeonmania.util.Position;

public class Wood extends Entity implements Collectable {

    /**
     * Constructor
     * @param id
     * @param position
     */
    public Wood(String id, Position position) {
        super(id, "wood", position, false);
    }

    /**
     * Constructor for load/save
     * @param json
     */
    public Wood(JSONObject json) {
        super(json);
    }

    @Override
    public void entitityInteraction(Entity e1) {
        // Fill out later
    }

}

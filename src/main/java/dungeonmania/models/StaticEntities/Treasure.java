package dungeonmania.models.StaticEntities;

import org.json.JSONObject;

import dungeonmania.models.Entity;
import dungeonmania.util.Position;

public class Treasure extends Entity implements Collectable {
    /**
     * Constructor
     */
    public Treasure(String id, Position position) {
        super(id, "treasure", position, false);
    }

    /**
     * Constuctor for load/save
     * @param json
     */
    public Treasure(JSONObject json) {
        super(json);
    }

    @Override
    public void entitityInteraction(Entity e1) {
        // Fill out later
    }

}
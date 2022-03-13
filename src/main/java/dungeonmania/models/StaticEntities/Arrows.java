package dungeonmania.models.StaticEntities;

import org.json.JSONObject;

import dungeonmania.models.Entity;
import dungeonmania.util.Position;

public class Arrows extends Entity implements Collectable {
    /**
     * Constructor
     * @param id
     * @param position
     */
    public Arrows(String id, Position position) {
        super(id, "arrow", position, false);
    }

    /**
     * Constructor for load/save
     * @param json
     */
    public Arrows(JSONObject json) {
        super(json);
    }

    public void entitityInteraction(Entity e1) {
        // Fill out later
    }
}

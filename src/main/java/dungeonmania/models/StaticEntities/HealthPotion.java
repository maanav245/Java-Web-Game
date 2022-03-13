package dungeonmania.models.StaticEntities;

import dungeonmania.models.Entity;
import dungeonmania.util.Position;

import org.json.JSONObject;

public class HealthPotion extends Potion {
    /**
     * Constructor
     * @param id
     * @param position
     */
    public HealthPotion(String id, Position position) {
        super(id, position, "health_potion");
    }

    /**
     * Constructor for load/save
     * @param json
     */
    public HealthPotion(JSONObject json) {
        super(json);
    }

    @Override
    public void entitityInteraction(Entity e1) {
        // Fill out later
    }

}
package dungeonmania.models.StaticEntities;

import dungeonmania.models.Entity;
import dungeonmania.util.Position;

import org.json.JSONObject;

public class InvisibilityPotion extends Potion {
    /**
     * Constructor
     * @param id
     * @param position
     */
    public InvisibilityPotion(String id, Position position) {
        super(id, position, "invisibility_potion");
    }

    /**
     * Constructor for load/save
     * @param json
     */
    public InvisibilityPotion(JSONObject json) {
        super(json);
    }
    
    @Override
    public void entitityInteraction(Entity e1) {
        // Fill out later
    }
}

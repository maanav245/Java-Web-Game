package dungeonmania.models.StaticEntities;

import dungeonmania.models.Entity;
import dungeonmania.util.Position;

import org.json.JSONObject;

public class TimeTurner extends Entity implements Collectable {
    /**
     * Constructor
     */
    public TimeTurner(String id, Position position) {
        super(id, "time_turner", position, false);
    }

    /**
     * Constructor for load/save
     * 
     */
    public TimeTurner(JSONObject json) {
        super(json);
    }

    @Override
    public void entitityInteraction(Entity e1) {
        // TODO Auto-generated method stub

    }

}

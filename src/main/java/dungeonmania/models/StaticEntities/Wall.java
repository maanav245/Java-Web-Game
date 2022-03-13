package dungeonmania.models.StaticEntities;

import dungeonmania.models.Entity;
import dungeonmania.util.Position;
import dungeonmania.models.MovingEntities.*;

import org.json.JSONObject;
import java.util.List;

public class Wall extends Entity implements Blockable {
    /**
     * Constructor
     */
    public Wall(String id, Position position) {
        super(id, "wall", position, false);
    }

    /** Constructor for load/save
     * 
     */
    public Wall(JSONObject json) {
        super(json);
    }

    @Override
    public boolean isBlocking(Entity entity, List<Entity> entities) {
        if (entity instanceof Spider) {
            return false;
        } else {
            return true;
        }
    }

}

package dungeonmania.models.StaticEntities;

import org.json.JSONObject;

import dungeonmania.models.Entity;
import dungeonmania.util.Position;

public class Exit extends Entity {

    /**
     * Constructor
     * @param id
     * @param position
     */
    public Exit(String id, Position position) {
        super(id, "exit", position, false);
    }

    /**
     * Constructor for load/save
     * @param json
     */
    public Exit(JSONObject json) {
        super(json);
    }
    
}

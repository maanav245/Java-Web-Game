package dungeonmania.models.StaticEntities;

import org.json.JSONObject;

import dungeonmania.models.Entity;
import dungeonmania.util.Position;

public class TimeTravelingPortal extends Entity {
    /**
     * Constructor
     * @param id
     * @param position
     */
    public TimeTravelingPortal(String id, Position position) {
        super(id, "time_travelling_portal", position, false);
    }

    /**
     * Constructor for load/save
     * @param json
     */
    public TimeTravelingPortal(JSONObject json) {
        super(json);
    }
}


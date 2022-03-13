package dungeonmania.models.StaticEntities;

import dungeonmania.models.Entity;
import dungeonmania.util.Position;

import org.json.JSONObject;

public class Switch extends Entity {
    private boolean isActive = false;

    /**
     * Constructor
     * @param id
     * @param position
     */
    public Switch(String id, Position position) {
        super(id, "switch", position, false);
    }

    /**
     * Constructor for load/save
     * @param json
     */
    public Switch(JSONObject json) {
        super(json);
    }

    /**
     * Getter for isActive, if is set to explode upon boulder on top
     */
    public boolean isActive() {
        return isActive;
    }

    /**
     * Setter for isActive to indicate a bomb has been placed adjacent
     * @param value
     */
    public void setActive(boolean value) {
        isActive = value;
    }

}
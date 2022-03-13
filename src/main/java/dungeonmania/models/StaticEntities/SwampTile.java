package dungeonmania.models.StaticEntities;

import org.json.JSONObject;

import dungeonmania.models.Entity;
import dungeonmania.util.Position;

public class SwampTile extends Entity {
    
    private final int movementFactor; 
    
    /**
     * Constructor
     * @param id
     * @param position
     */
    public SwampTile(String id, Position position, int movementFactor) {
        super(id, "swamp_tile", position, false);
        this.movementFactor = movementFactor; 
    }

    /**
     * Constructor for load/save
     * @param json
     */
    public SwampTile(JSONObject json) {
        super(json);
        movementFactor = json.getInt("movement_factor"); 
    }

    public int getMovementFactor() {
        return movementFactor;
    }

    @Override
    public JSONObject getJSON() {
        JSONObject jsonEntity = super.getJSON();
        jsonEntity.put("movement_factor", movementFactor);
        return jsonEntity; 
    }


}

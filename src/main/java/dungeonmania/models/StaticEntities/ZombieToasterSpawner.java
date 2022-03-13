package dungeonmania.models.StaticEntities;

import dungeonmania.models.Entity;
import dungeonmania.util.Position;

import org.json.JSONObject;

public class ZombieToasterSpawner extends Entity {
    /**
     * Constructor
     * @param id
     * @param position
     */
    public ZombieToasterSpawner(String id, Position position) {
        super(id, "zombie_toast_spawner", position, true);
    }
    /**
     * Constructor for load/save
     * @param json
     */
    public ZombieToasterSpawner(JSONObject json) {
        super(json);
        this.setIsInteractable(true);
    }
}
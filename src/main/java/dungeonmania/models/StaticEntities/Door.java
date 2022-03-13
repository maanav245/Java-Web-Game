package dungeonmania.models.StaticEntities;

import java.util.List;
import java.util.stream.Collectors;

import org.json.JSONObject;

import dungeonmania.models.Entity;
import dungeonmania.models.Inventory;
import dungeonmania.models.Player;
import dungeonmania.models.MovingEntities.*;
import dungeonmania.util.Position;

public class Door extends Entity implements Blockable {
    private final int keyNumber;
    private boolean isOpen = false;

    /**
     * Constructor
     * @param id
     * @param position
     * @param keyNumber
     */
    public Door(String id, Position position, int keyNumber) {
        super(id, "door", position, false);
        this.keyNumber = keyNumber;
    }

    /**
     * Constructor for load/save
     * @param json
     */
    public Door(JSONObject json) {
        super(json);
        keyNumber = json.getInt("key");
        isOpen = json.getBoolean("isOpen");
    }

    @Override
    public boolean isBlocking(Entity entity, List<Entity> entities) {
        if (entity instanceof Spider)
            return false;
        if (isOpen)
            return false;
        if (entity instanceof Player) {
            Player player = (Player) entity;
            Inventory inventory = player.getInventory();
            if(inventory.getIfContains("sun_stone").size() > 0) return false; 
            List<Entity> keys = inventory.getIfContains("key");
            if (keys.size() == 0) {
                return true;
            }
            Key key = (Key) keys.get(0);
            if (key.getDoorNumber() == keyNumber) {
                isOpen = true;
                super.setType("door_unlocked");
                inventory.useFromInventory((Entity) key);
                return false;
            }
        }
        return true;
    }

    @Override
    public JSONObject getJSON() {
        JSONObject jsonEntity = new JSONObject();
        jsonEntity.put("x", getPosition().getX());
        jsonEntity.put("y", getPosition().getY());
        jsonEntity.put("type", getType());
        jsonEntity.put("key", keyNumber);
        jsonEntity.put("isOpen", isOpen);
        jsonEntity.put("id", getId());
        return jsonEntity;
    }

}

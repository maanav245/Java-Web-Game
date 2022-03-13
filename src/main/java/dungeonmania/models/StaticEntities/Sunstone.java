package dungeonmania.models.StaticEntities;

import java.util.List;
import java.util.stream.Collectors;

import org.json.JSONObject;

import dungeonmania.models.Entity;
import dungeonmania.models.Player;
import dungeonmania.util.Position;

public class Sunstone extends Entity implements Collectable {

    /**
     * Constructor
     */
    public Sunstone(String id, Position position) {
        super(id, "sun_stone", position, false);
    }

    /**
     * Constructor for json
     * 
     * @param json
     */
    public Sunstone(JSONObject json) {
        super(json);
    }

    @Override
    public JSONObject getJSON() {
        JSONObject jsonEntity = super.getJSON();
        return jsonEntity;
    }

    @Override
    public void entitityInteraction(Entity e1) {
        // TODO Auto-generated method stub
    }

    @Override
    public boolean addToInventory(Entity entity) {
        if (entity instanceof Player) {
            List<Entity> inventory = ((Player) entity).getInventory().getInventoryInfo();
            if (inventory.stream().filter(e -> e.getType().equals("sun_stone")).collect(Collectors.toList())
                    .isEmpty()) {
                ((Player) entity).addToInventory((Entity) this);
                return true;
            }
        }
        return false;
    }

}

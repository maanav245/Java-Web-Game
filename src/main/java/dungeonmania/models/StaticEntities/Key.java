package dungeonmania.models.StaticEntities;

import java.util.List;
import java.util.stream.Collectors;

import org.json.JSONObject;

import dungeonmania.models.Entity;
import dungeonmania.models.Player;
import dungeonmania.util.Position;

public class Key extends Entity implements Collectable {
    private final int doorNumber; 

    /**
     * Constructor
     */
    public Key(String id, Position position, int doorNumber) {
        super(id, "key", position, false);
        this.doorNumber = doorNumber; 
    }
    
    /**
     * Constructor for json
     * @param json
     */
    public Key(JSONObject json) {
        super(json);
        doorNumber = json.getInt("key"); 
    }

    /**
     * Getter for the door which this key opens
     * @return
     */
    public int getDoorNumber() {
        return doorNumber;
    }

    @Override
    public JSONObject getJSON() {
        JSONObject jsonEntity = super.getJSON();
        jsonEntity.put("key", doorNumber);
        return jsonEntity; 
    }

    @Override
    public void entitityInteraction(Entity e1) {
        // TODO Auto-generated method stub
    }

    @Override
    public boolean addToInventory(Entity entity){
        if (entity instanceof Player){
            List<Entity> inventory = ((Player) entity).getInventory().getInventoryInfo(); 
            if (inventory.stream().filter(e -> e.getType().equals("key")).collect(Collectors.toList()).isEmpty()){
                ((Player) entity).addToInventory((Entity) this);
                return true; 
            }  
        }
        return false; 
    }

}

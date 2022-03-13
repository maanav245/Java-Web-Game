package dungeonmania.models.StaticEntities;

import dungeonmania.models.Entity;
import dungeonmania.models.Player;


public interface Collectable {

    /**
     * Set out interaction between two entities
     */
    public void entitityInteraction(Entity e1);

    /**
     * Add this item to inventory
     */
    public default boolean addToInventory(Entity entity){
        if (entity instanceof Player){
            ((Player) entity).addToInventory((Entity) this);
        }
        return true; 
    }

}
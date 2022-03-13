package dungeonmania.models.StaticEntities;

import dungeonmania.models.Inventory;

public interface Buildable {
    /**
     * convert materials into buildable items within the player's Inventory and return
     * the Inventory as result
     * @param playeInventory
     * @return
     */
    public Inventory build(Inventory playeInventory);

}

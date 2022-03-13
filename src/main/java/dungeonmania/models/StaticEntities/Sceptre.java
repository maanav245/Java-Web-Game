package dungeonmania.models.StaticEntities;

import org.json.JSONObject;

import dungeonmania.exceptions.InvalidActionException;
import dungeonmania.models.Entity;
import dungeonmania.models.Inventory;
import dungeonmania.util.Position;

public class Sceptre extends Entity implements Collectable, Buildable {
    /**
     * Constructor
     * 
     * @param id
     * @param position
     */
    public Sceptre(String id, Position position) {
        super(id, "sceptre", position, false);
    }

    /**
     * Constructor for load/save game
     * 
     * @param json
     */
    public Sceptre(JSONObject json) {
        super(json);
    }

    @Override
    public void entitityInteraction(Entity e1) {
        // TODO Auto-generated method stub

    }

    @Override
    public Inventory build(Inventory playerInventory) {
        // test crafting with 1wood/2 arrows + treasure/key + sunstone
        if (1 <= playerInventory.getCount("sun_stone")) {
            Entity s1 = playerInventory.getIfContains("sun_stone").get(0);
            playerInventory.useFromInventory(s1);
            if (1 <= playerInventory.getCount("wood")) {
                Entity w1 = playerInventory.getIfContains("wood").get(0);
                playerInventory.useFromInventory(w1);

                if (1 <= playerInventory.getCount("key")) {
                    Entity k1 = playerInventory.getIfContains("key").get(0);
                    playerInventory.useFromInventory(k1);
                } else if (2 <= playerInventory.getCount("treasure")) {
                    Entity t1 = playerInventory.getIfContains("treasure").get(0);
                    playerInventory.useFromInventory(t1);
                } else {
                    throw new InvalidActionException("Invalid. Not enough ingredients to build a septre.");
                }
            } else if (2 <= playerInventory.getCount("arrow")) {
                Entity a1 = playerInventory.getIfContains("arrow").get(0);
                Entity a2 = playerInventory.getIfContains("arrow").get(1);
                playerInventory.useFromInventory(a1);
                playerInventory.useFromInventory(a2);
                if (1 <= playerInventory.getCount("key")) {
                    Entity k1 = playerInventory.getIfContains("key").get(0);
                    playerInventory.useFromInventory(k1);
                } else if (2 <= playerInventory.getCount("treasure")) {
                    Entity t1 = playerInventory.getIfContains("treasure").get(0);
                    playerInventory.useFromInventory(t1);
                } else {
                    throw new InvalidActionException("Invalid. Not enough ingredients to build a sceptre.");
                }
            } else {
                throw new InvalidActionException("Invalid. Not enough ingredients to build a sceptre.");
            }
        } else {
            throw new InvalidActionException("Invalid. Not enough ingredients to build a sceptre.");
        }
        return playerInventory;
    }
}

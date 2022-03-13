package dungeonmania.models.StaticEntities;

import org.json.JSONObject;

import dungeonmania.exceptions.InvalidActionException;
import dungeonmania.models.Entity;
import dungeonmania.models.Inventory;
import dungeonmania.models.Battle.DoBattle;
import dungeonmania.util.Position;

public class Shield extends Entity implements Collectable, Wieldable, Buildable {
    private int durability = 8;

    /**
     * Constructor
     * 
     * @param id
     * @param position
     */
    public Shield(String id, Position position) {
        super(id, "shield", position, false);
    }

    /**
     * Constructor for load/save
     * 
     * @param json
     */
    public Shield(JSONObject json) {
        super(json);
        this.durability = json.getInt("durability");
    }

    @Override
    public JSONObject getJSON() {
        JSONObject superJSON = super.getJSON();
        superJSON.put("durability", this.durability);
        return superJSON;
    }

    @Override
    public Inventory build(Inventory playerInventory) {
        // 2 wood + (1 treasure OR 1 key)
        if (2 <= playerInventory.getCount("wood") && 1 <= playerInventory.getCount("treasure")) {
            Entity w1 = playerInventory.getIfContains("wood").get(0);
            Entity w2 = playerInventory.getIfContains("wood").get(1);
            Entity t1 = playerInventory.getIfContains("treasure").get(0);
            playerInventory.useFromInventory(w1);
            playerInventory.useFromInventory(w2);
            playerInventory.useFromInventory(t1);
        } else if (2 <= playerInventory.getCount("wood") && 1 <= playerInventory.getCount("key")) {
            Entity w1 = playerInventory.getIfContains("wood").get(0);
            Entity w2 = playerInventory.getIfContains("wood").get(1);
            Entity k1 = playerInventory.getIfContains("key").get(0);
            playerInventory.useFromInventory(w1);
            playerInventory.useFromInventory(w2);
            playerInventory.useFromInventory(k1);
        } else if (2 <= playerInventory.getCount("wood") && 1 <= playerInventory.getCount("sun_stone")) {
            Entity w1 = playerInventory.getIfContains("wood").get(0);
            Entity w2 = playerInventory.getIfContains("wood").get(1);
            Entity t1 = playerInventory.getIfContains("sun_stone").get(0);
            playerInventory.useFromInventory(w1);
            playerInventory.useFromInventory(w2);
            playerInventory.useFromInventory(t1);
        } else {
            throw new InvalidActionException("Invalid. Not enough ingredients to build a shield.");
        }
        return playerInventory;
    };

    @Override
    public void entitityInteraction(Entity e1) {
        // Fill out later
    }

    @Override
    public int getDurability() {
        return durability;
    }

    @Override
    public void reduceDurability() {
        durability = durability - 1;
    }

    @Override
    public boolean instantWinBattle() {
        return false;
    }

    @Override
    public boolean isLegendaryWieldable() {
        return false;
    }

    @Override
    public double changeRatioAttackPower(DoBattle attacker, DoBattle defender) {
        return 1.0;
    }

    @Override
    public double changeRatioDefensePower(DoBattle attacker, DoBattle defender) {
        return 2.0;
    }
}

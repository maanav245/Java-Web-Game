package dungeonmania.models.StaticEntities;

import org.json.JSONObject;

import dungeonmania.exceptions.InvalidActionException;
import dungeonmania.models.Entity;
import dungeonmania.models.Inventory;
import dungeonmania.models.Battle.DoBattle;
import dungeonmania.util.Position;

public class Bow extends Entity implements Collectable, Wieldable, Buildable {
    private int durability = 5;

    /**
     * Constructor
     * 
     * @param id
     * @param position
     */
    public Bow(String id, Position position) {
        super(id, "bow", position, false);
    }

    /**
     * Constructor for load/save
     * 
     * @param json
     */
    public Bow(JSONObject json) {
        super(json);
        this.durability = json.getInt("durability");
    }

    /**
     * changes inventory raw materials into a buildable item & returns the new
     * Inventory with those buildable items
     */
    public Inventory build(Inventory playerInventory) {
        // ingredients 1 wood + 3 arrows
        if (1 <= playerInventory.getCount("wood") && 3 <= playerInventory.getCount("arrow")) {
            Entity w1 = playerInventory.getIfContains("wood").get(0);
            Entity a1 = playerInventory.getIfContains("arrow").get(0);
            Entity a2 = playerInventory.getIfContains("arrow").get(1);
            playerInventory.useFromInventory(w1);
            playerInventory.useFromInventory(a1);
            playerInventory.useFromInventory(a2);
        } else {
            throw new InvalidActionException("Invalid. Not enough ingredients to build a bow.");
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
        return 1.5;
    }

    @Override
    public double changeRatioDefensePower(DoBattle attacker, DoBattle defender) {
        return 1.0;
    }

    @Override
    public JSONObject getJSON() {
        JSONObject superJSON = super.getJSON();
        superJSON.put("durability", this.durability);
        return superJSON;
    }
}

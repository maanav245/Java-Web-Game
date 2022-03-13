package dungeonmania.models.StaticEntities;

import org.json.JSONObject;

import dungeonmania.exceptions.InvalidActionException;
import dungeonmania.models.Entity;
import dungeonmania.models.Inventory;
import dungeonmania.models.Battle.DoBattle;
import dungeonmania.util.Position;

public class MidnightArmour extends Entity implements Collectable, Wieldable, Buildable {
    private int durability = 20;

    /**
     * Constructor
     * 
     * @param id
     * @param position
     */
    public MidnightArmour(String id, Position position) {
        super(id, "midnight_armour", position, false);
    }

    /**
     * Constructor for load/save game
     * 
     * @param json
     */
    public MidnightArmour(JSONObject json) {
        super(json);
        this.durability = json.getInt("durability");
    }

    @Override
    public JSONObject getJSON() {
        JSONObject superJSON = super.getJSON();
        superJSON.put("durability", this.durability);
        return superJSON;
    }

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
    public Inventory build(Inventory playerInventory) {
        // test crafting with 1 armour + sunstone
        if (1 <= playerInventory.getCount("armour") && 1 <= playerInventory.getCount("sun_stone")) {
            Entity a1 = playerInventory.getIfContains("armour").get(0);
            Entity s1 = playerInventory.getIfContains("sun_stone").get(0);
            playerInventory.useFromInventory(a1);
            playerInventory.useFromInventory(s1);
        } else {
            throw new InvalidActionException("Invalid. Not enough ingredients to build Midnight Armour.");
        }
        return playerInventory;
    }

    @Override
    public boolean isLegendaryWieldable() {
        return false;
    }

    @Override
    public double changeRatioAttackPower(DoBattle attacker, DoBattle defender) {
        return 3.0;
    }

    @Override
    public double changeRatioDefensePower(DoBattle attacker, DoBattle defender) {
        return 2.0;
    }
}
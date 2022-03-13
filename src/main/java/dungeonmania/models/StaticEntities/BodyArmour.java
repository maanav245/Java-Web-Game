package dungeonmania.models.StaticEntities;

import org.json.JSONObject;

import dungeonmania.models.Entity;
import dungeonmania.models.Battle.DoBattle;
import dungeonmania.util.Position;

public class BodyArmour extends Entity implements Collectable, Wieldable {
    private int durability = 20;

    /**
     * Constructor
     * @param id
     * @param position
     */
    public BodyArmour(String id, Position position) {
        super(id, "armour", position, false);
    }

    /**
     * Constructor for load/save game
     * @param json
     */
    public BodyArmour(JSONObject json) {
        super(json);
        this.durability = json.getInt("durability");
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
    public boolean isLegendaryWieldable() {
        return false;
    }

    @Override
    public double changeRatioAttackPower(DoBattle attacker, DoBattle defender) {
        return 2.0;
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

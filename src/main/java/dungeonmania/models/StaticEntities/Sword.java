package dungeonmania.models.StaticEntities;

import org.json.JSONObject;

import dungeonmania.models.Entity;
import dungeonmania.models.Battle.DoBattle;
import dungeonmania.util.Position;

public class Sword extends Entity implements Collectable, Wieldable {
    private int durability = 10;

    /**
     * Constructor
     */
    public Sword(String id, Position position) {
        super(id, "sword", position, false);
    }

    /**
     * Constructor for load/ save
     * @param json
     */
    public Sword(JSONObject json) {
        super(json);
        this.durability = json.getInt("durability"); 
    }

    /**
     * Constructor allowing subtypes
     */
    public Sword(String id, Position position, String subType) {
        super(id, subType, position, false);
    }

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
        durability -= 1;
        
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

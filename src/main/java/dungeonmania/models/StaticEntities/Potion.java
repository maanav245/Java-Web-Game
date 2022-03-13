package dungeonmania.models.StaticEntities;

import org.json.JSONObject;

import dungeonmania.models.Entity;
import dungeonmania.util.Position;

public class Potion extends Entity implements Collectable {
    private int durability = 1;
    private boolean instantWinBattle;

    /**
     * Constructor
     */
    public Potion(String id, Position position, String type) {
        super(id, type, position, false);
        setInstantWinBattle(false);
    }

    /**
     * Constructor
     * @param json
     */
    public Potion(JSONObject json) {
        super(json);
    }

    public void entitityInteraction(Entity e1) {
        // Fill out later
    }

    public int getDurability() {
        return durability;
    }

    public void reduceDurability() {
        durability = durability - 1;

    }

    public double changeRatioAttackPower() {
        return 1.0;
    }

    public double changeRatioDefensePower() {
        return 1.0;
    }

    /**
     * @return boolean return the instantWinBattle
     */
    public boolean instantWinBattle() {
        return instantWinBattle;
    }

    /**
     * @param instantWinBattle the instantWinBattle to set
     */
    public void setInstantWinBattle(boolean instantWinBattle) {
        this.instantWinBattle = instantWinBattle;
    }

}
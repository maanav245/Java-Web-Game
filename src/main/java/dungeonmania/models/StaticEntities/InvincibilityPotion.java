package dungeonmania.models.StaticEntities;

import dungeonmania.util.Position;

import org.json.JSONObject;

public class InvincibilityPotion extends Potion {
    /**
     * Constructor
     * @param id
     * @param position
     */
    public InvincibilityPotion(String id, Position position) {
        super(id, position, "invincibility_potion");
        setInstantWinBattle(true);
    }

    /**
     * Constructor for load/save
     * @param json
     */
    public InvincibilityPotion(JSONObject json) {
        super(json);
    } 

}
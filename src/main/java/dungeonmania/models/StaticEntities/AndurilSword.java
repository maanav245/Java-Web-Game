package dungeonmania.models.StaticEntities;

import org.json.JSONObject;

import dungeonmania.models.Battle.DoBattle;
import dungeonmania.models.MovingEntities.Boss;
import dungeonmania.util.Position;

public class AndurilSword extends Sword {
    private Sword wrappee;

    /**
     * Constructor
     */
    public AndurilSword(String id, Position position) {
        super(id, position, "anduril");
        wrappee = new Sword(id, position);

    }

    /**
     * Constructor for load/ save
     * @param json
     */
    public AndurilSword(JSONObject json) {
        super(json);
        wrappee = new Sword(json);
    }

    @Override
    public boolean isLegendaryWieldable() {
        return true;
    }

    @Override
    public double changeRatioAttackPower(DoBattle attacker, DoBattle defender) {
        // increases attack by 3x against Bosses
        return (defender instanceof Boss) ?  3.0*  wrappee.changeRatioAttackPower(attacker, defender) : wrappee.changeRatioAttackPower(attacker, defender) ;
    }

    @Override
    public JSONObject getJSON() {
        JSONObject jsonEntity = super.getJSON();
        jsonEntity.put("durability", super.getDurability());
        return jsonEntity; 
    }
}

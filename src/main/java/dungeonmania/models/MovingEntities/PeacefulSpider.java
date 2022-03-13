package dungeonmania.models.MovingEntities;

import org.json.JSONObject;

import dungeonmania.util.Position;

public class PeacefulSpider extends Spider {
    private final Position spawnPosition;
    private boolean isClockwise = true;

    /**
     * The constructor used when a spider is created for the first time
     * 
     * @param id       The id of the spider
     * @param position Where the spider is located
     */
    public PeacefulSpider(String id, Position position) {
        super(id, position);
        setHP(25);
        setAP(15);
        spawnPosition = position;
        super.setBattleEnabled(false);
    }

    /**
     * The constructor used when the spider is loaded from a saved game
     * 
     * @param json
     */
    public PeacefulSpider(JSONObject json) {
        super(json);
        spawnPosition = new Position(json.getInt("spawnX"), json.getInt("spawnY"));
        isClockwise = json.getBoolean("isClockwise");
        super.setBattleEnabled(false);
    }


}

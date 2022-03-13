package dungeonmania.models.MovingEntities;

import org.json.JSONObject;

import dungeonmania.util.Position;

public class StandardSpider extends Spider {
    private final Position spawnPosition;
    private boolean isClockwise = true;

    /**
     * The constructor used when a spider is created for the first time
     * 
     * @param id       The id of the spider
     * @param position Where the spider is located
     */
    public StandardSpider(String id, Position position) {
        super(id, position);
        spawnPosition = position;
        setBattleEnabled(true);
    }

    /**
     * The constructor used when the spider is loaded from a saved game
     * 
     * @param json
     */
    public StandardSpider(JSONObject json) {
        super(json);
        spawnPosition = new Position(json.getInt("spawnX"), json.getInt("spawnY"));
        isClockwise = json.getBoolean("isClockwise");
        setBattleEnabled(true);
    }

}

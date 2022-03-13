package dungeonmania.models.MovingEntities;

import org.json.JSONObject;

import dungeonmania.models.Movement.Circle;
import dungeonmania.util.Position;

public class HardSpider extends Spider {
    private final Position spawnPosition;
    private boolean isClockwise = true;

    /**
     * The constructor used when a spider is created for the first time
     * 
     * @param id       The id of the spider
     * @param position Where the spider is located
     */
    public HardSpider(String id, Position position) {
        super(id, position);
        setHP(25);
        setAP(15);
        spawnPosition = position;
        setBattleEnabled(true);
    }

    /**
     * The constructor used when the spider is loaded from a saved game
     * 
     * @param json
     */
    public HardSpider(JSONObject json) {
        super(json);
        spawnPosition = new Position(json.getInt("spawnX"), json.getInt("spawnY"));
        isClockwise = json.getBoolean("isClockwise");
        setBattleEnabled(true);
    }

    /**
     * Updates the movement strategy of the spider
     */
    @Override
    public void updateMovement(Object o) {
        // invicibility potion has no effect on battle outcome
        super.setInstantLose(false);      
        super.setStrategy(new Circle());
    }
}

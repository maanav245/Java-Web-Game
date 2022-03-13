package dungeonmania.models.MovingEntities;

import java.util.Random;

import org.json.JSONObject;

import dungeonmania.models.Movement.Circle;
import dungeonmania.models.StaticEntities.InvincibilityPotion;
import dungeonmania.models.StaticEntities.Potion;
import dungeonmania.util.Position;

public  abstract class Spider extends Character {
    private final Position spawnPosition;
    private boolean isClockwise = true;

    /**
     * The constructor used when a spider is created for the first time
     * 
     * @param id       The id of the spider
     * @param position Where the spider is located
     */
    public Spider(String id, Position position) {
        super(id, "spider", position, new Circle());
        setHP(5);
        setAP(5);
        spawnPosition = position;
        setShouldRemove(false);
    }

    /**
     * The constructor used when the spider is loaded from a saved game
     * 
     * @param json
     */
    public Spider(JSONObject json) {
        super(json);
        spawnPosition = new Position(json.getInt("spawnX"), json.getInt("spawnY"));
        isClockwise = json.getBoolean("isClockwise");
    }

    /**
     * Getter for clockwise/anti-clockwise movement
     */
    public boolean isClockwise() {
        return isClockwise;
    }

    /**
     * Setter for clockwise/anti-clockwise movement
     */
    public void setClockwise(boolean isClockwise) {
        this.isClockwise = isClockwise;
    }

    /**
     * Get position where this spider will spawn
     * 
     * @return
     */
    public Position getSpawnPosition() {
        return spawnPosition;
    }

    public void spawnWithArmour(Random seed) {
        return;
    }

    /**
     * Cretes a json object of the spider
     */
    @Override
    public JSONObject getJSON() {
        JSONObject jsonEntity = super.getJSON();
        jsonEntity.put("isClockwise", isClockwise);
        jsonEntity.put("spawnX", spawnPosition.getX());
        jsonEntity.put("spawnY", spawnPosition.getY());
        return jsonEntity;
    }

    /**
     * Updates the movement strategy of the spider
     */
    @Override
    public void updateMovement(Object o) {
        if (o instanceof InvincibilityPotion && ((Potion) o).instantWinBattle()) {
            super.setInstantLose(true);
        } else {
            super.setInstantLose(false);
        }
        super.setStrategy(new Circle());
    }

}

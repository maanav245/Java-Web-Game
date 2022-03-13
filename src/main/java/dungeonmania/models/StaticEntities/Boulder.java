package dungeonmania.models.StaticEntities;

import dungeonmania.DungeonManiaController;
import dungeonmania.models.Dungeon;
import dungeonmania.models.Entity;
import dungeonmania.models.Player;
import dungeonmania.models.MovingEntities.*;
import dungeonmania.models.MovingEntities.Character;
import dungeonmania.util.Position;
import java.util.stream.Collectors;
import org.json.JSONObject;
import java.util.List;

public class Boulder extends Entity implements Blockable {

    /**
     * Constructor
     * 
     * @param id
     * @param position
     */
    public Boulder(String id, Position position) {
        super(id, "boulder", position, false);
    }

    /**
     * Constructor for load/save
     * 
     * @param json
     */
    public Boulder(JSONObject json) {
        super(json);
    }

    @Override
    public boolean isBlocking(Entity entity, List<Entity> entities) {
        if (!(entity instanceof Player)) {
            return true;
        } else {
            // TODO: Vary this with difficulty
            Position tempNewPosition = ((Player) entity).getTempNewPosition();
            Position currPosition = ((Player) entity).getPosition();
            int xDir = tempNewPosition.getX() - currPosition.getX();
            int yDir = tempNewPosition.getY() - currPosition.getY();
            Position movementDirection = new Position(xDir, yDir);
            Position blockableNewPosition = this.getPosition().translateBy(movementDirection);
            boolean boulderAdjacent = entities.stream().anyMatch(entityVar -> entityVar.getType().equals("boulder")
                    && entityVar.getPosition().equals(blockableNewPosition));
            boolean wallAdjacent = entities.stream().anyMatch(entityVar -> entityVar.getType().equals("wall")
                    && entityVar.getPosition().equals(blockableNewPosition));
            if (!boulderAdjacent && !wallAdjacent) {
                // Set boulder position
                this.setPosition(blockableNewPosition);
                // check if boulder is now on same postion as active Switch
                Bomb.explodeInRadius(this, entities);
                return false;
            } else {
                return true;
            }
        }
    }

}
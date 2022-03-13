package dungeonmania.models.Movement;

import dungeonmania.models.Dungeon;
import dungeonmania.models.Entity;
import dungeonmania.util.Direction;

import java.util.concurrent.ThreadLocalRandom;

public class MoveRandom extends MovementStrategy {

    /**
     * Returns the correct direction when the entity is moving in a random direction 
     */
    @Override
    public Direction getDirection(Dungeon dungeon, Entity entity) {
        int randomNum = ThreadLocalRandom.current().nextInt(0, 4);
        Direction[] directions = new Direction[]{Direction.UP, Direction.DOWN, Direction.LEFT, Direction.RIGHT};
        return directions[randomNum]; 
    }
    
}

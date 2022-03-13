package dungeonmania.models.Movement;

import dungeonmania.models.Dungeon;
import dungeonmania.models.Entity;
import dungeonmania.util.Direction;
import dungeonmania.util.Position;

import java.util.concurrent.ThreadLocalRandom;

public class MoveWith extends MovementStrategy {

    /**
     * Returns the correct direction when the entity is moving with the player
     */
    @Override
    public Direction getDirection(Dungeon dungeon, Entity entity) {
        
        // Calculate new position closer to player
        Position currPosition = entity.getPosition();
        Position playerPosition = dungeon.getPlayer().getPosition();
        
        int xDiff = playerPosition.getX() - currPosition.getX(); 
        int yDiff = playerPosition.getY() - currPosition.getY(); 
        int xAbsDiff = Math.abs(xDiff);
        int yAbsDiff = Math.abs(yDiff);
        Direction[] leftRight = new Direction[]{Direction.LEFT, Direction.RIGHT};
        Direction[] downUp = new Direction[]{Direction.DOWN, Direction.UP};

        // up and down side to side cases (aviod division by zero)
        if (xDiff == 0 && yDiff == 0) {
            int randomNum = ThreadLocalRandom.current().nextInt(0, 2);
            if (yAbsDiff == 1) {
                return leftRight[randomNum];
            } else {
                return downUp[randomNum];
            }
        } else if ((xAbsDiff == 1 && yAbsDiff == 0) || (xAbsDiff == 0 && yAbsDiff == 1)) {
            return Direction.NONE;
        } else if (xDiff == 0) {
            return (yDiff > 0) ? Direction.DOWN : Direction.UP;  
        } else if (yDiff == 0) {
            return (xDiff > 0) ? Direction.RIGHT : Direction.LEFT; 
        } else {
            double ratio = xDiff/yDiff; 
            if (Math.abs(ratio) > 1) {
                return (xDiff > 0) ? Direction.RIGHT : Direction.LEFT;  
            } else {
                return (yDiff > 0) ? Direction.DOWN : Direction.UP;
            }
        } 
    }
    
}

package dungeonmania.models.Movement;

import dungeonmania.models.Dungeon;
import dungeonmania.models.Entity;
import dungeonmania.util.Direction;
import dungeonmania.util.Position;

public class MoveAway extends MovementStrategy {

    /**
     * Returns the correct direction when the entity is moving away from the player 
     */
    @Override
    public Direction getDirection(Dungeon dungeon, Entity entity) {

        Position currPosition = entity.getPosition();
        Position playerPosition = dungeon.getPlayer().getPosition();
        
        double xDiff = playerPosition.getX() - currPosition.getX(); 
        double yDiff =  playerPosition.getY() - currPosition.getY(); 

        // up and down side to side cases (aviod division by zero)
        if (xDiff == 0 && yDiff == 0) {
            return Direction.NONE; 
        } else if (xDiff == 0) {
            return (yDiff > 0) ? Direction.UP : Direction.DOWN;  
        } else if (yDiff == 0) {
            return (xDiff > 0) ? Direction.LEFT : Direction.RIGHT; 
        } else {
            double ratio = xDiff/yDiff; 
            if (Math.abs(ratio) > 1) {
                return (xDiff > 0) ? Direction.LEFT : Direction.RIGHT;  
            } else {
                return (yDiff > 0) ? Direction.UP : Direction.DOWN;
            }
        } 
    }}

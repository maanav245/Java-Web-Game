package dungeonmania.models.Movement;

import java.util.List;

import dungeonmania.models.Dungeon;
import dungeonmania.models.Entity;
import dungeonmania.util.Direction;
import dungeonmania.util.Position;
import dungeonmania.models.MovingEntities.Spider;
import dungeonmania.models.StaticEntities.Blockable;

public class Circle extends MovementStrategy {

    /**
     * Caculates the direction to move
     * @param xDiff The difrrence between the x-coordinates 
     * @param yDiff The difrrence between the y-coordinates 
     * @param isClockwise Is the entity moveing in a clockwise direction 
     * @return
     */
    private Direction caculatDirection(int xDiff, int yDiff, Boolean isClockwise){
        if (xDiff == 0 && yDiff == 0) {
            return (isClockwise) ? Direction.UP : Direction.DOWN;
        } else if (xDiff != 0) {
            if (!isClockwise){
                if (yDiff == 0 || xDiff == yDiff) xDiff = -xDiff;
                else yDiff = -yDiff;
            }  
            if (xDiff == 1 && yDiff <= 0){
                return Direction.UP;
            } else if (xDiff == -1 && yDiff >= 0) {
                return Direction.DOWN;
            } else if (xDiff == 1) {
                return Direction.RIGHT; 
            } else {
                return Direction.LEFT;
            } 
        } else {
            if (!isClockwise) yDiff = -yDiff; 
            return (yDiff > 0) ? Direction.RIGHT : Direction.LEFT; 
        }
    }

    /**
     * Determines if the sider was to move in that direction if they would be blocked 
     * @param dungeon The dungeon the spider is in 
     * @param entity The spider 
     * @param direction The desired movement direction 
     * @return If the spider is blocked 
     */
    private Boolean spiderBlocked(Dungeon dungeon, Entity entity, Direction direction){
        Position newPosition = entity.getPosition().translateBy(direction); 
        List<Entity> entities = dungeon.getEntitiesAtPosition(newPosition); 

        for(Entity e: entities) {
            if (e instanceof Blockable) {
                if(((Blockable) e).isBlocking(entity, dungeon.getEntities())) return true;
            }
        }
        return false; 
    }
    
    /**
     * returns the correct direction for the spider to move 
     */
    @Override
    public Direction getDirection(Dungeon dungeon, Entity entity) {
        if (entity instanceof Spider) {

            Spider spider = (Spider)entity; 
            Position spawnPosition = spider.getSpawnPosition();
            Position currPosition = spider.getPosition();

            int xDiff = spawnPosition.getX() - currPosition.getX(); 
            int yDiff = spawnPosition.getY() - currPosition.getY(); 
            
            Direction direction = caculatDirection(xDiff, yDiff, spider.isClockwise()); 

            if (spiderBlocked(dungeon, entity, direction)){
                spider.setClockwise(!spider.isClockwise());
                Direction newDirection = caculatDirection(xDiff, yDiff, spider.isClockwise());
                if (!spiderBlocked(dungeon, entity, newDirection)){
                    return newDirection; 
                } 
            } else {
                return direction; 
            }

        }
        return Direction.NONE;
    }
}

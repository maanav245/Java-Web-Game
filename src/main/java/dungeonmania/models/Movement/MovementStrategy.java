package dungeonmania.models.Movement;

import dungeonmania.models.Dungeon;
import dungeonmania.models.Entity;
import dungeonmania.util.Direction;

public abstract class MovementStrategy {
    
    /**
     * Gets the direction for the character to move 
     * @param dungeon The dungeon the Character is in 
     * @param entity The character 
     * @return A direction for the character to move 
     */
    public abstract Direction getDirection(Dungeon dungeon, Entity entity);
}   

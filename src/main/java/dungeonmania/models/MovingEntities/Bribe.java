package dungeonmania.models.MovingEntities;

import dungeonmania.models.Entity;
import dungeonmania.util.Position;
import dungeonmania.exceptions.InvalidActionException;

import java.util.List;

public interface Bribe {

    /**
     * Get item needed for bribing
     * @param playerInventory
     * @return item
     * @throws InvalidActionException
     */
    public Entity bribeWith(List<Entity> playerInventory) throws InvalidActionException;

    /**
     * Find out what they can be bribed with
     * @return list of string types that the entity can be bribed with
     */
    public List<String> useToBribe();

    /**
     * Check if they can be bribed
     * @param playerPos
     * @param enenmyPos
     * @return
     */
    public boolean withinBribeableDistance(Position playerPos);
}

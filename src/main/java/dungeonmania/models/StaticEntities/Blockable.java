package dungeonmania.models.StaticEntities;

import java.util.List;
import java.util.stream.Collectors;

import dungeonmania.models.Entity;
import dungeonmania.util.Position;

public interface Blockable {

    /**
     * Return true if this Entity is Blocking the player/ Character from moving
     */
    public boolean isBlocking(Entity entity, List<Entity> entities);
    
}

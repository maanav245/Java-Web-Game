package dungeonmania.models.StaticEntities;

import java.util.List;
import java.util.stream.Collectors;

import org.json.JSONObject;

import dungeonmania.models.Entity;
import dungeonmania.util.Position;

public class Bomb extends Entity implements Collectable {

    /**
     * Constructor
     * 
     * @param id
     * @param position
     */
    public Bomb(String id, Position position) {
        super(id, "bomb", position, false);
    }

    /**
     * Constructor for load/ save
     */
    public Bomb(JSONObject json) {
        super(json);
    }

    @Override
    public void entitityInteraction(Entity e1) {
        // TODO: Fill out later
    }

    /**
     * returns a list of any entities to be deleted from an exploded bomb
     * 
     * @param boulder
     * @param entities
     * @return
     */
    public static void explodeInRadius(Boulder boulder, List<Entity> entities) {

        List<Switch> activeSwitches = entities.stream()
                .filter(e -> e.getType().equals("switch") && boulder.sameXYPosition(e)).map(e -> (Switch) e)
                .filter(s -> s.isActive()).collect(Collectors.toList());
        // explode 'bomb' & destroy all enemies in area except for player
        activeSwitches.forEach(aSwitch -> {
            List<Position> postitionsToExplode = aSwitch.getPosition().getAdjacentPositions();
            List<Entity> entitiesToDelete = entities.stream().filter(e -> postitionsToExplode.contains(e.getPosition()))
                    .filter(e -> !e.getType().equals("player")).collect(Collectors.toList());
            // mark entity except for player to be delete
            entitiesToDelete.forEach(e -> e.setShouldRemove(true));
        });
    }

}

package dungeonmania.models.StaticEntities;

import java.util.stream.Collectors;

import org.json.JSONObject;

import dungeonmania.models.Dungeon;
import dungeonmania.models.Entity;
import dungeonmania.models.Player;
import dungeonmania.models.MovingEntities.Character;
import dungeonmania.models.MovingEntities.Spider;
import dungeonmania.models.MovingEntities.Zombie;
import dungeonmania.util.Direction;
import dungeonmania.util.Position;

public class Portal extends Entity {
    private final String colour; 

    /**
     * Constructor
     */
    public Portal(String id, String type, Position position, String colour) {
        super(id, type, position, false);
        this.colour = colour; 
    }
    /**
     * Constructor for load/save
     * @param json
     */
    public Portal(JSONObject json) {
        super(json);
        colour = json.getString("colour");
    }

    /**
     * Getter for colour
     */
    public String getColour() {
        return colour; 
    }

    /**
     * Moves Entity to corresponding portal
     * @param entity
     * @param direction
     * @param dungeon
     */
    public void teleport(Entity entity, Direction direction, Dungeon dungeon){
        
        if (!(entity instanceof Spider || entity instanceof Zombie)){
            Portal connectedPortal = (Portal) dungeon.getEntities().stream()
                                    .filter(e -> ((e instanceof Portal) && !e.equals(this) && ((Portal) e).getColour().equals(colour)))
                                    .collect(Collectors.toList()).get(0); 
        
            entity.setPosition(connectedPortal.getPosition());
    
            if (entity instanceof Player){
                ((Player) entity).playerMove(direction); 
            } else {
                ((Character) entity).characterMove(direction);
            }
        }
    }

    @Override
    public JSONObject getJSON() {
        JSONObject jsonEntity = super.getJSON();
        jsonEntity.put("colour", colour);
        return jsonEntity;
    }
    
}

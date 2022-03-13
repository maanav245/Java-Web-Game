package dungeonmania.models;

import dungeonmania.response.models.EntityResponse;
import dungeonmania.response.models.ItemResponse;
import dungeonmania.util.Position;

import org.json.JSONObject;

public abstract class Entity {

    private String id;
    private Position position;
    private String type;
    private boolean isInteractable = false;
    private Boolean shouldRemove = false;

    /**
     * The constructor used when the Character is created for the first time
     * 
     * @param id             The id of the Entity
     * @param type           The type of Entity
     * @param position       Where the Entity is located
     * @param isInteractable If the Entity is interactable
     */
    public Entity(String id, String type, Position position, boolean isInteractable) {
        this.id = id;
        this.type = type;
        this.position = position;
        this.isInteractable = isInteractable;
    }

    /**
     * The constructor used when the Entityr loaded from a saved game
     * 
     * @param json The coresponding json object
     */
    public Entity(JSONObject json) {
        this.id = json.getString("id");
        this.type = json.getString("type");
        this.position = new Position(json.getInt("x"), json.getInt("y"));
    }

    /**
     * Gets id for entity for dungeon
     * 
     * @return String
     */
    // ------------------------------- getters and setters
    // --------------------------------------//

    public String getId() {
        return this.id;
    }

    /**
     * Gets type of entity important for frontend
     * 
     * @return String
     */
    public String getType() {
        return this.type;
    }

    /**
     * sets the type of entity
     * 
     * @param type
     */
    public void setType(String type) {
        this.type = type;
    }

    /**
     * sets the id
     * 
     * @param id
     */
    public void setId(String id) {
        this.id = id;
    }

    /**
     * Find the position of the entity
     * 
     * @return Position
     */
    public Position getPosition() {
        return this.position;
    }

    /**
     * Checks if entity is interactable from the frontend
     * 
     * @return boolean
     */
    public boolean isInteractable() {
        return this.isInteractable;
    }

    /**
     * Sets if entity is interactable
     * 
     * @param value
     */
    public void setIsInteractable(boolean value) {
        isInteractable = value;
    }

    /**
     * sets position
     * 
     * @param position
     */
    public void setPosition(Position position) {
        this.position = position;
    }

    // ------------------------------- other functions
    // --------------------------------------//

    /**
     * Says if an enity is in the same location as it
     * 
     * @param other The other entity
     * @return true if they are in the same XY location
     */
    public boolean sameXYPosition(Entity other) {
        return getPosition().getX() == other.getPosition().getX() && getPosition().getY() == other.getPosition().getY();
    }

    /**
     * Checks if entity should be removed from map
     * 
     * @return Boolean
     */
    public Boolean getShouldRemove() {
        return this.shouldRemove;
    }

    /**
     * Sets if entity should be removed
     * 
     * @param shouldRemove
     */
    public void setShouldRemove(Boolean shouldRemove) {
        this.shouldRemove = shouldRemove;
    }

    /**
     * @return An Item response of the entity
     */
    public ItemResponse createItemResponse() {
        return new ItemResponse(id, type);
    }

    /**
     * @return An entity reponse for the entity
     */
    public EntityResponse createEntityResponse() {
        return new EntityResponse(id, type, position, isInteractable);
    }

    /**
     * function to override in classes for additional info, e.g. health
     * 
     * @return
     */
    public JSONObject getJSON() {
        JSONObject jsonEntity = new JSONObject();
        jsonEntity.put("x", position.getX());
        jsonEntity.put("y", position.getY());
        jsonEntity.put("type", type);
        jsonEntity.put("id", id); 
        return jsonEntity; 
    }  


    @Override
    public boolean equals(Object obj) {
        if (obj == null)
            return false;
        if (obj == this)
            return true;

        if (this.getClass() != obj.getClass())
            return false;

        Entity other = (Entity) obj;
        if (this.type.equals(other.type) && this.position.getX() == other.position.getX() && this.position.getY() == other.position.getY() ) {
            return true;
        } else {
            return false;
        }

    }
}
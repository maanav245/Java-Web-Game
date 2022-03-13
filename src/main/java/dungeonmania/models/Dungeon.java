package dungeonmania.models;

import dungeonmania.models.Goals.GoalComponent;
import dungeonmania.models.Modes.GameModeFactory;
import dungeonmania.response.models.AnimationQueue;
import dungeonmania.response.models.DungeonResponse;
import dungeonmania.response.models.EntityResponse;
import dungeonmania.util.Position;
import dungeonmania.models.MovingEntities.Character;
import dungeonmania.models.StaticEntities.Switch;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import org.json.JSONArray;

public class Dungeon implements MovementSubject {

    private final String dungeonId;
    private final String dungeonName;
    private GoalComponent goals;
    private GameModeFactory gameMode;
    private List<Entity> entities;
    private int width;
    private int height;
    private List<AnimationQueue> animations;
    private int tickCounter;
    private Player player;
    private Player oldPlayer; 
    private Position entryPosition;

    /**
     * Constructor for Dungeon
     * 
     * @param dungeonId
     * @param dungeonName
     * @param entities
     * @param goals
     */
    public Dungeon(String dungeonId, String dungeonName, List<Entity> entities, GoalComponent goals) {
        this(dungeonId, dungeonName, entities, goals, new ArrayList<>());
    }

    /**
     * Constructor for Dungeon - only used by dungeonbuilder, need to add player and goals in post
     * 
     * @param dungeonId
     * @param dungeonName
     * @post add player, goal to complete basic dungon in client
     */
    public Dungeon(String dungeonId, String dungeonName) {
        this.dungeonId = dungeonId;
        this.dungeonName = dungeonName;
    }

    public void setPlayer() {
        this.player = (Player) entities.stream().filter(entity -> entity.getType().equals("player"))
                .collect(Collectors.toList()).get(0);
    }

    /**
     * Constructor for Dungeon with animations
     * 
     * @param dungeonId
     * @param dungeonName
     * @param entities
     * @param goals
     * @param animations
     */
    public Dungeon(String dungeonId, String dungeonName, List<Entity> entities, GoalComponent goals,
            List<AnimationQueue> animations) {
        this.dungeonId = dungeonId;
        this.dungeonName = dungeonName;
        this.entities = entities;
        this.goals = goals;
        this.animations = animations;
        setPlayers();
        player.setDungeon(this);
        setEntryPosition(player.getPosition());
    }

    /**
     * Setter for GoalComponent
     */
    public void setGoalComponent(GoalComponent goals) {
        this.goals = goals;
    }

    /**
     * increment internal number of ticks, used for spawning enemines
     */
    public void iterateTickCounter() {
        int currTick = this.getTickCounter();
        this.setTickCounter(currTick + 1);
    }

    /**
     * remove an Entity from this Dungeon
     * 
     * @param entity
     */
    public void removeEntity(Entity entity) {
        this.entities.remove(entity);
    }

    /**
     * add an Entity to this Dungeon
     * 
     * @param entity
     */
    public void addEntity(Entity entity) {
        this.entities.add(entity);
    }

    /**
     * update the position of an entity
     * 
     * @param index
     * @param position
     */
    public void setEntityPosition(int index, Position position) {
        this.entities.get(index).setPosition(position);
    }

    /**
     * Removes any entity that has been marked for 'death'/ bomb explosion
     */
    public void removeList() {
        List<Entity> remove = new ArrayList<>();
        List<Entity> add = new ArrayList<>();
        for (Entity e : entities) {
            if (e.getShouldRemove()) {
                remove.add(e);
                if (e instanceof Character) {
                    Character character = (Character) e;
                    List<Entity> inv = character.getInventory().getInventoryInfo();
                    for (Entity item : inv) {
                        item.setPosition(character.getPosition());
                    }
                    add.addAll(inv);
                    removeObserver(character);
                } else if (e instanceof Player) {
                    Player player = (Player) e;
                    List<Entity> inv = player.getInventory().getInventoryInfo();
                    for (Entity item : inv) {
                        item.setPosition(player.getPosition());
                    }
                    add.addAll(inv);
                }

            }
        }
        entities.addAll(add);
        entities.removeAll(remove);
    }

    /**
     * Convert this Dungeon to a DungeonResponse
     * 
     * @return
     */
    public DungeonResponse returnDungeonResponse() throws IllegalArgumentException {
        List<EntityResponse> entitiesResponse = new ArrayList<>();
        entities.forEach(e -> entitiesResponse.add(e.createEntityResponse()));
        String goalStringEmptyIfAllCompleted;
        if (this.goals == null) {
            goalStringEmptyIfAllCompleted = "";
        } else {
            goalStringEmptyIfAllCompleted = this.goals.isComplete(this.entities) ? "" : this.goals.toString();
        }
        if (this.dungeonId == null) {
            throw new IllegalArgumentException("1");
        }
        if (this.dungeonName == null) {
            throw new IllegalArgumentException("2");
        }
        if (entitiesResponse == null) {
            throw new IllegalArgumentException("3");
        }
        if (this.player.getInventory().getInventoryResponse() == null) {
            throw new IllegalArgumentException("4");
        }
        if (this.getBuildables() == null) {
            throw new IllegalArgumentException("5");
        }
        if (goalStringEmptyIfAllCompleted == null) {
            throw new IllegalArgumentException("6");
        }
        /*
        if (this.animations == null) {
            throw new IllegalArgumentException("7");
        }
        */
        return new DungeonResponse(this.dungeonId, this.dungeonName, entitiesResponse, this.player.getInventory().getInventoryResponse(), this.getBuildables(), goalStringEmptyIfAllCompleted, this.animations);
    }

    /**
     * Get all Entities in Dungeon at the given position
     * 
     * @param position
     * @return
     */
    public List<Entity> getEntitiesAtPosition(Position position) {
        return entities.stream().filter(entity -> entity.getPosition().equals(position)).collect(Collectors.toList());
    }

    /**
     * Get the Entities adjacent to the player in the Dungeon
     * 
     * @return
     */
    public List<Entity> getAdjacentEntities() {
        return player.getPosition().getAdjacentPositions().stream().map(p -> getEntitiesAtPosition(p))
                .flatMap(List::stream).collect(Collectors.toList());
    }

    /**
     * Spawn all different types of enemies at 'tick' intervals guided by spec
     */
    public void spawnCharacters() {
        getGameModeFactory().spawnCharacters(getTickCounter());
    }

    /**
     * Updates all the Characters after a tick has occurred so they move according
     * to spec
     */
    @Override
    public void notifyObservers() {
        for (Entity entity : entities) {
            if (entity instanceof MovementObserver) {
                ((MovementObserver) entity).updateMovement();
            }
        }
    }

    // ------------------------------- getters and setters
    // --------------------------------------//
    /**
     * Get GameMode
     * 
     * @return
     */
    public GameModeFactory getGameModeFactory() {
        return gameMode;
    }

    /**
     * Set GameMode
     * 
     * @return
     */
    public void setGameModeFactory(GameModeFactory newGameMode) {
        this.gameMode = newGameMode;
    }

    /**
     * @param entities the entities to set
     */
    public void setEntities(List<Entity> entities) {
        this.entities = entities;
    }

    /**
     * @return int return the width
     */
    public int getWidth() {
        return width;
    }

    /**
     * @param width the width to set
     */
    public void setWidth(int width) {
        this.width = width;
    }

    /**
     * @return int return the height
     */
    public int getHeight() {
        return height;
    }

    /**
     * @param height the height to set
     */
    public void setHeight(int height) {
        this.height = height;
    }

    /**
     * @return List<String> return the buildables
     */
    public List<String> getBuildables() {
        return this.player.getInventory().getBuildables();
    }

    public Entity getEntity(String entityId) {
        return entities.stream().filter(entity -> entity.getId().equals(entityId)).collect(Collectors.toList()).get(0);
    }

    public int getTickCounter() {
        return tickCounter;
    }

    public void setTickCounter(int tickCounter) {
        this.tickCounter = tickCounter;
    }

    public String getGameMode() {
        return this.getGameModeFactory().toString();
    }

    public Position getEntryPosition() {
        return entryPosition;
    }

    public void setEntryPosition(Position entryPosition) {
        this.entryPosition = entryPosition;
    }

    public void setAnimations(List<AnimationQueue> animations) {
        this.animations = animations;
    }

    public List<AnimationQueue> getAnimations() {
        return this.animations;
    }

    /**
     * Return pointer from entity list
     * 
     * @return
     */
    public Player getPlayer(boolean isOld) {
        return (isOld) ? oldPlayer : player; 
    }

    public Player getPlayer() {
        return player; 
    }

    public void setPlayers(){
        try {
            player = (Player)this.entities.stream().filter(entity -> entity.getType().equals("player")).collect(Collectors.toList()).get(0); 
        } catch (Exception e) {
            player = (Player)this.entities.stream().filter(entity -> entity.getType().equals("older_player")).collect(Collectors.toList()).get(0); ; 
        }

        try {
            oldPlayer = (Player)this.entities.stream().filter(entity -> entity.getType().equals("older_player")).collect(Collectors.toList()).get(0); 
        } catch (Exception e) {
            oldPlayer = null; 
        }
        
    }

    public boolean isPlayerRemovedGameLost() {
        if (this.entities.stream().filter(entity -> entity.getType().equals("player")).collect(Collectors.toList())
                .size() == 0) {
            return true;
        }
        return false;
    }

    public String getDungeonName() {
        return this.dungeonName;
    }

    public GoalComponent getGoals() {
        return this.goals;
    }

    public String getDungeonId() {
        return this.dungeonId;
    }

    public List<Entity> getEntities() {
        return this.entities;
    }

}

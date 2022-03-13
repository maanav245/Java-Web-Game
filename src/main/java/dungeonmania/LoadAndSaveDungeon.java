package dungeonmania;

import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.json.JSONTokener;

import dungeonmania.exceptions.InvalidActionException;
import dungeonmania.models.Dungeon;
import dungeonmania.models.Entity;
import dungeonmania.models.MovementObserver;
import dungeonmania.models.Player;
import dungeonmania.models.Goals.*;
import dungeonmania.models.Modes.GameModeFactory;
import dungeonmania.models.Modes.HardMode;
import dungeonmania.models.Modes.PeacefulMode;
import dungeonmania.models.Modes.StandardMode;
import dungeonmania.models.MovingEntities.*;
import dungeonmania.models.StaticEntities.*;
import dungeonmania.util.FileLoader;
import dungeonmania.util.Position;
import dungeonmania.models.MovingEntities.Character;

public class LoadAndSaveDungeon {

    /**
     * Creates a dungon of the type of dungeon given
     * 
     * @param dungeonName The dungeon template to be used
     * @return The new dungeon
     */
    public static Dungeon createDungeon(String dungeonName, String gameMode) {
        JSONObject gameMaze;
        try {
            gameMaze = parseFromJSON(FileLoader.loadResourceFile("/dungeons/" + dungeonName + ".json"));
        } catch (Exception e) {
            throw new IllegalArgumentException("Invalid dungeon name");
        }
        String dungeonID =  UUID.randomUUID().toString();
        List<Entity> entities = loadDungeonEntities(true, gameMaze, gameMode);

        GoalComponent goals;
        if (gameMaze.has("goal-condition")) {
            // check if there are goals
            GoalFactory goalsFactory = new GoalFactory();
            goals = goalsFactory.fromJSON(gameMaze.getJSONObject("goal-condition"), entities);
        } else {
            goals = null;
        }

        Dungeon newDungeon = new Dungeon(dungeonID, dungeonName, entities, goals);
        try {
            newDungeon.setWidth(gameMaze.getInt("width"));
            newDungeon.setHeight(gameMaze.getInt("height"));
        } catch (Exception e) {
            newDungeon.setWidth(50);
            newDungeon.setHeight(50);
        }
        

        for (Entity e : entities) {
            if (e instanceof Character) {
                ((Character) e).setDungeon(newDungeon);
                newDungeon.getPlayer().registerObserver((MovementObserver) e);
            }
        }
        return newDungeon;
    }

    /**
     * Loads a dungon frm the game id
     * 
     * @param name the dungon id
     * @return The new dungeon
     */
    public static Dungeon loadDungeon(JSONObject gameMaze) {

        String dungeonName = gameMaze.getString("dungeonName");
        String gameModeType = gameMaze.getString("game_mode");
        List<Entity> entities = loadDungeonEntities(false, gameMaze, gameModeType);

        GoalComponent goals;
        if (gameMaze.has("goal-condition")) {
            GoalFactory goalsFactory = new GoalFactory();
            goals = goalsFactory.fromJSON(gameMaze.getJSONObject("goal-condition"), entities);
        } else {
            goals = null;
        }

        Dungeon newDungeon = new Dungeon(gameMaze.getString("id"), dungeonName, entities, goals);
        if (gameMaze.has("width")) {
            newDungeon.setWidth(gameMaze.getInt("width"));
        } else {
            newDungeon.setWidth(50); // set to max size
        }
        if (gameMaze.has("height")) {
            newDungeon.setHeight(gameMaze.getInt("height"));
        } else {
            newDungeon.setHeight(50);
        }

        newDungeon.setTickCounter(gameMaze.getInt("tickCounter"));

        for (Entity e : entities) {
            if (e instanceof Character) {
                ((Character) e).setDungeon(newDungeon);
                newDungeon.getPlayer().registerObserver((MovementObserver) e);
            }
        }

        newDungeon.setGameModeFactory(returnGameMode(gameModeType, newDungeon));
        newDungeon = returnGameMode(gameModeType, newDungeon).loadGame();

        // TODO: Reconsider this design
        // Load allies into player class (had to do this to ensure battle interface code
        // works)
        // If I regenerate them inside player, they will have different memory addresses
        List<String> allyListID = newDungeon.getPlayer().getAlliesID();
        String sceptredEnemy = newDungeon.getPlayer().getSceptredEnemyID();
        for (Entity ally : newDungeon.getEntities()) {
            if (allyListID.contains(ally.getId())) {
                newDungeon.getPlayer().addAlly(ally);
            }
            if (ally.getId().equals(sceptredEnemy)) {
                newDungeon.getPlayer().setSceptredEnemy(ally);
            }
        }

        return newDungeon;
    }

    /**
     * Turns a json file into a json object
     * 
     * @param path The path to the file from src
     * @return The json object
     * @throws IllegalArgumentException
     */
    private static JSONObject parseFromJSON(String path) throws IllegalArgumentException {
        try {
            JSONObject json = new JSONObject(new JSONTokener(path));
            return json;
        } catch (Exception e) {
            throw new IllegalArgumentException("Invalid dungeon name");
        }
    }

    /**
     * Turns a dungeon into a json object
     * 
     * @param dungeon The dungeon to convert
     * @return A json object of the dungeon
     */
    public static JSONObject convertDungetonToJSON(Dungeon dungeon) {
        // create empty object
        JSONObject json = new JSONObject();
        JSONArray jsonEntitiesArray = new JSONArray();
        // convert all entities
        List<Entity> dEntities = dungeon.getEntities();
        dEntities.forEach(e -> jsonEntitiesArray.put(e.getJSON()));

        // convert goals back to json format
        if (dungeon.getGoals() != null) {
            GoalFactory goalsFactory = new GoalFactory();
            JSONObject goals = goalsFactory.toJSON(dungeon.getGoals());
            json.put("goal-condition", goals);
        }

        // place converted entities in return json
        json.put("entities", jsonEntitiesArray);
        json.put("dungeonName", dungeon.getDungeonName());
        json.put("height", dungeon.getHeight());
        json.put("width", dungeon.getWidth());
        json.put("game_mode", dungeon.getGameMode());
        json.put("id", dungeon.getDungeonId());
        json.put("tickCounter", dungeon.getTickCounter()); 

        return json;
    }

    /**
     * Translates a valid JSONObject maze for our game into valid classes used in
     * our Dungeon
     * 
     * @param gameMaze
     * @pre gameMaze is itself a valid, parsed JSONObject
     * @post a Dungeon object is created from extracting the relevant
     *       entities/goals/other fields, for this to be used in a 'newGame'/
     *       'loadGame'
     * @return List of Entities for the creation of a new/ loading of a previous
     *         game
     */
    private static List<Entity> loadDungeonEntities(boolean isNew, JSONObject gameMaze, String gameModeType) {
        JSONArray entitiesJSON = gameMaze.getJSONArray("entities");
        List<Entity> entities = new ArrayList<Entity>();
        entitiesJSON.forEach(obj -> entities.add(createNewEntity(isNew, (JSONObject) obj, gameModeType)));
        return entities;
    }

    /**
     * Loads one from saved game
     * 
     * @param json The json object for the entity
     * @return An entity
     */
    public static Entity createNewEntity(JSONObject json) {

        String type = json.getString("type");

        switch (type) {
        case "key":
            return new Key(json);
        case "player":
            return new Player(json);
        case "wall":
            return new Wall(json);
        case "exit":
            return new Exit(json);
        case "treasure":
            return new Treasure(json);
        case "boulder":
            return new Boulder(json);
        case "arrow":
            return new Arrows(json);
        case "armour":
            return new BodyArmour(json);
        case "bomb":
            return new Bomb(json);
        case "bow":
            return new Bow(json);
        case "shield":
            return new Shield(json);
        case "health_potion":
            return new HealthPotion(json);
        case "invincibility_potion":
            return new InvincibilityPotion(json);
        case "invisibility_potion":
            return new InvisibilityPotion(json);
        case "sword":
            return new Sword(json);
        case "anduril":
            return new AndurilSword(json);
        case "one_ring":
            return new TheOneRing(json);
        case "wood":
            return new Wood(json);
        case "sun_stone":
            return new Sunstone(json);
        case "midnight_armour":
            return new MidnightArmour(json);
        case "sceptre":
            return new Sceptre(json);
        case "time_turner":
            return new TimeTurner(json);
        }

        return null;

    }

    /**
     * Creates either a new Entity or loads one from saved game
     * 
     * @param isNew Is it a new game
     * @param json  The json object for the entity
     * @return An entity
     */
    public static Entity createNewEntity(boolean isNew, JSONObject json, String gameModeType) {
        String type = json.getString("type");
        int x = json.getInt("x");
        int y = json.getInt("y");
        String id = UUID.randomUUID().toString();
        Position position = new Position(x, y);

        switch (type) {
        case "door":
            return (isNew) ? new Door(id, position, json.getInt("key")) : new Door(json);
        case "key":
            return (isNew) ? new Key(id, position, json.getInt("key")) : new Key(json);
        case "swamp_tile":
            return (isNew) ? new SwampTile(id, position, json.getInt("movement_factor")) : new SwampTile(json);
        case "player":
            return (isNew) ? new Player(id, position) : new Player(json);
        case "older_player":
            Player player = (isNew) ? new Player(id, position) : new Player(json);
            player.setType("older_player");
            return player; 
        case "time_travelling_portal":
            return (isNew) ? new TimeTravelingPortal(id, position) : new TimeTravelingPortal(json);
        case "wall":
            return (isNew) ? new Wall(id, position) : new Wall(json);
        case "exit":
            return (isNew) ? new Exit(id, position) : new Exit(json);
        case "treasure":
            return (isNew) ? new Treasure(id, position) : new Treasure(json);
        case "boulder":
            return (isNew) ? new Boulder(id, position) : new Boulder(json);
        case "arrow":
            return (isNew) ? new Arrows(id, position) : new Arrows(json);
        case "armour":
            return (isNew) ? new BodyArmour(id, position) : new BodyArmour(json);
        case "bomb":
            return (isNew) ? new Bomb(id, position) : new Bomb(json);
        case "bow":
            return (isNew) ? new Bow(id, position) : new Bow(json);
        case "shield":
            return (isNew) ? new Shield(id, position) : new Shield(json);
        case "health_potion":
            return (isNew) ? new HealthPotion(id, position) : new HealthPotion(json);
        case "invincibility_potion":
            return (isNew) ? new InvincibilityPotion(id, position) : new InvincibilityPotion(json);
        case "invisibility_potion":
            return (isNew) ? new InvisibilityPotion(id, position) : new InvisibilityPotion(json);
        case "sword":
            return (isNew) ? new Sword(id, position) : new Sword(json);
        case "anduril":
            return (isNew) ? new AndurilSword(id, position) : new AndurilSword(json);
        case "one_ring":
            return (isNew) ? new TheOneRing(id, position) : new TheOneRing(json);
        case "wood":
            return (isNew) ? new Wood(id, position) : new Wood(json);
        case "sun_stone":
            return (isNew) ? new Sunstone(id, position) : new Sunstone(json);
        case "sceptre":
            return (isNew) ? new Sceptre(id, position) : new Sceptre(json);
        case "midnight_armour":
            return (isNew) ? new MidnightArmour(id, position) : new MidnightArmour(json);
        case "time_turner":
            return (isNew) ? new TimeTurner(id, position) : new TimeTurner(json);
        case "zombie_toast_spawner":
            return (isNew) ? new ZombieToasterSpawner(id, position) : new ZombieToasterSpawner(json);
        case "zombie_toast":
            if (gameModeType.equals("standard")) {
                return (isNew) ? new StandardZombie(id, position) : new StandardZombie(json);
            } else if (gameModeType.equals("peaceful")) {
                return (isNew) ? new PeacefulZombie(id, position) : new PeacefulZombie(json);
            } else {
                return (isNew) ? new HardZombie(id, position) : new HardZombie(json);
            }

        case "spider":
            if (gameModeType.equals("standard")) {
                return (isNew) ? new StandardSpider(id, position) : new StandardSpider(json);
            } else if (gameModeType.equals("peaceful")) {
                return (isNew) ? new PeacefulSpider(id, position) : new PeacefulSpider(json);
            } else {
                return (isNew) ? new HardSpider(id, position) : new HardSpider(json);
            }
        case "mercenary":
            if (gameModeType.equals("standard")) {
                return (isNew) ? new StandardMercenary(id, position) : new StandardMercenary(json);
            } else if (gameModeType.equals("peaceful")) {
                return (isNew) ? new PeacefulMercenary(id, position) : new PeacefulMercenary(json);
            } else {
                return (isNew) ? new HardMercenary(id, position) : new HardMercenary(json);
            }
        case "assassin":
            if (gameModeType.equals("standard")) {
                return (isNew) ? new StandardAssassin(id, position) : new StandardAssassin(json);
            } else if (gameModeType.equals("peaceful")) {
                return (isNew) ? new PeacefulAssassin(id, position) : new PeacefulAssassin(json);
            } else {
                return (isNew) ? new HardAssassin(id, position) : new HardAssassin(json);
            }
        case "hydra":
            if (gameModeType.equals("hard")) {
                return (isNew) ? new Hydra(id, position) : new Hydra(json);
            }
        case "switch":
            return (isNew) ? new Switch(id, position) : new Switch(json);
        case "portal":
            if (json.getString("colour").equals("RED")) {
                type = "portal_red";
            } else if (json.getString("colour").equals("BLUE")) {
                type = "portal_blue";
            } else {
                type = "portal";
            }
            return new Portal(id, type, position, json.getString("colour"));
        // ones that we have added to change the look of the front end
        case "door_unlocked":
            return new Door(json);
        case "portal_red":
            return new Portal(json);
        case "portal_blue":
            return new Portal(json);
        }
        return null;

    }

    public static GameModeFactory returnGameMode(String gameModeType, Dungeon newDungeon) {
        gameModeType = gameModeType.toLowerCase(); 
        GameModeFactory gMode;
        switch (gameModeType) {
            case "standard":
                gMode = new StandardMode(newDungeon);
                break;
            case "peaceful":
                gMode = new PeacefulMode(newDungeon);
                break;
            case "hard":
                gMode = new HardMode(newDungeon);
                break;
            default:
                throw new IllegalArgumentException("unmatched game mode type: " + gameModeType);

        }
        newDungeon.setGameModeFactory(gMode);
        return gMode;
    }

}

package dungeonmania;

import dungeonmania.exceptions.InvalidActionException;
import dungeonmania.models.*;
import dungeonmania.models.Goals.GoalComponent;
import dungeonmania.models.Modes.GameModeFactory;
import dungeonmania.models.Modes.HardMode;
import dungeonmania.models.StaticEntities.Bomb;
import dungeonmania.models.StaticEntities.HealthPotion;
import dungeonmania.models.StaticEntities.InvincibilityPotion;
import dungeonmania.models.StaticEntities.InvisibilityPotion;
import dungeonmania.models.StaticEntities.MidnightArmour;
import dungeonmania.models.StaticEntities.Sceptre;
import dungeonmania.response.models.DungeonResponse;
import dungeonmania.models.MovingEntities.Character;
import dungeonmania.models.StaticEntities.Bow;
import dungeonmania.models.StaticEntities.Shield;
import dungeonmania.models.StaticEntities.TimeTravelingPortal;
import dungeonmania.models.StaticEntities.TimeTurner;
import dungeonmania.util.Direction;
import dungeonmania.util.FileLoader;
import dungeonmania.util.Position;

import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.UUID;
import java.util.Random;
import java.util.stream.Collectors;

import org.json.JSONArray;
import org.json.JSONObject;
import org.json.JSONTokener;

public class DungeonManiaController {
    // Changed list of dungeons into hashmap for easier 'getting'
    // the key is dungeonId
    private HashMap<String, Dungeon> dungeonGames = new HashMap<>();
    private Dungeon currDungeon;
    private Dungeon oneTickAway = null;
    private Dungeon fiveTickAway = null;
    private Dungeon thirtyTickAway = null;
    private ArrayList<Direction> timerDirection = new ArrayList<Direction>();
    private ArrayList<String> timerUserInput = new ArrayList<String>();
    private ArrayList<String> interactions = new ArrayList<String>();
    private ArrayList<String> builds = new ArrayList<String>();
    private boolean timeTraveling = false; 
    private int timetravelCounter = -1; 

    public String getSkin() {
        return "default";
    }

    public String getLocalisation() {
        return "en_US";
    }

    public List<String> getGameModes() {
        return Arrays.asList("standard", "peaceful", "hard");
    }

    public Dungeon getDungeon(String name) {
        return dungeonGames.get(name);
    }

    /**
     * /dungeons
     * 
     * Done for you.
     */
    public static List<String> dungeons() {
        try {
            return FileLoader.listFileNamesInResourceDirectory("/dungeons");
        } catch (IOException e) {
            return new ArrayList<>();
        }
    }

    /**
     * Creates a new dungeon and a save file
     * 
     * @param dungeonName The name of the dungeon template to use
     * @param gameMode    The difficulty
     * @return The new dungeon as a dungeon response
     * @throws IllegalArgumentException
     */
    public DungeonResponse newGame(String dungeonName, String gameMode) throws IllegalArgumentException {
        timeTraveling = false; 
        timetravelCounter = -1; 
        gameMode = gameMode.toLowerCase(); 
        if (!getGameModes().contains(gameMode))
            throw new IllegalArgumentException("Not a game mode");
        currDungeon = LoadAndSaveDungeon.createDungeon(dungeonName, gameMode);
        setDungeon(dungeonName, gameMode, currDungeon);

        if (!currDungeon.getEntities().stream()
                .filter(entity -> entity instanceof TimeTravelingPortal || entity instanceof TimeTurner)
                .collect(Collectors.toList()).isEmpty()) {
            oneTickAway = LoadAndSaveDungeon.createDungeon(dungeonName, gameMode);
            setDungeon(dungeonName, gameMode, oneTickAway);
            setIDs(oneTickAway); 
            oneTickAway.getPlayer().setType("older_player");
            oneTickAway.setPlayers();
    
            fiveTickAway = LoadAndSaveDungeon.createDungeon(dungeonName, gameMode);
            setDungeon(dungeonName, gameMode, fiveTickAway); 
            setIDs(fiveTickAway); 
            fiveTickAway.getPlayer().setType("older_player");
            fiveTickAway.setPlayers();  
    
            thirtyTickAway = LoadAndSaveDungeon.createDungeon(dungeonName, gameMode); 
            setDungeon(dungeonName, gameMode, thirtyTickAway); 
            setIDs(thirtyTickAway); 
            thirtyTickAway.getPlayer().setType("older_player");
            thirtyTickAway.setPlayers();
        }

        return this.currDungeon.returnDungeonResponse();
    }

    public void setDungeon(String dungeonName, String gameMode, Dungeon newDungeon) {
        newDungeon.setTickCounter(0);
        newDungeon.setEntryPosition(newDungeon.getPlayer().getPosition());
        GameModeFactory mode = LoadAndSaveDungeon.returnGameMode(gameMode, newDungeon);
        newDungeon.setGameModeFactory(mode);
        dungeonGames.put(newDungeon.getDungeonId(), mode.createGame());
    }

    public void setIDs(Dungeon newDungeon) {
        for (Entity ent : currDungeon.getEntities()) {
            for (Entity ent1 : newDungeon.getEntities()) {
                if (ent.equals(ent1) && !(ent instanceof Player)) {
                    ent1.setId(ent.getId());
                    break;
                }
            }
        }
    }

    /**
     * @pre name is a unique string id for the dungeon game to be saved
     * @param name
     * @return
     * @throws IllegalArgumentException
     * @throws IOException
     */
    public DungeonResponse saveGame(String name) throws IllegalArgumentException {
        // write to json file in savedGames path, calling own helper to convert dungeon
        // to JSONObject
        JSONObject jsonToSave = new JSONObject();
        jsonToSave.put("currDungeon", LoadAndSaveDungeon.convertDungetonToJSON(currDungeon));
        if (oneTickAway != null) {
            jsonToSave.put("oneTickAway", LoadAndSaveDungeon.convertDungetonToJSON(oneTickAway));
            jsonToSave.put("fiveTickAway", LoadAndSaveDungeon.convertDungetonToJSON(fiveTickAway));
            jsonToSave.put("thirtyTickAway", LoadAndSaveDungeon.convertDungetonToJSON(thirtyTickAway));
        }
        jsonToSave.put("timeTraveling", timeTraveling);
        jsonToSave.put("timetravelCounter", timetravelCounter);

        ArrayList<String> directions = new ArrayList<String>();
        for (int i = 0; i < timerDirection.size(); i++) {
            if (timerDirection.get(i).equals(Direction.NONE))
                directions.add("NONE");
            if (timerDirection.get(i).equals(Direction.RIGHT))
                directions.add("RIGHT");
            if (timerDirection.get(i).equals(Direction.LEFT))
                directions.add("LEFT");
            if (timerDirection.get(i).equals(Direction.UP))
                directions.add("UP");
            if (timerDirection.get(i).equals(Direction.DOWN))
                directions.add("DOWN");
        }

        jsonToSave.put("timerDirection", new JSONArray(directions));
        jsonToSave.put("timerUserInput", new JSONArray(timerUserInput));
        jsonToSave.put("interactions", new JSONArray(interactions));
        jsonToSave.put("builds", new JSONArray(builds));

        FileWriter fw;
        try {
            Files.createDirectories(Paths.get("savedGames/"));
            fw = new FileWriter("savedGames/" + name + ".json");
            fw.write(jsonToSave.toString());
            fw.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
        // convert selected dungeon to DungeonResponse and return it
        return currDungeon.returnDungeonResponse();
    }

    /**
     * Loads a previously created or saved game
     * 
     * @param name The id of the game to load
     * @return The loaded dungeon as a dungeon response
     * @throws IllegalArgumentException
     */
    public DungeonResponse loadGame(String name) throws IllegalArgumentException {
        try {
            if (!FileLoader.listFileNamesInDirectoryOutsideOfResources("savedGames/").contains(name))
                throw new IllegalArgumentException("Not a saved game");
        } catch (IOException e) {
            throw new InvalidActionException("Not a valid directory");
        }

        try {
            JSONObject gameMaze = new JSONObject(new JSONTokener(new FileReader("savedGames/" + name + ".json")));
            currDungeon = LoadAndSaveDungeon.loadDungeon(gameMaze.getJSONObject("currDungeon"));
            dungeonGames.put(name, currDungeon);
            if (gameMaze.has("oneTickAway")) {
                oneTickAway = LoadAndSaveDungeon.loadDungeon(gameMaze.getJSONObject("oneTickAway"));
                fiveTickAway = LoadAndSaveDungeon.loadDungeon(gameMaze.getJSONObject("fiveTickAway"));
                thirtyTickAway = LoadAndSaveDungeon.loadDungeon(gameMaze.getJSONObject("currDungeon"));
            }

            JSONArray jArray = gameMaze.getJSONArray("timerDirection");
            timerDirection.clear();
            if (jArray != null) {
                for (int i = 0; i < jArray.length(); i++) {
                    if (jArray.getString(i).equals("NONE"))
                        timerDirection.add(Direction.NONE);
                    else if (jArray.getString(i).equals("RIGHT"))
                        timerDirection.add(Direction.RIGHT);
                    else if (jArray.getString(i).equals("LEFT"))
                        timerDirection.add(Direction.LEFT);
                    else if (jArray.getString(i).equals("UP"))
                        timerDirection.add(Direction.UP);
                    else if (jArray.getString(i).equals("DOWN"))
                        timerDirection.add(Direction.DOWN);
                }
            }
            jArray = gameMaze.getJSONArray("timerUserInput");
            timerUserInput.clear();
            if (jArray != null) {
                for (int i = 0; i < jArray.length(); i++) {
                    try {
                        timerUserInput.add(jArray.getString(i));
                    } catch (Exception e) {
                        timerUserInput.add(null);
                    }
                }
            }

            interactions.clear();
            jArray = gameMaze.getJSONArray("interactions");
            if (jArray != null) {
                for (int i = 0; i < jArray.length(); i++)
                    interactions.add(jArray.getString(i));
            }

            builds.clear();
            jArray = gameMaze.getJSONArray("builds");
            if (jArray != null) {
                for (int i = 0; i < jArray.length(); i++)
                    builds.add(jArray.getString(i));
            }

            timeTraveling = gameMaze.getBoolean("timeTraveling");
            timetravelCounter = gameMaze.getInt("timetravelCounter");


        } catch (Exception e) {
            throw new IllegalArgumentException("Not a saved game");
        }
        return currDungeon.returnDungeonResponse();
    }

    /**
     * Lists all of the saved games
     * 
     * @return
     */
    public List<String> allGames() {
        try {
            List<String> savedGames = FileLoader.listFileNamesInDirectoryOutsideOfResources("savedGames/");
            return savedGames;
        } catch (IOException e) {
            return new ArrayList<>();
        }
        // List<String> games = new ArrayList<>();
        // dungeonGames.keySet().forEach(keyId -> games.add(keyId));
        // return games;
    }

    /**
     * The main player interaction function
     * 
     * @param itemUsed          The item from the invantory to be used
     * @param movementDirection The movement direction
     * @return The updated dungeon response
     * @throws IllegalArgumentException If the item isnt in the players inventory
     * @throws InvalidActionException
     */
    public DungeonResponse tick(String itemUsed, Direction movementDirection)
            throws IllegalArgumentException, InvalidActionException {
        Position pos = currDungeon.getPlayer().getPosition().translateBy(movementDirection);
        List<Entity> entitiesAtPosition = currDungeon.getEntitiesAtPosition(pos);

        Boolean shouldTravel = false;
        Entity portal = null;
        for (Entity e : entitiesAtPosition) {
            if (e instanceof TimeTravelingPortal){
                if (timetravelCounter == -1 && currDungeon.getTickCounter() > 30 && thirtyTickAway != null) shouldTravel = true; 
                portal = e; 
            }
        }

        if (timetravelCounter > 0)
            timetravelCounter--;
        else if (timetravelCounter == 0) {
            timetravelCounter--;
            currDungeon.removeEntity(currDungeon.getPlayer(true));
            currDungeon.removeEntity(portal); 
            timeTraveling = false; 
            oneTickAway = null; 
            fiveTickAway = null; 
            thirtyTickAway = null; 
        }

        if(oneTickAway != null) setTimerDungeons(); 

        if (!shouldTravel) {
            if (timerDirection.size() == 30) {
                timerDirection.remove(29);
                timerUserInput.remove(29);
            }
            timerDirection.add(0, movementDirection);
            timerUserInput.add(0, itemUsed);
            interactions.add(0, "");
            builds.add(0, "");
            return dungeonTick(this.currDungeon, itemUsed, movementDirection, false);
        } else {
            Player player = currDungeon.getPlayer();
            int tickCounter = currDungeon.getTickCounter();
            currDungeon = thirtyTickAway;
            currDungeon.setTickCounter(tickCounter);
            currDungeon.addEntity(player);
            timetravelCounter = 30;
            player.setPosition(pos);
            currDungeon.setPlayers();
            player.setDungeon(currDungeon);
            timeTraveling = true;
            return currDungeon.returnDungeonResponse();
        }

    }

    private DungeonResponse dungeonTick(Dungeon currFakeDungeon, String itemUsed, Direction movementDirection,
            boolean old) {

        Player player = currFakeDungeon.getPlayer(old);

        Entity item = null;
        for (Entity entity : player.getInventory().getInventoryInfo()) {
            if (entity.getId().equals(itemUsed)) {
                item = entity;
            }
        }

        if((itemUsed != null) && !(itemUsed.equals(""))){
            if (item == null) throw new IllegalArgumentException();
        }

        // Potion stuff
        if (item instanceof HealthPotion) {
            player.setHP(player.getInitialHP());
            player.getInventory().useFromInventory(item);
        } else if (item instanceof InvisibilityPotion) {
            player.setPotionInUse((InvisibilityPotion) item);
            player.setPotionCounter(15);
            player.getInventory().useFromInventory(item);
        } else if (item instanceof InvincibilityPotion) {
            if (!(currFakeDungeon.getGameModeFactory() instanceof HardMode)) {
                player.setPotionInUse((InvincibilityPotion) item);
                player.setPotionCounter(15);
            }
            player.getInventory().useFromInventory(item);
        }

        if (!old) {
            // Spawn Characters
            currFakeDungeon.spawnCharacters();
            currFakeDungeon.getEntities().stream().filter(Character.class::isInstance)
                    .forEach(e -> ((Character) e).setDungeon(currFakeDungeon));
            player.notifyObservers();
        }

        // Move player
        if (player.playerMove(movementDirection) || player.getShouldRemove()) {
            // if player loses a battle, check if can respawn instead of removing
            if (!player.useOneRingToRespawn()) {
                currFakeDungeon.removeEntity(player);
                return currFakeDungeon.returnDungeonResponse();
            }

        }
        currDungeon.removeList();

        if (item instanceof Bomb) {
            // place bomb in player's new position
            player.placeBomb(currDungeon);
        }

        if (!old) {
            // update all the character movement
            currFakeDungeon.notifyObservers();
            currFakeDungeon.removeList();
            currFakeDungeon.iterateTickCounter();
        }
        return currFakeDungeon.returnDungeonResponse();
    }

    /**
     * Allows the frontend to interact with enities
     * 
     * @param entityId
     * @return DungeonResponse
     * @throws IllegalArgumentException
     * @throws InvalidActionException
     */
    public DungeonResponse interact(String entityId) throws IllegalArgumentException, InvalidActionException {
        if (interactions.size() == 0)
            interactions.add("");
        interactions.set(0, interactions.get(0) + "," + entityId);
        return dungeonInteract(entityId, currDungeon);
    }

    public DungeonResponse dungeonInteract(String entityId, Dungeon dungeon)
            throws IllegalArgumentException, InvalidActionException {
        // Check if entity id is valid (also assume that not interactable entities are
        // invalid)
        if (entityId.equals(""))
            return dungeon.returnDungeonResponse();

        if (!dungeon.getEntities().stream().anyMatch(e -> e.getId().equals(entityId) && e.isInteractable())) {
            throw new IllegalArgumentException("Illegal Arguments");
        }
        Player player = dungeon.getPlayer();
        player.playerInteracts(entityId);
        return dungeon.returnDungeonResponse();
    }

    /**
     * Allows the building of items
     * 
     * @param buildable, a string with the type of entity which can be buily
     * @return DungeonResponse
     * @throws IllegalArgumentException
     * @throws InvalidActionException
     */
    public DungeonResponse build(String buildable) throws IllegalArgumentException, InvalidActionException {
        builds.set(0, builds.get(0) + "," + buildable);
        return dungeonBuild(buildable, currDungeon);
    }

    public DungeonResponse dungeonBuild(String buildable, Dungeon dungeon)
            throws IllegalArgumentException, InvalidActionException {

        if (buildable.equals(""))
            return dungeon.returnDungeonResponse();

        Player p = dungeon.getPlayer();
        Inventory currInv = p.getInventory();
        Position pos = p.getPosition();

        // check that it is a buildble item
        if (buildable.equals("bow")) {
            Bow newBow = new Bow(UUID.randomUUID().toString(), pos);
            p.setInventory(newBow.build(currInv));
            p.addToInventory(newBow);
        } else if (buildable.equals("sceptre")) {
            Sceptre newSceptre = new Sceptre(UUID.randomUUID().toString(), pos);
            p.setInventory(newSceptre.build(currInv));
            p.addToInventory(newSceptre);
        } else if (buildable.equals("midnight_armour")) {
            boolean areZombies = dungeon.getEntities().stream().anyMatch(e -> e.getType().equals("zombie_toast"));
            if (!areZombies) {
                MidnightArmour newMidnightArmour = new MidnightArmour(UUID.randomUUID().toString(), pos);
                p.setInventory(newMidnightArmour.build(currInv));
                p.addToInventory(newMidnightArmour);
            } else {
                throw new InvalidActionException(
                        "Invalid. Can not build midnight armour when there is a zombie toast.");
            }
        } else if (buildable.equals("shield")) {
            Shield newShield = new Shield(UUID.randomUUID().toString(), pos);
            p.setInventory(newShield.build(currInv));
            p.addToInventory(newShield);
        } else {
            throw new IllegalArgumentException("Invalid Enity! Enitity given is not a buildable item.");
        }

        return dungeon.returnDungeonResponse();
    }

    /**
     * boolean returning if this dungeon has been won
     * 
     * @pre valid DungeonId already created by this DungeonManiaController instance
     * @param DungeonId
     * @return
     */
    public boolean isDungeonComplete(String DungeonId) {
        Dungeon currDungeon = dungeonGames.get(DungeonId);
        GoalComponent winCondition = currDungeon.getGoals();
        return winCondition.isComplete(currDungeon.getEntities());
    }

    public DungeonResponse generateDungeon(int xStart, int yStart, int xEnd, int yEnd, String gameMode) throws IllegalArgumentException {
        // Error checking
        if (!getGameModes().contains(gameMode)) {
            throw new IllegalArgumentException("Not a game mode");
        }
        DungeonBuilder generator = new DungeonBuilder(xStart, yStart, xEnd, yEnd, gameMode);
        this.currDungeon = generator.getResult();
        dungeonGames.put(this.currDungeon.getDungeonId(), this.currDungeon);
        return this.currDungeon.returnDungeonResponse();
    }

    public DungeonResponse rewind(int ticks) throws IllegalArgumentException {
        int tickCounter = currDungeon.getTickCounter(); 
        if (tickCounter < ticks) throw new IllegalArgumentException("You cant time travel to a time you have never been"); 
        
        Player player = currDungeon.getPlayer(); 
        Inventory inv = player.getInventory(); 
        Entity timeTurner = inv.getIfContains("time_turner").get(0);
        inv.useFromInventory(timeTurner);
        if (timeTurner == null){
            throw new IllegalArgumentException("No time turner in inventory"); 
        }

        currDungeon = (ticks == 5) ? fiveTickAway : oneTickAway;

        inv = currDungeon.getPlayer(true).getInventory(); 
        List<Entity> timeTurners = inv.getIfContains("time_turner"); 
        if (timeTurners.size() != 0){
            inv.useFromInventory(timeTurner);
        } else {
            currDungeon.removeEntity(timeTurner);
        }
        player.setDungeon(currDungeon); 
        currDungeon.setTickCounter(tickCounter);
        currDungeon.addEntity(player);
        timetravelCounter = ticks;
        currDungeon.setPlayers();
        player.setDungeon(currDungeon);
        timeTraveling = true; 
        return currDungeon.returnDungeonResponse(); 
    }

    private void setTimerDungeons() {
        if (currDungeon.getTickCounter() >= 1) {
            if (timeTraveling) dungeonTick(oneTickAway, timerUserInput.get(0), timerDirection.get(0), true); 
            else dungeonTick(oneTickAway, timerUserInput.get(0), timerDirection.get(0), false);
            for(String build : builds.get(0).split(",")) if (!build.equals("")) dungeonBuild(build, oneTickAway); 
            for(String interact : interactions.get(0).split(",")) if (!interact.equals("")) dungeonBuild(interact, oneTickAway);
        }
        if (currDungeon.getTickCounter() >= 5) {
            if (timeTraveling) dungeonTick(fiveTickAway, timerUserInput.get(4), timerDirection.get(4), true); 
            else dungeonTick(fiveTickAway, timerUserInput.get(4), timerDirection.get(4), false);
            for(String build : builds.get(4).split(",")) if (!build.equals("")) dungeonBuild(build, fiveTickAway);
            for(String interact : interactions.get(4).split(",")) if (!interact.equals("")) dungeonBuild(interact, fiveTickAway);
        } 
        if (currDungeon.getTickCounter() >= 30) {
            if (timeTraveling) dungeonTick(thirtyTickAway, timerUserInput.get(29), timerDirection.get(29), true); 
            else dungeonTick(thirtyTickAway, timerUserInput.get(29), timerDirection.get(29), false); 
            for(String build : builds.get(29).split(",")) if (!build.equals("")) dungeonBuild(build, thirtyTickAway); 
            for(String interact : interactions.get(29).split(",")) if (!interact.equals("")) dungeonBuild(interact, thirtyTickAway);
        }
    }

    public Dungeon getThirtyTickAway() {
        return thirtyTickAway;
    }

    public Dungeon getFiveTickAway() {
        return fiveTickAway;
    }

    public Dungeon getOneTickAway() {
        return oneTickAway;
    }

    public boolean isTimeTraveling() {
        return timeTraveling;
    }
}
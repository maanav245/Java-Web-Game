package dungeonmania.models.Modes;

import dungeonmania.models.Dungeon;
import dungeonmania.models.Entity;
import dungeonmania.models.MovementObserver;

import java.util.List;
import java.util.Random;
import java.util.stream.Collectors;

import dungeonmania.models.MovingEntities.Character;
import dungeonmania.models.MovingEntities.Spider;
import dungeonmania.models.MovingEntities.Zombie;
import dungeonmania.models.StaticEntities.Blockable;
import dungeonmania.util.Position;

public abstract class GameMode implements GameModeFactory {
    protected Dungeon dungeon;
    private Random rareObjectSeed = new Random(6);
    private Random armourSeed = new Random(99);
    private Random spawnEntitesSeed = new Random(3);
    private int freqZombies;
    private boolean BattleEnabled;
    private int maxSpiders = 6;
    private int spiderCounter = 0;
    private Random assassinSpawn = new Random();

    /**
     * Constuctor for GameMode
     * 
     * @param dungeon
     */
    GameMode(Dungeon dungeon) {
        this.dungeon = dungeon;
    }

    /**
     * Constuctor for GameMode
     * 
     * @param dungeon
     */
    GameMode(Dungeon dungeon, long randomAssassinSpawnSeed) {
        this.dungeon = dungeon;
        assassinSpawn = new Random(randomAssassinSpawnSeed);
    }

    /**
     * return the Created game, default values as Standard Mode unless overriden
     * 
     * @return the created dungeon
     */
    @Override
    public Dungeon createGame() {

        // set the HP & AP of all moving entities & player
        // generate random object of moving entities generated at the start of the game
        setFreqZombies(20);
        setBattleEnabled(true);

        Random rareObjectSeed = getRareObjectSeed();
        Random armourSeed = getArmourSeed();

        List<Character> characters = dungeon.getEntities().stream().filter(Character.class::isInstance)
                .map(e -> (Character) e).collect(Collectors.toList());

        characters.forEach(c -> {
            c.spawnWithRareObject(rareObjectSeed);
            c.spawnWithArmour(armourSeed);
        });
        return dungeon;
    }

    /**
     * load a saved dungeon game with default Standard game values for Character/
     * enemies unless overriden
     * 
     * @return
     */
    @Override
    public Dungeon loadGame() {
        setFreqZombies(20);
        setBattleEnabled(true);

        return dungeon;
    }

    @Override
    public void spawnCharacters(int tickCounter) {

        int numSpider = dungeon.getEntities().stream().filter(entity -> entity.getType().equals("spider"))
                .collect(Collectors.toList()).size();

        if (tickCounter == 3 && maxSpiders > 0) {
            this.spiderCounter = numSpider;
            int m = spawnEntitesSeed.nextInt(maxSpiders);
            for (int i = 0; i < m; i++) {
                spawnSpider(tickCounter);
            }
        } else if (tickCounter % 15 == 0 && this.spiderCounter < maxSpiders) {
            this.spiderCounter++;
            spawnSpider(tickCounter);
        }

        spawnZombie(tickCounter);
        spawnMercenary(tickCounter);
    }

    public abstract void spawnMercenary(int tickCounter);

    /**
     * Logic to spawn zombies, aka 'zombie_toast'
     */
    public void spawnZombie(int tickCounter) {
        int freq = getFreqZombies();
        List<Entity> zspawners = dungeon.getEntities().stream()
                .filter(entity -> entity.getType().equals("zombie_toast_spawner")).collect(Collectors.toList());
        if (tickCounter > 0 && tickCounter % freq == 0) {
            for (Entity zs : zspawners) {
                List<Position> adjacentPositions = zs.getPosition().getAdjacentPositions();
                List<Position> possibleSpawn = adjacentPositions.stream()
                        .filter(pos -> dungeon.getEntitiesAtPosition(pos).stream()
                                .allMatch(e -> (e instanceof Blockable)
                                        && !((Blockable) e).isBlocking(zs, dungeon.getEntities())))
                        .collect(Collectors.toList());
                if (!possibleSpawn.isEmpty()) {
                    Zombie newZombie = this.getNewZombie(possibleSpawn.get(0));
                    newZombie.spawnWithRareObject(getRareObjectSeed());
                    newZombie.spawnWithArmour(getArmourSeed());
                    newZombie.setBattleEnabled(isBattleEnabled());
                    dungeon.getPlayer().registerObserver((MovementObserver) newZombie);
                    dungeon.addEntity(newZombie);
                    return;
                }
            }
        }
    }

    /**
     * Logic to spawn spiders
     */
    public void spawnSpider(int tickCounter) {
        int x = getSpawnEntitesSeed().nextInt(dungeon.getWidth());
        int y = getSpawnEntitesSeed().nextInt(dungeon.getHeight());
        Position pos = new Position(x, y);
        Spider newSpider = getNewSpider(pos);
        dungeon.getEntitiesAtPosition(pos);
        List<Position> adjacentPositions = pos.getAdjacentPositions();

        // Ensure spider doesnt spawn at a blocked enitiy or next to a blocked entity
        int blocked = dungeon.getEntitiesAtPosition(pos).stream()
                .filter(e -> (e instanceof Blockable) && ((Blockable) e).isBlocking(newSpider, dungeon.getEntities()))
                .collect(Collectors.toList()).size();

        if (!pos.getAllPositions(dungeon.getWidth(), dungeon.getHeight()).stream()
                .filter(p -> dungeon.getEntitiesAtPosition(p).stream().anyMatch(
                        e -> (e instanceof Blockable) && ((Blockable) e).isBlocking(newSpider, dungeon.getEntities())))
                .collect(Collectors.toList()).isEmpty()) {
            setMaxSpiders(0);
        } else if (blocked > 0) {
            spawnSpider(tickCounter);
        } else {
            dungeon.addEntity(newSpider);
            newSpider.spawnWithRareObject(getRareObjectSeed());
            newSpider.setBattleEnabled(isBattleEnabled());
            dungeon.getPlayer().registerObserver((MovementObserver) newSpider);
        }
    }

    @Override
    public abstract String toString();

    @Override
    public boolean isBattleEnabled() {
        return BattleEnabled;
    }

    public void setBattleEnabled(boolean battleEnabled) {
        this.BattleEnabled = battleEnabled;
    }

    @Override
    public int getFreqZombies() {
        return freqZombies;
    }

    public void setFreqZombies(int freqZombies) {
        this.freqZombies = freqZombies;
    }

    /**
     * @return int return the maxSpiders
     */
    public int getMaxSpiders() {
        return maxSpiders;
    }

    /**
     * @param maxSpiders the maxSpiders to set
     */
    @Override
    public void setMaxSpiders(int maxSpiders) {
        this.maxSpiders = maxSpiders;
    }

    @Override
    public Random getRareObjectSeed() {
        return rareObjectSeed;
    }

    @Override
    public Random getArmourSeed() {
        return armourSeed;
    }

    /**
     * @param rareObjectSeed the rareObjectSeed to set
     */
    public void setRareObjectSeed(Random rareObjectSeed) {
        this.rareObjectSeed = rareObjectSeed;
    }

    @Override
    public Random getSpawnEntitesSeed() {
        return this.spawnEntitesSeed;
    }

    public abstract Spider getNewSpider(Position spawnPosition);

    public abstract Zombie getNewZombie(Position spawnPosition);

    /**
     * Setter for creating a Random seed
     * 
     * @param spawnEntitesSeed the spawnEntitesSeed to set
     */
    public void setSpawnEntitesSeed(Random spawnEntitesSeed) {
        this.spawnEntitesSeed = spawnEntitesSeed;
    }

    /**
     * @param armourSeed the armourSeed to set
     */
    public void setArmourSeed(Random armourSeed) {
        this.armourSeed = armourSeed;
    }

    /**
     * Getter for randomly spawning assassins instead of mercenaries
     */
    public Random getAssassinSpawnChance() {
        return assassinSpawn;
    }

    /**
     * Setter for randomly spawning assassins instead of mercenaries
     */
    public void setAssassinSpawnChance(long seed) {
        assassinSpawn = new Random(seed);
    }

    public Dungeon getDungeon() {
        return this.dungeon;
    }

    public void setDungeon(Dungeon dungeon) {
        this.dungeon = dungeon;
    }

}

package dungeonmania.models.Modes;

import java.util.List;
import java.util.Random;
import java.util.UUID;
import java.util.stream.Collectors;

import dungeonmania.models.Dungeon;
import dungeonmania.models.Entity;
import dungeonmania.models.MovingEntities.HardAssassin;
import dungeonmania.models.MovementObserver;
import dungeonmania.models.MovingEntities.HardMercenary;
import dungeonmania.models.MovingEntities.HardSpider;
import dungeonmania.models.MovingEntities.HardZombie;
import dungeonmania.models.MovingEntities.Hydra;
import dungeonmania.models.MovingEntities.Mercenary;
import dungeonmania.models.MovingEntities.Spider;
import dungeonmania.models.MovingEntities.Zombie;
import dungeonmania.models.StaticEntities.Blockable;
import dungeonmania.models.StaticEntities.InvincibilityPotion;
import dungeonmania.util.Position;

public class HardMode extends GameMode {
    private int freqHydra = 50;
    /**
     * Constructor for HardMode
     * 
     * @param dungeon
     */
    public HardMode(Dungeon dungeon) {
        super(dungeon);
    }

    /**
     * Creates game with hard mode values instead of default from GameMode
     */
    @Override
    public Dungeon createGame() {
        dungeon = super.createGame();
        super.setFreqZombies(15);
        dungeon.getPlayer().setHP(5);
        dungeon.getPlayer().setInitalHP(5);
        dungeon.getEntities().stream().filter(InvincibilityPotion.class::isInstance)
                .forEach(e -> ((InvincibilityPotion) e).setInstantWinBattle(false));
        return dungeon;
    }

    /**
     * Loads game with hard mode values instead of default from GameMode
     */
    @Override
    public Dungeon loadGame() {
        dungeon = super.loadGame();
        super.setFreqZombies(15);
        dungeon.getPlayer().setHP(5);
        dungeon.getPlayer().setInitalHP(5);
        dungeon.getEntities().stream().filter(InvincibilityPotion.class::isInstance)
                .forEach(e -> ((InvincibilityPotion) e).setInstantWinBattle(false));
        return dungeon;
    }


    @Override
    public void spawnCharacters(int tickCounter) {

        super.spawnCharacters(tickCounter);
        spawnHydra(tickCounter);
    }

    /**
     * Logic to spawn hydras, every 50 ticks
     */
    public void spawnHydra(int tickCounter) {
        // ensures spawn every 50 ticks
        if (tickCounter % freqHydra != freqHydra - 1 ) {
            return;
        }
        
        int x = getSpawnEntitesSeed().nextInt(dungeon.getWidth());
        int y = getSpawnEntitesSeed().nextInt(dungeon.getHeight());
        Position pos = new Position(x, y);
        Hydra newHydra = new Hydra(UUID.randomUUID().toString(), pos);
        dungeon.getEntitiesAtPosition(pos);
        
        // get all positions in dungeon without Blockable Entity there
        List<Position> blockedPositions = dungeon.getEntities()
            .stream()
            .filter(e -> (e instanceof Blockable))
            .map( e -> e.getPosition())
            .collect(Collectors.toList());

        // if no possible spawn positions, do nothing
        List<Position> possiblePositions = pos.getAllPositions(dungeon.getWidth(), dungeon.getHeight());
        // minus blockedPositions from possible positions
        if (possiblePositions.removeAll(blockedPositions) && possiblePositions.size() == 0){
            return;
        } 
        // if original position is possible, no need to update position
        else if (possiblePositions.contains(pos)) {
            // continue
        } 
        // need at least one possiblePostion not in blocked positions
        else {// choose from avaiable remaining postions
            int choosePosIdx = new Random().nextInt(possiblePositions.size());
            newHydra.setPosition(possiblePositions.get(choosePosIdx));
        }
        newHydra.spawnWithRareObject(getRareObjectSeed());
        newHydra.setBattleEnabled(isBattleEnabled());
        dungeon.getPlayer().registerObserver((MovementObserver) newHydra);
        dungeon.addEntity(newHydra);
        
    }


    
     
    
    
    /**
     * Logic to spawn mercenaries
     */

    @Override
    public void spawnMercenary(int tickCounter) {
        if (tickCounter > 0 && tickCounter % 20 == 0) {
            Mercenary newMercenary; 
            if (getAssassinSpawnChance().nextInt(11) < 3) {
                newMercenary = new HardAssassin(UUID.randomUUID().toString(), dungeon.getEntryPosition());
            } else {
                newMercenary = new HardMercenary(UUID.randomUUID().toString(), dungeon.getEntryPosition());
            }            
            newMercenary.spawnWithArmour(super.getArmourSeed());
            newMercenary.spawnWithRareObject(super.getRareObjectSeed());
            newMercenary.setBattleEnabled(isBattleEnabled());
            super.dungeon.getPlayer().registerObserver((MovementObserver) newMercenary);
            dungeon.addEntity(newMercenary);
        }
    }
    

    @Override
    public String toString() {
        return "hard";
    }

    @Override
    public Spider getNewSpider(Position spawnPosition) {
        Spider newSpider = new HardSpider(UUID.randomUUID().toString(), spawnPosition); 
        return newSpider;
    }

    @Override
    public Zombie getNewZombie(Position spawnPosition) {
        Zombie newZombie = new HardZombie(UUID.randomUUID().toString(), spawnPosition); 
        return newZombie;
    }
}
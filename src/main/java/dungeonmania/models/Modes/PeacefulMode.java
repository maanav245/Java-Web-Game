package dungeonmania.models.Modes;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

import dungeonmania.models.Dungeon;
import dungeonmania.models.Entity;
import dungeonmania.models.MovementObserver;
import dungeonmania.models.MovingEntities.Character;
import dungeonmania.models.MovingEntities.Mercenary;
import dungeonmania.models.MovingEntities.PeacefulAssassin;
import dungeonmania.models.MovingEntities.PeacefulMercenary;
import dungeonmania.models.MovingEntities.PeacefulSpider;
import dungeonmania.models.MovingEntities.PeacefulZombie;
import dungeonmania.models.MovingEntities.Spider;
import dungeonmania.models.MovingEntities.Zombie;
import dungeonmania.models.StaticEntities.Blockable;
import dungeonmania.util.Position;

public class PeacefulMode extends GameMode {
    /**
     * Constructor for PeacefulMode
     * @param dungeon
     */
    public PeacefulMode(Dungeon dungeon) {
        super(dungeon);
        dungeon.getPlayer().setBattleEnabled(false);
    }

    /**
     * Creates game with peaceful values instead of default from GameMode
     * No battles will be instigated with any Character/enemies that usually 
     * would be instigated with default
     */
    @Override
    public Dungeon createGame() {
        dungeon = super.createGame();
        super.setBattleEnabled(false);
        dungeon.getPlayer().setBattleEnabled(false);
        dungeon.getEntities().stream().filter(Character.class::isInstance)
                .forEach(e -> ((Character) e).setBattleEnabled(false));
        return dungeon;
    }

    /**
     * Loads game with peaceful values instead of default from GameMode
     * No battles will be instigated with any Character/enemies that usually 
     * would be instigated with default
     */
    @Override
    public Dungeon loadGame() {
        dungeon = super.loadGame();
        super.setBattleEnabled(false);
        dungeon.getEntities().stream().filter(Character.class::isInstance)
                .forEach(e -> ((Character) e).setBattleEnabled(false));
        return dungeon;
    }

    /**
     * Logic to spawn mercenaries
     */
    @Override
    public void spawnMercenary(int tickCounter) {
        if (tickCounter > 0 && tickCounter % 20 == 0) {
            Mercenary newMercenary; 
            if (getAssassinSpawnChance().nextInt(11) < 3) {
                newMercenary = new PeacefulAssassin(UUID.randomUUID().toString(), dungeon.getEntryPosition());
            } else {
                newMercenary = new PeacefulMercenary(UUID.randomUUID().toString(), dungeon.getEntryPosition());
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
        return "peaceful";
    }


    @Override
    public Spider getNewSpider(Position spawnPosition) {
        Spider newSpider = new PeacefulSpider(UUID.randomUUID().toString(), spawnPosition); 
        return newSpider;
    }
    @Override
    public Zombie getNewZombie(Position spawnPosition) {
        Zombie newZombie = new PeacefulZombie(UUID.randomUUID().toString(), spawnPosition); 
        return newZombie;
    }
}

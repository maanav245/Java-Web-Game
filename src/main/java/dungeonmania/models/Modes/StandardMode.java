package dungeonmania.models.Modes;

import java.util.UUID;

import dungeonmania.models.Dungeon;
import dungeonmania.models.MovementObserver;
import dungeonmania.models.MovingEntities.Mercenary;
import dungeonmania.models.MovingEntities.Spider;
import dungeonmania.models.MovingEntities.StandardAssassin;
import dungeonmania.models.MovingEntities.StandardMercenary;
import dungeonmania.models.MovingEntities.StandardSpider;
import dungeonmania.models.MovingEntities.StandardZombie;
import dungeonmania.models.MovingEntities.Zombie;
import dungeonmania.util.Position;

public class StandardMode extends GameMode {
    /**
     * Constructor for standard game inherits all methods from GameMode
     * @param dungeon
     */
    public StandardMode(Dungeon dungeon) {
        super(dungeon);
    }

    /**
     * Logic to spawn mercenaries
     */
    @Override
    public void spawnMercenary(int tickCounter) {
        if (tickCounter > 0 && tickCounter % 20 == 0) {
            Mercenary newMercenary; 
            if (getAssassinSpawnChance().nextInt(11) < 3) {
                newMercenary = new StandardAssassin(UUID.randomUUID().toString(), dungeon.getEntryPosition());
            } else {
                newMercenary = new StandardMercenary(UUID.randomUUID().toString(), dungeon.getEntryPosition());
            }            
            newMercenary.spawnWithArmour(super.getArmourSeed());
            newMercenary.spawnWithRareObject(super.getRareObjectSeed());
            newMercenary.setBattleEnabled(isBattleEnabled());
            dungeon.getPlayer().registerObserver((MovementObserver) newMercenary);
            dungeon.addEntity(newMercenary);
        }
    }

    @Override
    public String toString() {
        return "standard";
    }


    @Override
    public Spider getNewSpider(Position spawnPosition) {
        Spider newSpider = new StandardSpider(UUID.randomUUID().toString(), spawnPosition); 
        return newSpider;
    }

    @Override
    public Zombie getNewZombie(Position spawnPosition) {
        Zombie newZombie = new StandardZombie(UUID.randomUUID().toString(), spawnPosition); 
        return newZombie;
    }
}

package dungeonmania.models.Modes;

import java.util.Random;

import dungeonmania.models.Dungeon;
import dungeonmania.util.Position;

public interface GameModeFactory {
    public abstract Dungeon createGame();

    public abstract Dungeon loadGame();

    public abstract void spawnCharacters(int tickCounter);

    public abstract void setMaxSpiders(int number);

    public abstract Random getArmourSeed();

    public abstract Random getRareObjectSeed();

    public abstract Random getSpawnEntitesSeed();

    public abstract int getFreqZombies();

    @Override
    public abstract String toString();
    
    public abstract boolean isBattleEnabled();
    
}
package dungeonmania.models.MovingEntities;

import java.util.Random;

import org.json.JSONObject;

import dungeonmania.models.Movement.MoveRandom;
import dungeonmania.util.Position;

public class Hydra  extends Character implements Boss{
    private Random doubleHeadDuringBattleAttack = new Random();

    /**
     * The constructor used when a hydra is created for the first time
     * 
     * @param id       The id of the hydra
     * @param position Where the hydras is located
     */
    public Hydra(String id, Position position) {
        super(id, "hydra", position, new MoveRandom());
        this.setIsInteractable(true);
        super.setHP(40);
        super.setAP(15);
    }

    public Hydra(JSONObject json) {
        super(json);
    }

    public void setNewRandomSeed(long randomSeed) {
        doubleHeadDuringBattleAttack = new Random(randomSeed);
    }

    @Override
    public void spawnWithArmour(Random seed) {
        return;
    }

    @Override
    public void spawnWithRareObject(Random seed) {
        return;
    }

    @Override
    public void updateMovement(Object o) {
        // invincibility_potion has no effect in hard mode and hydras only spawn/ load in hard mode
        super.setStrategy(new MoveRandom());
        super.setInstantLose(false);
        return;
    }

    /**
     * Decreases the health of the character
     */
    @Override
    public void updateHealthAfterAttack(int decreasedHealthAmount, boolean againstLegendaryWieldable) {
        int chanceIncreaseHealth = doubleHeadDuringBattleAttack.nextInt();
        if (againstLegendaryWieldable == false && chanceIncreaseHealth % 2 != 0) {
            setHP(getHP() + decreasedHealthAmount);
            return;
        } else {
            setHP(getHP() - decreasedHealthAmount);
            return;
        }
        
    }

    
}

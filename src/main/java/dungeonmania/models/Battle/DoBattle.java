package dungeonmania.models.Battle;

import dungeonmania.models.Inventory;


// this interface allows each Player/ Character to do attack in a Battle
public interface DoBattle {
    /**
     * Indicates whether this character/player can enter into a battle
     * @return
     */
    public boolean isBattleEnabled();

    /**
     * Allows the switching on/off of entering battles for allies & GameMode
     * @return
     */
    public void setBattleEnabled(boolean battleEnabled);

    /**
     * Reduce own health after an opponent's attack, calculated with oponent's health * oponnent attack damage
     * @param opponent
     */
    //public void updateHealthAfterAttack(int decreasedHealthAmount);
    public void updateHealthAfterAttack(int decreasedHealthAmount, boolean againstLegendaryWieldable);

    /**
     * at current health & attack damange, what would be the reduction in health against
     * any opponent in 1 battle round
     * @return int, to be used to updateHealthAfterAttack
     */
    public int calculateAttackDamageAgainstOpponent();

    /**
     * Get this HP
     * @return
     */
    public int getHP();

    /**
     * Set this HP
     * @param i, an int 
     */
    public void setHP(int i);

    /**
     * Get this's Inventory
     * @return
     */
    public Inventory getInventory();

    /**
     * If the opponent has an invincibility potion in effect, this will lose instantly
     * @return
     */
    public boolean isInstantLose();

    /**
     * If the opponent uses an invincibility potion, this will set to true
     * @return
     */
    public void setInstantLose(boolean instantLose);


    
}

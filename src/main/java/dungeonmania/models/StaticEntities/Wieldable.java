package dungeonmania.models.StaticEntities;

import dungeonmania.models.Battle.DoBattle;

public interface Wieldable {

    /**
     * Find out the current durability of the item.
     * Durability is the # of uses left in battle rounds before item 'disappears from inventory'
     * @invariant durability >= 0; if it is 0; the item is deleted from existence
     */
    public int getDurability();

    /**
     * Simmulate 1 round of battle by reducing durability by 1
     */
    public void reduceDurability();

    /**
     * If float is equal to 1.0, then no effect on battle
     * but if return is equal to 1.3, then the Wielder will have 130% higher effective attack
     * against opponents in Battle
     * @return
     */
    public double changeRatioAttackPower(DoBattle attacker, DoBattle defender);

    /**
     * If float is equal to 1.0, then no effect on battle
     * but if return is equal to 2, then any attacks on Wielder will have 1/2 the effect
     * against opponents in Battle
    */
    public double changeRatioDefensePower(DoBattle attacker, DoBattle defender);

    /**
     * If true, will cause the battle to win immediately in player's favour
     * @return
     */
    public boolean instantWinBattle();

    /**
     * If true, can cause special effects in Battle
     */
    public boolean isLegendaryWieldable();


}

package dungeonmania.models.Battle;

import java.util.List;

import dungeonmania.models.Entity;
import dungeonmania.models.Player;
import dungeonmania.models.StaticEntities.Wieldable;
import dungeonmania.models.MovingEntities.Character;

public class Battle {
    /**
     * return losing entity to be removed by client, whether player or enemy
     * at the end of each single battle, will need to reset the Entities in Dungeon client
     * so that any losers of the battle are removed from game play
     * The Character/player may have inventory items removed if used in battle and durability used up
     * 
     * @param player
     * @param enemy
     * @return
     */
    public static Entity startBattle(DoBattle player, DoBattle enemy) {
        // Do not trigger battle if battles are not enabled with the character
        if (!enemy.isBattleEnabled()) {
            return null;
        }
        return progressBattleRoundsUntilComplete(player, enemy);
    }

    /**
     * calling on methods from DoBattle in each Entity to have a single round of Battle
     * @param player
     * @param enemy
     */
    private static  void progressSingleBattleRound(DoBattle player, DoBattle enemy) {
        double modifiedPlayerAttackFromWieldables = 1;
        double modifiedPlayerDefenceFromWieldables = 1;
        boolean playerHasLegendaryWieldable = false;

        for (Wieldable w: player.getInventory().getWieldables()) {
            if (w.isLegendaryWieldable()) {
                playerHasLegendaryWieldable = true;
            }
            modifiedPlayerAttackFromWieldables *= w.changeRatioAttackPower(player, enemy);
            modifiedPlayerDefenceFromWieldables *= w.changeRatioDefensePower(player, enemy);
        }

        double modifiedEnemyAttackFromWieldables = 1;
        double modifiedEnemyDefenceFromWieldables = 1;
        boolean enemyHasLegendaryWieldable = false;
        
        for (Wieldable w: enemy.getInventory().getWieldables()) {
            if (w.isLegendaryWieldable()) {
                enemyHasLegendaryWieldable = true;
            }
            modifiedEnemyAttackFromWieldables *= w.changeRatioAttackPower(player,enemy);
            modifiedEnemyDefenceFromWieldables *= w.changeRatioDefensePower(player,enemy);
        }

        




        // enemy attacks player
        double enemyAttackDamage = enemy.calculateAttackDamageAgainstOpponent() * modifiedEnemyAttackFromWieldables;
        Integer ewieldablesAttackDefenceInt = ((Double)(enemyAttackDamage / modifiedPlayerDefenceFromWieldables)).intValue();
        player.updateHealthAfterAttack(ewieldablesAttackDefenceInt, enemyHasLegendaryWieldable);
        decreaseDurabilityOfWieldables(enemy);

        // stop further attacks if player dies first so that only EITHER player or enemy will 'die'
        if (player.getHP() <= 0) {
            return;
        }

        // player attacks enemy
        double playerAttackDamage = player.calculateAttackDamageAgainstOpponent() * modifiedPlayerAttackFromWieldables;
        Integer pwieldablesAttackDefenceInt = ((Double) (playerAttackDamage/ modifiedEnemyDefenceFromWieldables)).intValue();
        enemy.updateHealthAfterAttack(pwieldablesAttackDefenceInt, playerHasLegendaryWieldable);
        decreaseDurabilityOfWieldables(player);


        // if player has allies, allow to attack
        if (player instanceof Player) {
            allowAlliesToAttack(player, enemy, modifiedPlayerDefenceFromWieldables);
        } // if enemy is Player and has allies, allow to attack
        if (enemy instanceof Player) {
            allowAlliesToAttack(enemy, player, modifiedEnemyAttackFromWieldables);
        }
        if (enemy.getHP() <= 0) {
            return;
        }
        // else 1 round is complete so return
        return;

    }
    
    /**
     * loop on single battle round until player or enemy Character's HP is 0
     * @param player
     * @param enemy
     * @return
     */
    private static Entity progressBattleRoundsUntilComplete(DoBattle player, DoBattle enemy) {
        // handles if either DoBattle has an invicibility potion
        if (player.isInstantLose() == true && enemy.isInstantLose() == false) {
            player.setHP(0); // prevent negative HP for player
            return (Entity) player; 
        } else if ( enemy.isInstantLose() == true && player.isInstantLose() == false) {
            return (Entity) enemy;
        }
        // if both or neither have invicibility potion, continue battle rounds as normal
        else {
            Integer roundCount = 0;
            while (player.getHP() > 0 && enemy.getHP() > 0) {
                progressSingleBattleRound(player, enemy);
                roundCount += 1;
            }
            // player has lost
            if (player.getHP() <= 0) {
                player.setHP(0); // prevent negative HP for enemy
                return (Entity) player;
            // enemy has lost
            } else {
                enemy.setHP(0); // prevent negative HP for player
                return (Entity) enemy;
            }
        }

    } 
  
    /** Decrease Wieldable item by 1 per round it is called
     * 
     * @param battler, either Player or Character class
     */
    private static void decreaseDurabilityOfWieldables(DoBattle battler) {
        List<Wieldable> allWieldable = battler.getInventory().getWieldables();
        allWieldable.forEach(item -> {
            // decrement durabiltiy
            item.reduceDurability();
            // if durability is 0, remove from inventory
            if (item.getDurability() <= 0) {
                battler.getInventory().useFromInventory((Entity)item);
            }
        });
    }

    /**
     * If any of the battlers are players & have allies, handle this
     * @pre currPlayer is an instance of Player
     */
    private static void allowAlliesToAttack(DoBattle currPlayer, DoBattle enemy, double modifiedEnemyDefenceFromWieldables) {
        // if player has ally mercenary, they also attack the enemy.
        Player playerBattler = (Player) currPlayer;
        if (playerBattler.getAllies().size()  > 0) {
            for(Entity allyMercenary : playerBattler.getAllies()) {
                DoBattle allyBattler = (DoBattle) allyMercenary;
                Character allyCharacter = (Character) allyMercenary;
                double modifiedAllyAttackFromWieldables = 1;
                boolean allyHasLegendaryWieldable = false;


                //check ally for any attack equipment (defence not relevant as enemy only attacks player)
                for (Wieldable w : allyCharacter.getInventory().getWieldables()) {
                    if (w.isLegendaryWieldable()) {
                        allyHasLegendaryWieldable = true;
                    }
                    modifiedAllyAttackFromWieldables *= w.changeRatioAttackPower(currPlayer, enemy);
                }

                Double allyAttackDamage = allyBattler.calculateAttackDamageAgainstOpponent() * modifiedAllyAttackFromWieldables;
                Integer awieldablesAttackInt = ((Double) (allyAttackDamage/ modifiedEnemyDefenceFromWieldables)).intValue();
                enemy.updateHealthAfterAttack(awieldablesAttackInt, allyHasLegendaryWieldable);
                decreaseDurabilityOfWieldables(allyBattler);
            }
        }
    }
}

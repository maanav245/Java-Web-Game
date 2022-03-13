package dungeonmania.models.Goals;

import java.util.List;
import java.util.stream.Collectors;

import dungeonmania.models.Entity;
import dungeonmania.models.Player;
import dungeonmania.models.MovingEntities.Character;

public class EnemySpawnersGoal extends LeafGoal {
    private int n;
    private String enemyType = "enemies";

    /**
     * assumes only one player
     * 
     * @pre player is still alive and an enity in dungeon when this is called
     */
    @Override
    public boolean isComplete(List<Entity> entities) {
        List<Entity> playerE = entities.stream().filter(entity -> entity.getType().equals("player"))
                .collect(Collectors.toList());
        if (playerE.isEmpty()) {
            return false;
        }
        Player player = (Player) playerE.get(0);
        // get the position of all 'enemy' characters
        List<Entity> enemyEntities = entities.stream()
                // filter out ally mercenaries
                .filter(m -> !(player.getAllies().contains(m)))
                // get other instances of Character
                .filter(e -> ((e instanceof Character))).collect(Collectors.toList());
        this.n = enemyEntities.size();
        if (n > 0) {
            this.enemyType = enemyEntities.get(0).getType();
        }
        // assume vanquished enemies no longer maped in 'entities' if all destroyed
        return n == 0 ? true : false;
    }

    @Override
    public String toString() {
        if (n == 0) {
            return "";
        } else if (n > 1) {
            return ":" + enemyType + "(" + n + ")";
        } else {
            return ":" + enemyType;
        }
    }

    @Override
    public String toJSONString() {
        return "enemies";
    }
}

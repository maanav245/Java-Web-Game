package dungeonmania.models.Goals;

import java.util.List;
import java.util.stream.Collectors;

import dungeonmania.models.Entity;

public class ExitGoal extends LeafGoal {
    private boolean atExit;

    @Override
    public boolean isComplete(List<Entity> entities) {
        // checks the player is alive still
        this.atExit = false;
        List<Entity> playerE = entities.stream().filter(entity -> entity.getType().equals("player"))
                .collect(Collectors.toList());

        if (!playerE.isEmpty()) {
            // get the position of any 'exit'
            List<Entity> exitEntities = entities.stream().filter(e -> e.getType().equals("exit"))
                    .collect(Collectors.toList());
            // compare position of the player with the exit
            // check player is on any of the exits
            for (Entity exit : exitEntities) {
                // assumes only one player
                if (exit.sameXYPosition(playerE.get(0))) {
                    this.atExit = true;
                }
            }
        }
        return this.atExit;
    };

    @Override
    public String toString() {
        if (this.atExit) {
            return "";
        } else {
            return ":exit";
        }
    }

    @Override
    public String toJSONString() {
        return "exit";
    }
}

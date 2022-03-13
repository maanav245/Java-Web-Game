package dungeonmania.models.Goals;

import dungeonmania.models.Entity;

import java.util.ArrayList;
import java.util.List;

public class AndCompositeGoal extends CompositeGoals {

    /**
     * Constructor
     */
    public AndCompositeGoal() {
        super("AND");
    }

    @Override
    public boolean isComplete(List<Entity> entities) {
        boolean isCompleteReturn = true;
        // needs to iterate through all the children to update their display
        for (GoalComponent i : this.childGoals) {
            // if any of the childGoals are not done, return false
            if (!i.isComplete(entities)) {
                isCompleteReturn = false;
            }
        }
        return isCompleteReturn;

    }

    @Override
    public String toString() {
        String andString = "";
        for (GoalComponent leafGoal : this.childGoals) {
            if (leafGoal.equals(childGoals.get(0)) || andString.equals("")) {
                andString = leafGoal.toString();
            } else if (!leafGoal.toString().equals("")) {
                andString = "(" + leafGoal.toString() + " AND " + andString + ")";
            }
        }
        return andString;
    }
}

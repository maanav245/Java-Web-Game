package dungeonmania.models.Goals;

import dungeonmania.models.Entity;

import java.util.ArrayList;
import java.util.List;

public class OrCompositeGoal extends CompositeGoals {

    /**
     * Constructor
     */
    public OrCompositeGoal() {
        super("OR");
    }

    @Override
    public boolean isComplete(List<Entity> entities) {
        for (GoalComponent i : childGoals) {
            // if any of the childGoals are done, return true
            if (i.isComplete(entities)) {
                return true;
            }
        }
        // return false if no trues earlier
        return false;

    }

    @Override
    public String toString() {
        String orString = "";
        for (GoalComponent leafGoal : this.childGoals) {
            if (leafGoal.equals(childGoals.get(0))) {
                orString = leafGoal.toString();
            } else if (!leafGoal.toString().equals("") && !orString.equals("")) {
                orString = "(" + leafGoal.toString() + " OR " + orString + ")";
            } else {
                orString = "";
            }
        }
        return orString;
    }
}

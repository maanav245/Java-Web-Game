package dungeonmania.models.Goals;

import java.util.ArrayList;
import java.util.List;

import org.json.JSONArray;
import org.json.JSONObject;

import dungeonmania.models.Entity;

public abstract class CompositeGoals implements GoalComponent {
    protected List<GoalComponent> childGoals;
    // default "AND" boolean logic
    private String booleanLogic = "AND";

    /**
     * allows creation of "OR" chaining of leaf goals
     * 
     * @param logic
     */
    public CompositeGoals(String logic) {
        booleanLogic = logic;
        childGoals = new ArrayList<>();

    }

    /**
     * @return String
     */
    public String getBooleanLogic() {
        return this.booleanLogic;
    }

    /**
     * @param newGoal
     */
    public void addGoalComponent(GoalComponent newGoal) {
        childGoals.add(newGoal);
    }

    /**
     * @return List<GoalComponent>
     */
    public List<GoalComponent> getLeafGoals() {
        return childGoals;
    }
}

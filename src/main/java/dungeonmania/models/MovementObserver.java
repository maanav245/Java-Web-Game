package dungeonmania.models;

public interface MovementObserver {
    /**
     * Updates the movement of the observer 
     */
    public void updateMovement(); 

    /**
     * Updates the movement strategy of the observer 
     * @param o
     */
    public void updateMovement(Object o);
}

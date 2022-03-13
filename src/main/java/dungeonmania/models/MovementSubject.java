package dungeonmania.models;

public interface MovementSubject {
    /**
     * registers an observer 
     * @param o The observer to be registered 
     */
    public default void registerObserver(MovementObserver o) {}; 

    /**
     * Removes an observer
     * @param o The observer to be removed 
     */
    public default void removeObserver(MovementObserver o) {}; 

    /**
     * Notifies the observers when needed 
     */
    public void notifyObservers(); 
}

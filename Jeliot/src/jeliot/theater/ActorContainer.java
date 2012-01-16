package jeliot.theater;

/**
  * <code>ActoContainer</code> interface is implemented by classes that
  * are going to contain <code>Actor</code>s as their fields and take
  * care of their painting. This means that those classes having
  * actors as their fields but not taking care of the painting of
  * the actors are not implementing this class.
  *
  * @author Pekka Uronen
  * @author Niko Myller
  */
public interface ActorContainer {
    
    /**
     * Remove the the given actor from the ActorContainer. 
     * 
     * @param actor The actor to be removed.
     */
    public abstract void removeActor(Actor actor);
}

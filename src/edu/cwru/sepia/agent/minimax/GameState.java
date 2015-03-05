package edu.cwru.sepia.agent.minimax;

import edu.cwru.sepia.action.Action;
import edu.cwru.sepia.action.ActionType;
import edu.cwru.sepia.action.DirectedAction;
import edu.cwru.sepia.action.TargetedAction;
import edu.cwru.sepia.environment.model.state.State;
import edu.cwru.sepia.environment.model.state.State.StateBuilder;
import edu.cwru.sepia.environment.model.state.Unit;
import edu.cwru.sepia.environment.model.state.Unit.UnitView;
import edu.cwru.sepia.util.Direction;

import java.util.*;

/**
 * This class stores all of the information the agent
 * needs to know about the state of the game. For example this
 * might include things like footmen HP and positions.
 *
 * Add any information or methods you would like to this class,
 * but do not delete or change the signatures of the provided methods.
 */
public class GameState 
{
	
	private final int xExtent;	// The x dimension of the board
	private final int yExtent;	// The y dimension of the board
	
	private final List<Integer> resourceIds;	// The list of resource IDs in the game
	
	private int xCoordinate;	// The x coordinate of the cell
	private int yCoordinate;	// The y coordinate of the cell

    /**
     * You will implement this constructor. It will
     * extract all of the needed state information from the built in
     * SEPIA state view.
     *
     * You may find the following state methods useful:
     *
     * state.getXExtent() and state.getYExtent(): get the map dimensions
     * state.getAllResourceIDs(): returns all of the obstacles in the map
     * state.getResourceNode(Integer resourceID): Return a ResourceView for the given ID
     *
     * For a given ResourceView you can query the position using
     * resource.getXPosition() and resource.getYPosition()
     *
     * For a given unit you will need to find the attack damage, range and max HP
     * unitView.getTemplateView().getRange(): This gives you the attack range
     * unitView.getTemplateView().getBasicAttack(): The amount of damage this unit deals
     * unitView.getTemplateView().getBaseHealth(): The maximum amount of health of this unit
     *
     * @param state Current state of the episode
     */
    public GameState(State.StateView state) 
    {
    	this.xExtent = state.getXExtent();
    	this.yExtent = state.getYExtent();
    	this.resourceIds = state.getAllResourceIds();
    }

    /**
     * You will implement this function.
     *
     * You should use weighted linear combination of features.
     * The features may be primitives from the state (such as hp of a unit)
     * or they may be higher level summaries of information from the state such
     * as distance to a specific location. Come up with whatever features you think
     * are useful and weight them appropriately.
     *
     * It is recommended that you start simple until you have your algorithm working. Then watch
     * your agent play and try to add features that correct mistakes it makes. However, remember that
     * your features should be as fast as possible to compute. If the features are slow then you will be
     * able to do less plys in a turn.
     *
     * Add a good comment about what is in your utility and why you chose those features.
     *
     * @return The weighted linear combination of the features
     */
    public double getUtility() 
    {
    	// getDistanceFromArcherUtility() Shorter distance to archer
    	// getCurrentHealthUtility()
    	// Farther distance from archer - 1.0
        return 0.0;
    }

    /**
     * You will implement this function.
     *
     * This will return a list of GameStateChild objects. You will generate all of the possible
     * actions in a step and then determine the resulting game state from that action. These are your GameStateChildren.
     *
     * You may find it useful to iterate over all the different directions in SEPIA.
     *
     * for(Direction direction : Directions.values())
     *
     * To get the resulting position from a move in that direction you can do the following
     * x += direction.xComponent()
     * y += direction.yComponent()
     *
     * @return All possible actions and their associated resulting game state
     */
    public List<GameStateChild> getChildren() 
    {
    	List<GameStateChild> children = new ArrayList<GameStateChild>();
    	Map<Integer, Action> stateActions = new HashMap<Integer, Action>();
    	int actionIndex = 0;
    	    	
    	// Generate possible actions from this state
    	for (Direction direction : Direction.values())
    	{   
    		// Create move actions for each direction
    		//stateActions.put(actionIndex, Action.createPrimitiveMove(actionIndex, ));
    	}
    	// Build potential next states from these actions
    	// Add states to children
    	// GameStateChild nextChild = new GameStateChild(actions, nextState);
    	
        return children;
    }
    
    // Getter method for x coordinate
    public int getXCoordinate()
    {
    	return new Integer(xCoordinate);
    }
    
    // Getter method for y coordinate
    public int getYCoordinate()
    {
    	return new Integer(yCoordinate);
    }
    
    // Setter method for x coordinate
    public void setXCoordinate(int xCoordinate)
    {
    	this.xCoordinate = xCoordinate;
    }
    
    // Setter method for y coordinate
    public void setYCoordinate(int yCoordinate)
    {
    	this.yCoordinate = yCoordinate;
    }
}
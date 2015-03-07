package edu.cwru.sepia.agent.minimax;

import edu.cwru.sepia.action.Action;
import edu.cwru.sepia.action.ActionType;
import edu.cwru.sepia.action.DirectedAction;
import edu.cwru.sepia.action.TargetedAction;
import edu.cwru.sepia.environment.model.state.State;
import edu.cwru.sepia.environment.model.state.State.StateBuilder;
import edu.cwru.sepia.environment.model.state.State.StateView;
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
 * 
 * @author Tim Sesler
 * @author Adam Boe
 */
public class GameState 
{
	private final int xExtent;	// The x dimension of the board
	private final int yExtent;	// The y dimension of the board
	private final List<Integer> resourceIds;	// The list of resource IDs in the game
	private StateView parentState;	// The state view from which this game state was created
	private List<Integer> footmanUnitIds;	// The ids of all footmen in the game
	private List<Integer> archerUnitIds;	// The ids of all archers in the game
	private List<UnitView> units;	// The list of all units in the game
	private Double utility;	// The utility of the game state
	
	private Map<UnitView, FootmanPosition> footmanPositions = new HashMap<UnitView, FootmanPosition>();	// Map locations to footmen

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
    	this(state, true);    	
    }
    
    /**
     * An additional constructor that will perform certain functions if we are creating the initial game state.
     */
    public GameState(State.StateView state, boolean isInitial)
    {
    	this.xExtent = state.getXExtent();
    	this.yExtent = state.getYExtent();
    	this.resourceIds = state.getAllResourceIds();
    	this.units = state.getAllUnits();
    	this.parentState = state;

    	// Find all of the active units in this state
    	footmanUnitIds = new ArrayList<Integer>();
    	archerUnitIds = new ArrayList<Integer>();
    	
    	// Find all of the units in the game and add them to the appropriate list.
    	for (UnitView unit : this.units)
    	{
    		String unitTypeName = unit.getTemplateView().getName();
    		
    		if (unitTypeName.equals("Footman"))
    		{
    			footmanUnitIds.add(unit.getID());
    			footmanPositions.put(unit, new FootmanPosition(unit.getXPosition(), unit.getYPosition()));    		
    		}
    		else if (unitTypeName.equals("Archer"))
    		{
    			archerUnitIds.add(unit.getID());
    		}    		
    	}
    	
    	// If it is the initial state then we need to calculate the utility.
    	if (isInitial)
    	{
    		this.calculateUtility();
    	}
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
    	return this.utility;
    }
    
    /**
     * Calculate the utility of a state based on the positions of the footmen relative to the archers.
     */
    public void calculateUtility()
    {
    	Double minDistance = 0.0;
    	
    	// For every footman, find the minimum distance to any archer.
    	for (UnitView footman : footmanPositions.keySet())
    	{
    		Double individualMinDistance = Double.POSITIVE_INFINITY;
    		
    		for (Integer archerID : archerUnitIds)
        	{
        		UnitView archerUnit = this.parentState.getUnit(archerID);        		
        		Double distance = Math.sqrt(Math.abs((Math.pow((footmanPositions.get(footman).xPosition - archerUnit.getXPosition()), 2)) + (Math.pow((footmanPositions.get(footman).yPosition - archerUnit.getYPosition()), 2))));        		
        		
        		if (distance < individualMinDistance)
        		{        			        			
        			individualMinDistance = distance;
        		}        		
        	}
    		minDistance += individualMinDistance;
    	}
    	// Average the minimum distance and invert it to get a utility.
    	// This way, low minimum distance has high utility. 
    	this.utility = 100 / (minDistance / footmanPositions.size());
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
     *
     *** We could not find a way to have this method behave dynamically (i.e., accept an arbitrary number of footmen)
     *** so it is designed to handle exactly two footmen.
     */
    public List<GameStateChild> getChildren() 
    {
    	List<GameStateChild> children = new ArrayList<GameStateChild>();	// The list of children to be returned
		
    	// Look in directions NORTH, SOUTH, EAST, and WEST
    	for (Direction direction1 : getCardinal())
    	{
    		// Look in every direction for every direction we're looking in
    		for (Direction direction2 : getCardinal())
    		{
    			// Generate possible actions from this state for each footman unit    		
        		int unitID1 = 0;	// the first footman id
        		int unitID2 = 1;	// the second footman id
        		UnitView unit1 = this.parentState.getUnit(unitID1); // footman 1
        		UnitView unit2 = this.parentState.getUnit(unitID2);	// footman 2
    		
        		Integer archerID = getArcherInRange(unit1.getXPosition(), unit1.getYPosition());	// Checks if an archer is in attacking range        
        		
        		int nextFootman1XPosition;	// The next position of the footman in a child game state
        		int nextFootman1YPosition;
        		
        		int nextFootman2XPosition;	// The next position of the second footman in a child game state
        		int nextFootman2YPosition;
        		
        		// Check that the next state actually has positions mapped to the footman
        		if (footmanPositions.containsKey(unit1) && footmanPositions.containsKey(unit2))
        		{
        			// Update footman positions given the directions
        			nextFootman1XPosition = footmanPositions.get(unit1).xPosition + direction1.xComponent();
            		nextFootman1YPosition = footmanPositions.get(unit1).yPosition + direction1.yComponent();
            		
            		nextFootman2XPosition = footmanPositions.get(unit2).xPosition + direction2.xComponent();
            		nextFootman2YPosition = footmanPositions.get(unit2).yPosition + direction2.yComponent();            	
        		
            		// See if an archer is in range to attack and that the next state is in bounds for both footmen
            		if (archerID != null && inBounds(nextFootman1XPosition, nextFootman1YPosition) && inBounds(nextFootman2XPosition, nextFootman2YPosition))
            		{
            			Map<Integer, Action> stateActions = new HashMap<Integer, Action>();
            			
            			// Add primitive attack actions to each footman
            			stateActions.put(unitID1, Action.createPrimitiveAttack(unitID1, archerID));
            			stateActions.put(unitID2, Action.createPrimitiveAttack(unitID2, archerID));
            			
            			GameState nextGameState = new GameState(this.parentState, false);
            			
            			// Remap the unit in the new game state with its updated future position
            			nextGameState.footmanPositions.remove(unit1);
            			nextGameState.footmanPositions.remove(unit2);
            			nextGameState.footmanPositions.put(unit1, new FootmanPosition(nextFootman1XPosition, nextFootman1YPosition));
            			nextGameState.footmanPositions.put(unit2, new FootmanPosition(nextFootman2XPosition, nextFootman2YPosition));
            			
            			// Since we are attacking in this state, assign a very high utility
            			nextGameState.utility = Double.POSITIVE_INFINITY;
            			GameStateChild nextChild = new GameStateChild(stateActions, nextGameState);
        				children.add(nextChild);	// Add the child
            		}
            		// The resulting move is still inbounds
            		else if (inBounds(nextFootman1XPosition, nextFootman1YPosition) && inBounds(nextFootman2XPosition, nextFootman2YPosition))
            		{        			
            			Map<Integer, Action> stateActions = new HashMap<Integer, Action>();
            			
            			// Add move actions to each footman
            			stateActions.put(unitID1, Action.createPrimitiveMove(unitID1, direction1));
            			stateActions.put(unitID2, Action.createPrimitiveMove(unitID2, direction2));

            			GameState nextGameState = new GameState(this.parentState, false);
            			
            			// Remap the unit in the new game state with its updated future position
            			nextGameState.footmanPositions.remove(unit1);
            			nextGameState.footmanPositions.remove(unit2);
            			nextGameState.footmanPositions.put(unit1, new FootmanPosition(nextFootman1XPosition, nextFootman1YPosition));
            			nextGameState.footmanPositions.put(unit2, new FootmanPosition(nextFootman2XPosition, nextFootman2YPosition));
            			
            			// Calculate utility of state
            			nextGameState.calculateUtility();
            			GameStateChild nextChild = new GameStateChild(stateActions, nextGameState);        			                			    			
        				children.add(nextChild);     // Add the child   				
            		}        	
        		}
    		}	
    	}
    	
        return children;
    }
    
    /**
     * Get the cardinal directions for footman movement
     * @return an array of the cardinal directions
     */
    private Direction[] getCardinal()
    {
    	Direction[] cardinalDirections = new Direction[4];
    	cardinalDirections[0] = Direction.NORTH;
    	cardinalDirections[1] = Direction.SOUTH;
    	cardinalDirections[2] = Direction.EAST;
    	cardinalDirections[3] = Direction.WEST;    
    	
    	return cardinalDirections;
    }
    
    /**
     * Check if the position after a potential move/attack is still on the board
     * @param x
     * @param y
     * @return
     */
    private boolean inBounds(int x, int y)
    {
    	return !(x > this.xExtent || y > this.yExtent || x < 0 || y < 0);    	
    }
    
    /**
     * Check if there is an archer that the footman can attack given its coordinates
     * @param x
     * @param y
     * @return
     */
    private Integer getArcherInRange(int x, int y)
    {
    	for (Integer archerID : archerUnitIds)
    	{
    		UnitView archerUnit = this.parentState.getUnit(archerID);
    		
    		if (Math.abs(archerUnit.getXPosition() - x) <= 1 && Math.abs(archerUnit.getYPosition() - y) <= 1)
    		{
    			// Can evaluate an attack state
    			return archerID;
    		}
    	}
    	return null;
    }
    
    /**
     * Prints the coordinates of each footman's cell location
     * @return
     */
    public String getFootmanCoordinates()
    {
    	StringBuilder footmanCoordinates = new StringBuilder();
    	for (UnitView footman : footmanPositions.keySet())
    	{
    		footmanCoordinates.append("Footman " + footman.getID() + " is at node: (" + footmanPositions.get(footman).xPosition + ", " + footmanPositions.get(footman).yPosition + "), ");
    	}
    	return footmanCoordinates.toString();
    }
    
    /**
     * This class maps current and future potential positions to a footman unit.  It is used to look 
     * at future states and analyze the positions of the footmen.
     * @author Tim Sesler
     * @author Adam Boe
     */
    private class FootmanPosition
    {
    	public int xPosition;
    	public int yPosition;
    	
    	public FootmanPosition(int x, int y)
    	{
    		this.xPosition = x;
    		this.yPosition = y;
    	}
    }
}
package edu.cwru.sepia.agent.minimax;

import edu.cwru.sepia.action.Action;
import edu.cwru.sepia.agent.Agent;
import edu.cwru.sepia.environment.model.history.History;
import edu.cwru.sepia.environment.model.state.State;
import edu.cwru.sepia.util.Direction;

import java.io.InputStream;
import java.io.OutputStream;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class MinimaxAlphaBeta extends Agent 
{

    private final int numPlys;

    public MinimaxAlphaBeta(int playernum, String[] args)
    {
        super(playernum);	// 0 is footman, 1 is archer

        if(args.length < 1)
        {
            System.err.println("You must specify the number of plys.");
            System.exit(1);
        }

        this.numPlys = Integer.parseInt(args[0]);
    }

    @Override
    public Map<Integer, Action> initialStep(State.StateView newstate, History.HistoryView statehistory) {
        return middleStep(newstate, statehistory);
    }

    @Override
    public Map<Integer, Action> middleStep(State.StateView newstate, History.HistoryView statehistory) 
    {
        GameStateChild bestChild = alphaBetaSearch(new GameStateChild(newstate),
                numPlys,
                Double.NEGATIVE_INFINITY,
                Double.POSITIVE_INFINITY,
                true);
        
        //Map<Integer, Action> actions = new HashMap<Integer, Action>();
        
        //actions.put(0, Action.createPrimitiveMove(0, Direction.SOUTH));
        
        //return actions;
        return bestChild.action;     
    }

    @Override
    public void terminalStep(State.StateView newstate, History.HistoryView statehistory) {}

    @Override
    public void savePlayerData(OutputStream os) {}

    @Override
    public void loadPlayerData(InputStream is) {}

    /**
     * You will implement this.
     *
     * This is the main entry point to the alpha beta search. Refer to the slides, assignment description
     * and book for more information.
     *
     * Try to keep the logic in this function as abstract as possible (i.e. move as much SEPIA specific
     * code into other functions and methods)
     *
     * @param node The action and state to search from
     * @param depth The remaining number of plys under this node
     * @param alpha The current best value for the maximizing node from this node to the root
     * @param beta The current best value for the minimizing node from this node to the root
     * @return The best child of this node with updated values
     */
    public GameStateChild alphaBetaSearch(GameStateChild node, int depth, double alpha, double beta, boolean isMaximizer)
    {
    	List<GameStateChild> children = orderChildrenWithHeuristics(node.state.getChildren());    
    	
		System.out.println("Current Depth: " + depth);
		System.out.println("Alpha: " + alpha);
		System.out.println("Beta: " + beta);
    	
    	// We are at a terminal node
    	if (depth == 0)
    	{
    		System.out.println(node.state.getUtility() + " is the best choice.");
    		
    		return node; // return best state
    	}
    	
    	// Evaluate footman's turn (MAX)
    	if (isMaximizer)
    	{
    		System.out.println("Maximizing node.");

    		double v = Double.NEGATIVE_INFINITY;
    		
	    	for (GameStateChild child : children)
	    	{
	    		v = Math.max(v, alphaBetaSearch(child, depth - 1, alpha, beta, false).state.getUtility());
	    		alpha = Math.max(alpha, v);
	    		
	    		if (beta <= alpha)
	    		{
        			System.out.println(child.toString() + " is the best choice");
        			
	    			return child; // Beta cut off
	    		}
	    	}
	    }
	    
	    // Evaluate archer's turn (MIN)
	    else
	    {
	    	System.out.println("Minimizing node.");
	    	
	    	double v = Double.POSITIVE_INFINITY;
	    			
	    	for (GameStateChild child : node.state.getChildren())
	    	{	    		
	    		v = Math.min(v, alphaBetaSearch(child, depth - 1, alpha, beta, true).state.getUtility());
	    		beta = Math.min(beta, v);
	    		
	    		if (beta <= alpha)
	    		{
	    			return child; // alpha cut off
	    		}
	    	}
	    } 
        return node;
    }

    /**
     * You will implement this.
     *
     * Given a list of children you will order them according to heuristics you make up.
     * See the assignment description for suggestions on heuristics to use when sorting.
     *
     * Use this function inside of your alphaBetaSearch method.
     *
     * Include a good comment about what your heuristics are and why you chose them.
     *
     * @param children
     * @return The list of children sorted by your heuristic.
     */
    public List<GameStateChild> orderChildrenWithHeuristics(List<GameStateChild> children)
    {
        return children;
    }
}

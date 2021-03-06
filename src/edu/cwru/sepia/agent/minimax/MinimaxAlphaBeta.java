package edu.cwru.sepia.agent.minimax;

import edu.cwru.sepia.action.Action;
import edu.cwru.sepia.agent.Agent;
import edu.cwru.sepia.environment.model.history.History;
import edu.cwru.sepia.environment.model.state.State;
import edu.cwru.sepia.util.Direction;

import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;
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
        
        System.out.println(bestChild.state.getFootmanCoordinates() + " was chosen for the action.");     
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
    	System.out.println("\n==> Starting Alpha-Beta...");
    	System.out.println("Current Depth: " + depth);
		System.out.println("Alpha: " + alpha);
		System.out.println("Beta: " + beta);
		System.out.println(node.state.getFootmanCoordinates());
		
    	List<GameStateChild> children = orderChildrenWithHeuristics(node.state.getChildren());    
    	
    	// We are at a terminal node
    	if (depth == 0)
    	{
    		System.out.println("Reached depth 0: " + node.state.getFootmanCoordinates() + " is the best choice.");    		
    		return node; // return best state
    	}
    	
    	// Evaluate maximizing states
    	if (isMaximizer)
    	{
    		System.out.println("Maximizing node: " + node.state.getFootmanCoordinates());

    		double v = Double.NEGATIVE_INFINITY;
    		
	    	for (GameStateChild child : children)
	    	{
	    		GameStateChild tempNode = alphaBetaSearch(child, depth - 1, alpha, beta, false);
	    		double tempUtility = tempNode.state.getUtility();
	    		v = Math.max(v, tempUtility);	    		
	    		
		    	// Set node to child with v utility
	    		if (v == tempUtility)
	    		{
	    			node = tempNode;
	    		}
	    		alpha = Math.max(alpha, v);
	    		
	    		if (beta <= alpha)
	    		{
	    			System.out.println("Beta cutoff: " + child.state.getFootmanCoordinates());        			
        			break;	// Beta cutoff
	    		}
	    	}	    
	    } 
	    
	    // Evaluate minimizing states
	    else
	    {
	    	System.out.println("Minimizing node: " + node.state.getFootmanCoordinates() + ".");
	    	
	    	double v = Double.POSITIVE_INFINITY;
	    			
	    	for (GameStateChild child : children)
	    	{	
	    		GameStateChild tempNode = alphaBetaSearch(child, depth - 1, alpha, beta, true);
	    		double tempUtility = tempNode.state.getUtility();	    		
	    		v = Math.min(v, tempUtility);	    			    			    	
	    		
		    	// Set node to child with v utility
	    		if (v == tempUtility)
	    		{	    			
	    			node = tempNode;
	    		}
	    		beta = Math.min(beta, v);
	    		
	    		if (beta <= alpha)
	    		{
	    			System.out.println("Alpha cutoff at " + child.state.getFootmanCoordinates());
	    			break;	// Alpha cutoff
	    		}
	    	}
	    } 
    	System.out.println(node.state.getFootmanCoordinates() + " is the best choice.");    	
    	System.out.println("Final Alpha: " + alpha);
		System.out.println("Final Beta: " + beta);
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
    	List<GameStateChild> returnList = new ArrayList<GameStateChild>();
    	
    	// Sort the children by their utility.  We want to examine children with high utility first.
    	// This was our first choice of heuristic and it never really matured beyond this...
    	for (GameStateChild considerChild : children)
    	{	
    		if (returnList.size() == 0)
    		{
    			returnList.add(considerChild);
    		}
    		else
    		{
    			boolean placed = false;
    			int index = 0;
    			while (!placed && index < returnList.size())
    			{
    				if (considerChild.state.getUtility() > returnList.get(index).state.getUtility())
        			{
        				returnList.add(index, considerChild);
        				placed = true;
        			}
    				index++;
    			}
    			if (!placed)
    			{
    				returnList.add(returnList.size(), considerChild);
    			}
    		}
    	}
        return returnList;
    }
}

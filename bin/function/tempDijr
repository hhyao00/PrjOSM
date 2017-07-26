package function;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import elements.Node;


/**
 * This class encapsulates the algorithm for computing a path
 * from one Node to another. This class provides methods for finding
 * directions from start to end. Uses Dijkstra's algorithm.
 */
public class DirectionsGenerator {

    /** 
     * All nodes that are neighbor to some node from the start.
     * This is the list of nodes that we consider looking at.
     * It probably extends to all nodes connected to the start.
     */
    private ArrayList<Node> toVisit; 

    /** 
     * Map the nodeID to the shortest distance that that node.
     * The distance ( value ) is the shortest distance from the 
     * startNode to the node with the key nodeID.
     * @key The node reached through the value
     * @value The distance from startNode to the key node.
     */
    private HashMap<Node, Double> distanceMap;

    /** 
     * The Set of visited nodes. Each node is visited once.
     * If a node is contained in this set, then it is not 
     * visited again.
     */
    private HashSet<Node> visited;

    /**
     * The a HashMap of visited nodes that caused an update in distanceMap.
     * An update in distanceMap indicates that there is a shorter path
     * to the most recently visited node. Used to trace back to startNode.
     * 
     * @key The node that is reached through its value node. 
     * @value The node that should be backtracked to, 
     * since the shortest possible path is through this node.
     */
    private HashMap<Node, Node> predecessorMap;

    
    // ------- end of fields ------- // 

    /**
     * The constructor.
     * @param start The start Node
     * @param end The end Node
     * @param data Relevant mapData
     */
    public DirectionsGenerator(){
	distanceMap = new HashMap<Node, Double>();
	predecessorMap = new HashMap<Node, Node>();
	toVisit = new ArrayList<Node>();
	visited = new HashSet<Node>();
    }

    /**
     * Generate a path from the specified start Node
     * to the specified end Node. Called by outside.
     * 
     * @param start The start Node.
     * @param end The end Node to be reached.
     * @return an ArrayList<Node> of nodes on the path from
     * start to end Nodes if start and end are connected,
     * otherwise null.
     */
    public ArrayList<Node> generateDirections( Node start, Node end ){
	return genDirections(start, end);
    }

    /**
     * Generates a path from start node to end node
     * using Dijkstra's algorithm. A key == null is 
     * the equivalent to it's distance being infinity.
     * 
     * @return an ArrayList of Node objects that makeup
     * the path from start node to end node. 
     */
    private synchronized ArrayList<Node> genDirections( Node start, Node end ){

	Node startTemp = start;
	toVisit.add(start);
	distanceMap.put(start, 0.0); 

	while( toVisit.size() > 0 ){ // there are eligible nodes to visit

	    visited.add(start); // mark 'start' as visited

	    // get neighbors reachable from one step from start.
	    ArrayList<Node> neighbors = start.getNeighbors();

	    // for all neighbor nodes to start; "n" is neighbor to start.
	    for(Node n: neighbors){
		if( visited.contains(n) ){ continue; } // already visited. 
		if(!toVisit.contains(n)){ 
		    toVisit.add(n); 
		}

		// compute distance from startNode to this node n.
		double dist = distanceMap.get(start) + start.distFrom(n);

		// if the distance is null (infinity), or if new distance is less than current,
		// replace old distance value with new shorter distance, then mark the node
		// stepped from as the predecessor allowing shorter distance. It caused update.
		if( distanceMap.get(n) == null || dist < distanceMap.get(n) ){
		    distanceMap.put(n, dist);
		    predecessorMap.put(n, start);
		}

		// Add all neighbors of one step reachable nodes to toVisit.
		// These are the neighbors of start's neighbors.
		ArrayList<Node> nNeighbors = n.getNeighbors(); // neighbors of neighbor
		for( Node otherN : nNeighbors ){
		    if( visited.contains(otherN) ){ continue; }
		    if(!toVisit.contains(otherN)){
			double od = distanceMap.get(n) + n.distFrom(otherN);
			distanceMap.put(otherN, od);
			predecessorMap.put(otherN, n);
			toVisit.add(otherN); 
		    }
		}
	    }

	    // Select the unvisited node v for which distance[v] is smallest
	    // (this can be done by simply scanning distance[]).
	    // in other words, set as 'start'
	    double dist = -1;
	    Node m = null;
	    for(Node v: toVisit){
		double vDist = distanceMap.get(v);
		if(dist == -1 || vDist < distanceMap.get(m)){
		    m = v;
		    dist = vDist;
		}
	    }
	    toVisit.remove(m);
	    start = m;


	} // end of while loop
	return backtrack(startTemp, end);
    }

    /** 
     * Backtrack method, to determine shortest path. 
     * Look through the predecessors, such that the 
     * shortest path is through the predecessor node.
     * @return path The path from start to end if 
     * the end was reached, otherwise null.
     */
    private ArrayList<Node> backtrack(Node start, Node end){
	if(!visited.contains(end)){ return null; } // never reached end.

	ArrayList<Node> path = new ArrayList<Node>();
	Node prevN = end;
	path.add(end);

	while( prevN != null && !prevN.equals(start)){
	    prevN = predecessorMap.get(prevN);
	    path.add(prevN);
	}
	resetMaps();
	return path;
    }

    /** 
     * Reset some necessary fields. It would make sense 
     * to put all these fields in the method itself,
     * but I needed to write out descriptions to think.
     */
    private void resetMaps(){
	distanceMap.clear();
	predecessorMap.clear();
	toVisit.clear();
	visited.clear();
    }
}



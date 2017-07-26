package elements;
import java.util.ArrayList;

import org.xml.sax.Attributes;

/** 
 * A way, which represents a connected sequence of nodes. 
 */
public class Way extends Element{

    /** The id of this object, unique */
    private String id; 

    /** The name of this way object (if any) */
    private String name;

    /** The sequence of nodes of this way */
    private ArrayList<Node> nodeSeq;

    /** Segments, defined by edges between nodes */
    private int segments = 0;

    /** The attributes of this way object */
    private Attributes attributes;

    /** The distance spanned of this Way. Total distance */
    private double distance = -1;

    /** If this way is drive-able ( not a building ) */
    private boolean driveable = true; // assume true.

    /** 
     * List of nodes that this Way shares with another Way.
     * If graph is connected, each Way will have >= one sharedNode.
     * Exception case (e.g.) is Evan Ct. is just a random line.
     * Also, only drive-able ways have a concept of sharedNode.
     * So sharedNodes is possibly empty. 
     */
    private ArrayList<String> sharedNodes;

    // --- end of fields --- // 

    /**
     * The constructor.
     * @param atts The attributes of this Way.
     * @param id The ID of this way.
     */
    public Way(String id, Attributes atts){ 
	this.id = id;
	attributes = atts;
	nodeSeq = new ArrayList<Node>();
	sharedNodes = new ArrayList<String>();
    }

    /** Get the id of this object. */
    public String getID(){ return id; }

    /** Private method to compute distance */
    private void computeDistance(){
	Node n0 = nodeSeq.get(0);
	double accumDist = 0;

	for( int i = 1; i < nodeSeq.size() ; i++ ){
	    segments++;
	    Node n1 = nodeSeq.get(i);
	    double dist = n0.distFrom(n1);
	    accumDist += dist;
	    n0 = n1;
	}
	distance = accumDist * 69;
    }

    /**
     * Add a node to the node sequence.
     * @param n The node to be added.
     */
    public void addNode(Node n){
	nodeSeq.add(n);
    }

    /** 
     * Get the name of this way
     * @return the name of the way object
     * @return null if the way object does not have a name.
     */
    public String getName(){ 
	name = getTag("name");
	return name;
    }

    /** 
     * Get the number of segments make up this Way obj.
     * @return the number of segments
     */
    public int getSegmentsNum(){
	if( segments == 0 ){ 
	    segments++ ;
	    computeDistance(); 
	}
	return segments;
    }

    /**
     * Check if this Way object contains a certain node.
     * @param node The node want to check if contained
     * @return true If the node is contained.
     */
    public boolean containsNode( Node node ){ return nodeSeq.contains(node); }

    /**
     * Get the sequence of nodes that make up this Way.
     * @return The sequence of nodes that make up this Way.
     */
    public ArrayList<Node> getNodeSeq(){ return nodeSeq; }

    /** 
     * Get the distance spanned of this Way.
     * @return The distance spanned.
     */
    public double getTotalDistance(){ 
	if( distance == -1 ){ computeDistance(); }
	return distance; 
    }

    /** 
     * If this Way is drive able.
     */
    public boolean isDriveable(){
	String canDrive = super.getTag("highway");
	if( canDrive == null ){ driveable = false; }
	return driveable;
    }

    /** 
     * Add a shared node; this node is shared with another Way.
     */
    public void addSharedNode(String nodeID){
	sharedNodes.add(nodeID);
    }

    /** Get shared nodesIDs.
     * @return the list of nodes that this Way shares with another
     */
    public ArrayList<String> getSharedNodes(){ return sharedNodes; }


    /** 
     * Get the priority of this way.
     * Lower numbers have higher priority.
     */
    public double getPriority(){
	String b = getTag("building");
	if(b!=null){ return 2.5; }

	String h = getTag("highway");
	if(h!=null){ 
	    if(h.equals("unclassified")) return 2; 
	    if(h.equals("residential")) return 1.5;
	}
	return 0;
    }


    /**
     * "equals" is defined by having the same ID number.
     * @param otherString
     * @return true if otherRef and this object are equal.
     */
    public boolean equals(String otherString) {
	if (otherString.equals(id)) {
	    return true;
	}
	return false;
    }

    /**
     * "equals" is defined by having same ID number.
     */
    @Override
    public int hashCode() {
	int hashC = Integer.parseInt(id);
	return hashC;
    }

}

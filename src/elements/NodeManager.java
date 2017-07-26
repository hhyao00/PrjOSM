package elements;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.Set;

import org.xml.sax.Attributes;


/**
 * Class to create and manage Node objects.
 */
public class NodeManager {

    /** 
     * HashMap for mapping nodes to their ID. 
     * @key The node ID.
     * @value The node object
     */
    private HashMap<String, Node> nodeMap;

    /** 
     * HashMap for mapping nodes to their ID
     * @key The name of the Node object.
     * @value The Node object's id.
     */
    private HashMap<String, Node> nameMap;

    /** 
     * Storing nodes by their Longitude and ID.
     * @key The longitude value of the node
     * @value The node object ID.
     */
    private HashMap<Double, String> longitudeMap;

    /** Storing nodes by their latitude and ID */
    private HashMap<Double, String> latitudeMap;


    // --- end of fields --- //

    /** 
     * The constructor for NodeManager.
     */
    public NodeManager() { 
	nodeMap = new HashMap<String, Node>();
	longitudeMap = new HashMap<Double, String>();
	latitudeMap = new HashMap<Double, String>();
	nameMap = new HashMap<String, Node>();
    }

    /** 
     * Create and add a new Node object.
     * @param atts The attributes of the node.
     * @return the newly created node with the attributes.
     */
    public Node newNode(Attributes atts){ 
	String id = atts.getValue("id");
	Node n = new Node(id, atts);
	nodeMap.put(id, n);
	return n;
    }

    /**
     * Get a node given it's ID.
     * @param ref The ID of the node.
     * @return null if the node does not exist.
     */
    public Node getNode(String ref){  
	return nodeMap.get(ref);
    }

    /**
     * Get a node given it's name.
     * @param ref The ID of the node.
     * @return null if the node does not exist.
     */
    public Node getByName(String name){  
	if( nameMap.isEmpty() ) genImportantNodes();
	return nameMap.get(name);
    }

    /**
     * Get all the nodes objects themselves.
     * @return set of all the nodes.
     */
    public Collection<Node> getNodeSet(){ return nodeMap.values(); }

    /**
     * Get all the nodes objects by their ID.
     * @return set of all the nodes' IDs.
     */
    public Set<String> getNodeIDs(){ return nodeMap.keySet(); }

    /** 
     * Get all the nodes that have names; the important ones.
     * @return all nodes that have names.
     */
    public Collection<Node> getNodesWithName(){ return nameMap.values(); }

    /** 
     * Get all the names of nodes which have one.
     * @return the names of important nodes.
     */
    public ArrayList<String> getNodeNames(){ 
	ArrayList<String> names = new ArrayList<String>(nameMap.keySet());
	Collections.sort(names);
	return names;
    }

    /** Store nodes by their longitude and latitude */
    public void organizeByLonLat(){
	// I don't think I end up using longitude and latitude methods.
	for(String id: nodeMap.keySet()){
	    Node n = getNode(id);
	    double lon = n.getLongitude();
	    double lat = n.getLatitude();
	    longitudeMap.put(lon, id);
	    latitudeMap.put(lat, id);
	}
    }

    /** Get the longitudeMap */
    public HashMap<Double, String> getLongitudeMap(){
	return longitudeMap;
    }

    /** Get the latitudeMap */
    public HashMap<Double, String> getByLatitudeMap(){
	return latitudeMap;
    }

    /** Find the nodes with names; "important" nodes */
    public void genImportantNodes(){
	for(Node n: nodeMap.values()){
	    String name = n.getTag("name");
	    if( name != null ){
		n.setName(name);
		nameMap.put(name, n);
	    }
	}
    }

    /** 
     * Find the node closest to the given longitude
     * and latitude. Compares using pythagorean triangle.
     * @return the node closest to the parameters.
     */
    public Node closestNode(double lon, double lat){
	Node minN = null;
	double minDist = -1;
	for(Node n: nodeMap.values()){
	    double dist = n.distFrom(lon, lat);
	    if( minN == null || dist < minDist ){
		minN = n;
		minDist = dist;
	    }
	}
	return minN;
    }

    /** 
     * Find the node closest to the given longitude
     * and latitude. Compares using pythagorean triangle.
     * @param nodes The specific nodes that we want searched.
     * @return the node closest to the parameters.
     */
    public Node closestNode(double lon, double lat, ArrayList<Node> nodes){
	Node minN = null;
	double minDist = -1;
	for(Node n: nodes){
	    double dist = n.distFrom(lon, lat);
	    if(dist == 0) return n;
	    if( minDist == -1 || dist < minDist ){
		minN = n;
		minDist = dist;
	    }
	}
	return minN;
    }



}

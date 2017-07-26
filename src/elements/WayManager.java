package elements;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Set;

import org.xml.sax.Attributes;

/**
 * Class that creates and manages Way objects.
 */
public class WayManager {

    /** HashMap that maps Way objects to their ID */
    private HashMap<String, Way> idWayMap;

    /** ArrayList of Ways because HashMap is view only */
    private ArrayList<Way> allWays;

    /** 
     * HashMap that maps Way objects to their name (if any).
     * But there's no point in this map bc, multiple values share key. 
     */
    private HashSet<String> wayNames;

    /**
     * Some Way objects could have the same name.
     * Because i.e. multiple Way objects make up one road.
     * Maps the ID ( unique ) of Way to Name ( un-unique ).
     * @key The ID of the Way
     * @value The name of the Way
     */
    private HashMap<String, String> idNameMap;

    /**
     * A name to the ArrayList<Way> with all same names.
     * @key The name of the Way.
     * @value An ArrayList with all ways of the same name.
     */
    private HashMap<String, ArrayList<Way>> nameWayMap;

    /** 
     * A HashMap mapping the node ID to Way objects IDs
     * that contain that Node. That is, the node exists in the way.
     * The ArrayList should have size() >= 1 
     * Important note: nodeInWayMap only contains nodes 
     * that are drive able.
     */
    private HashMap<String, ArrayList<Way>> nodeInWayMap;


    // ----- end of fields ----- //

    /**
     * The constructor.
     */
    public WayManager(){
	idWayMap = new HashMap<String, Way>();
	nodeInWayMap = new HashMap<String, ArrayList<Way>>();
	allWays = new ArrayList<Way>();
	// some fields are only possible  after parsing is done.
    }

    /**
     * Create and add a new Way object
     * @param atts the Attributes of the way object.
     */
    public Way newWay(Attributes atts){ 
	String id = atts.getValue("id");
	Way w = new Way(id, atts);
	idWayMap.put(id, w);
	allWays.add(w);
	return w;
    }

    /**
     * Get all existing Ways objects.
     * @return Set of existing Ways.
     */
    public Collection<Way> getAllWays(){ return allWays; }

    /**
     * Get all existing Ways by getting their id.
     * @return Set of existing Ways IDs.
     */
    public Set<String> getAllWayIDs(){ return idWayMap.keySet(); }


    /**
     * Get all names of ways.
     * @return Set of all Way names.
     */
    public Set<String> getNameableWays(){ 
	return wayNames;
    }

    /**
     * Get all nameable Ways by their names.
     * @return Set of all nameable Ways object names.
     */
    public Set<String> getWayNames(){ 
	idNameMapInitialized();
	return idNameMap.keySet();
    }

    /**
     * Get names of all NameableWays. Sorted alphabetically.
     * @return The names of nameable ways.
     */
    public ArrayList<String> getAlphabeticalOrderedNames(){ 
	idNameMapInitialized();
	wayNamesInitialized();

	ArrayList<String> namesList = new ArrayList<String>(wayNames);
	Collections.sort(namesList);
	return namesList;
    }

    /** 
     * Get a Way object given either its name or ID.
     * @param id The reference by which we look up the Way.
     * @return the desired Way object.
     * @return null if the Way object does not exist.
     */
    public Way getWay(String id){ 
	Way w = null;
	if (idWayMap.containsKey(id)) {
	    w = idWayMap.get(id);
	    return w;
	}
	return w;
    }

    /**
     * Get a way (or several, if more than one) that have the 
     * same name. 
     * @param name The name of the Way desired
     * @return sumWays All ways that have that name.
     */
    public ArrayList<Way> getByName(String name){
	nameWayMapInitialized();
	return nameWayMap.get(name);
    }

    /**
     * Initialize the nameWayMap.
     * Want to assure that all ways are created before initializing.
     */
    private void idNameMapInitialized(){	
	if(idNameMap == null){
	    idNameMap =  new HashMap<String, String>();
	    for(String id: idWayMap.keySet()){
		Way w = this.getWay(id);
		String name = w.getName();
		if( name != null ){ 
		    idNameMap.put(id, name); 
		}
	    }
	}
    }


    /**
     * Check if wayNames is initialized.
     * @return true if initialized. Otherwise initializes.
     */
    private boolean wayNamesInitialized(){
	if(wayNames == null){
	    idNameMapInitialized();
	    wayNames = new HashSet<String>();
	    for( String name : idNameMap.values() ){
		wayNames.add(name);
	    }
	}
	return true;
    }

    /**
     * Check if nameWayMap is initialized.
     * @return true if initialized. Otherwise initializes.
     */
    private boolean nameWayMapInitialized(){
	if(nameWayMap == null){
	    nameWayMap = new HashMap<String, ArrayList<Way>>();
	    idNameMapInitialized();
	    for( Way w : idWayMap.values() ){
		String name = w.getName();
		if( name != null ){
		    ArrayList<Way> sameNameWays = findByName(name);
		    nameWayMap.put(name, sameNameWays);
		}
	    }
	}
	return true;
    }


    /**
     * Helper method. 
     * @param name The name of the Way desired
     * @return sumWays All ways that have that name.
     */
    private ArrayList<Way> findByName(String ref){
	ArrayList<Way> waySum = new ArrayList<Way>();
	for( Way w : idWayMap.values() ){
	    String name = w.getName();
	    if( ref.equals(name) ){
		waySum.add(w);
	    }
	}
	return waySum;
    }

    /** Compute the shared nodes */
    public void computeSharedNodes() {

	for( int i = 0 ; i < allWays.size(); i++ ){
	    Way way = allWays.get(i);
	    ArrayList<Node> containedNodes = way.getNodeSeq();

	    for( int j = 0 ; j < containedNodes.size(); j++ ){
		Node node = containedNodes.get(j);
		String nID = node.getID();

		if( !nodeInWayMap.containsKey(nID) ){
		    ArrayList<Way> wayList = new ArrayList<Way>();
		    wayList.add(way);
		    nodeInWayMap.put(nID, wayList);

		} else {
		    node.setShared(true);
		    ArrayList<Way> wayList = nodeInWayMap.get(nID);
		    wayList.add(way);
		    for( Way w: wayList ){
			if(w.isDriveable()) { w.addSharedNode(nID); }
		    }
		}
	    }
	}
    }

    /** Get map of Node IDs to Ways that contain the Node. */
    public HashMap<String, ArrayList<Way>> getnodeInWayMap(){ return nodeInWayMap; }

    /** 
     * Compute neighbors for nodes. A node add the
     * previous as neighbor, and the previous adds this.
     */
    public void setNeighboringNodes(){
	for( int i = 0; i < allWays.size(); i++ ){
	    Way way = allWays.get(i);
	    ArrayList<Node> nodeSeq = way.getNodeSeq();

	    for( int j = 1; j < nodeSeq.size(); j++){
		Node node = nodeSeq.get(j);
		Node prevNode = nodeSeq.get(j-1);
		node.addNeighbor(prevNode); // add each other
		prevNode.addNeighbor(node);
	    }
	}
    }


}

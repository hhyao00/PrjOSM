package elements;
import java.util.ArrayList;
import java.util.HashMap;

import org.xml.sax.Attributes;

/**
 * This class that serves to simplify the interaction between the GUI, the Parser, and the 
 * data manager classes. It contains these three classes; and makes the map data easier to
 * pass around between Classes.
 */
public class MapData {

    /** Object responsible for managing nodes */
    private NodeManager nodeManager;

    /** Object responsible for managing ways */
    private WayManager wayManager;

    /** Object responsible for managing relations */
    private RelationManager relationManager;

    /** For the bounds of this particular map.
     * @key what the value is specifying. 
     * i.e. key = "minlon" is value = min. longitude value. */
    private HashMap<String, String> boundRange;

    /** An HashMap mapping the node ID to all Way objects IDs
     * that contain that Node. That is, the node exists in the way.
     * The ArrayList should have size() >= 1 
     */
    private HashMap<String, ArrayList<Way>> nodeInWayMap;


    // -- end of fields -- //

    /**
     * Constructor for this class.
     */
    public MapData(){ 
	nodeManager = new NodeManager();
	wayManager = new WayManager();
	relationManager = new RelationManager();
	boundRange = new HashMap<String, String>();
    }

    /**
     * Add a node object.
     * @param atts The attributes of the Node object
     */
    public Node newNode( Attributes atts ){ 
	Node n = nodeManager.newNode(atts);
	return n;
    }

    /**
     * Add a Way object.
     * @param atts The attributes of the Way object
     */
    public Way newWay( Attributes atts ){ 
	Way w = wayManager.newWay(atts);
	return w;
    }

    /**
     * Add a Relation object
     * @param atts The attributes of the Relation object
     */
    public Relation newRelation( Attributes atts ){
	Relation r = relationManager.newRelation(atts);
	return r;
    }

    /**
     * Add Bounds. Attributes indicates the key to value.
     */
    public void addBounds( Attributes atts ){
	for( int i = 0 ; i < atts.getLength() ; i++ ){
	    String bound = atts.getQName(i);
	    String boundValue = atts.getValue(i);
	    boundRange.put(bound, boundValue);
	}
    }

    /**
     * Add Bound Box: Order: min lat, min lon, max lat, max lon.
     * Example of tag: bound box="41.14964,-71.89513,42.02115,-71.12079";
     */
    public void addBoundBox( Attributes atts ){
	String allBounds = atts.getValue("box");
	String[] bounds = allBounds.split(",");

	boundRange.put("minlat", bounds[0]);
	boundRange.put("minlon", bounds[1]);
	boundRange.put("maxlat", bounds[2]);
	boundRange.put("maxlon", bounds[3]);
    }

    /**
     * if there is a path between this node and other node.
     * @return the Way if there is a shared way. 
     * otherwise null, if there is no shared way between n1 and n2.
     */
    public Way connectedBy(Node n1, Node n2){
	String id1 = n1.getID();
	String id2 = n2.getID();
	ArrayList<Way> ways1 = nodeInWayMap.get(id1);
	ArrayList<Way> ways2 = nodeInWayMap.get(id2);
	for(Way w1: ways1){
	    for(Way w2: ways2){
		if(w1.equals(w2)){ return w1; }
	    }
	}
	return null;
    }

    /** 
     * Finalize the data. Must be called after parsing is complete.
     * 
     * Because there are some things that could not be computed
     * until all data is available. This also applies to Way methods 
     * involving the name, but not sure if it should be moved here. 
     */
    public void finalizeData(){ 
	// nodeManager.organizeByLonLat(); /* never used */
	nodeManager.genImportantNodes();
	wayManager.computeSharedNodes(); 
	nodeInWayMap = wayManager.getnodeInWayMap();
	wayManager.setNeighboringNodes();
    }

    /**
     * Get the nodeManager object.
     */
    public NodeManager getNodeManager(){ return nodeManager; }

    /**
     * Get the wayManager object.
     */
    public WayManager getWayManager(){ return wayManager; }

    /**
     * Get the relationManager object.
     */
    public RelationManager getRelationManager(){ return relationManager; }

    /**
     * Get the bound ranges of longitude and latitude.
     */
    public HashMap<String, String> getBounds(){ return boundRange; }


}

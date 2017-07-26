package elements;
import java.util.ArrayList;

import org.xml.sax.Attributes;

/**
 * A node. Determined by the longitude and latitude at which it is located.
 * A location "point" in a sense. It is contained in one or more Ways supposedly.
 */
public class Node extends Element{

    /** The id of this object, unique */
    private String id; 

    /** If it's an important node, it has a name */
    private String name; // possibly null

    /** The longitude of the node */
    private double longitude;

    /** The latitude of the node */
    private double latitude;

    /** Attributes associated with this node object */
    private Attributes attributes;

    /** "Neighbors" */
    private ArrayList<Node> neighbors; 

    /** If this is a shared node */
    private boolean isShared = false;


    // --- end of fields --- //

    /** 
     * Constructor for a node object.
     * @param atts Associated attributes.
     * @param id The ID of this node object.
     */
    public Node( String id, Attributes atts ){ 
	this.id = id;
	attributes = atts;
	longitude = Double.parseDouble(attributes.getValue("lon"));
	latitude = Double.parseDouble(attributes.getValue("lat"));
	neighbors = new ArrayList<Node>();
    }

    /** Get the id of this object. */
    public String getID(){ return id; }

    /** Set name of this object. */
    public void setName(String n){ name = n; }

    /** Get the id of this object. */
    public String getName(){ return name; }

    /** Add a neighbor */
    public void addNeighbor( Node n ){ neighbors.add(n); }

    /** Get the neighbors of this node */
    public ArrayList<Node> getNeighbors(){ return neighbors; }

    /**
     * Get the longitude location of the node.
     */
    public double getLongitude(){ return longitude; }

    /**
     * Get the latitude location of the node.
     */
    public double getLatitude(){ return latitude; }

    /**
     * Set this node as a shared node.
     * @param true if this node exists in more than one way.
     */
    public void setShared(boolean b){
	isShared = b;
    }

    /**
     * Get if this node is a shared node.
     * @return true if this node exists in more than one way.
     */
    public boolean isShared(){ 
	return isShared; 
    }

    /** 
     * Compute distance between two nodes. Uses pythagorean.
     * Miles per longitude and latitude formula is from:
     * www.answers.google.com/answers/threadview?id=577262
     * @return the distance between two nodes in miles. 
     */
    public double distFrom(Node n2){

	double lon2 = n2.getLongitude();
	double lat2 = n2.getLatitude();	

	double EARTH_RADIUS = 3963.1676;
	double MILES_PER_LAT = 68.703;
	double MILES_PER_LON = (Math.PI/180)* EARTH_RADIUS* Math.cos(latitude);

	double dLon = Math.abs(lon2 - longitude)* MILES_PER_LON;
	double dLat = Math.abs(lat2 - latitude)* MILES_PER_LAT;

	double x = Math.pow(dLon, 2);
	double y = Math.pow(dLat, 2);
	double z = Math.sqrt(x + y);

	return z;
    }

    /** 
     * Compute distance between two nodes. 
     * @return the distance between two nodes using 
     * the pythagorean formula in coordinate values.
     */
    public double distFrom(double lon, double lat){

	if(longitude == lon && latitude == lat) return 0;

	double dLon = Math.abs(longitude - lon);
	double dLat = Math.abs(latitude - lat);

	double x = Math.pow(dLon, 2);
	double y = Math.pow(dLat, 2);
	double z = Math.sqrt(x + y);

	return z;
    }

    /** To string method. */
    public String toString(){
	return "Node: " + id + "; " + " Longitude: " + longitude + ", Latitude: " + latitude;
    }

    /**
     * "equals" is defined by having the same ID number.
     * @param otherString 
     * @return true if otherRef and this obj are equal.
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

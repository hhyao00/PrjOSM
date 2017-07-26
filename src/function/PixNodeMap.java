package function;

import java.awt.Shape;
import java.awt.geom.Point2D;
import java.util.HashMap;

import elements.Node;

/**
 * This class maps an area of pixels to a Node. A Shape
 * object is used because of mouse pin point reasons.
 * This class is essentially a HashMap.
 */
public class PixNodeMap {

    /** Node information; map from pixel To Node.
     * @key The Shape at the Node.
     * @value The Node Object that has more info. */
    private HashMap<Shape, Node> pixNodeMap;

    /** Constructor. */
    public PixNodeMap(){
	pixNodeMap = new HashMap<Shape, Node>();
    }

    /** Put method. */
    public void put(Shape s, Node n){
	pixNodeMap.put(s, n);
    }

    /** 
     * Get method; get Node corresponding to Point2D.
     * @return the Node if it exists mapped to the 
     * point2D of a node, otherwise null.
     */
    public Node getPoint(Point2D pt){
	for( Shape s : pixNodeMap.keySet()){
	    if( s.contains(pt) ){
		return pixNodeMap.get(s);
	    }
	}
	return null;
    }

    /** Check if this key exists. */
    public boolean containsPoint( Point2D pt ){
	return getPoint(pt) != null;
    }

    /** Clear the map. Should be called along with repaint() */
    public void clear(){
	pixNodeMap.clear();
    }

}

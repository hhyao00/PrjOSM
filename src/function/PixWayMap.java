package function;

import java.awt.Shape;
import java.awt.geom.Point2D;
import java.util.HashMap;


/**
 * Purpose of this class is to allow pixel coordinates clicked 
 * on by mouse to give out the line that is the 'way' name.
 * It maps graphics to the associated place.
 */
public class PixWayMap {

    /** Allow pixel values map to a way name */
    private HashMap<Point2D, String> ptMap;

    /** Allow pixel values map to a way name */
    private HashMap<Shape, String> shapeMap;

    // --- end of fields --- //

    /** Constructor */
    public PixWayMap(){
	ptMap = new HashMap<Point2D, String>();
	shapeMap = new HashMap<Shape, String>();
    }

    /** Put a Point object to correspond with a name */
    public void put(Point2D point, String name){
	ptMap.put(point, name);
    }

    /** Put a Shape area to correspond with a name */
    public void put(Shape shape, String name){
	shapeMap.put(shape, name);
    }

    /** 
     * Check if specified parameter exists in this map.
     * @param pt the Point2D to be checked if contained.
     * @return true if there is a Shape that 
     * contains the Point2D value
     */
    public boolean containsPoint(Point2D pt){
	int WITHIN_DISTANCE = 15;
	for( Point2D point : ptMap.keySet() ){
	    if( point.distance(pt) < WITHIN_DISTANCE ){ return true; }
	}
	for( Shape s : shapeMap.keySet() ){
	    if( s.contains(pt) ){ return true; }
	}
	return false;
    }

    /**
     * Find the value that is associated with a given Point2D.
     * @param pt the Point2D object to be checked if an
     * associated value exists.
     * @return name The value at passed parameter
     * @return null If there is no associated value.
     */
    public String getValue(Point2D pt){
	for( Point2D point : ptMap.keySet() ){
	    if( point.equals(pt) ){ return ptMap.get(point); }
	}
	for( Shape s : shapeMap.keySet() ){
	    if( s.contains(pt) ){ return shapeMap.get(s); }
	}
	return null;
    }

    /**
     * Check if this object is empty.
     * @return true if empty.
     */
    public boolean isEmpty(){
	return ptMap.isEmpty() && shapeMap.isEmpty();
    }

    /**
     * Removes all the mappings from this map.
     * Should be called when repaint() is called.
     */
    public void clear(){
	ptMap.clear();
	shapeMap.clear();
    }


}
